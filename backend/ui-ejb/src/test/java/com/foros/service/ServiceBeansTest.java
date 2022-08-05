package com.foros.service;

import com.foros.util.clazz.ClassFilter;
import com.foros.util.clazz.ClassNameFilter;
import com.foros.util.clazz.ClassSearcher;

import group.Unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class ServiceBeansTest extends Assert {
    private String[] ignoredInterfaces = new String[] {
            "net.sourceforge.cobertura.coveragedata.HasBeenInstrumented"
    };

    private String[] excludes = new String[] {
            "com.foros.session.MailServiceBean",
            "com.foros.util.unixcommons.UnixCommonsTools",
            "com.foros.service.mock.ChannelSearchBean",
            "com.foros.monitoring.TimedManagerServiceMBean",
            "com.foros.service.timed.TimedManagerServiceBean"
    };

    private Set<Class> serviceBeans = new HashSet<Class>();
    private int failed;

    @org.junit.Before public void setUp() throws Exception {
        failed = 0;

        ClassSearcher classSearcher = new ClassSearcher("com.foros", true);

        serviceBeans = classSearcher.search(
            new ClassNameFilter() {
                @Override
                public boolean accept(String className) {
                    return !ArrayUtils.contains(excludes, className);
                }
            },

            new ClassFilter() {
                @Override
                public boolean accept(Class<?> clazz) {
                    if (clazz.getAnnotation(Stateless.class) != null) {
                        return true;
                    }

                    if (clazz.getAnnotation(Singleton.class) != null) {
                        return true;
                    }

                    return false;
                }
        });
    }

    @Test
    public void testAnnotations() {

        for (Class<?> serviceBean : serviceBeans) {
            Class<?> lookupClass = findLookupClass(serviceBean);
            if (lookupClass == null) {
                failLater(serviceBean + ": failed to locate lookup class");
                continue;
            }

            if (lookupClass == serviceBean) {
                if (serviceBean.getAnnotation(LocalBean.class) == null) {
                    failLater(serviceBean + " doesn't have LocalBean annotation");
                }
            }

            String ejbName = getEjbName(serviceBean);
            if (!ejbName.equals(lookupClass.getSimpleName())) {
                failLater(serviceBean + " ejb name doesn't match simple name of lookup class");
            }
        }
        assertEquals("Some expectations was failed, see test output", 0, failed);
    }

    private void failLater(String s) {
        System.out.println(s);
        failed++;
    }

    private String getEjbName(Class<?> serviceBean) {
        String ejbName = "";
        Stateless stateless = serviceBean.getAnnotation(Stateless.class);
        if (stateless != null) {
            ejbName = stateless.name();
        }

        if ("".equals(ejbName)) {
            Singleton singleton = serviceBean.getAnnotation(Singleton.class);
            if (singleton != null) {
                ejbName = singleton.name();
            }
        }

        if ("".equals(ejbName)) {
            ejbName = serviceBean.getSimpleName();
        }
        return ejbName;
    }

    private Class<?> findLookupClass(Class<?> serviceBean) {
        boolean isLocalBean = serviceBean.getAnnotation(LocalBean.class) != null;
        List<Class<?>> interfaces = getInterfaces(serviceBean);
        if (isLocalBean || interfaces.size() == 0) {
            return serviceBean;
        }

        Class<?> local = null;
        for (Class<?> anInterface : interfaces) {
            if (anInterface.getAnnotation(Local.class) != null) {
                if (local == null) {
                    local = anInterface;
                } else {
                    failLater("Multiply local interfaces for " + serviceBean);
                }
            }
        }
        if (local == null) {
            failLater("No local interface for " + serviceBean);
        }

        return local;
    }

    private List<Class<?>> getInterfaces(Class<?> serviceBean) {
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        for (Class<?> anInterface : serviceBean.getInterfaces()) {
            if (ArrayUtils.contains(ignoredInterfaces, anInterface.getName())) {
                continue;
            }

            interfaces.add(anInterface);
        }
        return interfaces;
    }
}
