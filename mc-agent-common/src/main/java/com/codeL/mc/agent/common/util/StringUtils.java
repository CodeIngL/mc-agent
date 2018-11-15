package com.codeL.mc.agent.common.util;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/2/5
 */
public abstract class StringUtils {

    /**
     * 是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

}
