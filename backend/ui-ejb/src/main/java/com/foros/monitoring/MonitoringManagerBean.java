package com.foros.monitoring;

import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;
import org.glassfish.gmbal.ManagedObjectManager;
import org.glassfish.gmbal.ManagedObjectManagerFactory;
import java.util.logging.Logger;

@ManagedObject
@AMXMetadata(isSingleton = true)
@Singleton(name = "MonitoringManager")
@Startup
public class MonitoringManagerBean implements MonitoringManager {
    private static final Logger logger = Logger.getLogger(MonitoringManagerBean.class.getName());

    private static final String NAME = "FOROS UI MONITOR";
    private static final String VERSION = "1.0";
    private static final String DOMAIN_NAME = "com.foros";
    private static final ManagedObjectManager objectManager = ManagedObjectManagerFactory.createStandalone(DOMAIN_NAME);

    @Override
    @ManagedAttribute
    public String getMonitorName() {
        return NAME;
    }

    @Override
    @ManagedAttribute
    public String getMonitorVersion() {
        return VERSION;
    }

    @Override
    @ManagedAttribute
    public String getMonitorDomain() {
        return DOMAIN_NAME;
    }

    @PostConstruct
    public void activate() {
        String errorMessagePrefix = "Can't activate " + MonitoringManager.class.getName() + ". ";

        try {
            checkIsNotActive(errorMessagePrefix);

            objectManager.stripPrefix(DOMAIN_NAME);
            objectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
            objectManager.setRuntimeDebug(false);
            objectManager.setTypelibDebug(0);
            objectManager.createRoot(this);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, errorMessagePrefix, ex);
        }
    }

    @PreDestroy
    public void deactivate() {
        String errorMessagePrefix = "Can't deactivate " + MonitoringManager.class.getName() + ". ";

        try {
            checkIsActive(errorMessagePrefix);
            
            objectManager.unregister(this);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, errorMessagePrefix, ex);
        }
    }

    /**
    * @param mbeanObject gmbal spec compliant instance of a class with @NameValue annotation
    * @throws MonitorActivationException if it is not activated
    */
    @Override
    public void registerMBean(Object mbeanObject) throws MonitorActivationException {
        String errorMessagePrefix = "Can't register " + mbeanObject.getClass().getName() + ". ";
        checkIsActive(errorMessagePrefix);

        try {
            objectManager.registerAtRoot(mbeanObject);
        } catch (Exception e) {
            throw new MonitorActivationException(errorMessagePrefix, e);
        }
    }

    /**
    * @param mbeanObject gmbal spec compliant instance of a class
    * @param objectName name visible for external clients through JMX/AMX
    * @throws MonitorActivationException if it is not activated
    */
    @Override
    public void registerMBean(Object mbeanObject, String objectName) throws MonitorActivationException {
        String errorMessagePrefix = "Can't register " + mbeanObject.getClass().getName() + ". ";
        checkIsActive(errorMessagePrefix);

        try {
            objectManager.registerAtRoot(mbeanObject, objectName);
        } catch (Exception e) {
            throw new MonitorActivationException(errorMessagePrefix, e);
        }
    }

    /**
    * @param mbeanObject previously registered instance of a class
    * @throws MonitorActivationException if it is not activated
    */
    @Override
    public void unregisterMBean(Object mbeanObject) throws MonitorActivationException {
        String errorMessagePrefix = "Can't unregister " + mbeanObject.getClass().getName() + ". ";
        checkIsActive(errorMessagePrefix);

        try {
            objectManager.unregister(mbeanObject);
        } catch (Exception e) {
            throw new MonitorActivationException(errorMessagePrefix, e);
        }
    }

    private static void checkIsActive(String pefixMessage) throws MonitorActivationException {
        if (objectManager.getRoot() == null) {
            throw new MonitorActivationException(pefixMessage + MonitoringManager.class.getName() + " is not active");
        }
    }

    private static void checkIsNotActive(String pefixMessage) throws MonitorActivationException {
        if (objectManager.getRoot() != null) {
            throw new MonitorActivationException(pefixMessage + MonitoringManager.class.getName() + " is active");
        }
    }
}
