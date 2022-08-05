package com.foros.action.campaign;

import com.foros.action.WeekScheduleSet;
import com.foros.action.campaign.campaignGroup.CampaignGroupActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.campaign.Campaign;


public class ViewDeliveryScheduleAction extends CampaignGroupActionSupport {
    private Long id;

    @ReadOnly
    public String viewCampaign() {
        if (id != null) {
            Campaign campaign = campaignService.view(id);
            scheduleSet = new WeekScheduleSet(campaign.getCampaignSchedules());
            campaignScheduleSet = WeekScheduleSet.WHOLE_RANGE_SET;
        }
        return SUCCESS;
    }

    @ReadOnly
    public String viewCCG() {
        if (id != null) {
            campaignCreativeGroup = campaignCreativeGroupService.view(id);
            Campaign campaign = campaignService.view(campaignCreativeGroup.getCampaign().getId());

            if (campaign.getCampaignSchedules().isEmpty()) {
                campaignScheduleSet = WeekScheduleSet.WHOLE_RANGE_SET;
            } else {
                campaignScheduleSet = new WeekScheduleSet(campaign.getCampaignSchedules());
            }

            if (campaignCreativeGroup.isDeliveryScheduleFlag()) {
                scheduleSet = new WeekScheduleSet(campaignCreativeGroup.getCcgSchedules());
            } else {
                scheduleSet = campaignScheduleSet;
            }
        }
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
