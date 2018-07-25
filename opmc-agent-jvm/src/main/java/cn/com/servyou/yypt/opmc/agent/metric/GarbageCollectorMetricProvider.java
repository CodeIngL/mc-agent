package cn.com.servyou.yypt.opmc.agent.metric;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/24
 */
@Slf4j
public class GarbageCollectorMetricProvider {

    public GarbageCollectorMetric get() {
        GarbageCollectorMetric garbageCollectorMetric = null;
        Map<String, GarbageCollectorMXBean> garbageCollectorMap = createGarbageCollectorMap();
        for (GarbageCollectorType garbageCollectorType : GarbageCollectorType.values()) {
            if (garbageCollectorMap.containsKey(garbageCollectorType.oldGenName())) {
                GarbageCollectorMXBean garbageCollectorMXBean = garbageCollectorMap.get(garbageCollectorType.oldGenName());
                garbageCollectorMetric = new DefaultGarbageCollectorMetric(garbageCollectorType, garbageCollectorMXBean);
                break;
            }
        }
        if (garbageCollectorMetric == null) {
            garbageCollectorMetric = new UnknownGarbageCollectorMetric();
        }
        log.info("loaded : {}", garbageCollectorMetric);
        return garbageCollectorMetric;
    }

    private Map<String, GarbageCollectorMXBean> createGarbageCollectorMap() {
        Map<String, GarbageCollectorMXBean> garbageCollectorMap = new HashMap<String, GarbageCollectorMXBean>();
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            garbageCollectorMap.put(garbageCollectorMXBean.getName(), garbageCollectorMXBean);
        }
        return garbageCollectorMap;
    }

}
