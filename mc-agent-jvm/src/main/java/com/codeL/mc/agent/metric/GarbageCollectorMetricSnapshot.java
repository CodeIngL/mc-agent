package com.codeL.mc.agent.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 * <p></p>
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
