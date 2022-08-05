package com.foros.service;

import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearch;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearchHelper;
import com.foros.util.PropertyHelper;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CorbaServiceFactory implements ServiceFactory {
    private static Logger logger = Logger.getLogger(CorbaServiceFactory.class.getName());

    private static final String CORBANAME_PREFIX = "corbaname:";
    private static final String CORBALOC_PREFIX = "corbaloc:";

    private static org.omg.CORBA_2_3.ORB orb;


    public CorbaServiceFactory(Properties props) {
        String[] args = null;
        Properties orbProps = PropertyHelper.search("com.sun.CORBA.*", props);

        logger.log(Level.FINE, "current encoding : " + Charset.defaultCharset());
        logger.log(Level.FINE, "com.sun.CORBA.codeset.wcharsets: " + orbProps.get("com.sun.CORBA.codeset.wcharsets"));
        logger.log(Level.FINE, "com.sun.CORBA.codeset.charsets: " + orbProps.get("com.sun.CORBA.codeset.charsets"));

        // create and initialize the ORB
        orb = (org.omg.CORBA_2_3.ORB) org.omg.CORBA_2_3.ORB.init(args, orbProps);

    }

    /**
     * Is it corba URL?
     */
    public boolean supports(String url) {
        return url != null && (url.startsWith(CORBALOC_PREFIX) || url.startsWith(CORBANAME_PREFIX));
    }

    public <T> T create(Class<T> servantClass, String objRefString) throws RemoteServiceRegistrationException {
        try {
            ServantHelper helper = getServantHelper(servantClass);

            if (helper == null) {
                throw new RemoteServiceRegistrationException("Configuration error, " +
                    "cannot resolve servant helper for " + servantClass.getSimpleName() + ", " + objRefString);
            }

            org.omg.CORBA.Object servantRef = orb.string_to_object(objRefString);
            org.omg.CORBA.Object service = helper.narrow(servantRef);
            return servantClass.cast(service);
        } catch (Throwable e) {
            throw new RemoteServiceRegistrationException("Failed to register remote service: " +
                    servantClass.getSimpleName() + ", ref: " + objRefString, e);
        }
    }

    private ServantHelper getServantHelper(Class servantClass) {
        if (servantClass.equals(ChannelSearch.class)) {
            return new ServantHelper() {
                public org.omg.CORBA.Object narrow(org.omg.CORBA.Object ref) {
                    return ChannelSearchHelper.narrow(ref);
                }
            };
        }

        return null;
    }

    private interface ServantHelper {
        org.omg.CORBA.Object narrow(org.omg.CORBA.Object ref);
    }    
}
