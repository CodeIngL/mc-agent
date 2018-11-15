package com.codeL.mc.agent.statistic.base;


import com.codeL.mc.agent.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @param <T>
 * @author laihj
 *         2018/7/31
 */
public abstract class LeapArray<T> {

    protected int windowLength;
    protected int sampleCount;
    protected int intervalInMs;

    protected AtomicReferenceArray<WindowWrap<T>> array;

    /**
     * 构造函数
     *
     * @param windowLength  窗口长度ms
     * @param intervalInSec 间隔时间
     */
    public LeapArray(int windowLength, int intervalInSec) {
        this.windowLength = windowLength;
        this.sampleCount = intervalInSec * 1000 / windowLength;
        this.intervalInMs = intervalInSec * 1000;

        this.array = new AtomicReferenceArray<WindowWrap<T>>(sampleCount);
    }

    /**
     * 获得当前的时间窗口
     *
     * @return 当前的时间窗口
     * @see #currentWindow(long)
     */
    public WindowWrap<T> currentWindow() {
        return currentWindow(TimeUtil.currentTimeMillis());
    }

    /**
     * 获得当前的时间窗口
     *
     * @param time
     * @return 当前的时间窗口
     */
    abstract public WindowWrap<T> currentWindow(long time);

    public WindowWrap<T> getPreviousWindow(long time) {
        long timeId = (time - windowLength) / windowLength;
        int idx = (int) (timeId % array.length());
        time = time - windowLength;
        WindowWrap<T> wrap = array.get(idx);

        if (wrap == null) {
            return wrap;
        }

        if (wrap.windowStart() + windowLength < (time)) {
            return null;
        }

        return wrap;
    }

    /**
     * 获得上一个时间窗口
     *
     * @return 时间窗口
     * @see WindowWrap
     * @see #getPreviousWindow(long)
     */
    public WindowWrap<T> getPreviousWindow() {
        return getPreviousWindow(System.currentTimeMillis());
    }

    /**
     * 获得时间窗口值
     *
     * @param time
     * @return 时间窗口
     */
    public T getWindowValue(long time) {
        long timeId = time / windowLength;
        int idx = (int) (timeId % array.length());

        WindowWrap<T> old = array.get(idx);
        if (old == null) {
            return null;
        }

        return old.value();
    }

    public AtomicReferenceArray<WindowWrap<T>> array() {
        return array;
    }

    public List<WindowWrap<T>> list() {
        ArrayList<WindowWrap<T>> result = new ArrayList<WindowWrap<T>>();

        for (int i = 0; i < array.length(); i++) {
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null) {
                continue;
            }
            result.add(windowWrap);
        }

        return result;
    }

    public List<T> values() {
        ArrayList<T> result = new ArrayList<T>();

        for (int i = 0; i < array.length(); i++) {
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null) {
                continue;
            }
            result.add(windowWrap.value());
        }
        return result;
    }
}
