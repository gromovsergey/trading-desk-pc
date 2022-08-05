package com.foros.action.xml.sundry;

import com.foros.action.WeekScheduleSet;
import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.session.campaign.CampaignService;
import com.foros.util.StringUtil;

import javax.ejb.EJB;

public class AffectedCCGForDeliveryXmlAction extends AbstractXmlAction<String> {

    @EJB
    private CampaignService campaignService;

    private Long id;

    private WeekScheduleSet scheduleSet = new WeekScheduleSet() ;

    @Override
    protected String generateModel() throws ProcessException {
        return StringUtil.join(campaignService.getAffectedCCGForCampaignDelivery(id, scheduleSet.getSchedules()));
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setScheduleSet(WeekScheduleSet scheduleSet) {
        if (scheduleSet != null) {
            this.scheduleSet = scheduleSet;
        }
    }
}
