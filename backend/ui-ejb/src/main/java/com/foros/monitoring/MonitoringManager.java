package com.foros.monitoring;

import javax.ejb.Local;

/**
 * Helper class for monitoring. It facilitates registration of MBeans. And also it is a root object of foros monitoring,
 * it is visible for external clients through JMX/AMX. It must be activated before using and it MUST be
 * deactivated when application is shutting down.
 */
@Local
public interface MonitoringManager {
    public String getMonitorName();

    public String getMonitorVersion();

    public String getMonitorDomain();

    /**
    * @param mbeanObject gmbal spec compliant instance of a class with @NameValue annotation
    * @throws MonitorActivationException if ForosMonitor is not activated
    */
    public void registerMBean(Object mbeanObject) throws MonitorActivationException;

    /**
    * @param mbeanObject gmbal spec compliant instance of a class
    * @param objectName name visible for external clients through JMX/AMX
    * @throws MonitorActivationException if ForosMonitor is not activated
    */
    public void registerMBean(Object mbeanObject, String objectName) throws MonitorActivationException;

    /**
    * @param mbeanObject previously registered instance of a class
    * @throws MonitorActivationException if ForosMonitor is not activated
    */
    public void unregisterMBean(Object mbeanObject) throws MonitorActivationException;
}
