package cn.com.servyou.yypt.opmc.agent.util;

import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTPq请求工具类，做到简单即可，原则上不依赖任何第三方库
 * <p>
 * Created by wangql on 2017/5/3.
 */
public class HttpUtils {

    private static Log LOGGER = LogFactory.getLog(HttpUtils.class);

    public static String get(String url, String encoding) throws IOException {
        HttpURLConnection conn = null;
        try {
            // 利用string url构建URL对象
            conn = getConnection("GET", url, encoding);
            return getResult(conn, encoding);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 字节流长度
     */
    private static final int BYTE_LENGTH = 1024;

    /**
     * 根据流返回一个字符串信息
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static String getStringFromInputStream(InputStream is, String encoding) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[BYTE_LENGTH];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString(encoding);// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }

    public static String postForm(String url, Map formMap, String encoding) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = getConnection("POST", url, encoding);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
            OutputStream out = conn.getOutputStream();// 获得一个输出流,向服务器写数据
            StringBuilder postData = new StringBuilder();
            Iterator<Map.Entry<Object, Object>> iterator = formMap.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<Object, Object> entry = iterator.next();
                if (entry.getValue() != null) {
                    postData.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), encoding));
                }
                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> next = iterator.next();
                    if (entry.getValue() != null) {
                        postData.append("&").append(next.getKey()).append("=")
                                .append(URLEncoder.encode(next.getValue().toString(), encoding));
                    }
                }
            }
            out.write(postData.toString().getBytes(encoding));
            out.flush();
            out.close();
            return getResult(conn, encoding);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String postJson(String url, String postJsonBody, String encoding) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = getConnection("POST", url, encoding);
            conn.setRequestProperty("Content-Type", "application/json;charset=" + encoding);

            OutputStream out = conn.getOutputStream();// 获得一个输出流,向服务器写数据
            out.write(postJsonBody.getBytes(encoding));
            out.flush();
            out.close();
            return getResult(conn, encoding);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 默认超时时间
     */
    private static final int DEFAULT_TIME_OUT = 5000;


    private static HttpURLConnection getConnection(String method, String url, String encoding) throws IOException {
        URL mURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
        conn.setRequestMethod(method);// 设置请求方法为post
        //读取超时时间设置为5秒
        conn.setReadTimeout(DEFAULT_TIME_OUT);
        //连接超时时间设置为5秒
        conn.setConnectTimeout(DEFAULT_TIME_OUT);
        conn.setRequestProperty("Accept-Charset", encoding);
        conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
        return conn;
    }

    /**
     * 成功的返回值
     */
    private static final int SUCCESS_RESPONSE_CODE = 200;

    /**
     * 获取服务端返回的结果，统统转成字符串
     *
     * @param conn
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String getResult(HttpURLConnection conn, String encoding) throws IOException {
        int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
        if (responseCode == SUCCESS_RESPONSE_CODE) {
            InputStream is = conn.getInputStream();
            String state = getStringFromInputStream(is, encoding);
            return state;
        } else {
            LOGGER.warn("call failed " + responseCode);
        }
        return null;
    }

    /**
     * Downlaod file from remote http server
     *
     * @param savedLocalFile local saved file
     * @param url            remote url
     * @throws IOException
     */
    public static void download(String savedLocalFile, String url) throws IOException {
        HttpURLConnection conn = null;
        try {
            // 利用string url构建URL对象
            conn = getConnection("GET", url, "utf-8");
            int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == SUCCESS_RESPONSE_CODE) {
                InputStream is = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(savedLocalFile);
                // 模板代码 必须熟练
                byte[] buffer = new byte[BYTE_LENGTH];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.flush();
                os.close();
            } else {
                LOGGER.warn("Download Failed: " + responseCode);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
