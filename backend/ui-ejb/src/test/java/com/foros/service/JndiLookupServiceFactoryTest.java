package com.foros.service;

import group.Unit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.naming.Context;
import javax.naming.InitialContext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(Unit.class)
public class JndiLookupServiceFactoryTest {
    @Before
    public void setUp() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.commons.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.commons.naming");
    }

    @After
    public void tearDown() throws Exception {
        System.getProperties().remove(Context.INITIAL_CONTEXT_FACTORY);
        System.getProperties().remove(Context.URL_PKG_PREFIXES);
    }

    @Test
    public void jndi() throws Exception {
        InitialContext ic = new InitialContext();
        ic.bind("FooService", new FooService());
        ServiceFactory factory = new JndiLookupServiceFactory();

        assertTrue(factory.supports("jndi:FooService"));
        assertFalse(factory.supports("dsdsd:FooService"));
        assertFalse(factory.supports("FooService"));
        assertFalse(factory.supports(null));

        FooService service = factory.create(FooService.class, "jndi:FooService");
        assertNotNull(service);

        factory.create(FooService.class, "jndi:FooService");

        try {
            factory.create(FooService2.class, "jndi:FooService");
            fail();
        } catch (Throwable t) {
            assertNotNull(t instanceof RemoteServiceRegistrationException);
        }

        try {
            factory.create(FooService.class, "jndi:WrongFooService");
            fail();
        } catch (Throwable t) {
            assertNotNull(t instanceof RemoteServiceRegistrationException);
        }
    }

    private static class FooService { }
    private static class FooService2 { }
}
