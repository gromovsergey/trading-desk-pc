package com.foros.changes;

import com.foros.audit.serialize.AuditChange;
import com.foros.cache.generic.CacheProviderService;
import com.foros.changes.inspection.ChangeDescriptorRegistry;
import com.foros.changes.inspection.ChangeDescriptorRegistryImpl;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.util.command.HibernateWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.hibernate.Session;

/**
 * Service bean provide changing inspection functionality
 */
@Singleton(name = "ChangesService")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ChangesServiceBean implements ChangesService {

    private ChangeDescriptorRegistry registry;

    @EJB
    private HibernateWorkExecutorService executorService;

    @EJB
    private CacheProviderService cacheProviderService;

    @PostConstruct
    public void init() {
        executorService.execute(new HibernateWork<Object>() {
            @Override
            public Object execute(Session session) {
                registry = new ChangeDescriptorRegistryImpl(session.getSessionFactory());
                return null;
            }
        });

    }

    public static abstract class AbstractChangesInterceptorWork extends HibernateInterceptor.AbstractHibernateInterceptorWork {
        @Override
        protected final void process(Session session, HibernateInterceptor interceptor) {
            processChanges(session, interceptor.getChangesHibernateInterceptor());
        }

        public abstract void processChanges(Session session, ChangesInterceptorHandler changesInterceptor);
    }

    @Override
    public void initialize() {
        executorService.execute(new AbstractChangesInterceptorWork() {
            @Override
            public void processChanges(Session session, ChangesInterceptorHandler changesInterceptor) {
                if (!changesInterceptor.isInitialized()) {
                    changesInterceptor.initialize(session, registry);
                }
            }
        });
    }

    @Override
    public void addChange(final AuditChange auditChange) {
        executorService.execute(new AbstractChangesInterceptorWork() {
            @Override
            public void processChanges(Session session, ChangesInterceptorHandler changesInterceptor) {
                if (!changesInterceptor.isInitialized()) {
                    throw new CapturingNotInitializedException();
                }
                changesInterceptor.addAuditChange(auditChange);
            }
        });
    }

    @Override
    public void handleChanges() {
        executorService.execute(new AbstractChangesInterceptorWork() {
            @Override
            public void processChanges(Session session, ChangesInterceptorHandler changesInterceptor) {
                changesInterceptor.processChanges();
            }
        });
    }

    public ChangeDescriptorRegistry getRegistry() {
        return registry;
    }
}
