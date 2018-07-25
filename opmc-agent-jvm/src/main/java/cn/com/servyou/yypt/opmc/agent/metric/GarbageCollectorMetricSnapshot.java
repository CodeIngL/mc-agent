package cn.com.servyou.yypt.opmc.agent.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/24
 */
@Getter
@AllArgsConstructor
public class GarbageCollectorMetricSnapshot {
    private final long gcOldCount;
    private final long gcOldTime;
}
