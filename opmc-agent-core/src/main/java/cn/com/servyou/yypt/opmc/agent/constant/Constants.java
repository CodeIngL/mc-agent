package cn.com.servyou.yypt.opmc.agent.constant;

/**
 * <p>Function: [功能模块：]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class Constants {

    /**
     * 插件中,字符串分割符.
     * 用于:
     * 包扫描时的包名分割
     * 插件的异常配置处,include和exclude的异常名称字符串分割
     * 包装监控项key值为Json字符串时,不同key值之间的分割
     */
    public static final String STRING_SEPARATOR = ",";

    /**
     * 用于在jmx里生成一个固定名称的mbean,该mbean的值为本次包括扫包以及动态生成的全部监控项的key值
     */
    public static final String METRICS_DYNAMIC_KEY = "DynamicItems";

    /**
     * 心跳主机名
     */
    public static final String HOST_NAME = "hostName";
    /**
     * 心跳应用名
     */
    public static final String APP_NAME = "appName";

    /**
     * 心跳版本
     */
    public static final String VERSION = "version";

    /**
     * 异常发生的主机ip
     */
    public static final String EXCEPTION_KEY_IP = "ip";
    /**
     * 异常的创建时间
     */
    public static final String EXCEPTION_KEY_CREATE_DATE = "createDate";
    /**
     * 异常的信息
     */
    public static final String EXCEPTION_KEY_MESSAGE = "message";

    /**
     * JSON形式的键值的格式化模板
     */
    public static final String KEY_SETS_FORMAT = "\"{0}\":\"{1}\",";

}
