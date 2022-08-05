package com.foros.session.colocation;

import com.foros.model.Status;
import com.foros.session.bulk.Paging;

import java.util.List;

public class ColocationSelector {
    private Paging paging = new Paging();
    private String name;
    private List<Long> colocationIds;
    private List<Long> accountIds;
    private List<Status> statuses;


    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getColocationIds() {
        return colocationIds;
    }

    public void setColocationIds(List<Long> colocationIds) {
        this.colocationIds = colocationIds;
    }

    public List<Long> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<Long> accountIds) {
        this.accountIds = accountIds;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }
}
