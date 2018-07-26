package cn.com.servyou.yypt.opmc.agent.data.cache;

import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.common.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * <p>Function: [功能模块：系统属性读取]</p>
 * <p>Description: [功能描述：读取系统属性并且缓存]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 *          2017/7/18
 */
public class SystemPropertiesRegistry {

    private static final Log LOGGER = LogFactory.getLog(SystemPropertiesRegistry.class);

    /**
     * 系统属性
     */
    private static Properties systemProperties = null;
    /**
     * InetAddress实例
     */
    private static InetAddress inetAddress = null;
    /**
     * jmx远程端口的属性名
     */
    private static final String JMX_REMOTE_PORT_KEY = "com.sun.management.jmxremote.port";
    /**
     * jmx端口
     */
    private static String jmxPort;

    /**
     * 初始化时获取系统属性, systemProperties jmxPort inetAddress
     */
    static {
        systemProperties = System.getProperties();
        jmxPort = systemProperties.getProperty(JMX_REMOTE_PORT_KEY);
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * 获取主机名
     *
     * @return String
     */
    public static String getHostName() {
        //先获取一把环境变量里的HOSTNAME(linux里必定会有)
        String hostName = System.getenv("HOSTNAME");
        if (StringUtils.isEmpty(hostName)) {
            //如果为空,可能是windows机器,那么就获取一把COMPUTERNAME
            hostName = System.getenv("COMPUTERNAME");
        }
        //如果都是空,那么就以inetAddress来获取hostName
        if (StringUtils.isEmpty(hostName)) {
            hostName = inetAddress.getHostName();
        }
        return hostName;
    }

    /**
     * 获取所有注册ip，多个网卡会进行拼接，用|进行分割
     *
     * @return String
     */
    public static String getAllIp() {
        StringBuilder builder = new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses(); addressEnumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = addressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        builder.append(inetAddress.getHostAddress().toString() + "|");
                    }
                }
            }
        } catch (SocketException ex) {
            LOGGER.error("", ex);
        }
        return builder.toString();
    }

    public static String getJmxPort() {
        return jmxPort;
    }

}
