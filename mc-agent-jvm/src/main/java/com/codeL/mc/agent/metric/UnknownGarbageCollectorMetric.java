package com.codeL.mc.agent.metric;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/24
 */
public class UnknownGarbageCollectorMetric implements GarbageCollectorMetric {

    private static final GarbageCollectorMetricSnapshot UNSUPPORTED_SNAPSHOT = new GarbageCollectorMetricSnapshot(UNCOLLECTED_VALUE, UNCOLLECTED_VALUE);

    @Override
    public String toString() {
        return "Unknown garbage collector metric";
    }

    @Override
    public GarbageCollectorMetricSnapshot getSnapshot() {
        return UNSUPPORTED_SNAPSHOT;
    }

    @Override
    public GarbageCollectorType getType() {
        return null;
    }
}
