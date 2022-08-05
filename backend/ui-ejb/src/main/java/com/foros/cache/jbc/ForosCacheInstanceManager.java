package com.foros.cache.jbc;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.jbc2.builder.MultiplexingCacheInstanceManager;
import org.hibernate.cfg.Settings;
import org.jboss.cache.Cache;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author alexey_chernenko
 */
public class ForosCacheInstanceManager extends MultiplexingCacheInstanceManager {

    private static Logger logger = Logger.getLogger(ForosCacheInstanceManager.class.getName());

    private Cache forosCache;

    private static final String FOROS_CACHE_CONFIG_NAME = "foros-cache";
    private static final String CACHE_JNDI_NAME = "jbossCache";

    @Override
    public void start(final Settings settings, final Properties properties) throws CacheException {
        super.start(settings, properties);
        try {
            forosCache = getCacheFactory().getCache(FOROS_CACHE_CONFIG_NAME, true);
            forosCache.getConfiguration().setTransactionManagerLookupClass("org.jboss.cache.transaction.GenericTransactionManagerLookup");
            registerCacheInJndi(forosCache, CACHE_JNDI_NAME);
            forosCache.addCacheListener(new ForosCacheListener());
            forosCache.start();
        } catch (Exception ex) {
            throw new CacheException(ex);
        }
    }

    @Override
    public void stop() {
        if (forosCache != null) {
            unregisterCacheFromJndi(CACHE_JNDI_NAME);
            getCacheFactory().releaseCache(FOROS_CACHE_CONFIG_NAME);
            forosCache = null;
        }
        super.stop();
    }

    private void registerCacheInJndi(final Cache forosCache, final String jndiName) {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            ctx.rebind(jndiName, forosCache);
        } catch (NamingException ne) {
            String msg = "Unable to bind Cache into JNDI [" + jndiName + "]";
            logger.log(Level.SEVERE, msg, ne);
            throw new CacheException(msg);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (NamingException ne) {
                    logger.log(Level.SEVERE, "Unable to release initial context", ne);
                }
            }
        }
    }

    private void unregisterCacheFromJndi(final String jndiName) {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            ctx.unbind(jndiName);
        } catch (NamingException ne) {
            String msg = "Unable to unbind Cache from JNDI [" + jndiName + "]";
            logger.log(Level.SEVERE, msg, ne);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (NamingException ne) {
                    logger.log(Level.SEVERE, "Unable to release initial context", ne);
                }
            }
        }
    }
}
