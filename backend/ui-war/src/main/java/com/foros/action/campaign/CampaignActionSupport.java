package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.action.WeekScheduleSet;
import com.foros.model.campaign.Campaign;
import com.foros.session.campaign.CampaignService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class CampaignActionSupport extends BaseActionSupport implements ModelDriven<Campaign> {
    @EJB
    protected CampaignService campaignService;

    protected Campaign campaign;

    protected boolean isDeliverySchedule;

    private  WeekScheduleSet scheduleSet;

    @Override
    public Campaign getModel() {
        return campaign;
    }

    public WeekScheduleSet getScheduleSet() {
        if (scheduleSet == null) {
            scheduleSet = new WeekScheduleSet(campaign.getCampaignSchedules());
        }
        return scheduleSet;
    }

    public void setScheduleSet(WeekScheduleSet scheduleSet) {
        this.scheduleSet = scheduleSet;
    }

    public void setDeliverySchedule(boolean deliverySchedule) {
        isDeliverySchedule = deliverySchedule;
    }

    public boolean isDeliverySchedule() {
        return isDeliverySchedule;
    }
}