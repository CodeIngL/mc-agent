package cn.com.servyou.yypt.opmc.agent.metric.statistic;


import cn.com.servyou.yypt.opmc.agent.statistic.base.Window;
import cn.com.servyou.yypt.opmc.agent.statistic.base.WindowWrap;

import java.util.List;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/31
 */
public class ArrayMetric implements Metric {

    private final WindowLeapArray data;

    public ArrayMetric(int windowLength, int interval) {
        this.data = new WindowLeapArray(windowLength, interval);
    }


    @Override
    public long count() {
        data.currentWindow();
        long pass = 0;
        List<Window> list = data.values();

        for (Window window : list) {
            pass += window.count();
        }
        return pass;
    }


    @Override
    public Window[] windows() {
        data.currentWindow();
        return data.values().toArray(new Window[data.values().size()]);
    }


    @Override
    public void addCount() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addCount();
    }

    @Override
    public void addCount(long count) {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addCount(count);
    }

    @Override
    public long previousWindowCount() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap = data.getPreviousWindow();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().count();
    }

}
