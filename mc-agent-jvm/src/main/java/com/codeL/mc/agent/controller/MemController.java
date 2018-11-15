package com.codeL.mc.agent.controller;

import com.codeL.mc.agent.Controller;
import com.codeL.mc.agent.common.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/30
 */
public class MemController implements Controller {

    private final static MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

    private static double threshold = 0.8;

    static {
        String userThreshold = System.getProperty("mc.mem.threshold");
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
