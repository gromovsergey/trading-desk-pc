package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.ChartStats;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class CCGStatsXmlAction extends AbstractXmlAction<ChartStats> {
    private String ccgId;
    private String x;
    private String y1;
    private String y2;
    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @RequiredFieldValidator(key = "errors.required", message = "ccgId")
    public String getCcgId() {
        return ccgId;
    }

    public void setCcgId(String ccgId) {
        this.ccgId = ccgId;
    }

    @RequiredFieldValidator(key = "errors.required", message = "x")
    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    @RequiredFieldValidator(key = "errors.required", message = "y1")
    public String getY1() {
        return y1;
    }

    public void setY1(String y1) {
        this.y1 = y1;
    }

    @RequiredFieldValidator(key = "errors.required", message = "y2")
    public String getY2() {
        return y2;
    }

    public void setY2(String y2) {
        this.y2 = y2;
    }

    @Override
    protected ChartStats generateModel() throws ProcessException {
        return campaignCreativeGroupService.getChartStats(Long.valueOf(ccgId), x, y1, y2);
    }
}
