package cn.com.servyou.yypt.opmc.agent.config;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.*;

/**
 * <p>Function: [功能模块：opmc配置项类]</p>
 * <p>Description: [功能描述：opmc配置项类,包含插件的启动,包声明,异常过滤声明,服务端URL等配置内容]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class Configuration {


    /**
     * 应用名
     */
    @ConfigAnnotation(name = OPMC_USER_CONFIG_APP_NAME)
    private String appName;

    /**
     * opmc服务端的地址
     */
    @ConfigAnnotation(name = OPMC_USER_CONFIG_SERVER_URL)
    private String serverUrl;

    /**
     * 插件是否开启的实际配置选项,即提供给插件读取的.为boolean类型
     */
    @ConfigAnnotation(name = OPMC_USER_CONFIG_ENABLE)
    private boolean enable = false;

    /**
     * 需要监测的异常列表
     */
    @ConfigAnnotation(name = OPMC_USER_CONFIG_EXCEPTION_INCLUDES)
    private Set<String> exceptionIncludes = new HashSet<String>();

    //------------------get-----set---------//

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Collection<String> getExceptionIncludes() {
        return exceptionIncludes;
    }

    public void setExceptionIncludes(Set<String> exceptionIncludes) {
        this.exceptionIncludes = exceptionIncludes;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
