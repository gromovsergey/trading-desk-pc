package com.foros.test.factory;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignSchedule;

public abstract class CampaignTestFactory extends TestFactory<Campaign> {
    public void addSchedule(Campaign campaign, Long timeFrom, Long timeTo) {
        CampaignSchedule campaignSchedule = new CampaignSchedule();
        campaignSchedule.setCampaign(campaign);
        campaignSchedule.setTimeFrom(timeFrom);
        campaignSchedule.setTimeTo(timeTo);

        campaign.getCampaignSchedules().add(campaignSchedule);
    }
}
