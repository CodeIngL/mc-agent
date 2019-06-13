package com.codeL.mc.agent.metric.statistic;


import com.codeL.mc.agent.statistic.base.LeapArray;
import com.codeL.mc.agent.statistic.base.Window;
import com.codeL.mc.agent.statistic.base.WindowWrap;

import java.util.concurrent.locks.ReentrantLock;


/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/31
 */
public class WindowLeapArray extends LeapArray<Window> {

    private final int timeLength;

    public WindowLeapArray(int windowLengthInMs, int intervalInSec) {
        super(windowLengthInMs, intervalInSec);
        timeLength = intervalInSec * 1000;
    }

    private ReentrantLock addLock = new ReentrantLock();

    /**
     * 当前的窗口
     *
     * @param time
     * @return 时间窗口
     */
    @Override
    public WindowWrap<Window> currentWindow(long time) {

        long timeId = time / windowLength;
        int idx = (int) (timeId % array.length());

        time = time - time % windowLength;

        while (true) {
            WindowWrap<Window> old = array.get(idx);
            if (old == null) {
                WindowWrap<Window> window = new WindowWrap<Window>(windowLength, time, new Window());
                if (array.compareAndSet(idx, null, window)) {
                    return window;
                } else {
                    Thread.yield();
                }
            } else if (time == old.windowStart()) {
                return old;
            } else if (time > old.windowStart()) {
                if (addLock.tryLock()) {
                    try {
                        WindowWrap<Window> window = new WindowWrap<Window>(windowLength, time, new Window());
                        if (array.compareAndSet(idx, old, window)) {
                            for (int i = 0; i < array.length(); i++) {
                                WindowWrap<Window> tmp = array.get(i);
                                if (tmp == null) {
                                    continue;
                                } else {
                                    if (tmp.windowStart() < time - timeLength) {
                                        array.set(i, null);
                                    }
                                }
                            }
                            return window;
                        }
                    } finally {
                        addLock.unlock();
                    }

                } else {
                    Thread.yield();
                }

            } else if (time < old.windowStart()) {
                return new WindowWrap<Window>(windowLength, time, new Window());
            }
        }
    }
}
