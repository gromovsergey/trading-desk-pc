package com.foros.session.reporting.channelInventoryForecast;

import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SizeConstraint;

import java.util.Collection;
import java.util.LinkedHashSet;

public class ChannelInventoryForecastReportParameters {

    @RequiredConstraint
    private Long accountId;

    @RequiredConstraint
    private ChannelFilter channelFilter = ChannelFilter.IDS;

    @RequiredConstraint
    private DateRangeFilter dateRange = DateRangeFilter.DR_30_DAYS_AVERAGE;

    @SizeConstraint(min = 1, message = "report.actions.noSizesSelected")
    @RequiredConstraint(message = "report.actions.noSizesSelected")
    private Collection<Long> creativeSizeIds = new LinkedHashSet<Long>();

    @RequiredConstraint(message = "errors.field.channelTriggers.channelsEmpty")
    private Collection<Long> channelIds = new LinkedHashSet<Long>();

    @SizeConstraint(max = 100)
    private Integer percentile = 80;

    private String targetCurrencyCode;

    public enum DetailLevelType {
        FULL,
        PERCENTILE;
    }

    private DetailLevelType detailLevelType = DetailLevelType.FULL;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public ChannelFilter getChannelFilter() {
        return channelFilter;
    }

    public void setChannelFilter(ChannelFilter channelFilter) {
        this.channelFilter = channelFilter;
    }

    public DateRangeFilter getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRangeFilter dateRange) {
        this.dateRange = dateRange;
    }

    public Integer getPercentile() {
        return percentile;
    }

    public void setPercentile(Integer percentile) {
        this.percentile = percentile;
    }

    public DetailLevelType getDetailLevelType() {
        return detailLevelType;
    }

    public String getDetailLevel() {
        return detailLevelType.name();
    }

    public void setDetailLevel(String detailLevel) {
        detailLevelType = DetailLevelType.FULL.name().equals(detailLevel) ? DetailLevelType.FULL : DetailLevelType.PERCENTILE;
    }

    public Collection<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(Collection<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Collection<Long> getCreativeSizeIds() {
        return creativeSizeIds;
    }

    public void setCreativeSizeIds(Collection<Long> creativeSizeIds) {
        this.creativeSizeIds = creativeSizeIds;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public enum ChannelFilter {
        LIVE,
        LINKED,
        IDS;

        public String getKey() {
            return "channel.inventoryForecast.filter." + name();
        }
    }

    public enum DateRangeFilter {
        DR_30_DAYS_AVERAGE,
        DR_YESTERDAY;

        public String getKey() {
            return "channel.inventoryForecast.dateRange." + name();
        }
    }
}
