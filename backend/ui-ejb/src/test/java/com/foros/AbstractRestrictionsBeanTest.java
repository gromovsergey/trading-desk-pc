package com.foros;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.security.User;
import com.foros.restriction.permission.PermissionServiceBean;
import com.foros.security.SecurityContextMock;
import com.foros.security.principal.SecurityPrincipal;
import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.UserRoleTestFactory;
import com.foros.validation.ValidationContext;
import com.foros.validation.util.ValidationUtil;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.eq;
import java.util.LinkedHashMap;
import java.util.Map;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRestrictionsBeanTest extends AbstractServiceBeanIntegrationTest{

    @Autowired
    private PermissionServiceBean realPermissionService;

    @Autowired
    protected IspAccountTestFactory ispAccountTF;

    @Autowired
    protected CmpAccountTestFactory cmpAccountTF;

    @Autowired
    protected AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    protected PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    protected InternalAccountTestFactory internalAccountTF;

    @Autowired
    protected AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    protected UserRoleTestFactory userRoleTF;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    protected AdvertiserAccountDefinition agencyAdvertiser1;

    protected UserDefinition internalAllAccess;
    protected UserDefinition internalUserAccountNoAccess;
    protected UserDefinition internalMultipleAccountsAccess;
    protected UserDefinition internalMultipleAccountsNoAccess;
    protected UserDefinition internalNoAccess;

    protected UserDefinition ispManagerAllAccess1;
    protected UserDefinition ispManagerAllAccess2;
    protected UserDefinition ispManagerNoAccess;

    protected UserDefinition cmpManagerAllAccess1;
    protected UserDefinition cmpManagerAllAccess2;
    protected UserDefinition cmpManagerNoAccess;

    protected UserDefinition advertiserManagerAllAccess1;
    protected UserDefinition advertiserManagerAllAccess2;
    protected UserDefinition advertiserManagerNoAccess;

    protected UserDefinition publisherManagerAllAccess1;
    protected UserDefinition publisherManagerAllAccess2;
    protected UserDefinition publisherManagerNoAccess;

    protected UserDefinition ispAllAccess1;
    protected UserDefinition ispAllAccess2;
    protected UserDefinition ispNoAccess;

    protected UserDefinition publisherAllAccess1;
    protected UserDefinition publisherAllAccess2;
    protected UserDefinition publisherNoAccess;

    protected UserDefinition advertiserAllAccess1;
    protected UserDefinition advertiserAllAccess2;
    protected UserDefinition advertiserNoAccess;

    protected UserDefinition cmpAllAccess1;
    protected UserDefinition cmpAllAccess2;
    protected UserDefinition cmpNoAccess;

    protected UserDefinition agencyAllAccess1;
    protected UserDefinition agencyAllAccess2;
    protected UserDefinition agencyNoAccess;

    private Map<UserDefinition, ExpectationInfo> expectations;

    public void setUpDefaultExpectations() {
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        internalAllAccess = userDefinitionFactory.internalAllAccess;

        internalNoAccess = userDefinitionFactory.internalNoAccess;

        ispManagerAllAccess1 = userDefinitionFactory.ispManagerAllAccess1;
        ispManagerAllAccess2 = userDefinitionFactory.ispManagerAllAccess2;
        ispManagerNoAccess = userDefinitionFactory.ispManagerNoAccess;

        cmpManagerAllAccess1 = userDefinitionFactory.cmpManagerAllAccess1;
        cmpManagerAllAccess2 = userDefinitionFactory.cmpManagerAllAccess2;
        cmpManagerNoAccess = userDefinitionFactory.cmpManagerNoAccess;

        advertiserManagerAllAccess1 = userDefinitionFactory.advertiserManagerAllAccess1;
        advertiserManagerAllAccess2 = userDefinitionFactory.advertiserManagerAllAccess2;
        advertiserManagerNoAccess = userDefinitionFactory.advertiserManagerNoAccess;

        publisherManagerAllAccess1 = userDefinitionFactory.publisherManagerAllAccess1;
        publisherManagerAllAccess2 = userDefinitionFactory.publisherManagerAllAccess2;
        publisherManagerNoAccess = userDefinitionFactory.publisherManagerNoAccess;

        ispAllAccess1 = userDefinitionFactory.ispAllAccess1;
        ispAllAccess2 = userDefinitionFactory.ispAllAccess2;
        ispNoAccess = userDefinitionFactory.ispNoAccess;

        publisherAllAccess1 = userDefinitionFactory.publisherAllAccess1;
        publisherAllAccess2 = userDefinitionFactory.publisherAllAccess2;
        publisherNoAccess = userDefinitionFactory.publisherNoAccess;

        advertiserAllAccess1 = userDefinitionFactory.advertiserAllAccess1;
        advertiserAllAccess2 = userDefinitionFactory.advertiserAllAccess2;
        advertiserNoAccess = userDefinitionFactory.advertiserNoAccess;

        cmpAllAccess1 = userDefinitionFactory.cmpAllAccess1;
        cmpAllAccess2 = userDefinitionFactory.cmpAllAccess2;
        cmpNoAccess = userDefinitionFactory.cmpNoAccess;

        agencyAllAccess1 = userDefinitionFactory.agencyAllAccess1;
        agencyAllAccess2 = userDefinitionFactory.agencyAllAccess2;
        agencyNoAccess = userDefinitionFactory.agencyNoAccess;

        agencyAdvertiser1 = new AdvertiserAccountDefinition(agencyAllAccess1);

        expectations = new LinkedHashMap<>();

        SecurityContextMock.getInstance().setPrincipal(internalAllAccess.getUser());

        InternalAccount pubInternalAccount = ((PublisherAccount)publisherAllAccess1.getUser().getAccount()).getInternalAccount();
        InternalAccount advInternalAccount = agencyAdvertiser1.getAccount().getInternalAccount();
        InternalAccount ispInternalAccount = ((IspAccount)ispAllAccess1.getUser().getAccount()).getInternalAccount();

        internalUserAccountNoAccess = userDefinitionFactory.internalUserAccountNoAccess;

        internalMultipleAccountsAccess = userDefinitionFactory.internalMultipleAccountsAccess;
        internalMultipleAccountsAccess.getAccessAccountIds().add(pubInternalAccount.getId());
        internalMultipleAccountsAccess.getAccessAccountIds().add(advInternalAccount.getId());
        internalMultipleAccountsAccess.getAccessAccountIds().add(ispInternalAccount.getId());

        internalMultipleAccountsNoAccess = userDefinitionFactory.internalMultipleAccountsNoAccess;
        internalMultipleAccountsNoAccess.getAccessAccountIds().add(internalAllAccess.getUser().getAccount().getId());
    }

    @After
    public void cleanExpectations() throws Exception {
        expectations = null;
    }

    protected void expectResult(UserDefinition definition, boolean result) {
        expectations.put(definition, new ExpectationInfo(result));
    }

    protected void resetExpectationsToDefault() {
        expectations.clear();
        setUpDefaultExpectations();
    }

    protected void doCheck(Callable callable) {
        for (Map.Entry<UserDefinition, ExpectationInfo> entry : expectations.entrySet()) {
            doCheck(callable, entry.getKey(), entry.getValue());
        }
    }

    private void doCheck(Callable callable, UserDefinition ud, ExpectationInfo info) {
        User user = ud.getUser();
        SecurityPrincipal old = SecurityContextMock.getInstance().getPrincipal();
        SecurityContextMock.getInstance().setPrincipal(user);

        try {
            revoke(null, null);
            boolean result = callable.call();
            info.check(result);
            if (result && callable.type != null && callable.action != null) {
                revoke(callable.type, callable.action);
                // it should be always false without control permission
                info.checkFalse(callable.call());
            }
        } catch (AssertionError e) {
            e.printStackTrace();
            fail("Expectation is failed.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            clearRevokes();
            SecurityContextMock.getInstance().setPrincipal(old);
        }
    }

    private void revoke(final String type, final String action) {
        EasyMock.reset(permissionService);

        EasyMock.expect(permissionService.isGranted(eq(type), eq(action))).andReturn(false).anyTimes();
        EasyMock.expect(permissionService.isGranted(anyString(), anyString())).andAnswer(new IAnswer<Boolean>() {
            @Override
            public Boolean answer() throws Throwable {
                Object[] args = EasyMock.getCurrentArguments();
                return realPermissionService.isGranted((String) args[0], (String) args[1]);
            }
        }).anyTimes();

        EasyMock.expect(permissionService.isGranted(eq(type), eq(action), anyString())).andReturn(false).anyTimes();
        EasyMock.expect(permissionService.isGranted(anyString(), anyString(), anyString())).andAnswer(new IAnswer<Boolean>() {
            @Override
            public Boolean answer() throws Throwable {
                Object[] args = EasyMock.getCurrentArguments();
                return realPermissionService.isGranted((String) args[0], (String) args[1], (String) args[2]);
            }
        }).anyTimes();

        EasyMock.replay(permissionService);
    }

    private void clearRevokes() {
        EasyMock.reset(permissionService);
    }

    public enum PermissionsSet {
        ALL,
        NONE
    }

    protected class AdvertiserAccountDefinition {
        private UserDefinition agency;

        public AdvertiserAccountDefinition(UserDefinition agency) {
            this.agency = agency;
        }
        private AdvertiserAccount account;

        public AdvertiserAccount getAccount() {
            if (account == null) {
                AgencyAccount agencyAccount = (AgencyAccount) agency.getUser().getAccount();
                account = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
            }
            return account;
        }
    }

    private static class ExpectationInfo {
        private AssertionError exception;
        private boolean result;

        private ExpectationInfo(boolean result) {
            this.exception = new AssertionError("Unexpected result");
            this.result = result;
        }

        private void check(boolean result) {
            if (result != this.result) {
                throw exception;
            }
        }

        private void checkFalse(boolean result) {
            if (result) {
                throw exception;
            }
        }
    }

    public static abstract class Callable {
        private String type;
        private String action;

        protected Callable() {
        }

        protected Callable(String type, String action) {
            this.type = type;
            this.action = action;
        }

        public String getType() {
            return type;
        }

        public String getAction() {
            return action;
        }

        public abstract boolean call();
    }

    public static abstract class ContextCall extends Callable {

        protected ContextCall(String type, String action) {
            super(type, action);
        }

        protected ContextCall() {
        }

        @Override
        public final boolean call() {
            ValidationContext context = ValidationUtil.createContext();
            call(context);
            return context.ok();
        }

        public abstract void call(ValidationContext context);
    }
}
