package cn.com.servyou.yypt.opmc.agent.spring;

import cn.com.servyou.yypt.opmc.agent.GcReporter;
import lombok.Setter;

import javax.annotation.PostConstruct;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
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


    @PostConstruct
    public void init() {
        if (!toolPresent){
            return ;
        }
        GcReporter reporter = new GcReporter();
        reporter.setUrl(url);
        reporter.setInitDelayMs(initDelayMs);
        reporter.setPeriodMs(periodMs);
        reporter.init();
    }
}
