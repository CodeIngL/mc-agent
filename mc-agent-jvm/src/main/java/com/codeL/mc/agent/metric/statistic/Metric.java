package com.codeL.mc.agent.metric.statistic;


import com.codeL.mc.agent.statistic.base.Window;


/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/31
 */
public interface Metric {

    long count();

    void addCount();

    void addCount(long count);

    Window[] windows();

    long previousWindowCount();
}
