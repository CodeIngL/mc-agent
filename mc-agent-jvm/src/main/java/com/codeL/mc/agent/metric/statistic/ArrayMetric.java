package com.codeL.mc.agent.metric.statistic;


import com.codeL.mc.agent.statistic.base.Window;
import com.codeL.mc.agent.statistic.base.WindowWrap;

import java.util.List;

/**
 * <p>Description: </p>
 * <p></p>
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
