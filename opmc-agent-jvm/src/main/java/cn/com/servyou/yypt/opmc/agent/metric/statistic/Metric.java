package cn.com.servyou.yypt.opmc.agent.metric.statistic;


import cn.com.servyou.yypt.opmc.agent.statistic.base.Window;


/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/31
 */
public interface Metric {

    long count();

    void addCount();

    Window[] windows();

    long previousWindowCount();
}
