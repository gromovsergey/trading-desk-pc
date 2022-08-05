package com.foros.monitoring;

import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;
import org.glassfish.gmbal.NameValue;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
@ManagedObject
@AMXMetadata(type = "ThreadStatus")
public class TimedManagerServiceMBean {
    private static final Logger logger = Logger.getLogger(TimedManagerServiceMBean.class.getName());
    private static final String name = "ActiveThreads";

    @EJB
    private MonitoringManager monitoringManager;

    @EJB
    private TimedManagerServiceM timedManagerService;

    @PostConstruct
    public void startMonitoring() {
        try {
            monitoringManager.registerMBean(this);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can''t start monitoring of TimedManagerServices", e);
            return;
        }

        logger.log(Level.INFO, "Monitoring of TimedManagerServices is started.");
    }

    @PreDestroy
    public void stopMonitoring() {
        try {
            monitoringManager.unregisterMBean(this);

            logger.log(Level.INFO, "Monitoring of TimedManagerServices is stopped.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can''t unregister monitoring object TimedManagerServices (solution: glassfish server restart)", e);
        }
    }

    @NameValue
    public String getName() {
        return name;
    }

    @ManagedAttribute
    public String getStatus() {
        return timedManagerService.getStatus();
    }
}
