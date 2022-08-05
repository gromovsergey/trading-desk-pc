package com.foros.session.site;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.PublisherAccount;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.site.Site;
import com.foros.session.account.AccountServiceBean;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.test.factory.SiteTestFactory;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class SiteRestrictionBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @Autowired
    private AccountServiceBean accountServiceBean;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private AccountTypeService atService;

    private Site site;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        site = siteTF.createPersistent((PublisherAccount) publisherAllAccess1.getUser().getAccount());

    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(advertiserAllAccess1, false);
        expectResult(ispManagerAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(ispAllAccess2, false);
    }

    @Test
    public void testCanView() throws Exception {
        Callable callCanView = new Callable("publisher_entity", "view") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canView(site);
            }
        };


        // normal account
        expectResult(internalAllAccess, true);
//        expectResult(internalUserAccountAccess, true);
        expectResult(internalUserAccountNoAccess, false);
        expectResult(internalMultipleAccountsAccess, true);
        expectResult(internalMultipleAccountsNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess1, true);
        doCheck(callCanView);

        // deleted site
        expectResult(publisherAllAccess1, false);
        siteTF.delete(site.getId());
        doCheck(callCanView);

        // deleted account
        accountServiceBean.delete(site.getAccount().getId());
        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("publisher_entity", "edit") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canUpdate(site);
            }
        };

        // normal
        expectResult(internalAllAccess, true);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess1, true);

        doCheck(callCanUpdate);

        // deleted colo
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUndelete() throws Exception {
        Callable callCanUndelete = new Callable("publisher_entity", "undelete") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canUndelete(site);
            }
        };

        // normal
        expectResult(internalAllAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(callCanUndelete);

        // deleted colo
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, true);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess1, false);

        doCheck(callCanUndelete);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("publisher_entity", "create") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canCreate(site.getAccount());
            }
        };

        // normal
        expectResult(internalAllAccess, true);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess1, true);

        doCheck(callCanCreate);

        // deleted account
        accountServiceBean.delete(site.getAccount().getId());

        expectResult(internalAllAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanViewCreativesApproval() throws Exception{
        site.getAccount().getAccountType().setAdvExclusions(AdvExclusionsType.SITE_LEVEL);
        atService.update(site.getAccount().getAccountType());

        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canViewCreativesApproval(site);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess1, true);

        // remove creative exclusion flag
        site.getAccount().getAccountType().setAdvExclusions(AdvExclusionsType.DISABLED);
        atService.update(site.getAccount().getAccountType());

        expectResult(internalAllAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(callable);
    }

    @Test
    public void testCanReviewCreatives() throws Exception{
        site.getAccount().getAccountType().setAdvExclusions(AdvExclusionsType.SITE_LEVEL);
        atService.update(site.getAccount().getAccountType());

        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canReviewCreatives(site.getId());
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        site.getAccount().getAccountType().setAdvExclusions(AdvExclusionsType.DISABLED);
        atService.update(site.getAccount().getAccountType());

        expectResult(internalAllAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherAllAccess1, false);

        doCheck(callable);
    }
}
