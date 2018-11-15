package com.codeL.mc.agent.metric;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/30
 */
public class GarbageCollectorMetricTimerSnapshot {

    private GarbageCollectorMetricSnapshot snapshot;

    private Long timestamp = System.currentTimeMillis();

    public GarbageCollectorMetricTimerSnapshot(GarbageCollectorMetricSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public long getGcOldCount() {
        return snapshot.getGcOldCount();
    }

    public long getGcOldTime() {
        return snapshot.getGcOldTime();
    }
}
