package com.foros.session.reporting.custom;

import com.foros.session.reporting.OutputType;
import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.NotEmptyConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CustomReportParameters extends DatedReportParameters {

    private TimeZone timeZone;
    private String countryCode;

    private Long agencyId;
    private Long advertiserId;
    private Long campaignId;
    private Long campaignCreativeId;

    private Long ispId;
    private Long colocationId;

    private Long publisherId;
    private Long siteId;

    private Long sizeId;

    @NotEmptyConstraint
    private List<String> outputColumns = new ArrayList<String>();

    @NotEmptyConstraint
    private List<String> metricsColumns = new ArrayList<String>();

    private ColumnOrderTO sortColumn;
    private DetailLevel detailLevel;
    private String outputCurrencyCode = "USD";
    private OutputType outputType;

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getCampaignCreativeId() {
        return campaignCreativeId;
    }

    public void setCampaignCreativeId(Long campaignCreativeId) {
        this.campaignCreativeId = campaignCreativeId;
    }

    public Long getIspId() {
        return ispId;
    }

    public void setIspId(Long ispId) {
        this.ispId = ispId;
    }

    public Long getColocationId() {
        return colocationId;
    }

    public void setColocationId(Long colocationId) {
        this.colocationId = colocationId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public List<String> getOutputColumns() {
        return outputColumns;
    }

    public void setOutputColumns(List<String> outputColumns) {
        this.outputColumns = outputColumns;
    }

    public List<String> getMetricsColumns() {
        return metricsColumns;
    }

    public void setMetricsColumns(List<String> metricsColumns) {
        this.metricsColumns = metricsColumns;
    }

    public ColumnOrderTO getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(ColumnOrderTO sortColumn) {
        this.sortColumn = sortColumn;
    }

    public DetailLevel getDetailLevel() {
        return detailLevel;
    }

    public void setDetailLevel(DetailLevel detailLevel) {
        this.detailLevel = detailLevel;
    }

    public String getOutputCurrencyCode() {
        return outputCurrencyCode;
    }

    public void setOutputCurrencyCode(String outputCurrencyCode) {
        this.outputCurrencyCode = outputCurrencyCode;
    }

    public boolean isOutputInAccountCurrency() {
        return StringUtil.isPropertyEmpty(outputCurrencyCode);
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

}
