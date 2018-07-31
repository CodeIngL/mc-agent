package cn.com.servyou.yypt.opmc.agent.statistic.base;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
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
}
