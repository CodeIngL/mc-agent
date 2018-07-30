package cn.com.servyou.yypt.opmc.agent.metric;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
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
