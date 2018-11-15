package com.codeL.mc.agent.metric;

/**
 * @author laihj
 *         2018/7/24
 */
public enum GarbageCollectorType {

    SERIAL("MarkSweepCompact", "Copy"),
    PARALLEL("PS MarkSweep", "PS Scavenge"),
    CMS("ConcurrentMarkSweep", "ParNew"),
    G1("G1 Old Generation", "G1 Young Generation");

    private final String oldGenName;
    private final String newGenName;

    GarbageCollectorType(String oldGenName, String newGenName) {
        this.oldGenName = oldGenName;
        this.newGenName = newGenName;
    }

    public String oldGenName() {
        return oldGenName;
    }

    public String newGenName() {
        return newGenName;
    }
}
