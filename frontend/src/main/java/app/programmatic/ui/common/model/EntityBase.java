package app.programmatic.ui.common.model;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class EntityBase<T> implements Identifiable<T> {
    private static final ConcurrentMap<Class<? extends EntityBase<?>>, ? super EntityBase<?>> EMPTY_ENTITIES =
            new ConcurrentHashMap<>();

    public static <E extends EntityBase<?>> E getEmpty(Class<E> clazz) {
        return (E)EMPTY_ENTITIES.computeIfAbsent(clazz, EntityBase::createNewEmpty);
    }

    private static <E extends EntityBase<?>> E createNewEmpty(Class<E> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);

        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                return null;
            }
        });

        return (E)enhancer.create();
    }
}
