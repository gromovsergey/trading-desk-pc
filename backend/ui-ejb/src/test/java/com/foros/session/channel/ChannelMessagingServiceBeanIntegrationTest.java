package com.foros.session.channel;


import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.CmpAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.security.User;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.session.channel.service.ChannelMessagingService;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;

import group.Db;
import java.math.BigDecimal;
import java.util.Collection;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ChannelMessagingServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CmpAccountTestFactory cmpAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private DisplayCampaignTestFactory campaignTF;

    @Autowired
    private ChannelMessagingService channelMessagingService;

    @Autowired
    private AdvertisingFinanceServiceMock advertisingFinanceService;

    @Test
    public void testFindChannelAssociationsUsersForAdvertiserAccount() throws Exception {
        CmpAccount cmpAccount = cmpAccountTF.createPersistent();
        BehavioralChannel behavioralChannel = behavioralChannelTF.createPersistent(cmpAccount);
        behavioralChannelTF.submitToCmp(behavioralChannel);
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        commitChanges();
        clearContext();

        updateFinance(advertiserAccount); // make the account live

        displayCCGTF.createPersistentCCGWithChannelTarget(behavioralChannel, advertiserAccount);
        Collection<User> users = channelMessagingService.findAssociatedUsers(behavioralChannel.getId());

        assertEquals("Invalid users", 1, users.size());
    }

    @Test
    public void testFindChannelAssociationsUsersForInactive() throws Exception {
        CmpAccount cmpAccount = cmpAccountTF.createPersistent();
        BehavioralChannel behavioralChannel = behavioralChannelTF.createPersistent(cmpAccount);
        behavioralChannelTF.submitToCmp(behavioralChannel);
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();

        Campaign campaign = campaignTF.createPersistent(advertiserAccount);
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent(campaign);
        entityManager.flush();
        ccg.setChannelTarget(ChannelTarget.TARGETED);
        ccg.setChannel(behavioralChannel);
        displayCCGTF.update(ccg);

        getEntityManager().flush();
        getEntityManager().clear();

        Collection<User> users = channelMessagingService.findAssociatedUsers(behavioralChannel.getId());
        assertEquals("Invalid users", 0, users.size());
    }

    private void updateFinance(AdvertiserAccount account) {
        AdvertisingFinancialSettings settings = account.getFinancialSettings();
        if (account.getAgency() != null && !account.isFinancialFieldsPresent()) {
            settings = account.getAgency().getFinancialSettings();
        }
        settings.getData().setPrepaidAmount(new BigDecimal(4.1));
        advertisingFinanceService.updateFinance(settings);
        commitChanges();
    }
}
