package com.codeL.mc.agent.spring;

import com.codeL.mc.agent.GcReporter;
import lombok.Setter;

import javax.annotation.PostConstruct;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/8/22
 */
public class GcRepoterFactoryBean {

    static final Boolean toolPresent;

    static {
        Exception ee = null;
        try {
            Class.forName("sun.tools.jstat.Arguments");
        } catch (ClassNotFoundException e) {
            ee = e;
        }
        if (ee == null) {
            toolPresent = true;
        } else {
            toolPresent = false;
        }
    }

    @Setter
    private String url;

    @Setter
    private Long initDelayMs = -1L;

    @Setter
    private Long periodMs = -1L;

    @Setter
    private Boolean enable = true;


    @PostConstruct
    public void init() {
        if (!toolPresent) {
            return;
        }
        if (!enable) {
            return;
        }
        GcReporter reporter = new GcReporter();
        reporter.setUrl(url);
        reporter.setInitDelayMs(initDelayMs);
        reporter.setPeriodMs(periodMs);
        reporter.init();
    }
}
