package com.foros.service.mock;

import com.phorm.oix.service.mock.AdServer.ChannelSearchSvcs.ChannelSearchHelper;
import com.foros.util.PropertyHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Only for AdServer integration tests
 * <p/>
 * restrictions: jdk >= 1.4
 *
 * @author oleg_roshka
 */
public class AdServerMock {
    interface ServantHelper {
        org.omg.CORBA.Object narrow(org.omg.CORBA.Object ref);
    }

    class MockServerThread extends Thread {
        public MockServerThread(Runnable runnable) {
            super(runnable);
        }

        protected void finalize() throws Throwable {
            super.finalize();
            shutdown();
        }
    }
    public static final String ORB_PROPERTIES = "com/foros/service/mock/orb.properties";
    
    private static Logger logger = Logger.getLogger(AdServerMock.class.getName());
    
    private static AdServerMock instance;
    private Properties properties = null;
    private Process daemonProcess = null;
    private Thread daemonThread = null;
    private Thread serverThread = null;
    private boolean isStarted = false;
    private ORB orb;

    private AdServerMock() {
        readConfiguration();
        startORBDaemon();
        init();
    }

    public static synchronized AdServerMock getInstance() {
        if (instance == null) {
            instance = new AdServerMock();
        }
        return instance;
    }

    public void run() {
        synchronized (AdServerMock.class) {
            if (!isStarted) {
                startServer();
                isStarted = true;
            }
        }
    }

    private void startServer() {
        serverThread = new MockServerThread(new Runnable() {
            public void run() {
                try {
                    daemonThread.join();
                    runORB();
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "AdServer mock error: " + e.toString());
                    e.printStackTrace();
                    throw new RuntimeException("AdServer mock exception ", e);
                }
            }
        });
        
        serverThread.start();
    }

    private void startORBDaemon() {
        daemonThread = new Thread(new Runnable() {
            public void run() {
                try {
                    logger.log(Level.INFO, "ORB Daemon starting...");
                    String javaHome = System.getProperty("java.home");
                    String fileName = javaHome + File.separator + "bin" + File.separator +
                            "orbd -ORBInitialPort " + properties.get("org.omg.CORBA.ORBInitialPort") +
                            " -ORBInitialHost " + properties.get("org.omg.CORBA.ORBInitialHost");
                    daemonProcess = Runtime.getRuntime().exec(fileName);
                    logger.log(Level.INFO, "ORB Daemon started...");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "AdServer mock error, can't start orbd: " + e.toString());
                    e.printStackTrace();
                    throw new RuntimeException("AdServer mock exception, can't start orbd ", e);
                }
            }
        });
        
        daemonThread.start();
    }

    public void shutdown() {
        if (isStarted) {
            synchronized (AdServerMock.class) {
                if (isStarted) {
                    shutdownORB();
                    serverThread = null;
                    stopDaemon();

                    isStarted = false;
                }
            }
        }
    }

    private void runORB() {
        try {
            logger.log(Level.INFO, "AdServer mock ready and waiting... Thread " +
                    Thread.currentThread().getName() + " " + Thread.currentThread().getId());
            orb.run();
        } catch (Exception e) {
            logger.log(Level.WARNING, "AdServer mock error: " + e.toString());
            throw new RuntimeException("AdServer mock exception ", e);
        }
        
        logger.log(Level.INFO, "AdServer mock exiting...");
    }

    private void stopDaemon() {
        daemonProcess.destroy();
        daemonThread = null;
        
        logger.log(Level.INFO, "ORB daemon stoped...");
    }

    private void shutdownORB() {
        try {
            orb.shutdown(false);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AdServer mock error: " + e.toString());
            throw new RuntimeException("AdServer mock exception ", e);
        }
        
        logger.log(Level.INFO, "AdServer mock stoped...");
    }

    private void init() {
        String[] args = null;
        
        try {
            //waiting demon
            Thread.sleep(2000);

            logger.log(Level.INFO, "AdServer mock initializing...");

            // create and initialize the ORB
            orb = ORB.init(args, properties);

            // get reference to rootpoa & activate the POAManager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // create servant and register it with the ORB
            registerService("ChannelSearch", new ChannelSearchServiceMock(), new ServantHelper() {
                public org.omg.CORBA.Object narrow(org.omg.CORBA.Object ref) {
                    return ChannelSearchHelper.narrow(ref);
                }
            });

            logger.log(Level.INFO, "AdServer mock initialized...");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AdServer mock error: " + e.toString());
            throw new RuntimeException("AdServer mock exception  ", e);
        }
    }

    private void readConfiguration() {
        logger.log(Level.INFO, "AdServer mock reading configuration...");
        properties = PropertyHelper.readProperties(ORB_PROPERTIES);
        logProperties(properties);
    }

    private void logProperties(Properties properties) {
        for (Enumeration<?> enumeration = properties.propertyNames(); enumeration.hasMoreElements();) {
            String key = (String)enumeration.nextElement();
            String value = properties.getProperty(key);

            logger.log(Level.INFO, "orb property: " + key + " = " + value);
        }
    }

    private <S extends Servant> void registerService(String name, S servant, ServantHelper helper) {
        POA rootPOA;
        try {
            rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(servant);
            //org.omg.CORBA.Object serviceRef = ChannelSearchServiceHelper.narrow(ref);
            org.omg.CORBA.Object serviceRef = helper.narrow(ref);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, serviceRef);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AdServer mock error: " + e.toString());
            throw new RuntimeException("AdServer mock exception, failed to register service " + name + " ", e);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        shutdown();
    }
}
