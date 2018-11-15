package com.codeL.mc.agent.statistic.base;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/31
 */
public class Window {

    private final LongAdder count = new LongAdder();

    Window resetTo(long startTime) {
        count.reset();
        return this;
    }

    public long count() {
        return count.sum();
    }

    public void addCount() {
        count.add(1L);
    }

    public void addCount(long count) {
        this.count.add(count);
    }
}
