package com.foros.session;

import com.foros.cache.CacheManager;
import com.foros.cache.CacheManagerMock;
import com.foros.cache.local.LocalizedResourcesLocalCache;
import com.foros.cache.local.LocalizedResourcesLocalCacheImpl;
import com.foros.config.ConfigService;
import com.foros.config.MockConfigService;
import com.foros.session.admin.CustomizationResourcesService;
import com.foros.session.admin.CustomizationResourcesServiceMock;
import com.foros.session.admin.DynamicResourcesService;
import com.foros.session.admin.DynamicResourcesServiceMock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * ServiceLocator mock that replaces ServiceLocator in the actions during tests
 * and allows to inject mock services into itself.
 * As a result these mock services will be used in the actions.
 */
public class ServiceLocatorMock implements ServiceLookup, TestRule {
    private static ServiceLocatorMock instance;
    private Map<String, Object> services;
    private ListableBeanFactory beanFactory;

    private ServiceLocatorMock() {
        services = new HashMap<>();
    }

    public ListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Returns mock ServiceLocator and replaces production one with itself.
     *
     * @return mock ServiceLocator
     */
    public static ServiceLocatorMock getInstance() {
        if (instance == null) {
            instance = new ServiceLocatorMock();
            try {
                Field serviceLocatorField = ServiceLocator.class.getDeclaredField("instance");
                serviceLocatorField.setAccessible(true);
                serviceLocatorField.set(null, instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * Restores ServiceLocator to the production one
     */
    public void tearDown() {
        if (instance != null) {
            try {
                Field serviceLocatorField = ServiceLocator.class.getDeclaredField("instance");
                serviceLocatorField.setAccessible(true);
                serviceLocatorField.set(null, null);
                serviceLocatorField.setAccessible(false);
                services = null;
                beanFactory = null;
                instance = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <T> T lookup(Class<T> tclass) {
        String className = tclass.getSimpleName();
        Object service = services.get(className);

        if (service == null) {
            service = findInSpring(tclass);
        }

        if (service == null) {
            throw new RuntimeException("Can't find service for: " + tclass);
        }
        return tclass.cast(service);
    }

    private Object findInSpring(Class<?> tclass) {
        if (beanFactory == null) {
            return null;
        }

        try {
            return BeanFactoryUtils.beanOfType(beanFactory, tclass);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * Injects mock service.
     * @param tclass service class
     * @param service service mock
     */
    public <T> void injectService(Class<T> tclass, T service) {
        services.put(tclass.getSimpleName(), service);
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    injectService(CacheManager.class, new CacheManagerMock());
                    injectService(DynamicResourcesService.class, new DynamicResourcesServiceMock());
                    injectService(CustomizationResourcesService.class, new CustomizationResourcesServiceMock());

                    LocalizedResourcesLocalCacheImpl localizedResourcesLocalCache = new LocalizedResourcesLocalCacheImpl();
                    localizedResourcesLocalCache.init();
                    injectService(LocalizedResourcesLocalCache.class, localizedResourcesLocalCache);
                    injectService(ConfigService.class, new MockConfigService());

                    base.evaluate();
                } finally {
                    ServiceLocatorMock.this.tearDown();
                }
            }
        };
    }
}
