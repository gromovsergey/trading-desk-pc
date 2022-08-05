package com.foros.session.cache;

import com.foros.util.ExceptionUtil;
import com.foros.util.ThreadUtil;
import com.foros.util.command.AbstractNothingReturnHibernateWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;

import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.jboss.cache.lock.TimeoutException;

@Stateless(name = "CacheService")
public class CacheServiceBean implements CacheService {
    private static final Logger logger = Logger.getLogger(CacheServiceBean.class.getName());

    @EJB
    private HibernateWorkExecutorService executorService;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void evictNonTransactional(final Object entity) {
        executorService.execute(new AbstractNothingReturnHibernateWork() {
            public void executeIt(Session session) {
                // cascade evict for session factory
                cascadeEvict(session, entity);
            }

            private void cascadeEvict(Session session, Object hibernateEntity) {
                SessionImplementor implementor = (SessionImplementor) session;
                SessionFactory sessionFactory = session.getSessionFactory();
                Object reassociatedEntry = implementor.getPersistenceContext().unproxyAndReassociate(hibernateEntity);
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("evict " + reassociatedEntry);
                }
                doEvict(sessionFactory, reassociatedEntry);
                EntityEntry entry = implementor.getPersistenceContext().getEntry(reassociatedEntry);
                if (entry == null) return;
                EntityPersister persister = entry.getPersister();

                if (persister.hasCascades()) {
                    Type[] types = persister.getPropertyTypes();
                    CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

                    EntityMode entityMode = session.getEntityMode();
                    boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties(
                            reassociatedEntry, entityMode);
                    for (int i = 0; i < types.length; i++) {
                        CascadeStyle style = cascadeStyles[i];
                        if (hasUninitializedLazyProperties && persister.getPropertyLaziness()[i]) {
                            // do nothing to avoid a lazy property
                            // initialization
                            continue;
                        }
                        Object propertyValue = persister.getPropertyValue(reassociatedEntry, i, entityMode);
                        if (propertyValue != null) {
                            if (style.doCascade(CascadingAction.EVICT)) {
                                if (types[i].isEntityType()) {
                                    cascadeEvict(session, propertyValue);
                                }
                                if (types[i].isCollectionType()) {
                                    CollectionType type = (CollectionType) types[i];
                                    String idName = persister.getIdentifierPropertyName();
                                    Serializable id;
                                    try {
                                        id = (Serializable) PropertyUtils.getProperty(hibernateEntity, idName);
                                    } catch (Exception e) {
                                        throw new RuntimeException("Can't get entity id: " + hibernateEntity);
                                    }
                                    sessionFactory.evictCollection(type.getRole(), id);
                                    for (Object evictionObject:  ((Collection<Object>) propertyValue)) {
                                        cascadeEvict(session, evictionObject);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void evictNonTransactional(final Class persistentClass, final Serializable id) {
       executorService.execute(new AbstractNothingReturnHibernateWork() {
            public void executeIt(Session session) {
                doEvict(session.getSessionFactory(), persistentClass, id);
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void evictNonTransactional(final String persistentClass, final Serializable id) {
        executorService.execute(new AbstractNothingReturnHibernateWork() {
            public void executeIt(Session session) {
                doEvict(session.getSessionFactory(), persistentClass, id);
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void evictRegionNonTransactional(final Class... persistentClasses) {
        executorService.execute(new AbstractNothingReturnHibernateWork() {
            public void executeIt(Session session) {
                SessionFactory factory = session.getSessionFactory();
                for (Class persistentClass : persistentClasses) {
                    factory.evict(persistentClass);
                }
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void evictCollectionNonTransactional(final String collection, final Serializable id) {
        executorService.execute(new AbstractNothingReturnHibernateWork() {
            public void executeIt(Session session) {
                session.getSessionFactory().evictCollection(collection, id);
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void evictCollectionNonTransactional(final String collection) {
        executorService.execute(new AbstractNothingReturnHibernateWork() {
            public void executeIt(Session session) {
                session.getSessionFactory().evictCollection(collection);
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void evict(Object entity) {
        evictNonTransactional(entity);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void evict(Class persistentClass, Serializable id) {
        evictNonTransactional(persistentClass, id);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void evict(String persistentClass, Serializable id) {
        evictNonTransactional(persistentClass, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void evictCollection(Class className, String collectionField) {
        evictCollectionNonTransactional(className.getName() + "." + collectionField);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void evictCollection(Class className, String collectionField, Serializable id) {
        evictCollectionNonTransactional(className.getName() + "." + collectionField, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void evictRegion(Class className) {
        evictRegionNonTransactional(className);
    }

    private void doEvict(SessionFactory factory, Object entity) {

        Class<?> persistentClass = entity.getClass();
        ClassMetadata metadata = factory.getClassMetadata(persistentClass);

        if (metadata == null) {
            throw new RuntimeException("Can't entity class metadata: " + persistentClass);
        }

        String idName = metadata.getIdentifierPropertyName();

        Serializable id;
        try {
            id = (Serializable) PropertyUtils.getProperty(entity, idName);
        } catch (Exception e) {
            throw new RuntimeException("Can't get entity id: " + entity);
        }
        doEvict(factory, persistentClass, id);
    }

    private void doEvict(SessionFactory factory, String persistentClass, Serializable id) {
        factory.evictEntity(persistentClass, id);
    }

    private void doEvict(SessionFactory factory, Class persistentClass, Serializable id) {
        factory.evict(persistentClass, id);
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (Exception ex) {
            if (ExceptionUtil.getCause(ex, TimeoutException.class) != null) {
                logger.log(Level.SEVERE, "Dumping active threads : {0}", ThreadUtil.dumpThreads());
            }
            throw ex;
        }
    }
}
