package com.foros.session.reporting.channeltriggers;

import com.foros.model.channel.trigger.TriggerType;
import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SizeConstraint;

import java.util.ArrayList;
import java.util.List;

public class ChannelTriggersReportParameters extends DatedReportParameters {
    @RequiredConstraint
    private Long accountId;

    private TriggerType mode = null;

    @SizeConstraint(min = 1, message = "errors.field.channelTriggers.channelsEmpty")
    @RequiredConstraint(message = "errors.field.channelTriggers.channelsEmpty")
    private List<Long> channelIds;

    private ColumnOrderTO urlsSortColumn;
    private ColumnOrderTO pageKeywordsSortColumn;
    private ColumnOrderTO searchKeywordsSortColumn;
    private ColumnOrderTO urlKeywordsSortColumn;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        // we can't use XWorkList for store channel ids,
        // because it can't be serialized, just copy elements
        if (channelIds != null) {
            this.channelIds = new ArrayList<Long>(channelIds);
        } else {
            this.channelIds = null;
        }
    }

    public boolean isNeedUrls() {
        return mode == null || mode == TriggerType.URL;
    }

    public boolean isNeedPageKeywords() {
        return mode == null || mode == TriggerType.PAGE_KEYWORD;
    }

    public boolean isNeedSearchKeywords() {
        return mode == null || mode == TriggerType.SEARCH_KEYWORD;
    }

    public boolean isNeedUrlKeywords() {
        return mode == null || mode == TriggerType.URL_KEYWORD;
    }

    public ColumnOrderTO getUrlsSortColumn() {
        return urlsSortColumn;
    }

    public void setUrlsSortColumn(ColumnOrderTO urlsSortColumn) {
        this.urlsSortColumn = urlsSortColumn;
    }

    public ColumnOrderTO getPageKeywordsSortColumn() {
        return pageKeywordsSortColumn;
    }

    public void setPageKeywordsSortColumn(ColumnOrderTO pageKeywordsSortColumn) {
        this.pageKeywordsSortColumn = pageKeywordsSortColumn;
    }

    public ColumnOrderTO getSearchKeywordsSortColumn() {
        return searchKeywordsSortColumn;
    }

    public void setSearchKeywordsSortColumn(ColumnOrderTO searchKeywordsSortColumn) {
        this.searchKeywordsSortColumn = searchKeywordsSortColumn;
    }

    public ColumnOrderTO getUrlKeywordsSortColumn() {
        return urlKeywordsSortColumn;
    }

    public void setUrlKeywordsSortColumn(ColumnOrderTO urlKeywordsSortColumn) {
        this.urlKeywordsSortColumn = urlKeywordsSortColumn;
    }

    public void setMode(TriggerType mode) {
        this.mode = mode;
    }

    public void setTriggerType(String type) {
        this.mode = TriggerType.valueOf(type);
    }
}
