package com.foros.restriction;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.aspect.AspectException;
import com.foros.restriction.invocation.RestrictionInvocationService;
import com.foros.session.RestrictionTestService;

import group.Db;
import group.Restriction;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * See com.foros.model.TestEntity for permission and restriction definitions.
 * See com.foros.session.RestrictionTestServiceBean for test methods
 */
@Category({ Db.class, Restriction.class })
public class RestrictionServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private RestrictionInvocationService restrictionInvocationService;

    @Autowired
    private RestrictionTestService testService;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        permissionService.removePolicyCache();
    }

    private void grant(String objectType, String action) {
        grant(objectType, action, null);
    }

    private void grant(final String objectType, final String action, final String parameter) {
        EasyMock.reset(permissionService);
        EasyMock.expect(permissionService.isGranted(objectType, action)).andReturn(true);
        EasyMock.expect(permissionService.isGranted(EasyMock.anyString(), EasyMock.anyString())).andReturn(false);
        EasyMock.expect(permissionService.isGranted(objectType, action, parameter)).andReturn(true);
        EasyMock.expect(permissionService.isGranted(EasyMock.anyString(), EasyMock.anyString(), EasyMock.anyString())).andReturn(false);
        EasyMock.replay(permissionService);
    }

    @Test
    public void testPermission() throws NoSuchMethodException {
        grant("test", "testAction");
        invokeMethod("test");
    }

    @Test
    public void testPermissionFailed() throws NoSuchMethodException {
        grant("test", "testParameterizedAction");
        try {
            invokeMethod("test");
            fail();
        } catch (AccessRestrictedException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testParameterizedPermissionFailed() throws NoSuchMethodException {
        grant("test", "testParameterizedAction", "1");
        try {
            invokeMethod("parameterizedTest");
            fail();
        } catch (AccessRestrictedException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testInvalidRestriction() throws NoSuchMethodException {
        try {
            invokeMethod("restrictionNotFound");
            fail();
        } catch (AspectException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParameterizedPermission() throws NoSuchMethodException {
        grant("test", "testParameterizedAction", "0");
        invokeMethod("parameterizedTest");
    }

    @Test
    public void testParameterizedPermission2() throws NoSuchMethodException {
        grant("test", "testParameterizedAction", "0");
        invokeMethod("parameterizedTest");
    }

    private void invokeMethod(String methodName) throws NoSuchMethodException {
        restrictionInvocationService.checkMethodRestrictions(
                testService,
                testService.getClass().getMethod(methodName, Long.class),
                new Object[] { 0L }
        );
    }

}
