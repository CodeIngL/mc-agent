package com.codeL.mc.agent.config;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.codeL.mc.agent.constant.McConfigConstants.*;

/**
 * <p>Function: [功能模块：mc配置项类]</p>
 * <p>Description: [功能描述：mc配置项类,包含插件的启动,包声明,异常过滤声明,服务端URL等配置内容]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
public class Configuration {


    /**
     * 应用名
     */
    @ConfigAnnotation(name = MC_USER_CONFIG_APP_NAME)
    private String appName;

    /**
     * mc服务端的地址
     */
    @ConfigAnnotation(name = MC_USER_CONFIG_SERVER_URL)
    private String serverUrl;

    /**
     * 插件是否开启的实际配置选项,即提供给插件读取的.为boolean类型
     */
    @ConfigAnnotation(name = MC_USER_CONFIG_ENABLE)
    private boolean enable = false;

    /**
     * 需要监测的异常列表
     */
    @ConfigAnnotation(name = MC_USER_CONFIG_EXCEPTION_INCLUDES)
    private Set<String> exceptionIncludes = new HashSet<String>();

    /**
     * 需要监测的异常列表
     */
    @ConfigAnnotation(name = MC_USER_CONFIG_EXCEPTION_EXCLUDES)
    private Set<String> exceptionExcludes = new HashSet<String>();


    @ConfigAnnotation(name = MC_USER_CONFIG_CATCH_ALL)
    private boolean catchAll = true;

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

    public boolean isCatchAll() {
        return catchAll;
    }

    public void setCatchAll(boolean catchAll) {
        this.catchAll = catchAll;
    }

    public Collection<String> getExceptionExcludes() {
        return exceptionExcludes;
    }

    public void setExceptionExcludes(Set<String> exceptionExcludes) {
        this.exceptionExcludes = exceptionExcludes;
    }
}
