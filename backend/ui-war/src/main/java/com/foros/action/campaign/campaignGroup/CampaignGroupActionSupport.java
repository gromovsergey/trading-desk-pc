package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.WeekScheduleSet;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.campaign.CampaignCreativeGroupService;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaign.ScheduleHelper;

import javax.ejb.EJB;

public abstract class CampaignGroupActionSupport extends BaseActionSupport implements ModelDriven<CampaignCreativeGroup> {
    @EJB
    protected CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    protected CampaignService campaignService;

    protected CampaignCreativeGroup campaignCreativeGroup;

    protected WeekScheduleSet scheduleSet;

    protected WeekScheduleSet campaignScheduleSet;

    @Override
    public CampaignCreativeGroup getModel() {
        return campaignCreativeGroup;
    }

    protected boolean checkGroupType(CCGType type) {
        String actionName = getContextName();

        if (actionName.endsWith("Display")) {
            return type == CCGType.DISPLAY;
        } else if (actionName.endsWith("Text")) {
            return type == CCGType.TEXT;
        }

        return false;
    }

    public WeekScheduleSet getScheduleSet() {
        if (scheduleSet == null) {
            scheduleSet = new WeekScheduleSet(campaignCreativeGroup.getCcgSchedules());
        }
        return scheduleSet;
    }

    public void setScheduleSet(WeekScheduleSet scheduleSet) {
        this.scheduleSet = scheduleSet;
    }

    public WeekScheduleSet getCampaignScheduleSet() {
        if (campaignScheduleSet == null) {
            campaignScheduleSet = new WeekScheduleSet(campaignService.find(campaignCreativeGroup.getCampaign().getId()).getCampaignSchedules());
        }
        return campaignScheduleSet;
    }

    public boolean isOutsideCampaignSchedule() {
        return !getCampaignScheduleSet().isEmpty() && !ScheduleHelper.containsChildSchedule(getCampaignScheduleSet().getSchedules(), getScheduleSet().getSchedules());
    }
}
