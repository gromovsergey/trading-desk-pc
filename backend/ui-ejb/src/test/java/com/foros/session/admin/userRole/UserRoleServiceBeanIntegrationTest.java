package com.foros.session.admin.userRole;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.session.BusinessException;
import com.foros.session.account.AccountServiceBean;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.UserRoleTestFactory;
import com.foros.test.factory.UserTestFactory;

import group.Db;
import javax.persistence.Query;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class UserRoleServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleTestFactory userRoleTF;

    @Autowired
    public AccountServiceBean accountService;

    @Autowired
    public InternalAccountTestFactory internalAccountTF;

    @Autowired
    public CmpAccountTestFactory cmpAccountTF;

    @Autowired
    public PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    public IspAccountTestFactory ispAccountTF;

    @Autowired
    public AgencyAccountTestFactory accountTF;

    @Autowired
    public UserTestFactory userTF;

    @Autowired
    public AdvertiserAccountTestFactory advertiserAccountTF;

    @Test
    public void testCreate() {
        AccountRole accountRole = AccountRole.INTERNAL;
        UserRole role = userRoleTF.create(accountRole);
        Long internalAccountId = setInternalAccess(role);
        userRoleService.create(role);
        Long id = role.getId();
        assertNotNull(id);
        role = entityManager.find(UserRole.class, id);
        assertNotNull(role);
        assertTrue(role.getPolicyEntries().size() == 0);
        assertTrue(role.getAccessAccountIds().contains(internalAccountId));
        assertTrue(role.getInternalAccessType() != null);
    }

    @Test
    public void testCreateWithInternalAccessType() {
        AccountRole accountRole = AccountRole.ADVERTISER;
        UserRole role = userRoleTF.create(accountRole);
        setInternalAccess(role);
        userRoleService.create(role);
        Long id = role.getId();
        assertNotNull(id);
        role = entityManager.find(UserRole.class, id);
        assertTrue(role.getInternalAccessType() == null);
    }

    @Test
    public void testUpdate() {
        UserRole role = userRoleTF.createPersistent(AccountRole.INTERNAL);
        Long id = role.getId();
        role = userRoleService.view(id);
        String newName = userRoleTF.getTestEntityRandomName();
        role.setName(newName);
        userRoleService.update(role);
        role = entityManager.find(UserRole.class, id);
        assertEquals(newName, role.getName());
    }

    @Test
    public void testFind() {
        UserRole role = userRoleTF.createPersistent(AccountRole.INTERNAL);
        Long id = role.getId();
        userRoleService.view(id);
        assertNotNull(role);
    }

    @Test
    public void testIsRoleChangeAllowed() {
        AccountRole accountRole = AccountRole.INTERNAL;
        UserRole role = userRoleTF.create(accountRole);
        userRoleService.create(role);
        Long id = role.getId();
        assertNotNull(id);

        //user not assigned as Account Manger to any account so it is allowed to change the Account Manager
        userRoleService.update(role);

        role.setCMPAccountManager(true);
        role.setISPAccountManager(true);
        role.setAdvertiserAccountManager(true);
        role.setPublisherAccountManager(true);

        userRoleService.update(role);

        InternalAccount internalAccount = internalAccountTF.createPersistent();

        User accountMangerUser = userTF.create(internalAccount);
        accountMangerUser.setRole(role);
        userTF.persist(accountMangerUser);


        CmpAccount cmpAccount = cmpAccountTF.create(internalAccount);
        cmpAccount.setAccountManager(accountMangerUser);
        Long cmpId = accountService.createExternalAccount(cmpAccount);
        assertNotNull(cmpId);


        // Trying to change Account Manager flag
        role.setCMPAccountManager(false);
        try {
            userRoleService.update(role);
            fail("Must fail");
        } catch (BusinessException be) {
            // OK
            role.setCMPAccountManager(true); // Returning back
            userRoleService.update(role);
        }

        AdvertiserAccount advaccount = advertiserAccountTF.create(internalAccount);
        advaccount.setInternalAccount(internalAccount);
        advaccount.setAccountManager(accountMangerUser);
        Long advId = accountService.createExternalAccount(advaccount);
        assertNotNull(advId);

        // Trying to change Account Manager flag
        role.setAdvertiserAccountManager(false);
        try {
            userRoleService.update(role);
            fail("Must fail");
        } catch (BusinessException be) {
            // OK
            role.setAdvertiserAccountManager(true);
            userRoleService.update(role);
        }

        PublisherAccount publisherAccount = publisherAccountTF.create(internalAccount);
        publisherAccount.setAccountManager(accountMangerUser);
        accountService.createExternalAccount(publisherAccount);

        role.setPublisherAccountManager(false);
        try {
            userRoleService.update(role);
            fail("Must fail");
        } catch (BusinessException be) {
            // OK
            role.setPublisherAccountManager(true);
            userRoleService.update(role);
        }

        IspAccount ispAccount = ispAccountTF.create(internalAccount);
        ispAccount.setAccountManager(accountMangerUser);
        accountService.createExternalAccount(ispAccount);

        role.setISPAccountManager(false);
        try {
            userRoleService.update(role);
            fail("Must fail");
        } catch (BusinessException be) {
            // OK
            role.setISPAccountManager(true);
            userRoleService.update(role);
        }

        advaccount.setAccountManager(null);
        getEntityManager().merge(advaccount);
        getEntityManager().flush();

        AgencyAccount agencyAccount =  accountTF.create(internalAccount);
        agencyAccount.setAccountManager(accountMangerUser);
        accountService.createExternalAccount(agencyAccount);

        role.setAdvertiserAccountManager(false);

        try {
            userRoleService.update(role);
            fail("Must fail");
        } catch (BusinessException be) {
            // OK
            role.setAdvertiserAccountManager(true);
            userRoleService.update(role);
        }
    }

    @Test
    public void testIsAdvFinanceUser() {
        StringBuilder query = new StringBuilder();
        query.append(" select distinct p.user_role_id");
        query.append(" from policy p");
        query.append("  inner join userrole ur on ur.user_role_id = p.user_role_id");
        query.append(" where ur.account_role_id = 0");
        query.append("  and p.permission_type = 'advertising_account'");
        query.append("  and p.action_type = 'edit_finance'");
        query.append("  limit 1");
        Query q = getEntityManager().createNativeQuery(query.toString());
        Number userRoleId = (Number)q.getSingleResult();
        assertTrue(userRoleService.isAdvertisingFinanceUser(userRoleId.longValue()));
    }

    private Long setInternalAccess(UserRole role) {
        role.setInternalAccessType(InternalAccessType.MULTIPLE_ACCOUNTS);
        InternalAccount account = internalAccountTF.createPersistent();
        role.getAccessAccountIds().add(account.getId());
        return account.getId();
    }
}
