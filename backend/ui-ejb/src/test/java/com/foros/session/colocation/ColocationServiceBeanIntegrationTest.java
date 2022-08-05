package com.foros.session.colocation;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.session.query.PartialList;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.ColocationTestFactory;

import group.Db;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import javax.persistence.PersistenceException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ColocationServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    public ColocationService colocationService;

    @Autowired
    public ColocationTestFactory colocationTF;

    @Autowired
    public UserDefinitionFactory userDefinitionFactory;

    @Test
    public void testFindById() throws Exception {
        Colocation colocation = colocationTF.createPersistent();
        Long id = colocation.getId();

        colocation = colocationService.find(id);

        assertEquals("Can't find colocation <" + id + ">", id, colocation.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        Colocation colocation = colocationTF.createPersistent();
        getEntityManager().clear();

        String newName = colocationTF.getTestEntityRandomName();
        colocation.setName(newName);
        colocation.getColocationRate().setRevenueShare(BigDecimal.ONE);

        colocationService.update(colocation);
        getEntityManager().flush();
        getEntityManager().clear();

        colocation = colocationService.find(colocation.getId());

        assertEquals(newName, colocation.getName());
    }

    @Test
    public void testDelete() throws Exception {
        Colocation colocation = colocationTF.createPersistent();

        colocationService.delete(colocation.getId());
        colocation = colocationService.find(colocation.getId());

        assertEquals(Status.DELETED, colocation.getStatus());
    }

    @Test
    public void testUndelete() throws Exception {
        Colocation colocation = colocationTF.createPersistent();
        colocationService.delete(colocation.getId());

        colocationService.undelete(colocation.getId());
        colocation = colocationService.find(colocation.getId());

        assertEquals(Status.ACTIVE, colocation.getStatus());
    }

    @Test
    public void testCreate() throws Exception {
        Colocation colocation = colocationTF.create();
        IspAccount account = colocation.getAccount();
        Long id = colocationService.create(colocation).getId();
        colocation = colocationService.find(id);
        assertEquals("Wrong status", Status.ACTIVE, colocation.getStatus());
        assertTrue("Colocation wasn't added to the account", account.getColocations().contains(colocation));
        assertEquals("Account is wrong", account, colocation.getAccount());
    }

    @Test(expected = PersistenceException.class)
    public void testCreateWithNullName() throws Exception {
        Colocation colocation = colocationTF.create();
        colocation.setName(null);
        colocationService.create(colocation);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithNullAccount() throws Exception {
        Colocation colocation = colocationTF.create();
        colocation.setAccount(null);
        colocationService.create(colocation);
    }

    @Test
    public void testSearch() {
        Colocation colocation = colocationTF.createPersistent();
        Collection<ColocationTO> res = colocationService.search(colocation.getAccount().getId());
        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    public void testGet() {
        Colocation colocation = colocationTF.createPersistent();
        colocationTF.createPersistent(colocation.getAccount());

        ColocationSelector selector = new ColocationSelector();
        selector.setAccountIds(Arrays.asList(colocation.getAccount().getId()));
        PartialList<Colocation> colocations;

        currentUserRule.setPrincipal(userDefinitionFactory.ispManagerAllAccess1);
        colocations = colocationService.get(selector);
        assertEquals(0, colocations.size());

        currentUserRule.setPrincipal(userDefinitionFactory.internalMultipleAccountsAccess);
        colocations = colocationService.get(selector);
        assertEquals(0, colocations.size());

        currentUserRule.setPrincipal(userDefinitionFactory.internalAllAccess);
        colocations = colocationService.get(selector);
        assertEquals(2, colocations.size());
    }
}
