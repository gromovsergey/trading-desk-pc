package com.foros.session.security;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.campaign.Campaign;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class AuditLogRestrictionsTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AuditLogRestrictions auditLogRestrictions;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private TextCampaignTestFactory campaignTF;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
    }

    @Test
    public void testCanViewCampaign() throws Exception {
        final Campaign campaign = campaignTF.createPersistent();

        Callable callCanView = new Callable("advertiser_entity", "view") {
            @Override
            public boolean call() {
                return auditLogRestrictions.canView(campaign);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanViewChannel() throws Exception {
        final BehavioralChannel channel =  behavioralChannelTF.createPersistent();
        Callable callCanView = new Callable("advertiser_advertising_channel", "view") {
            @Override
            public boolean call() {
                return auditLogRestrictions.canView(channel);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanViewUserRole() throws Exception {
        Callable callCanView = new Callable("userRole", "view") {
            @Override
            public boolean call() {
                return auditLogRestrictions.canView(ObjectType.UserRole, ActionType.UPDATE, null);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }
}
