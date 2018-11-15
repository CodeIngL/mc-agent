package com.codeL.mc.agent.metric;

/**
 *
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 * 2018/7/24
 */
public interface GarbageCollectorMetric {

    long UNCOLLECTED_VALUE = -1L;

    GarbageCollectorMetricSnapshot getSnapshot();

    GarbageCollectorType getType();
}
