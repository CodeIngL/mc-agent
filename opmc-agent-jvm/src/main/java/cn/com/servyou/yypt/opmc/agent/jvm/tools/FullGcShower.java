package cn.com.servyou.yypt.opmc.agent.jvm.tools;

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
 * <p>税友软件集团有限公司</p>
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
            while (true) {
                try {
                    OptionFormat childFormat = format.getSubFormat(i);
                    if (childFormat instanceof ColumnFormat) {
                        ColumnFormat format1 = (ColumnFormat) childFormat;
                        if (gcIndex == -1 && "^FGC^".equals(format1.getHeader())) {
                            gcIndex = i;
                            i++;
                            continue;
                        }
                        if (gcTime == -1 && "^FGCT^".equals(format1.getHeader())) {
                            gcTime = i;
                            i++;
                            continue;
                        }
                        if (gcIndex != -1 && gcTime != -1) {
                            break;
                        }
                    }
                    i++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    i = -1;
                    break;
                }
            }
            if (i == -1) {
                return;
            }
            userFormat = new OptionFormat(format.getName());
            userFormat.insertSubFormat(0, format.getSubFormat(gcIndex));
            userFormat.insertSubFormat(1, format.getSubFormat(gcTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String fetchFGC() {
        try {
            return formatter.getRow();
        } catch (MonitorException e) {
            log.error("can't get FGC info", e);
        }
        return null;
    }


}
