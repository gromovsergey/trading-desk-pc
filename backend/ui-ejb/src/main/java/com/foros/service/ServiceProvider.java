package com.foros.service;

import com.foros.util.PropertyHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides business logic with ORB client stub or mock EJB
 *
 * @author oleg_roshka
 */
public final class ServiceProvider {
    private static Logger logger = Logger.getLogger(ServiceProvider.class.getName());

    //default servant refs, also can be defined as system properties (..in current implementation)
    private static final String SERVICE_PROPERTIES = "com/foros/service/adserver.orb.properties";

    private static ServiceProvider instance;
    private final Map<Class, Object> serviceMap;
    private final Properties props;
    private final List<ServiceFactory> factories;

    private ServiceProvider() throws RemoteServiceRegistrationException {
        logger.log(Level.FINE, "Start service initializing: " + ServiceProvider.class.getSimpleName());

        serviceMap = new HashMap<Class, Object>();
        props = PropertyHelper.readProperties(SERVICE_PROPERTIES);

        logger.log(Level.FINE, "Stop service initializing: " + ServiceProvider.class.getSimpleName());

        // Initializing factories
        factories = new ArrayList<ServiceFactory>();
        factories.add(new CorbaServiceFactory(props));
        factories.add(new JndiLookupServiceFactory());
    }

    public static synchronized ServiceProvider getInstance() throws RemoteServiceRegistrationException {
        if (instance == null) {
            instance = new ServiceProvider();
        }

        return instance;
    }

    private String readObjRefStringFromConfig(String servantName) {
        String objRefString = (String)System.getProperties().get(servantName);

        if (objRefString == null || objRefString.length() == 0) {
            objRefString = (String)props.get(servantName);
        }

        return objRefString;
    }

    private void registerService(Class<?> servantClass) throws RemoteServiceRegistrationException {
        String servantName = servantClass.getSimpleName();
        String objRefString = readObjRefStringFromConfig(servantName);

        if (objRefString != null) {
            for (ServiceFactory factory : factories) {
                if (factory.supports(objRefString)) {
                    Object service = factory.create(servantClass, objRefString);
                    serviceMap.put(servantClass, service);
                    break;
                }
            }
        } else {
            throw new RemoteServiceRegistrationException("Configuration error, " +
                    "cannot find object reference string for " + servantName);
        }

        logger.log(Level.INFO, "Service: " + servantName + ", ref: " + objRefString +  " registered");
    }

    @SuppressWarnings("unchecked")
    public synchronized <S> S getService(Class clazz) throws ServiceNotFoundException, RemoteServiceRegistrationException {
        S service = (S)serviceMap.get(clazz);

        if (service == null) {
            registerService(clazz);

            if ((service = (S)serviceMap.get(clazz)) == null) {
                String msg = "Cannot find registered instance of " + clazz.getSimpleName();
                logger.log(Level.SEVERE, msg);
                throw new ServiceNotFoundException(msg);
            }
        }
        return service;
    }
}