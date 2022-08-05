package com.foros.action.campaign.campaignGroup;

import javax.persistence.EntityNotFoundException;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.campaign.ccg.expressionPerformance.ExpressionPerformanceReportParameters;
import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.util.DateRangeSelect;
import com.foros.util.context.RequestContexts;

public class ViewExpressionPerformanceAction extends CampaignGroupActionSupport implements RequestContextsAware, BreadcrumbsSupport {
    private Long id;

    private String fromDateEP;
    private String toDateEP;
    private String fastChangeIdEP = "TOT";

    private ExpressionPerformanceReportParameters parameters = new ExpressionPerformanceReportParameters();
    private SimpleReportData reportData;

    @ReadOnly
    @Restrict(restriction = "CreativeGroup.viewExpressionPerformance", parameters = "find('CampaignCreativeGroup', #target.id)")
    public String view() {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        if (campaignCreativeGroup.getChannel() == null) {
            throw new EntityNotFoundException("Channel with id = null not found");
        }
        return SUCCESS;
    }

    @ReadOnly
    public String expressionPerformanceStats() {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        reportData = loadExpressionPerformanceStats();
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromDateEP() {
        return fromDateEP;
    }

    public void setFromDateEP(String fromDateEP) {
        this.fromDateEP = fromDateEP;
    }

    public String getToDateEP() {
        return toDateEP;
    }

    public void setToDateEP(String toDateEP) {
        this.toDateEP = toDateEP;
    }

    public String getFastChangeIdEP() {
        return fastChangeIdEP;
    }

    public void setFastChangeIdEP(String fastChangeIdEP) {
        this.fastChangeIdEP = fastChangeIdEP;
    }

    public ColumnOrderTO getSortColumn() {
        return parameters.getSortColumn();
    }

    public void setSortColumn(ColumnOrderTO sortColumn) {
        parameters.setSortColumn(sortColumn);
    }

    public SimpleReportData getReportData() {
        if (reportData == null) {
            reportData = loadExpressionPerformanceStats();
        }
        return reportData;
    }

    private SimpleReportData loadExpressionPerformanceStats() {
        DateRangeSelect dateRangeSelect = new DateRangeSelect(fastChangeIdEP, fromDateEP, toDateEP);
        DateRange dateRange = new DateRange(dateRangeSelect.getFromDate(), dateRangeSelect.getToDate());

        parameters.setCcgId(id);
        parameters.setDateRange(dateRange);
        reportData = campaignCreativeGroupService.getExpressionPerformanceReportData(parameters);

        return reportData;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(campaignCreativeGroup.getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(campaignCreativeGroup.getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(campaignCreativeGroup))
                .add("ccg.targeting.expressionPerformance");
    }
}
