package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.account.Account;
import com.foros.rs.client.model.account.AccountInAgencySelector;
import com.foros.rs.client.model.account.AccountRole;
import com.foros.rs.client.model.account.AccountSelector;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.model.operation.Result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.foros.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

public class AccountServiceTest extends AbstractUnitTest {

    private Long agencyId;

    @Before
    public void setUp() throws Exception {
        agencyId = longProperty("foros.test.agency.id");
    }

    @Test
    public void testSearch() throws Exception {
        AccountSelector selector = new AccountSelector();
        assertTrue(!accountService.search(selector).getEntities().isEmpty());

        PagingSelector paging = new PagingSelector();
        paging.setCount(1L);
        paging.setFirst(1L);
        selector.setRoles(Arrays.asList(AccountRole.ISP));
        assertTrue(accountService.search(selector).getEntities().get(0).getRole() == AccountRole.ISP);

        selector.setRoles(null);
        selector.setCountryCodes(Arrays.asList("GB"));
        assertTrue(accountService.search(selector).getEntities().get(0).getCountry().equals("GB"));

        selector.setCountryCodes(null);
        selector.setStatuses(Arrays.asList(Status.INACTIVE));
        assertTrue(accountService.search(selector).getEntities().get(0).getStatus() == Status.INACTIVE);
    }

    @Test
    public void testSearchInAgency() throws Exception {
        AccountInAgencySelector selector = new AccountInAgencySelector();
        selector.setAgencyId(agencyId);
        List<Account> entities = accountService.searchInAgency(selector).getEntities();
        assertTrue(!entities.isEmpty());

        PagingSelector paging = new PagingSelector();
        paging.setCount(1L);
        paging.setFirst(2L);
        selector.setPaging(paging);
        assertEquals(accountService.searchInAgency(selector).getEntities().get(0).getId(), entities.get(2).getId());

    }

    @Test
    public void testGetAgencyByName() {
        Account gotById = accountService.get(agencyId);
        assertTrue(gotById != null);

        Result<Account> gotByNameResult = accountService.get(gotById.getName());
        assertTrue(gotByNameResult != null);
        assertTrue(gotByNameResult.getEntities().size() == 1);
        Account gotByName = gotByNameResult.getEntities().get(0);

        assertEquals(gotById.getName(), gotByName.getName());
        assertEquals(gotById.getId(), gotByName.getId());
    }

    @Test
    public void testCreateAdvertiserInAgency() throws Exception {
        // CREATE
        Account advertiserAccount = new Account();
        EntityLink agency = new EntityLink();
        agency.setId(agencyId);
        advertiserAccount.setAgency(agency);
        advertiserAccount.setName("test_" + RandomUtil.getRandomString(10));
        advertiserAccount.setStatus(Status.ACTIVE);

        System.out.println("Creating account " + advertiserAccount.getName() + " for agency " + agencyId);

        Operations<Account> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(
                operation(advertiserAccount, OperationType.CREATE)
        ));


        OperationsResult operationsResult = accountService.perform(operations);
        assertEquals(1, operationsResult.getIds().size());

        // FIND
        AccountInAgencySelector selector = accountInAgencySelector();
        selector.setAgencyId(agencyId);
        List<Account> accounts = accountService.searchInAgency(selector).getEntities();
        assertTrue(!accounts.isEmpty());

        Account foundAccount = null;
        for (Account account : accounts) {
            if (account.getId().equals(operationsResult.getIds().get(0))) {
                foundAccount = account;
                break;
            }
        }
        assertNotNull(foundAccount);

        // UPDATE
        foundAccount.setStatus(Status.DELETED);
        operations = new Operations<>();
        operations.setOperations(Collections.singletonList(
                operation(foundAccount, OperationType.UPDATE)
        ));
        operationsResult = accountService.perform(operations);
        assertEquals(1, operationsResult.getIds().size());

        // FIND AGAIN
        selector.setStatuses(Arrays.asList(Status.DELETED));
        List<Account> deletedAccounts = accountService.searchInAgency(selector).getEntities();
        assertTrue(!deletedAccounts.isEmpty());

        boolean deletedAccountFound = false;
        for (Account deletedAccount : deletedAccounts) {
            if (deletedAccount.getId().equals(foundAccount.getId())) {
                deletedAccountFound = true;
                break;
            }
        }
        assertTrue(deletedAccountFound);
    }

    private AccountInAgencySelector accountInAgencySelector() {
        AccountInAgencySelector selector = new AccountInAgencySelector();
        PagingSelector paging = new PagingSelector();
        paging.setCount(500L);
        selector.setPaging(paging);

        return selector;
    }
}
