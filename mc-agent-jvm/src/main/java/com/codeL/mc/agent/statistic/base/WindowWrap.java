package com.codeL.mc.agent.statistic.base;

import lombok.AllArgsConstructor;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @param <T>
 * @author laihj
 *         2018/7/31
 */
@AllArgsConstructor
public class WindowWrap<T> {

    /**
     * 窗口的长度
     */
    private final long windowLength;

    /**
     * 窗口的开始时间
     */
    private long windowStart;

    /**
     * 性能值
     */
    private T value;

    public long windowLength() {
        return windowLength;
    }

    public long windowStart() {
        return windowStart;
    }

    public T value() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
