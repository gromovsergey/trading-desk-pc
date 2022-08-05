package com.foros.session.cache.changes;

import com.foros.audit.changes.ChangeRecord;
import com.foros.session.cache.CacheService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.cache.lock.TimeoutException;

/**
 * This class actually do not publish any changes, but evicts cached objects from second level cache.
 * Its' a JBossCache responsibility to propagate eviction among cluster instances.
 */
@Stateless(name = "ChangePublisherService")
public class JBossChangePublisherServiceBean implements ChangePublisherService {

    @EJB
    private CacheService cacheService;

    private static final Logger logger = Logger.getLogger(JBossChangePublisherServiceBean.class.getName());

    private final static Set<ChangeRecord> AWAITING_CHANGES = Collections.synchronizedSet(new LinkedHashSet<ChangeRecord>());

    public void publishChanges(Collection<ChangeRecord> changes) {

        Collection<ChangeRecord> allChanges = CollectionUtils.union(AWAITING_CHANGES, changes);

        for (ChangeRecord change : allChanges) {
            String objectInfo = change.getClassName() + "#" + change.getPrimaryKey();

            try {
                logger.log(Level.INFO, "Update object in JBoss Cache: " + objectInfo);

                Class clazz = Class.forName(change.getClassName());

                cacheService.evictNonTransactional(clazz, change.getPrimaryKey()); // Do eviction

                if (AWAITING_CHANGES.remove(change)) { // Removing from a waiting queue if succeed
                    logger.log(Level.INFO, "Awaiting object has been successfully reevicted. Object: " + objectInfo);
                }

            } catch (ClassNotFoundException ex) {
                logger.log(Level.WARNING, "Unknown class to be evicted. Skip it.", ex);
            } catch (Exception ex) {
                Throwable rootCause = ExceptionUtils.getRootCause(ex); // Need to unwrap because EJBException may be thrown by CacheService.
                if (rootCause == null) {
                    rootCause = ex;
                }
                if (rootCause instanceof TimeoutException) {
                    AWAITING_CHANGES.add(change); // Added to be reevicted next time.
                    logger.log(Level.INFO, "Object " + objectInfo + " wasn't evicted because of TimeoutException. Added into the awaiting queue. Queue size became: " + AWAITING_CHANGES.size());
                } else {
                    logger.log(Level.SEVERE, "Unknown exception ", ex);
                }
            }
        }
    }
}
