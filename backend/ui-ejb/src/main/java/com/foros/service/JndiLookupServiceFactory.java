package com.foros.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

/**
 * Lookup services in jndi.
 */
public class JndiLookupServiceFactory implements ServiceFactory {
    private static Logger logger = Logger.getLogger(JndiLookupServiceFactory.class.getName());
    private static final String JNDI_PREFIX = "jndi:";

    /**
     * Accept urls in jndi:<<any jndi name>> format
     */
    @Override
    public boolean supports(String url) {
        return url != null && url.startsWith(JNDI_PREFIX);
    }

    @Override
    public <T> T create(Class<T> tinterface, String url) throws RemoteServiceRegistrationException {
        logger.log(Level.FINE, "Creating service " + tinterface.getSimpleName() + " using " + url + " url.");
        
        try {
            // remove prefix
            url = url.substring(JNDI_PREFIX.length());
            Object service = new InitialContext().lookup(url);
            return tinterface.cast(service);
        } catch (Exception e) {
            throw new RemoteServiceRegistrationException(
                "DEBUG MODE Exception! " +
                "If current environment is production environment - check the server configuration." +
                "Can't locate " + tinterface.getSimpleName() + " using " + url + " jndi name",
                e
            );
        }
    }
}
