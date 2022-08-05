package com.foros.audit.changes;

import com.foros.session.cache.changes.ChangePublisherService;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Implements DB<->FOROS UI Caches synchronization.
 */
@Singleton(name = "DatabaseChangeAdapter")
@Startup
public class DatabaseChangeAdapterBean implements DatabaseChangeAdapter {
    private static final Logger logger = Logger.getLogger(DatabaseChangeAdapterBean.class.getName());

    @EJB
    private ChangePublisherService publisher;

    @EJB
    private DatabaseChangesService databaseChangesService;

    @Override
    public void proceed() {
        try {
            Collection<ChangeRecord> changes = databaseChangesService.readPersistentChanges();

            publisher.publishChanges(changes);

            logger.log(Level.INFO, "Processed {0} audit records from the changes list", changes.size());
        } catch (Exception e) {
            // any exception here need attention!!!
            logger.log(Level.SEVERE, "An exception in DatabaseChangeAdapterBean occurred.", e);
        }
    }
}
