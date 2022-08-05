package com.foros.session.site;

import group.Db;
import group.Restriction;

import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.test.factory.WDTagTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.PublisherAccount;
import com.foros.model.security.AccountType;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.WDTag;
import com.foros.test.factory.CreativeSizeTestFactory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class TagRestrictionBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private TagsService tagService;

    @Autowired
    private AccountTypeService accountTypeService;

    @Autowired
    private WDTagTestFactory wdTagTF;

    private Tag tag;
    private Site site;
    private WDTag wdTag;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        site = siteTF.create((PublisherAccount)publisherAllAccess1.getUser().getAccount());
        site.getAccount().getAccountType().getCreativeSizes().add(creativeSizeTF.createPersistent());
        accountTypeService.update(site.getAccount().getAccountType());
        siteTF.persist(site);
        tag = tagTF.createPersistent(site);
        wdTag = wdTagTF.create(site);

    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(advertiserAllAccess1, false);
        expectResult(ispManagerAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(ispAllAccess2, false);
    }

    @Test
    public void testCanCreate() {
        Callable callCanCreate = new Callable("publisher_entity", "create") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canCreate(site);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        doCheck(callCanCreate);

        // delete site, then check create tag access
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanView() {
        Callable callCanView = new Callable("publisher_entity", "view") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canView(tag);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        doCheck(callCanView);

        // delete tag, then check view tag access
        tagService.delete(tag.getId());

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanView);

        // undelete tag, delete site, check view tag access
        tagService.undelete(tag.getId());
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() {
        Callable callCanUpdate = new Callable("publisher_entity", "edit") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canUpdate(tag);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        doCheck(callCanUpdate);

        // delete tag, then check update tag access
        tagService.delete(tag.getId());

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanUpdate);

        // undelete tag, delete site, check update tag access
        tagService.undelete(tag.getId());
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanDelete() {
        Callable callCanDelete = new Callable("publisher_entity", "edit") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canDelete(tag);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        doCheck(callCanDelete);

        // delete a deleted tag, check delete tag access
        tagService.delete(tag.getId());

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanDelete);

        // undelete tag
        tagService.undelete(tag.getId());

        // delete site, then check delete tag access
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanDelete);

    }

    @Test
    public void testCanUndelete() {
        // check undelete access on undeleted tag
        Callable callCanUndelete = new Callable("publisher_entity", "undelete") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canUndelete(tag);
            }
        };

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        // delete tag, then check undelete tag access
        tagService.delete(tag.getId());

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanUndelete);

        // now delete site, and check undelete tag access
        siteTF.delete(site.getId());

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanUndelete);
    }

    @Test
    public void testCanCreateWDTag() {
        AccountType at = site.getAccount().getAccountType();
        // make sure account type does not support wd tags
        at.setWdTagsFlag(false);
        accountTypeService.update(at);

        Callable callCanCreate = new Callable("publisher_entity", "create") {
            @Override
            public boolean call() {
                return publisherEntityRestrictions.canCreateWDTag(wdTag);
            }
        };

        expectResult(internalAllAccess, false);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherNoAccess, false);

        doCheck(callCanCreate);

        // change account type to allow wd tags
        at = site.getAccount().getAccountType();

        at.setWdTagsFlag(true);
        accountTypeService.update(at);

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherNoAccess, false);

        doCheck(callCanCreate);
    }
}
