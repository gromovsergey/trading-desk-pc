package com.foros.session.channel.service;

import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.UserTestFactory;

import java.math.BigDecimal;

import group.Db;
import group.Restriction;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class CmpChannelRestrictionsBeanTest extends AbstractChannelRestrictionsBeanTest {

    private BehavioralChannel behavioralChannelNotCmp;

    private ExpressionChannel expressionChannelNotPub;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    public UserTestFactory userTF;

    @Autowired
    private AdvertisingFinanceService advertisingFinanceService;

    @Override @Before public void setUp() throws Exception {
        super.setUp();

        // public
        Account account = cmpAllAccess1.getUser().getAccount();
        account.getCountry().setLowChannelThreshold(0L); // to create live channels

        expressionChannel = expressionChannelTF.createPersistent(account);
        expressionChannelTF.makePublic(expressionChannel);

        // cmp
        behavioralChannel = behavioralChannelTF.createPersistent(account);
        behavioralChannelTF.submitToCmp(behavioralChannel);

        behavioralChannelNotCmp = behavioralChannelTF.createPersistent(account);
        behavioralChannelTF.makeLive(behavioralChannelNotCmp);

        expressionChannelNotPub = expressionChannelTF.createPersistent(account);

        entityManager.flush();

        behavioralChannel = behavioralChannelTF.refresh(behavioralChannel);
        behavioralChannelNotCmp = behavioralChannelTF.refresh(behavioralChannelNotCmp);
        expressionChannel= expressionChannelTF.refresh(expressionChannel);
        expressionChannelNotPub = expressionChannelTF.refresh(expressionChannelNotPub);

        commitChanges();
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();

        expectResult(ispAllAccess2, false);
        expectResult(ispAllAccess2, false);
    }

    @Test@Ignore
    public void testSubmitToCmp() throws Exception {
        Callable canSubmitToCmp = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canSubmitToCmp(behavioralChannelNotCmp);
            }
        };

        expectResult(cmpAllAccess1, true);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canSubmitToCmp);

    }

    @Test
    public void testMakePublic() throws Exception {
        Callable canMakePublic = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canMakePublic(expressionChannelNotPub);
            }
        };

        expectResult(cmpAllAccess1, true);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canMakePublic);

    }

    @Test
    public void testMakePublicUnalowable() throws Exception {
        Callable canMakePublic = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canMakePublic(expressionChannel);
            }
        };

        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canMakePublic);

    }

    @Test
    public void testView() {
        Callable canViewPublic = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(expressionChannel);
            }
        };
        Callable canViewPublicContent = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canViewContent(expressionChannel);
            }
        };


        Callable canViewCmp = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(behavioralChannel);
            }
        };

        expectResult(cmpAllAccess1, true);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);

        doCheck(canViewCmp);
        doCheck(canViewPublic);
        doCheck(canViewPublicContent);

        // deleted
        expressionChannelTF.delete(expressionChannel);

        expectResult(cmpAllAccess1, false);
        expectResult(advertiserAllAccess1, false);

        doCheck(canViewPublic);
        doCheck(canViewPublicContent);
    }

    @Test
    public void testCanViewCmpContent() {
        Callable canViewContent = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canViewContent(behavioralChannel);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(cmpAllAccess1, true);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canViewContent);

        // deleted
        behavioralChannel.setStatus(Status.DELETED);
        getEntityManager().flush();

        expectResult(cmpAllAccess1, false);

        doCheck(canViewContent);
    }

    @Test
    public void testContactCMPChannelUser() throws Exception {
        // link advertiser with cmp channel
        createCCG(behavioralChannel);

        behavioralChannel.setMessageSent(0);
        behavioralChannel.getAccount().setMessageSent(0);
        Callable canSendMessage = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canContactCMPChannelUser(behavioralChannel);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(cmpAllAccess1, true);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canSendMessage);

        resetExpectationsToDefault();

        // channel not used by any advertiser
        canSendMessage = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canContactCMPChannelUser(expressionChannel);
            }
        };

        expectResult(internalAllAccess, false);
        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canSendMessage);
    }

    @Test
    public void testContactCMPChannelUser2() {
        // non-cmp channel case
        resetExpectationsToDefault();

        final Channel internalChannel = behavioralChannelTF.createPersistent(internalAllAccess.getUser().getAccount());
        createCCG(internalChannel);

        behavioralChannel.setMessageSent(0);
        behavioralChannel.getAccount().setMessageSent(0);
        Callable canSendMessage = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canContactCMPChannelUser(internalChannel);
            }
        };

        expectResult(internalAllAccess, false);
        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);

        doCheck(canSendMessage);
    }

    public void createCCG(Channel channel) {
        AdvertiserAccount account = (AdvertiserAccount) advertiserAllAccess1.getUser().getAccount();
        advertiserAccountTF.persist(account);
        getEntityManager().flush();
        getEntityManager().clear();

        // to make account live
        updateFinance(account);
        displayCCGTF.createPersistentCCGWithChannelTarget(channel, account);
    }

    private void updateFinance(AdvertiserAccount account) {
        AdvertisingFinancialSettings settings = account.getFinancialSettings();
        if(account.getAgency() != null && !account.isFinancialFieldsPresent()){
            settings = account.getAgency().getFinancialSettings();
        }
        settings.getData().setPrepaidAmount(new BigDecimal(4.1));
        advertisingFinanceService.updateFinance(settings);
        entityManager.flush();
    }
}
