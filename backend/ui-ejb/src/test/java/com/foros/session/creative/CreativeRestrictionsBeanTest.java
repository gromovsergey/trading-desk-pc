package com.foros.session.creative;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CreativeRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountService accountServiceBean;

    @Autowired
    private DisplayCreativeService displayCreativeService;

    @Autowired
    private AdvertiserEntityRestrictions restrictionsBean;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    private Creative creative;
    private Account account;
    private UserDefinition advertiserAllAccessLocal;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();

        advertiserAllAccessLocal = userDefinitionFactory.create(AccountRole.ADVERTISER, PermissionsSet.ALL).managedBy(advertiserManagerAllAccess1);
        advertiserAllAccessLocal.setCreativeTemplate(template);
        advertiserAllAccessLocal.setCreativeSize(size);
        advertiserAllAccessLocal.addCcgType(CCGType.DISPLAY, TGTType.CHANNEL, RateType.CPM);

        account = advertiserAllAccessLocal.getUser().getAccount();
        creative = getCreative((AdvertiserAccount)account, template, size);
    }

    @Override
    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(advertiserAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("advertiser_entity", "view") {
            @Override
            public boolean call() {
                return restrictionsBean.canView(creative);
            }
        };

        // normal account
        setUpAllExpectations(true);
        doCheck(callCanView);

        displayCreativeTF.delete(creative.getId());

        expectResult(advertiserAllAccessLocal, false);
        doCheck(callCanView);

        // deleted account
        accountServiceBean.delete(account.getId());

        doCheck(callCanView);
    }

    @Test
    public void testUpdate() throws Exception {
        Callable callCanUpdate = new Callable("advertiser_entity", "edit") {
            @Override
            public boolean call() {
                return restrictionsBean.canUpdate(creative);
            }
        };

        // normal
        setUpAllExpectations(true);
        doCheck(callCanUpdate);

        // deleted creative
        displayCreativeTF.delete(creative.getId());

        setUpAllExpectations(false);
        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("advertiser_entity", "create") {
            @Override
            public boolean call() {
                return restrictionsBean.canCreate(account);
            }
        };

        // normal
        setUpAllExpectations(true);

        doCheck(callCanCreate);

        // deleted account
        accountServiceBean.delete(account.getId());

        setUpAllExpectations(false);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanUndelete() throws Exception {
        Callable callCanUndelete = new ContextCall("advertiser_entity", "undelete") {
            @Override
            public void call(ValidationContext context) {
                restrictionsBean.canUndelete(context, creative);
            }
        };

        // normal
        setUpAllExpectations(false);
        doCheck(callCanUndelete);

        creative.setStatus(Status.DELETED);

        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        doCheck(callCanUndelete);
    }

    @Test
    public void testCanApprove() throws Exception {
        creative.setQaStatus(ApproveStatus.HOLD);
        Callable callCanApprove = new ContextCall("advertiser_entity", "approve") {
            @Override
            public void call(ValidationContext context) {
                restrictionsBean.canApprove(context, creative);
            }
        };

        // normal
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserAllAccessLocal, false);
        doCheck(callCanApprove);

        // deleted account
        accountServiceBean.delete(account.getId());
        boolean result = false;
        setUpAllExpectations(result);
        doCheck(callCanApprove);
    }

    @Test
    public void testCanDelete() throws Exception {
        Callable callCanDelete = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                restrictionsBean.canDelete(context, creative);
            }
        };

        // normal
        setUpAllExpectations(true);
        doCheck(callCanDelete);

        // deleted creative
        displayCreativeTF.delete(creative.getId());

        setUpAllExpectations(false);
        doCheck(callCanDelete);
    }

    @Test
    public void testCanActivate() throws Exception {
        Callable callCanActivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                restrictionsBean.canActivate(context, creative);
            }
        };

        // normal
        creative.setStatus(Status.INACTIVE);
        setUpAllExpectations(true);
        doCheck(callCanActivate);

        // active
        creative.setStatus(Status.ACTIVE);
        setUpAllExpectations(false);
        doCheck(callCanActivate);

        // deleted creative
        creative.setStatus(Status.INACTIVE);
        displayCreativeTF.delete(creative.getId());
        doCheck(callCanActivate);
    }

    @Test
    public void testCanInactivate() throws Exception {
        Callable callCanInactivate = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                restrictionsBean.canInactivate(context, creative);
            }
        };

        // normal
        creative.setStatus(Status.ACTIVE);
        setUpAllExpectations(true);
        doCheck(callCanInactivate);

        // inactive
        creative.setStatus(Status.INACTIVE);
        setUpAllExpectations(false);
        doCheck(callCanInactivate);

        // deleted creative
        creative.setStatus(Status.ACTIVE);
        displayCreativeTF.delete(creative.getId());
        doCheck(callCanInactivate);
    }

    @Test
    public void testCanDecline() throws Exception {
        Callable callCanDecline = new ContextCall("advertiser_entity", "approve") {
            @Override
            public void call(ValidationContext context) {
                restrictionsBean.canDecline(context, creative);
            }
        };

        // normal
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserAllAccessLocal, false);
        doCheck(callCanDecline);

        // deleted account
        accountServiceBean.delete(account.getId());
        setUpAllExpectations(false);
        doCheck(callCanDecline);
    }

    private void setUpAllExpectations(boolean result) {
        expectResult(internalAllAccess, result);
        expectResult(advertiserManagerAllAccess1, result);
        expectResult(advertiserAllAccessLocal, result);
    }

    private Creative getCreative(AdvertiserAccount account, CreativeTemplate template, CreativeSize size) {
        Creative creative = displayCreativeTF.create(account, template, size);
        displayCreativeTF.persist(creative);
        return creative;
    }
}
