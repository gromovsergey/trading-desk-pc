package com.foros.monitoring;

import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;
import org.glassfish.gmbal.NameValue;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedObject
@AMXMetadata(type = "ThreadStatus")
public abstract class AbstractMonitoringBean {
    private static final Logger logger = Logger.getLogger(AbstractMonitoringBean.class.getName());

    private long intervalDuration;
    
    private String name;

    @EJB
    private MonitoringManager monitoringManager;

    protected abstract long getLastProcessedTime();

    protected abstract boolean isActive();

    public void startMonitoring(String jmxBeanName, long timeoutInterval) {
        intervalDuration = timeoutInterval;
        name = jmxBeanName;

        try {
            monitoringManager.registerMBean(this);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can''t start monitoring of {0}: {1}: {2}",
                new Object[]{getClass().getName(), e.getClass().getName(), e.getMessage()});
            return;
        }

        logger.log(Level.INFO, "Monitoring of {0} is started.", getClass().getName());
    }

    @PreDestroy
    public void stopMonitoring() {
        try {
            monitoringManager.unregisterMBean(this);

            logger.log(Level.INFO, "Monitoring of {0} is stopped.", getClass().getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can''t unregister monitoring object ''{0}'' (solution: glassfish server restart): {1}: {2}",
                new Object[]{getClass().getName(), e.getClass().getName(), e.getMessage()});
        }
    }

    @NameValue
    public String getName() {
        return name;
    }

    @ManagedAttribute
    public int getStatus() {
        if (!isActive()) {
            return ThreadStatuses.NOT_NEEDED.getValue();
        }

        if (StatusHelper.isLastProcessedTimeInThreshold(intervalDuration, getLastProcessedTime())) {
            return ThreadStatuses.ALIVE.getValue();
        }

        return ThreadStatuses.NOT_ALIVE.getValue();
    }
}
