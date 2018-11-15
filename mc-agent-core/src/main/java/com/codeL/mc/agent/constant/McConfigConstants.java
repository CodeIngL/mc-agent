package com.codeL.mc.agent.constant;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/2/7
 */
public class McConfigConstants {

    //about metrics

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_METRICS_KEY_REGISTRY = "mcSystem.class.internalMetricsKeyRegistry";

    /**
     * 委托
     */
    public static final String CLASS_INTERNAL_METRICS_DELEGATE = "mcSystem.class.internalMetricsDelegate";

    //about exception

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_EXCEPTION_HOLDER_REGISTRY = "mcSystem.class.internalExceptionHolderRegistry";

    /**
     * 委托
     */
    public static final String CLASS_INTERNAL_EXCEPTION_HOLDER_DELEGATE = "mcSystem.class.internalExceptionHolderDelegate";

    /**
     * 异常报告逻辑器
     */
    public static final String CLASS_INTERNAL_EXCEPTION_REPORTER = "mcSystem.class.internalExceptionReporter";

    /**
     * 异常报告器
     */
    public static final String CLASS_INTERNAL_EXCEPTION_REPORTER_STARTER = "mcSystem.class.internalExceptionReporterStarter";

    //about heartBeat

    /**
     * 心跳报告逻辑器
     */
    public static final String CLASS_INTERNAL_HEART_BEAT_REPORTER = "mcSystem.class.internalHeartBeatReporter";

    /**
     * 心跳报告器
     */
    public static final String CLASS_INTERNAL_HEART_BEAT_REPORTER_STARTER = "mcSystem.class.internalHeartBeatReporterStarter";

    //about keyCache

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_REGISTRY = "mcSystem.class.internalKeyCacheRegistry";


    /**
     * 委托
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_DELEGATE = "mcSystem.class.internalKeyCacheDelegate";

    /**
     * 报告逻辑器
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_REPORTER = "mcSystem.class.internalKeyCacheReporter";

    /**
     * 报告器
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_REPORTER_STARTER = "mcSystem.class.internalKeyCacheReporterStarter";


    //about other's

    /**
     * 配置类
     */
    public static final String CLASS_INTERNAL_CONFIGURATION = "mcSystem.class.internalConfiguration";

    /**
     * aspectj类
     */
    public static final String CLASS_INTERNAL_ASPECT_HELPER = "mcSystem.class.internalAspectHelper";

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_DIVIDE_PARAM_PARSER_REGISTRY = "mcSystem.class.internalDivideParamParserRegistry";


    //用户配置


    /**
     * 应用名
     */
    public static final String MC_USER_CONFIG_APP_NAME = "mc.user.config.appName";


    /**
     * 心跳地址
     */
    public static final String MC_USER_CONFIG_SERVER_URL = "mc.user.config.serverUrl";


    /**
     * 开关
     */
    public static final String MC_USER_CONFIG_ENABLE = "mc.user.config.enable";

    /**
     * 开关抓取所有异常
     */
    public static final String MC_USER_CONFIG_CATCH_ALL = "mc.user.config.catchAll";


    /**
     * 异常包括
     */
    public static final String MC_USER_CONFIG_EXCEPTION_INCLUDES = "mc.user.config.exceptionIncludes";

    /**
     * 异常排除
     */
    public static final String MC_USER_CONFIG_EXCEPTION_EXCLUDES = "mc.user.config.exceptionExcludes";

    /**
     * 队列大小
     */
    public static final String MC_USER_CONFIG_EXCEPTION_HOLDER_QUEUE_SIZE = "mc.user.config.exceptionHolderQueueSize";


    /**
     * 超时时间
     */
    public static final String MC_USER_KEY_CACHE_REGISTRY_EXPIRE_TIME = "mc.user.keyCache.registry.expire.time";

}
