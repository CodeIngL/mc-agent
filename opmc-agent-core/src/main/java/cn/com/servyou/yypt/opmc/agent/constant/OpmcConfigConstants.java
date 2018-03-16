package cn.com.servyou.yypt.opmc.agent.constant;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/2/7
 */
public class OpmcConfigConstants {

    //about metrics

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_METRICS_KEY_REGISTRY = "opmcSystem.class.internalMetricsKeyRegistry";

    /**
     * 委托
     */
    public static final String CLASS_INTERNAL_METRICS_DELEGATE = "opmcSystem.class.internalMetricsDelegate";

    //about exception

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_EXCEPTION_HOLDER_REGISTRY = "opmcSystem.class.internalExceptionHolderRegistry";

    /**
     * 委托
     */
    public static final String CLASS_INTERNAL_EXCEPTION_HOLDER_DELEGATE = "opmcSystem.class.internalExceptionHolderDelegate";

    /**
     * 异常报告逻辑器
     */
    public static final String CLASS_INTERNAL_EXCEPTION_REPORTER = "opmcSystem.class.internalExceptionReporter";

    /**
     * 异常报告器
     */
    public static final String CLASS_INTERNAL_EXCEPTION_REPORTER_STARTER = "opmcSystem.class.internalExceptionReporterStarter";

    //about heartBeat

    /**
     * 心跳报告逻辑器
     */
    public static final String CLASS_INTERNAL_HEART_BEAT_REPORTER = "opmcSystem.class.internalHeartBeatReporter";

    /**
     * 心跳报告器
     */
    public static final String CLASS_INTERNAL_HEART_BEAT_REPORTER_STARTER = "opmcSystem.class.internalHeartBeatReporterStarter";

    //about keyCache

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_REGISTRY = "opmcSystem.class.internalKeyCacheRegistry";


    /**
     * 委托
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_DELEGATE = "opmcSystem.class.internalKeyCacheDelegate";

    /**
     * 报告逻辑器
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_REPORTER = "opmcSystem.class.internalKeyCacheReporter";

    /**
     * 报告器
     */
    public static final String CLASS_INTERNAL_KEY_CACHE_REPORTER_STARTER = "opmcSystem.class.internalKeyCacheReporterStarter";


    //about other's

    /**
     * 配置类
     */
    public static final String CLASS_INTERNAL_CONFIGURATION = "opmcSystem.class.internalConfiguration";

    /**
     * aspectj类
     */
    public static final String CLASS_INTERNAL_ASPECT_HELPER = "opmcSystem.class.internalAspectHelper";

    /**
     * 注册表
     */
    public static final String CLASS_INTERNAL_DIVIDE_PARAM_PARSER_REGISTRY = "opmcSystem.class.internalDivideParamParserRegistry";


    //用户配置


    /**
     * 应用名
     */
    public static final String OPMC_USER_CONFIG_APP_NAME = "opmc.user.config.appName";


    /**
     * 心跳地址
     */
    public static final String OPMC_USER_CONFIG_SERVER_URL = "opmc.user.config.serverUrl";


    /**
     * 开关
     */
    public static final String OPMC_USER_CONFIG_ENABLE = "opmc.user.config.enable";


    /**
     * 异常包括
     */
    public static final String OPMC_USER_CONFIG_EXCEPTION_INCLUDES = "opmc.user.config.exceptionIncludes";


    /**
     * 注册排除
     */
    public static final String OPMC_USER_CONFIG_EXCEPTION_EXCLUDES = "opmc.user.config.exceptionExcludes";


    /**
     * 队列大小
     */
    public static final String OPMC_USER_CONFIG_EXCEPTION_HOLDER_QUEUE_SIZE = "opmc.user.config.exceptionHolderQueueSize";


    /**
     * 超时时间
     */
    public static final String OPMC_USER_KEY_CACHE_REGISTRY_EXPIRE_TIME = "opmc.user.keyCache.registry.expire.time";


    /**
     * 最大大小
     */
    public static final String OPMC_USER_KEY_CACHE_REGISTRY_MAX_SIZE = "opmc.user.keyCache.registry.maxSize";

}
