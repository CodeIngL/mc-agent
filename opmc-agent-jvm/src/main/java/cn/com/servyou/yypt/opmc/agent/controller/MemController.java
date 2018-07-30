package cn.com.servyou.yypt.opmc.agent.controller;

import cn.com.servyou.yypt.opmc.agent.Controller;
import cn.com.servyou.yypt.opmc.agent.common.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/30
 */
public class MemController implements Controller {

    private final static MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

    private static double threshold = 0.8;

    static {
        String userThreshold = System.getProperty("opmc.mem.threshold");
        if (StringUtils.isNotEmpty(userThreshold)) {
            threshold = Double.valueOf(userThreshold);
        }
    }

    @Override
    public boolean canDo() {
        MemoryUsage usage = memBean.getHeapMemoryUsage();
        return usage.getMax() * threshold > usage.getUsed();
    }

}
