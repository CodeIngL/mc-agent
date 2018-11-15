package com.codeL.mc.agent.task.reporter;

import com.codeL.mc.agent.Version;
import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.common.SystemPropertiesRegistry;
import com.codeL.mc.agent.config.Configuration;
import com.codeL.mc.agent.constant.Constants;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;
import com.codeL.mc.agent.util.HttpUtils;
import com.codeL.mc.agent.common.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_CONFIGURATION;

/**
 * <p>Function: [功能模块：心跳发送器]</p>
 * <p>Description: [功能描述：每隔若干时间(目前为5分钟),向mc-server发送心跳指令]</p>
 * <p>Company: </p>
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
     * 版本号
     */
    static final String version = Version.getVersion();

    /**
     * hostName
     */
    private final String hostName = SystemPropertiesRegistry.getHostName();

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
        heartBeatMap.put(Constants.HOST_NAME, this.hostName);
        heartBeatMap.put(Constants.VERSION, version);
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
                LOGGER.warn("Heart beat has send, but the result of MC server is null!");
            }
        } catch (IOException e) {
            LOGGER.error("Can not access MC server! ", e);
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
