package com.codeL.mc.agent;

import com.codeL.mc.agent.common.NamedThreadFactory;
import com.codeL.mc.agent.common.SystemPropertiesRegistry;
import com.codeL.mc.agent.common.util.StringUtils;
import com.codeL.mc.agent.jvm.tools.FullGcShower;
import com.codeL.mc.agent.metric.*;
import com.codeL.mc.agent.metric.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/25
 */
@Slf4j
public class GcReporter {

    private static final long GC_SEND_INIT_DELAY_MS = 5000;

    private static final long GC_SEND_BETWEEN_PERIOD_MS = 20000;

    private static ScheduledExecutorService schedualService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("mc-fgc"));

    private FullGcShower gcShower = new FullGcShower();

    private GarbageCollectorMetric garbageCollectorMetric = new GarbageCollectorMetricProvider().get();

    private GarbageCollectorMetricTimerSnapshot lastTimerSnapshot;

    private long lastFullGcCount = 0;

    private final String hostName = SystemPropertiesRegistry.getHostName();

    @Setter
    private String url;

    @Setter
    private Long initDelayMs = -1L;

    @Setter
    private Long periodMs = -1L;

    @PostConstruct
    public void init() {
        initDefault();
        gcShower.init();
        if (garbageCollectorMetric instanceof UnknownGarbageCollectorMetric) {
            log.info("can't distinguish the gc, so stop gc alert");
            return;
        }
        lastTimerSnapshot = new GarbageCollectorMetricTimerSnapshot(garbageCollectorMetric.getSnapshot());
        schedualService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    postForm(url, fgcInfo(), "UTF-8");
                } catch (Exception e) {
                    log.warn("something wrong happen", e);
                }
            }
        }, initDelayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    private Map<String, String> fgcInfo() {
        GarbageCollectorMetricSnapshot snapShot = garbageCollectorMetric.getSnapshot();
        Long currentOldCount = snapShot.getGcOldCount();
        if (currentOldCount == lastTimerSnapshot.getGcOldCount()) {
            return null;
        }
        String gcStr = gcShower.fetchFGC();
        if (StringUtils.isEmpty(gcStr)) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        for (String str : gcStr.split(" ")) {
            if (StringUtils.isNotEmpty(str)) {
                result.add(str);
            }
        }
        if (result.size() == 0) {
            return null;
        }
        Long fulCount = Long.valueOf(result.get(0).trim());
        if (fulCount <= lastFullGcCount) {
            return null;
        }
        lastFullGcCount = fulCount;
        Map<String, String> info = new HashMap<String, String>();
        info.put("hostName", hostName);
        info.put("gcDescription", garbageCollectorMetric.getType().oldGenName());
        info.put("gcCount", String.valueOf(snapShot.getGcOldCount()));
        info.put("gcTime", String.valueOf(snapShot.getGcOldTime()));
        info.put("fullGcCount", result.get(0));
        info.put("fullGcTime", result.get(1));
        lastTimerSnapshot = new GarbageCollectorMetricTimerSnapshot(snapShot);
        return info;
    }

    private void initDefault() {
        final String FULLGC_URL = "mc.fullgc.url";
        final String GC_INIT_DELAY_MS = "mc.gc.init.delay.ms";
        final String GC_PERIOD_MS = "mc.gc.period.ms";
        String goalUrl = System.getProperty(FULLGC_URL);
        if (StringUtils.isNotEmpty(goalUrl)) {
            url = goalUrl;
        }
        String initDelay = System.getProperty(GC_INIT_DELAY_MS);
        if (StringUtils.isNotEmpty(initDelay)) {
            try {
                initDelayMs = Long.valueOf(initDelay);
            } catch (Exception e) {
                // ignore
            }
        }
        if (initDelayMs <= 0) {
            initDelayMs = GC_SEND_INIT_DELAY_MS;
        }
        String period = System.getProperty(GC_PERIOD_MS);
        if (StringUtils.isNotEmpty(period)) {
            try {
                periodMs = Long.valueOf(period);
            } catch (Exception e) {
                // ignore
            }
        }
        if (periodMs <= 0) {
            periodMs = GC_SEND_BETWEEN_PERIOD_MS;
        }
    }

    private static final int DEFAULT_TIME_OUT = 5000;

    private static void postForm(String url, Map formMap, String encoding) throws IOException {
        if (formMap == null || formMap.size() == 0) {
            return;
        }
        HttpURLConnection conn = null;
        try {
            conn = getConnection("POST", url, encoding);
            OutputStream out = conn.getOutputStream();
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
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.getResponseCode();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static HttpURLConnection getConnection(String method, String url, String encoding) throws IOException {
        URL mURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
        conn.setRequestMethod(method);
        conn.setReadTimeout(DEFAULT_TIME_OUT);
        conn.setConnectTimeout(DEFAULT_TIME_OUT);
        conn.setRequestProperty("Accept-Charset", encoding);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        return conn;
    }

}
