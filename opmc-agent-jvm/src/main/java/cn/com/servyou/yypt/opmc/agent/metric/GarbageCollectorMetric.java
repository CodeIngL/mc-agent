package cn.com.servyou.yypt.opmc.agent.metric;

/**
 *
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * 2018/7/24
 */
public interface GarbageCollectorMetric {

    long UNCOLLECTED_VALUE = -1L;

    GarbageCollectorMetricSnapshot getSnapshot();

    GarbageCollectorType getType();
}
