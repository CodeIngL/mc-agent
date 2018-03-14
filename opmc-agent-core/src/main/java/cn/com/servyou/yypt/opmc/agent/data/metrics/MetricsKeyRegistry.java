package cn.com.servyou.yypt.opmc.agent.data.metrics;

import cn.com.servyou.yypt.opmc.agent.constant.Constants;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * <p>Function: [功能模块：key值缓存]</p>
 * <p>Description: [功能描述：存储需要在metrics中进行记录的监控项的key值(监控项名),由包扫描以及按参数明细统计得来.]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class MetricsKeyRegistry {

    private static final Log LOGGER = LogFactory.getLog(MetricsKeyRegistry.class);

    /**
     * 在转置为json的时候,StringBuffer的默认大小.
     * 避免按照默认值定义StringBuffer大小之后,在转置时不停的开辟内存空间提高耗时.
     */
    private static final int STRING_BUFFER_IN_JSON_CONVERTER_DEFAULT_SIZE = 4096;

    /**
     * 应用启动时,通过扫描和方法明细统计动态生成的监控项key
     */
    private final Map<String, Set<String>> dynamicKey = new HashMap<String, Set<String>>();

    /**
     * @param keyType
     */
    void putKeyType(String keyType) {
        if (keyType == null || "".equals(keyType.trim())) {
            return;
        }
        if (dynamicKey.containsKey(keyType)) {
            return;
        }
        dynamicKey.put(keyType, new HashSet<String>());
    }

    /**
     * 获取根据包扫描，扫描到的监控项值Set
     *
     * @param keyType key值类型,对应Timer,Gauge等
     * @return Set
     */
    Set<String> getKeySetFacade(String keyType) {
        return Collections.unmodifiableSet(dynamicKey.get(keyType));
    }

    /**
     * @param keyType
     * @return
     */
    private Set<String> getKeySet(String keyType) {
        return dynamicKey.get(keyType);
    }

    /**
     * 如果监控项key值在监控项key缓存中不存在,那么就放入到缓存中,否则不做任何处理.
     * 基于按照入参明细统计的功能，可以认为应用内的监控项是动态在增加的，所以在收到明细统计申请的时候应该比对对应的key值，如不存在则增加。
     *
     * @param keyType 监控项类型
     * @param keyName 监控项名称
     */
    boolean putKeyToCacheIfNotExist(String keyType, String keyName) {
        if (keyType == null || "".equals(keyType.trim())) {
            return false;
        }
        putKeyType(keyType);
        Set<String> keySet = getKeySet(keyType);
        if (!keySet.contains(keyName)) {
            keySet.add(keyName);
        }
        return true;
    }

    /**
     * 返回key值的json格式.
     *
     * @return String
     */
    public String getDynamicKeyJson() {
        return convertKeysMapToJsonString(dynamicKey);
    }

    /**
     * 将map里的key值转换为json格式
     *
     * @param keys
     * @return
     */
    private String convertKeysMapToJsonString(Map<String, Set<String>> keys) {
        String keyJson = "";
        if (keys.size() > 0) {
            //最外围的大括号
            StringBuilder builder = new StringBuilder(STRING_BUFFER_IN_JSON_CONVERTER_DEFAULT_SIZE);
            builder.append("{");
            //遍历keys,将其组装为类似json格式的字符串
            for (Map.Entry<String, Set<String>> entry : keys.entrySet()) {
                Set<String> set = entry.getValue();
                String keyForThisType = "";
                //循环set,将set中的key包装为以英文逗号分割的形式.
                for (String key : set) {
                    keyForThisType = keyForThisType + key + Constants.STRING_SEPARATOR;
                }
                //如果组装完毕的key值长度大于1,那么证明有值,去除最后的英文逗号
                if (keyForThisType.length() > 1) {
                    keyForThisType = keyForThisType.substring(0, keyForThisType.length() - 1);
                }
                //最后,将此类型的统计项key包装为json的key:value格式
                builder.append(MessageFormat.format(Constants.KEY_SETS_FORMAT, entry.getKey(), keyForThisType));
            }

            //最后,封装完成json形式的key值,并且返回
            int keyBufferLength = builder.length() - 1;
            //去除最后的逗号,如果最后是逗号,那么就去除,否则不处理
            if (builder.lastIndexOf(",") == keyBufferLength) {
                keyJson = builder.substring(0, keyBufferLength) + "}";
            } else {
                keyJson = builder.toString() + "}";
            }
        }
        return keyJson;
    }

}
