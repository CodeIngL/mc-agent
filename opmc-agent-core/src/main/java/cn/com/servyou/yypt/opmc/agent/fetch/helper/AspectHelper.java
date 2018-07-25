package cn.com.servyou.yypt.opmc.agent.fetch.helper;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.data.cache.DivideParamParserRegistry;
import cn.com.servyou.yypt.opmc.agent.data.key.KeyCache;
import cn.com.servyou.yypt.opmc.agent.data.key.KeyCacheDelegate;
import cn.com.servyou.yypt.opmc.agent.data.metrics.MetricsKey;
import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.DivideParamGetType;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter.AnnotationPropertiesGetter;
import cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.util.MethodUtil;
import cn.com.servyou.yypt.opmc.agent.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_DIVIDE_PARAM_PARSER_REGISTRY;
import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_KEY_CACHE_DELEGATE;
import static cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter.AnnotationPropertiesGetterUtils.getAnnotationDivideConfig;
import static cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter.AnnotationPropertiesGetterUtils.getAnnotationValue;

/**
 * <p>Function: [功能模块：aop拦截的工具类]</p>
 * <p>Description: [功能描述：获取被执行方法的统计项key,按明细统计的key,获取按明细统计的参数实际值]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 *          2017/8/15
 */
public class AspectHelper {

    private static final Log LOGGER = LogFactory.getLog(AspectHelper.class);

    private static final Map<String,String> aliasMapKV;

    /**
     * simple kv map for namespace
     */
    static {
        aliasMapKV = new HashMap<String, String>();
        aliasMapKV.put("RequestMapping","MCTimer");
    }

    /**
     * 方法级的监控项被包装后的格式,从注解扫入以及在aop拦截时包装.格式为统计类型.类名.方法别名.
     * 例:Timer.DemoClass.DemoName
     */
    public static final String KEY_PATTEN = "{0}.{1}.{2}";

    /**
     * 注册表
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_DIVIDE_PARAM_PARSER_REGISTRY)
    private DivideParamParserRegistry divideParamParserCache;

    /**
     * keyCache委托处理
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_KEY_CACHE_DELEGATE)
    private KeyCacheDelegate keyCacheDelegate;

    /**
     * 获取监控键值
     *
     * @param pjp    pjp
     * @param getter getter
     * @return MetricsKey
     * @throws NoSuchMethodException  异常
     * @throws IllegalAccessException 异常
     * @throws ClassNotFoundException 异常
     * @throws InstantiationException 异常
     */
    public MetricsKey fetchMetricsKeys(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) throws NoSuchMethodException,
            IllegalAccessException, ClassNotFoundException, InstantiationException {
        //命名空间
        String nameSpace = getter.getAnnotation().getSimpleName();
        if(aliasMapKV.containsKey(nameSpace)){
            nameSpace = aliasMapKV.get(nameSpace);
        }
        //获取注册方法
        Method method = MethodUtil.getSignatureMethod(pjp);
        KeyCache keyCache = keyCacheDelegate.takeKeyCache(method);

        //尝试从缓存中获得数据
        if (keyCache != null) {
            String staticKey = MessageFormat.format(keyCache.getStaticKey(), nameSpace);
            if (keyCache.isHasDynamicKey()) {
                DivideConfigInfo divideConfigInfo = keyCache.getConfigIno();
                String divideParamValue = getArgStringByDivideParam(pjp, divideConfigInfo);
                if (StringUtils.isEmpty(divideParamValue)) {
                    return new MetricsKey(staticKey, null);
                }
                String dynamicKeys = MessageFormat.format(KEY_PATTEN, staticKey, divideConfigInfo.getDivideParamName(), divideParamValue);
                return new MetricsKey(staticKey, dynamicKeys);
            }
            return new MetricsKey(staticKey, null);
        }


        Method outMethod = method;
        //缓存中不存在数据，或者已经过期
        if (method.getDeclaredAnnotations().length == 0) {
            method = MethodUtil.getRealMethod(pjp, method.getParameterTypes());
        }
        //key的命名规则为统计类型.类名.方法别名(如果注解时声明了name属性,就取此属性,否则取方法名),例:Timer.DemoClass.DemoName
        String value = getAnnotationValue(method, getter);
        if (StringUtils.isEmpty(value)) {
            value = pjp.getSignature().getName();
        }
        String staticKey = MessageFormat.format(KEY_PATTEN, "{0}", pjp.getTarget().getClass().getSimpleName(), value);

        keyCache = new KeyCache();
        keyCache.setStaticKey(staticKey);
        staticKey = MessageFormat.format(staticKey, nameSpace);


        //如果方法的注解有配置divideParamName属性, 说明该方法开启了按参数明细动态统计.
        //取方法的注解的divideParamName属性作为明细key值的开头,然后取方法名称为divideParamName的参数的实际值作为明细key值的结尾.
        //之后将明细key值与方法key值组装作为明细的key值,格式为方法初始key值 + .号 + 明细key值
        //获取按参数明细统计的配置实体
        boolean hasDynamicKey = true;
        DivideConfigInfo divideConfigInfo = getAnnotationDivideConfig(method, getter);
        switch (DivideConfigInfo.valid(divideConfigInfo)) {
            case ERR_CONF:
                LOGGER.warn("Method :[" +
                        pjp.getSignature().getName() +
                        "] in class :[" + pjp.getTarget() +
                        "] has divideParamName config ,but divideParamGetType has not been configurated.");
            case UN_CONF:
                hasDynamicKey = false;
        }
        keyCacheDelegate.registerKeyCache(outMethod, keyCache);
        keyCache.setHasDynamicKey(hasDynamicKey);
        if (!hasDynamicKey) {
            return new MetricsKey(staticKey, null);
        }
        keyCache.setConfigIno(divideConfigInfo);
        //获取参数名为divideParamName的具体参数值,根据参数获取类型来获取
        String divideParamValue = getArgStringByDivideParam(pjp, divideConfigInfo);
        if (StringUtils.isNotEmpty(divideParamValue)) {
            String dynamicKeys = MessageFormat.format(KEY_PATTEN, staticKey, divideConfigInfo.getDivideParamName(), divideParamValue);
            return new MetricsKey(staticKey, dynamicKeys);

        }
        return new MetricsKey(staticKey, null);
    }

    /**
     * 根据不同的明细统计参数获取方式来获取参数实际值
     *
     * @param pjp
     * @param info 配置参数实体
     * @return String
     */
    private String getArgStringByDivideParam(ProceedingJoinPoint pjp, DivideConfigInfo info) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        //参数获取类型取第一个值,虽然可以配置多个值,但是只取第一个.(为了default可以配置为一个空对象,才将该属性定义为数组)
        DivideParamGetType divideParamGetType = info.getDivideParamGetType()[0];
        //根据divideParamType的不同类型来进行不同的取数逻辑
        //如果是url,就从http request里取值
        switch (divideParamGetType) {
            case PARSE:
                //从注解中获取到的类的class
                //取第一个值,虽然可以配置多个值,但是只取第一个.(为了default可以配置为一个空对象,才将该属性定义为数组)
                Class<? extends DivideParamParser>[] divideParamParsers = info.getDivideParamParserClass();
                if (divideParamParsers == null || divideParamParsers.length == 0) {
                    //如果类的class为空,则是方法注解未配置BEAN实例,目前进行提示.不抛异常,不影响业务正常流动.
                    LOGGER.warn("Method :[" + pjp.getSignature().getName() + "] in class :[" + pjp.getTarget() + "] has divideParamName config , and the divideParamGetType is PARSE,but DivideParamParserClass has not been configurated.");
                    break;
                }
                Class<? extends DivideParamParser> divideParamParser = divideParamParsers[0];
                //如果类的class不为空,从缓存中获取对应的类实例,并且获取parse方法的返回值进行返回.以本次方法入参作为parse的入参.
                DivideParamParser divideParamParserInstance = divideParamParserCache.getBean(divideParamParser);
                return divideParamParserInstance.parse(pjp.getArgs());
            default:
                break;
        }
        return doGetArgStringByDivideParam(pjp, info);
    }


    /**
     * 子类覆写
     *
     * @param pjp
     * @param info
     * @return
     */
    protected String doGetArgStringByDivideParam(ProceedingJoinPoint pjp, DivideConfigInfo info) {
        return "";
    }
}
