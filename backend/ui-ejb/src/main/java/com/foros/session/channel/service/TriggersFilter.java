package com.foros.session.channel.service;

import com.foros.model.channel.trigger.TriggerType;

public class TriggersFilter {
    private Long page = 1L;

    private char qaStatus = 'A';
    private TriggerType triggerType;

    private String sortKey;
    private String sortOrder;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public int getPageSize() {
        return 100;
    }

    public char getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(char qaStatus) {
        this.qaStatus = qaStatus;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
