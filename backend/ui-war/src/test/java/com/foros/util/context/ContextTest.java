package com.foros.util.context;

import com.foros.AbstractUnitTest;
import com.foros.action.account.ContextNotSetException;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.session.account.AccountService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group.Unit;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ContextTest extends AbstractUnitTest {

    @Rule
    public EasyMockRule mockRule = new EasyMockRule(this);

    @Mock
    private AccountService accountService;

    @Before
    public void initServices() throws Exception {
        serviceLocatorMock.injectService(AccountService.class, accountService);
    }

    @Test
    @Category(Unit.class)
    public void advertiserContext() {
        AgencyAccount agn1 = new AgencyAccount(1L);
        AgencyAccount agn2 = new AgencyAccount(2L);
        AdvertiserAccount agnAdv2 = new AdvertiserAccount(22L);
        agnAdv2.setAgency(agn2);
        AdvertiserAccount adv3 = new AdvertiserAccount(3L);

        AdvertiserContext ac = new AdvertiserContext();

        // switch to agency itself
        assertTrue(ac.switchTo(agn1));
        assertFalse(ac.switchTo(agn1));
        assertTrue(ac.isSet());
        assertFalse(ac.isAgencyAdvertiserSet());

        ac.clear();
        assertFalse(ac.isSet());
        assertFalse(ac.isAgencyAdvertiserSet());

        // switch to non-agency advertiser
        assertTrue(ac.switchTo(adv3));
        assertTrue(ac.isSet());
        assertFalse(ac.isAgencyAdvertiserSet());

        // switch to agency advertiser
        assertTrue(ac.switchTo(agnAdv2));
        assertTrue(ac.isSet());
        assertTrue(ac.isAgencyAdvertiserSet());

        ac.clear();
    }

    @Test
    @Category(Unit.class)
    public void ispContext() {
        doTestSimpleContext(new IspContext(), new IspAccount(1L), new IspAccount(2L));
    }

    @Test
    @Category(Unit.class)
    public void publisherContext() {
        doTestSimpleContext(new PublisherContext(), new PublisherAccount(1L), new PublisherAccount(2L));
    }

    private <T extends Account> void doTestSimpleContext(ContextBase<T> context, T account1, T account2) {
        expect(findForSwitching(account1)).andReturn(account1).anyTimes();
        expect(findForSwitching(account2)).andReturn(account2).anyTimes();

        EasyMock.replay(accountService);

        long accountId = account1.getId();
        assertTrue(context.switchTo(accountId));
        assertFalse(context.switchTo(accountId));
        assertEquals(accountId, context.getAccountId().longValue());

        context.clear();

        assertFalse(context.isSet());
        try {
            context.getAccountId();
            fail();
        } catch (ContextNotSetException e) {
            assertNotNull(e.getMessage());
        }

        assertTrue(context.switchTo(account1));
        assertFalse(context.switchTo(account1));
        assertEquals(accountId, context.getAccountId().longValue());

        assertTrue(context.switchTo(account2.getId()));
        assertFalse(context.switchTo(account2));
        assertEquals(account2.getId(), context.getAccount().getId());

        EasyMock.verify(accountService);
    }

    /** @noinspection unchecked*/
    private <T extends Account> T findForSwitching(T account1) {
        return (T)accountService.findForSwitching(account1.getClass(), account1.getId());
    }
}
