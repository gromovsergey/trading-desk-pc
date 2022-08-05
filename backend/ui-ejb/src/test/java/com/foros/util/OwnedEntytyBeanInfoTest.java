package com.foros.util;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.OwnedEntity;
import com.foros.util.clazz.ClassFilter;
import com.foros.util.clazz.ClassSearcher;

import group.Unit;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class OwnedEntytyBeanInfoTest extends Assert {

    @Test
    public void testBeanInfoExists() throws Exception {
        Set<Class> classes = getOwnedEntities();
        List<Class> missing = new ArrayList<Class>();
        for (Class clazz : classes) {
            try {
                Class<?> biClass = clazz.getClassLoader().loadClass(clazz.getName() + "BeanInfo");
                if (!OwnedEntityBeanInfo.class.isAssignableFrom(biClass)) {
                    missing.add(clazz);
                    continue;
                }
            } catch (ClassNotFoundException e) {
                missing.add(clazz);
                continue;
            }

            try {
                Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();
            } catch (Exception e) {
                e.printStackTrace();
                missing.add(clazz);
                continue;
            }

        }

        if (missing.size() > 0) {
            String msg = "Missing or invalid BeanInfo for:";
            for (Class clazz : missing) {
                msg += "\n\t" + clazz.getName();
            }
            fail(msg);
        }
    }

    private boolean isBeanInfoRequired(Class clazz) {
        if (!OwnedEntity.class.isAssignableFrom(clazz)) {
            return false;
        }

        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        Method getAccount;
        try {
            getAccount = clazz.getMethod("getAccount");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            clazz.getMethod("setAccount", getAccount.getReturnType());
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;
    }

    private Set<Class> getOwnedEntities() throws Exception {
        return new ClassSearcher("com.foros.model", true).search(new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                return isBeanInfoRequired(clazz);

            }
        });
    }

    @Test
    public void testGetPropertyDescriptors() throws Exception {
        PropertyDescriptor[] descriptors = OwnedEntityBeanInfo.makeGoodDescriptors(Bar.class);
        assertNotNull(descriptors);
        boolean found = false;
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals("account")) {
                assertNotNull(descriptor.getReadMethod());
                assertEquals(AdvertiserAccount.class, descriptor.getReadMethod().getReturnType());
                assertNotNull(descriptor.getWriteMethod());
                assertEquals(AdvertiserAccount.class, descriptor.getWriteMethod().getParameterTypes()[0]);
                found = true;
            }
        }
        assertTrue(found);
    }

    public static class Bar implements OwnedEntity<AdvertiserAccount> {
        private AdvertiserAccount account;

        public AdvertiserAccount getAccount() {
            return account;
        }

        public void setAccount(AdvertiserAccount account) {
            this.account = account;
        }
    }
}
