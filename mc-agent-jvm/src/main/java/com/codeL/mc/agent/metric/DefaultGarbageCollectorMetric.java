package com.codeL.mc.agent.metric;

import java.lang.management.GarbageCollectorMXBean;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/24
 */
public class DefaultGarbageCollectorMetric implements GarbageCollectorMetric{

    private final GarbageCollectorType garbageCollectorType;
    private final GarbageCollectorMXBean garbageCollectorMXBean;

    public DefaultGarbageCollectorMetric(GarbageCollectorType garbageCollectorType, GarbageCollectorMXBean garbageCollectorMXBean) {
        if (garbageCollectorType == null) {
            throw new NullPointerException("garbageCollectorType must not be null");
        }
        if (garbageCollectorMXBean == null) {
            throw new NullPointerException("garbageCollectorMXBean must not be null");
        }
        this.garbageCollectorType = garbageCollectorType;
        this.garbageCollectorMXBean = garbageCollectorMXBean;
    }

    @Override
    public GarbageCollectorMetricSnapshot getSnapshot() {
        long gcCount = garbageCollectorMXBean.getCollectionCount();
        long gcTime = garbageCollectorMXBean.getCollectionTime();
        return new GarbageCollectorMetricSnapshot(gcCount, gcTime);
    }

    @Override
    public GarbageCollectorType getType() {
        return garbageCollectorType;
    }

    @Override
    public String toString() {
        return garbageCollectorType + " garbage collector metric";
    }
}
