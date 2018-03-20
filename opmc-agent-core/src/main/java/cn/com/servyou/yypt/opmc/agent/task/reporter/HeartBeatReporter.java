package cn.com.servyou.yypt.opmc.agent.task.reporter;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.config.Configuration;
import cn.com.servyou.yypt.opmc.agent.constant.Constants;
import cn.com.servyou.yypt.opmc.agent.data.cache.SystemPropertiesRegistry;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.util.HttpUtils;
import cn.com.servyou.yypt.opmc.agent.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_CONFIGURATION;

/**
 * <p>Function: [功能模块：心跳发送器]</p>
 * <p>Description: [功能描述：每隔若干时间(目前为5分钟),向opmc-server发送心跳指令]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 *          2017/7/28
 */
public class HeartBeatReporter implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(HeartBeatReporter.class);

    /**
     * 心跳rest接口的URL
     */
    private static final String HEART_BEAT_URL = "/heart/beat";

    /**
     *
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_CONFIGURATION)
    private Configuration configuration;

    /**
     * 心跳信息获取,如果为空,则初始化并且获取心跳信息
     *
     * @return
     */
    private Map<String, String> getHeartBeatMap() {
        HashMap<String, String> heartBeatMap = new HashMap<String, String>();
        heartBeatMap.put(Constants.HOST_NAME, SystemPropertiesRegistry.getHostName());
        String appName = configuration.getAppName();
        //appName在不为空的时候才放入心跳map中，为了避免心跳map形成类似{appName=null, hostName=server1}这种形式
        //造成在postForm封装数据的时候，由于第一个参数appName就是空，便跳出循环，那么hostName就不会被封装到待发送的数据里了。
        //hostName是心跳必须参数，未封装的话，心跳接口就会报错。
        if (!StringUtils.isEmpty(appName)) {
            heartBeatMap.put(Constants.APP_NAME, appName);
        }
        return heartBeatMap;
    }

    @Override
    public void run() {
        if (!configuration.isEnable()) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ready to send a heart beat ");
        }
        try {
            String result = HttpUtils.postForm(configuration.getServerUrl() + HEART_BEAT_URL, getHeartBeatMap(), "utf-8");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Result of the heart beat is : " + result);
            }
            //如果没有返回值,那么告警一把
            if ("".equals(result) || result == null) {
                LOGGER.warn("Heart beat has send, but the result of OPMC server is null!");
            }
        } catch (IOException e) {
            LOGGER.error("Can not access OPMC server! ", e);
        } catch (Throwable throwable) {
            LOGGER.error("Exception occurred on sending a heart beat ! ", throwable);
        }
    }

    //------------get---------set----------

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
