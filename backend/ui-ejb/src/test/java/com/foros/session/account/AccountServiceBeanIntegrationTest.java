package com.foros.session.account;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.model.site.Tag;
import com.foros.security.AccountRole;
import com.foros.security.SecurityContextMock;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Paging;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.security.AccountStatsTO;
import com.foros.session.security.AccountTO;
import com.foros.session.security.ExtensionAccountTO;
import com.foros.session.security.ManagerAccountTO;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.test.factory.TextCreativeTestFactory;
import com.foros.test.factory.UserTestFactory;
import com.foros.util.StringUtil;
import com.foros.util.bean.Filter;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import group.Db;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class AccountServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private AccountServiceBean accountService;

    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private IspAccountTestFactory ispAccountTF;

    @Autowired
    private CmpAccountTestFactory cmpAccountTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private PublisherAccountTypeTestFactory publisherAccountTypeTF;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private CreativeSizeTestFactory sizesTF;

    @Autowired
    private TextCreativeTestFactory creativeTF;

    @Autowired
    private DisplayCreativeService creativeService;

    @Autowired
    private CountryTestFactory countryTF;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    @Test
    public void testSearchByRoleAndTypeFlags() {
        setDeletedObjectsVisible(true);
        PublisherAccount account = publisherAccountTF.createPersistent();
        Long id = account.getId();
        account.getAccountType().setPublisherInventoryEstimationFlag(true);
        publisherAccountTF.update(account);
        commitChanges();

        List<AccountTO> accounts = accountService.searchByRoleAndTypeFlags(AccountRole.PUBLISHER, AccountType.PUBLISHER_INVENTORY_ESTIMATION_FLAG);
        assertTrue(accountExists(accounts, id));

        getEntityManager().clear();
        publisherAccountTF.delete(account);
        commitChanges();

        setDeletedObjectsVisible(false);
        accounts = accountService.searchByRoleAndTypeFlags(AccountRole.PUBLISHER, AccountType.PUBLISHER_INVENTORY_ESTIMATION_FLAG);
        assertFalse(accountExists(accounts, id));

        assertTrue(accountService.searchByRoleAndTypeFlags(AccountRole.PUBLISHER, AccountType.PUBLISHER_INVENTORY_ESTIMATION_FLAG) != null);

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        assertTrue(accountService.searchByRoleAndTypeFlags(AccountRole.PUBLISHER, AccountType.PUBLISHER_INVENTORY_ESTIMATION_FLAG) != null);
    }

    @Test
    public void testSearch() {
        AccountRole role = AccountRole.INTERNAL;

        List<AccountTO> accounts = accountService.search();
        assertTrue(accounts != null);

        accounts = accountService.search(role);
        assertTrue(accounts != null);

        accounts = accountService.search(true, role);
        assertTrue(accounts != null);

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        accounts = accountService.search(true, role);
        assertTrue(accounts != null);
    }

    public void testSearchAdvertisers() {
        setDeletedObjectsVisible(true);
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createAdvertiserInAgency(agencyAccount);
        accountService.addAdvertiser(advertiserAccount);

        assertTrue(accountService.searchAdvertisersWithCampaigns(agencyAccount.getId(), true) != null);

        setDeletedObjectsVisible(false);
        assertTrue(accountService.searchAdvertisersWithCampaigns(agencyAccount.getId(), true) != null);
    }

    @Test
    public void testCreateCmpAccount() {
        CmpAccount cmpAccount = cmpAccountTF.create();
        Long id = accountService.createExternalAccount(cmpAccount);
        assertNotNull(id);
        assertEquals(id, cmpAccount.getId());
        getEntityManager().refresh(cmpAccount);
    }

    @Test
    public void testCreatePublisherAccount() throws IOException {
        PublisherAccount account = publisherAccountTF.createPersistent();
        Account created = accountService.viewPublisherAccount(account.getId());
        assertNotNull(created);
    }

    @Test
    public void testAddAdvertiser() {
        AgencyAccount agency = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiser = advertiserAccountTF.createAdvertiserInAgency(agency);

        accountService.addAdvertiser(advertiser);

        assertNotNull(advertiser);
        assertEquals(advertiser.getAgency(), agency);
        assertEquals(advertiser.getCountry(), agency.getCountry());
        assertEquals(advertiser.getCurrency(), agency.getCurrency());
        assertEquals(advertiser.getInternalAccount(), agency.getInternalAccount());
        assertEquals(advertiser.getTimezone(), agency.getTimezone());
        assertTrue(agency.getAdvertisers().contains(advertiser));
    }

    @Test
    public void testUpdate() throws IOException {
        // test create and find
        PublisherAccount account = publisherAccountTF.createPersistent();
        Long id = account.getId();
        PublisherAccount created = accountService.viewPublisherAccount(id);

        // test update
        String updatedName = publisherAccountTF.getTestEntityRandomName();
        created.setName(updatedName);
        created.setLegalName(updatedName);

        Country updatedCountry = countryTF.findOrCreatePersistent("ZZ");
        created.setCountry(updatedCountry);

        accountService.updateExternalAccount(created);

        Account updated = accountService.viewPublisherAccount(id);
        assertEquals(updatedName, updated.getName());
        assertEquals(updatedName, updated.getLegalName());
        assertEquals(updatedCountry, updated.getCountry());
    }

    @Test
    public void testUpdateAdvertiser() {
        //create and find
        AgencyAccount agency = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        advertiser.unregisterChanges();
        String updatedName = agencyAccountTF.getTestEntityRandomName();
        String updatedLegalName = "legal" + updatedName;
        String updatedNotes = "updated notes of the buddy" + (new Date()).getTime();
        advertiser.setName(updatedName);
        advertiser.setLegalName(updatedLegalName);
        advertiser.setNotes(updatedNotes);

        accountService.updateAdvertiser(advertiser);

        advertiser = accountService.viewAdvertiserInAgencyAccount(advertiser.getId());
        assertEquals(updatedName, advertiser.getName());
        assertEquals(updatedLegalName, advertiser.getLegalName());
        assertEquals(updatedNotes, advertiser.getNotes());
    }

    @Test
    public void testActivate() {
        Account account = advertiserAccountTF.createPersistent();
        accountService.inactivate(account.getId());
        Account found = accountService.find(account.getId());
        assertEquals(found.getStatus(), Status.INACTIVE);

        accountService.activate(account.getId());
        found = accountService.find(account.getId());
        assertEquals(found.getStatus(), Status.ACTIVE);
    }

    @Test
    public void testActivateAdvertiser() {
        AgencyAccount agency = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        Long advertiserId = advertiser.getId();
        accountService.inactivate(advertiserId);
        accountService.activate(advertiserId);
        AdvertiserAccount found = accountService.viewAdvertiserInAgencyAccount(advertiserId);
        assertEquals(found.getStatus(), Status.ACTIVE);
    }

    @Test
    public void testInactivate() {
        Account account = advertiserAccountTF.createPersistent();
        accountService.inactivate(account.getId());
        Account found = accountService.find(account.getId());
        assertEquals(found.getStatus(), Status.INACTIVE);
    }

    @Test
    public void testInactivateAdvertiser() {
        AgencyAccount agency = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        Long advertiserId = advertiser.getId();
        accountService.inactivate(advertiserId);
        AdvertiserAccount found = accountService.viewAdvertiserInAgencyAccount(advertiserId);
        assertEquals(found.getStatus(), Status.INACTIVE);
    }

    @Test
    public void testDelete() {
        PublisherAccount account = publisherAccountTF.createPersistent();
        accountService.delete(account.getId());
        Account found = accountService.find(account.getId());
        assertEquals(found.getStatus(), Status.DELETED);
    }

    @Test
    public void testDeleteAdvertiser() {
        AgencyAccount agency = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        Long advertiserId = advertiser.getId();
        accountService.delete(advertiserId);
        AdvertiserAccount found = accountService.viewAdvertiserInAgencyAccount(advertiserId);
        assertEquals(found.getStatus(), Status.DELETED);
    }

    @Test
    public void testUndelete() {
        PublisherAccount account = publisherAccountTF.createPersistent();
        Long id = account.getId();
        accountService.delete(id);
        accountService.undelete(id);
        Account found = accountService.find(id);
        assertEquals(found.getStatus(), Status.INACTIVE);
    }

    @Test
    public void testUneleteAdvertiser() {
        AgencyAccount agency = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        Long advertiserId = advertiser.getId();
        accountService.delete(advertiserId);
        accountService.undelete(advertiserId);
        AdvertiserAccount found = accountService.viewAdvertiserInAgencyAccount(advertiserId);
        assertEquals(found.getStatus(), Status.INACTIVE);
    }

    @Test
    public void testGetAdvertiserAccounts() {
        List<AccountStatsTO> accounts;
        AdvertiserAccount account = advertiserAccountTF.createPersistent();

        // Search by AccountName
        String accountName = account.getName();
        accounts = accountService.searchAdvertiserAccounts(accountName, null, null, null, null, null, null);
        assertTOValid(accounts, account);

        // Search by AccountName & AccountType
        Long accountTypeId = account.getAccountType().getId();
        accounts = accountService.searchAdvertiserAccounts(accountName, accountTypeId, null, null, null, null, null);
        assertTOValid(accounts, account);

        // Search by AccountName & InternalAccountId
        Long internalAccountId = account.getInternalAccount().getId();
        accounts = accountService.searchAdvertiserAccounts(accountName, null, internalAccountId, null, null, null, null);
        assertTOValid(accounts, account);

        // Search by AccountName & country
        String countryCode = account.getCountry().getCountryCode();
        accounts = accountService.searchAdvertiserAccounts(accountName, null, null, countryCode, null, null, null);
        assertTOValid(accounts, account);

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        accounts = accountService.searchAdvertiserAccounts(accountName, null, null, countryCode, null, null, null);
        assertTOValid(accounts, account);
    }

    @Test
    public void testGetPublisherAccounts() {
        List<AccountStatsTO> accounts;
        PublisherAccount account = publisherAccountTF.createPersistent();

        // Search by AccountName
        String accountName = account.getName();
        accounts = accountService.searchPublisherAccounts(accountName, null, null, null, null, null, null);
        assertTOValid(accounts, account);

        // Search by AccountName & AccountType
        Long accountTypeId = account.getAccountType().getId();
        accounts = accountService.searchPublisherAccounts(accountName, accountTypeId, null, null, null, null, null);
        assertTOValid(accounts, account);

        // Search by AccountName & InternalAccountId
        Long internalAccountId = account.getInternalAccount().getId();
        accounts = accountService.searchPublisherAccounts(accountName, null, internalAccountId, null, null, null, null);
        assertTOValid(accounts, account);

        // Search by AccountName & country
        String countryCode = account.getCountry().getCountryCode();
        accounts = accountService.searchPublisherAccounts(accountName, null, null, countryCode, null, null, null);
        assertTOValid(accounts, account);
    }

    @Test
    public void testSearchWithAccountManager() {
        List<AccountStatsTO> accounts;

        PublisherAccount publisherAccount = publisherAccountTF.createPersistent();
        User user = userTF.createPersistent();
        publisherAccount.setAccountManager(user);
        getEntityManager().merge(publisherAccount);
        commitChanges();
        clearContext();

        String accountName = publisherAccount.getName();
        accounts = accountService.searchPublisherAccounts(accountName, null, null, null, 0L, null, null);
        assertTrue(accounts.isEmpty());

        accounts = accountService.searchPublisherAccounts(accountName, null, null, null, user.getId(), null, null);
        assertTrue(publisherAccount.getId().equals(accounts.iterator().next().getId()));
    }

    @Test
    public void testGetISPAccounts() {
        List<AccountStatsTO> accounts;
        IspAccount ispAccount = ispAccountTF.createPersistent();

        // Search by AccountName
        String accountName = ispAccount.getName();
        accounts = accountService.searchISPAccounts(accountName, null, null, null, null, null, null);
        assertTOValid(accounts, ispAccount);

        // Search by AccountName & AccountType
        Long accountTypeId = ispAccount.getAccountType().getId();
        accounts = accountService.searchISPAccounts(accountName, accountTypeId, null, null, null, null, null);
        assertTOValid(accounts, ispAccount);

        // Search by AccountName & InternalAccountId
        Long internalAccountId = ispAccount.getInternalAccount().getId();
        accounts = accountService.searchISPAccounts(accountName, null, internalAccountId, null, null, null, null);
        assertTOValid(accounts, ispAccount);

        // Search by AccountName & country
        String countryCode = ispAccount.getCountry().getCountryCode();
        accounts = accountService.searchISPAccounts(accountName, null, null, countryCode, null, null, null);
        assertTOValid(accounts, ispAccount);
    }

    @Test
    public void testGetCMPAccounts() {
        List<AccountStatsTO> accounts;
        CmpAccount cmpAccount = cmpAccountTF.createPersistent();

        // Search by AccountName
        String accountName = cmpAccount.getName();
        DisplayStatus displayStatus = cmpAccount.getDisplayStatus();
        accounts = accountService.searchCMPAccounts(accountName, null, null, null, null, displayStatus);
        assertTOValid(accounts, cmpAccount);

        // Search by AccountName & AccountType
        Long accountTypeId = cmpAccount.getAccountType().getId();
        accounts = accountService.searchCMPAccounts(accountName, accountTypeId, null, null, null, displayStatus);
        assertTOValid(accounts, cmpAccount);

        // Search by AccountName & InternalAccountId
        Long internalAccountId = cmpAccount.getInternalAccount().getId();
        accounts = accountService.searchCMPAccounts(accountName, null, internalAccountId, null, null, displayStatus);
        assertTOValid(accounts, cmpAccount);

        // Search by AccountName & country
        String countryCode = cmpAccount.getCountry().getCountryCode();
        accounts = accountService.searchCMPAccounts(accountName, null, null, countryCode, null, displayStatus);
        assertTOValid(accounts, cmpAccount);

    }

    @Test
    public void testEditAccountWithAccountType() {
        // create two AccountTypes
        AccountType accountTypeFirst = publisherAccountTypeTF.create();
        AccountType accountTypeSecond = publisherAccountTypeTF.create();
        Set<CreativeSize> sizes = new HashSet<>(1);
        sizes.add(sizesTF.createPersistent());
        accountTypeFirst.setPublisherInventoryEstimationFlag(true);
        accountTypeFirst.setCreativeSizes(sizes);
        accountTypeSecond.setPublisherInventoryEstimationFlag(false);
        accountTypeSecond.setCreativeSizes(sizes);
        publisherAccountTypeTF.persist(accountTypeFirst);
        publisherAccountTypeTF.persist(accountTypeSecond);

        // create account using first AccountType
        PublisherAccount account = publisherAccountTF.createPersistent(accountTypeFirst);

        // create a tag using first AccountType
        Tag tag = tagsTF.create(siteTF.createPersistent(account));
        tag.setInventoryEstimationFlag(true);
        tagsTF.persist(tag);

        // update Account to second AccountType which has different property
        PublisherAccount toUpdateAccount = publisherAccountTF.create(accountTypeSecond);

        toUpdateAccount.setId(account.getId());
        try {
            accountService.updateExternalAccount(toUpdateAccount);
            fail();
        } catch (ConstraintViolationException e) {
            assertEquals(1, e.getConstraintViolations().size());

            ConstraintViolation constraintViolation = e.getConstraintViolations().iterator().next();

            assertEquals("accountType", constraintViolation.getPropertyPath().toString());
            assertEquals(StringUtil.getLocalizedString("account.illegalAccountType"), constraintViolation.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testFindForSwitching() {
        IspAccount isp = ispAccountTF.createPersistent();

        assertEquals(isp, accountService.findForSwitching(isp.getClass(), isp.getId()));

        try {
            accountService.findForSwitching(AdvertiserAccount.class, isp.getId());
            fail();
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testGetChannelOwners() {
        List<ManagerAccountTO> list = accountService.getChannelOwners();
        assertNotNull(list);

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        list = accountService.getChannelOwners();
        assertNotNull(list);
    }

    @Test
    public void testFindIndex() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();

        AccountTO accountTO = accountService.findIndex(account.getId());

        assertNotNull(accountTO);
    }

    @Test
    public void testSalesManagers() throws Exception {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        assertNotNull(userTF.createPersistentSalesManager(advertiserAccount));
        commitChanges();
        clearContext();

        List<EntityTO> salesManagers = accountService.getSalesManagers(advertiserAccount);
        assertNotNull(salesManagers);
        assertTrue(salesManagers.size() > 0);
    }

    @Test
    public void testContentCategories() throws Exception {
        CreativeCategory category1 = createPersistedRandomContentCategory();
        CreativeCategory category2 = createPersistedRandomContentCategory();
        commitChanges();
        clearContext();

        AdvertiserAccount advertiser = advertiserAccountTF.createPersistent();
        commitChanges();
        clearContext();

        advertiser.getCategories().add(category1);
        accountService.updateExternalAccount(advertiser);
        commitChanges();
        clearContext();

        Set<CreativeCategory> categories = accountService.loadCategories(advertiser);
        assertEquals(1, categories.size());

        Creative creative = creativeTF.create(advertiser);
        creative.getCategories().add(category1);
        creativeTF.persist(creative);
        commitChanges();
        clearContext();

        creative = creativeService.view(creative.getId());
        assertEquals(1, creative.getCategories().size());

        advertiser = new AdvertiserAccount(advertiser.getId());
        advertiser.setCategories(new HashSet<>(Arrays.asList(category1, category2)));
        accountService.updateExternalAccount(advertiser);
        commitChanges();
        clearContext();

        categories = accountService.loadCategories(advertiser);
        assertEquals(2, categories.size());

        creative = creativeTF.refresh(creative);
        assertEquals(2, creative.getCategories().size());
    }

    private CreativeCategory createPersistedRandomContentCategory() {
        CreativeCategory category = new CreativeCategory();
        category.setDefaultName(advertiserAccountTF.getTestEntityRandomName());
        category.setType(CreativeCategoryType.CONTENT);
        getEntityManager().persist(category);
        return category;
    }

    private void assertTOValid(List<AccountStatsTO> accounts, Account cmpAccount) {
        AccountStatsTO cmpTO;
        assertFalse(accounts.isEmpty());
        cmpTO = findAccountTo(accounts, cmpAccount);
        assertNotNull(cmpTO);
        assertEquals(cmpAccount.getName(), cmpTO.getName());
        assertEquals(cmpAccount.getDisplayStatus(), cmpTO.getDisplayStatus());
    }

    private AccountStatsTO findAccountTo(List<AccountStatsTO> accountTOs, final Account account) {
        return (AccountStatsTO) CollectionUtils.find(accountTOs, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                AccountStatsTO to = (AccountStatsTO) o;
                return to.getId().equals(account.getId());
            }
        });
    }

    private boolean accountExists(List<AccountTO> entities, Long accountId) {
        for (AccountTO entity : entities) {
            if (entity.getId().equals(accountId)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testGetExtensionAccountTOByAgency() {
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();

        AdvertiserAccount advertiserAccount1 = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        assertNotNull(advertiserAccount1);
        AdvertiserAccount advertiserAccount2 = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        assertNotNull(advertiserAccount2);

        AccountInAgencySelector.Builder builder = new AccountInAgencySelector.Builder()
            .agencyId(agencyAccount.getId());

        List<ExtensionAccountTO> result = accountService.getExtensionAccountTOByAgency(builder.build()).getEntities();
        assertEquals(2, result.size());

        accountService.inactivate(advertiserAccount1.getId());
        builder.statuses(Status.INACTIVE);
        commitChanges();
        result = accountService.getExtensionAccountTOByAgency(builder.build()).getEntities();
        assertEquals(1, result.size());
        assertEquals(advertiserAccount1.getId(), result.get(0).getId());

        builder.statuses(Status.INACTIVE, Status.ACTIVE);
        result = accountService.getExtensionAccountTOByAgency(builder.build()).getEntities();
        assertEquals(2, result.size());

        builder.paging(new Paging(10, 10));
        assertEquals(0, accountService.getExtensionAccountTOByAgency(builder.build()).getEntities().size());

        builder.paging(new Paging(1, 1));
        assertEquals(1, accountService.getExtensionAccountTOByAgency(builder.build()).getEntities().size());

    }

    @Test
    public void testGet() {
        AgencyAccount agencyAccount1 = agencyAccountTF.createPersistent();
        AgencyAccount agencyAccount2 = agencyAccountTF.createPersistent();


        AccountSelector.Builder builder = new AccountSelector.Builder()
            .accountIds(agencyAccount1.getId(), agencyAccount2.getId());

        List<ExtensionAccountTO> result = accountService.get(builder.build()).getEntities();
        assertEquals(2, result.size());


        accountService.inactivate(agencyAccount1.getId());
        builder.statuses(Status.INACTIVE);
        commitChanges();
        result = accountService.get(builder.build()).getEntities();
        assertEquals(1, result.size());
        assertEquals(agencyAccount1.getId(), result.get(0).getId());

        builder.statuses(Status.INACTIVE, Status.ACTIVE);
        result = accountService.get(builder.build()).getEntities();
        assertEquals(2, result.size());

        builder.paging(new Paging(1000, 10));
        assertEquals(0, accountService.get(builder.build()).getEntities().size());

        builder.paging(new Paging(1, 1));
        assertEquals(1, accountService.get(builder.build()).getEntities().size());

        builder.paging(null);
        builder.countryCodes(agencyAccount1.getCountry().getCountryCode(), agencyAccount2.getCountry().getCountryCode());
        assertEquals(2, accountService.get(builder.build()).getEntities().size());

        builder.roles(AccountRole.PUBLISHER);
        assertEquals(0, accountService.get(builder.build()).getEntities().size());

        builder.roles(AccountRole.AGENCY);
        assertEquals(2, accountService.get(builder.build()).getEntities().size());

        builder.excludedStatuses(Status.INACTIVE);
        assertEquals(1, accountService.get(builder.build()).getEntities().size());

    }

    @Test
    public void testGetSearchByAccountManager() {
        User user = userDefinitionFactory.advertiserManagerNoAccess.getUser();
        InternalAccount internalAccount = (InternalAccount) user.getAccount();

        final AdvertiserAccount advertiserAccount1 = advertiserAccountTF.create(internalAccount);
        advertiserAccount1.setAccountManager(user);
        advertiserAccountTF.persist(advertiserAccount1);

        final AdvertiserAccount advertiserAccount2 = advertiserAccountTF.createPersistent(internalAccount);
        currentUserRule.setPrincipal(user);

        // no permissions required to search
        List<AccountTO> res = accountService.search(AccountRole.ADVERTISER);
        AccountTO found = com.foros.util.CollectionUtils.find(res, new Filter<AccountTO>() {
            @Override
            public boolean accept(AccountTO to) {
                return to.getId().equals(advertiserAccount1.getId());
            }
        });
        assertNotNull(found);


        AccountTO notFound = com.foros.util.CollectionUtils.find(res, new Filter<AccountTO>() {
            @Override
            public boolean accept(AccountTO to) {
                return to.getId().equals(advertiserAccount2.getId());
            }
        });
        assertNull(notFound);

        // can't get without permissions
        EasyMock.reset(permissionService);
        EasyMock.expect(permissionService.isGranted(EasyMock.anyString(), EasyMock.anyString())).andReturn(false).anyTimes();
        EasyMock.replay(permissionService);

        assertTrue(accountService.get(new AccountSelector.Builder().build()).getEntities().isEmpty());
    }

}
