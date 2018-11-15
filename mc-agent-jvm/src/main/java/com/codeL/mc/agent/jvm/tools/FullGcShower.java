package com.codeL.mc.agent.jvm.tools;

import lombok.extern.slf4j.Slf4j;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.jstat.*;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/25
 */
@Slf4j
public class FullGcShower {

    private String pid;

    private Arguments arguments;

    private MonitoredVm monitoredVm;

    private OptionFormat userFormat;

    private OutputFormatter formatter;

    @PostConstruct
    public void init() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name == null || "".equals(name)) {
            return;
        }
        pid = name.split("@")[0];
        try {
            Integer.parseInt(pid);
        } catch (Exception e) {
            return;
        }
        String[] args = new String[]{"-gc", pid};
        arguments = new Arguments(args);
        final VmIdentifier vmId = arguments.vmId();
        int interval = arguments.sampleInterval();
        final MonitoredHost monitoredHost;
        try {
            monitoredHost = MonitoredHost.getMonitoredHost(vmId);
            monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
            OptionFormat format = arguments.optionFormat();
            int gcIndex = -1;
            int gcTime = -1;
            int i = 0;
            try {
                while (true) {
                    OptionFormat childFormat = format.getSubFormat(i);
                    if (childFormat instanceof ColumnFormat) {
                        ColumnFormat format1 = (ColumnFormat) childFormat;
                        if (gcIndex == -1 && "^FGC^".equals(format1.getHeader())) {
                            gcIndex = i;
                            continue;
                        }
                        if (gcTime == -1 && "^FGCT^".equals(format1.getHeader())) {
                            gcTime = i;
                            continue;
                        }
                        if (gcIndex != -1 && gcTime != -1) {
                            break;
                        }
                    }
                    i++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("jstat has's nothing in optionFile,so fullgc collect will work fail", e);
                return;
            }
            userFormat = new OptionFormat(format.getName());
            userFormat.addSubFormat(format.getSubFormat(gcIndex));
            userFormat.addSubFormat(format.getSubFormat(gcTime));
            formatter = new OptionOutputFormatter(monitoredVm, userFormat);
        } catch (Exception e) {
            log.error("jstat have a error", e);
        }
    }

    public String fetchFGC() {
        try {
            if (formatter == null) {
                return null;
            }
            return formatter.getRow();
        } catch (Exception e) {
            log.error("can't get FGC info", e);
        }
        return null;
    }


}
