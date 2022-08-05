package app.programmatic.ui.common.aspect.prePersistProcessor.impl;

import static app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType.CREATE;
import static app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType.DELETE;
import static app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType.UPDATE;
import static app.programmatic.ui.common.model.MajorDisplayStatus.DELETED;
import static app.programmatic.ui.common.tool.reflection.ReflectionHelper.findAnnotatedBeans;
import static app.programmatic.ui.common.tool.reflection.ReflectionHelper.findPublicMethod;

import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.SubscribeForEntityChanges;
import app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType;
import app.programmatic.ui.common.aspect.prePersistProcessor.impl.PrePersistProcessorContextInternal.EntityOperation;
import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.model.Statusable;
import app.programmatic.ui.common.tool.javabean.EntityChangesGetter;
import app.programmatic.ui.common.tool.javabean.EntityChangesMap;
import app.programmatic.ui.common.tool.javabean.EntityBeanUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
@Order(1)
@Component
public class PrePersistProcessorAspect {
    private static final Logger logger = Logger.getLogger(PrePersistProcessorAspect.class.getName());
    private static final ThreadLocal<Boolean> isFirstProcessorInChainTL = ThreadLocal.withInitial(() -> Boolean.TRUE);

    @Autowired
    private PrePersistProcessorContextInternal context;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("mainAppWorker")
    private Executor taskExecutor;

    private final ConcurrentHashMap<Class<?>, PrePersistAwareServiceAccessors> interceptedBeanAccessors = new ConcurrentHashMap<>();
    private final HashMap<Class<?>, Method> subscribers = new HashMap<>();


    public PrePersistProcessorAspect() {
        Set<Class<?>> annotatedBeans = findAnnotatedBeans(
                PrePersistAwareService.class,
                SubscribeForEntityChanges.class);

        annotatedBeans.stream()
                .forEach( bean -> {
                    processAnnotation(bean.getAnnotation(PrePersistAwareService.class), bean);
                    processAnnotation(bean.getAnnotation(SubscribeForEntityChanges.class), bean);
                });
    }

    @Around("execution(public * *(..)) && " +
            "@within(app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService) && " +
            "@annotation(app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareMethod) ")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        processArgs(pjp.getArgs(), pjp.getTarget());

        boolean isFirstProcessorInChain = isFirstProcessorInChainTL.get();
        if (isFirstProcessorInChain) {
            isFirstProcessorInChainTL.set(Boolean.FALSE);
        }
        boolean isRollback = false;

        try {
            return pjp.proceed();
        } catch (Throwable t) {
            isRollback = true;
            throw t;
        } finally {
            if (isFirstProcessorInChain) {
                if (!isRollback) {
                    notifySubscribers();
                }
                context.clear();
                isFirstProcessorInChainTL.remove();
            }
        }
    }

    private void processArgs(Object[] args, Object bean) {
        EntityChangesMap entityChangesMap = new EntityChangesMap();

        for (Object obj : args) {

            EntityChangesMap tmpMap = EntityChangesMap.getEmpty();
            if (obj instanceof EntityBase) {
                tmpMap = processEntity((EntityBase<?>) obj, bean);

            } else if (obj instanceof Collection) {
                tmpMap = processCollection((Collection<?>)obj, bean);
            }

            entityChangesMap.merge(tmpMap);
        }
        context.mergeChanges(entityChangesMap);
    }

    private EntityChangesMap processEntity(EntityBase<?> entity, Object bean) {
        EntityBase<?> prevEntity = fetchPrevEntity(entity, bean);
        EntityChangesMap result = EntityBeanUtils.mergeEntityWithPrev(entity, prevEntity, context.getEmptyValuesStrategy());

        ServiceOperationType operationType = determineOperationType(entity);
        context.addEntityOperation(entity, operationType);

        return result;
    }

    private <T> EntityChangesMap processCollection(Collection<T> collection, Object bean) {
        EntityChangesMap result = new EntityChangesMap();

        Iterator<T> iterator = collection.iterator();
        while(iterator.hasNext()) {
            Object element = iterator.next();
            if (!(element instanceof EntityBase)) {
                break;
            }
            EntityChangesMap entityChangesMap = processEntity((EntityBase<?>)element, bean);
            result.merge(entityChangesMap);
        }

        return result;
    }

    private EntityBase<?> fetchPrevEntity(EntityBase<?> entity, Object bean) {
        if (entity.getId() != null) {
            return interceptedBeanAccessors.get(bean.getClass())
                    .getStoredValue(bean, entity.getId());
        }

        return interceptedBeanAccessors.get(bean.getClass())
                .getDefaultValue(bean);
    }

    private ServiceOperationType determineOperationType(EntityBase<?> entity) {
        if (entity.getId() == null) {
            return CREATE;
        }

        if (entity instanceof Statusable && ((Statusable)entity).getInheritedMajorStatus() == DELETED) {
            return DELETE;
        }

        return UPDATE;
    }

    private void processAnnotation(PrePersistAwareService annotation, Class<?> bean) {
        if (annotation == null) {
            return;
        }

        Method storedGetter = findPublicMethod(bean, annotation.storedValueGetter(), EntityBase.class, Object.class);
        Method defaultGetter = annotation.defaultValueGetter().isEmpty() ? null :
                findPublicMethod(bean, annotation.defaultValueGetter(), EntityBase.class, Object.class);
        PrePersistAwareServiceAccessors accessor = new PrePersistAwareServiceAccessors(storedGetter, defaultGetter);
        interceptedBeanAccessors.put(bean, accessor);
    }

    private void processAnnotation(SubscribeForEntityChanges annotation, Class<?> bean) {
        if (annotation == null) {
            return;
        }

        subscribers.put(bean, findPublicMethod(bean,
                                               annotation.processChangesMethod(),
                                               void.class,
                                               EntityBase.class,
                                               ServiceOperationType.class,
                                               EntityChangesGetter.class));
    }

    private void notifySubscribers() {
        if (subscribers.isEmpty() || context.getEntityOperations().isEmpty()) {
            return;
        }

        try {
            Set<EntityOperation> operations = Collections.unmodifiableSet(context.getEntityOperations());
            EntityChangesGetter changes = context.getChanges();
            subscribers.entrySet().stream()
                    .forEach( subscriber -> {
                        Object subscribedBean = applicationContext.getBean(subscriber.getKey());
                        Method method = subscriber.getValue();
                        taskExecutor.execute( () -> notifySubscriber(subscribedBean, method, operations, changes ) );
                    });

        } catch (Throwable t) {
            logErrorSilently("Can't send changes to subscribers", t);
        }
    }

    private static void notifySubscriber(Object subscribedBean,
                                         Method method,
                                         Set<EntityOperation> operations,
                                         EntityChangesGetter changes) {
        operations.stream().forEach(
                op -> {
                    try {
                        method.invoke(subscribedBean,
                                op.getEntity(), op.getOperationType(), changes);
                    } catch (Throwable t) {
                        logErrorSilently("Subscriber " + subscribedBean.getClass() + "." + method.getName() + " failed.", t);
                    }
                }
        );
    }

    private static void logErrorSilently(String msg, Throwable t) {
        try {
            logger.log(Level.SEVERE, msg, t);
        } catch (Throwable again) {
        }
    }

    private class PrePersistAwareServiceAccessors {
        private final Method storedAccessor;
        private final Method defaultAccessor;

        public PrePersistAwareServiceAccessors(Method storedAccessor, Method defaultAccessor) {
            this.storedAccessor = storedAccessor;
            this.defaultAccessor = defaultAccessor;
        }

        public EntityBase getStoredValue(Object target, Object id) {
            try {
                return (EntityBase)storedAccessor.invoke(target, id);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public EntityBase getDefaultValue(Object target) {
            try {
                return defaultAccessor == null ? null : (EntityBase)defaultAccessor.invoke(target);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
