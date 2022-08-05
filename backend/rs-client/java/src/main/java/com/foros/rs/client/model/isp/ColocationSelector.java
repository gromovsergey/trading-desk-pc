package com.foros.rs.client.model.isp;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

public class ColocationSelector implements PagingSelectorContainer {

    @QueryParameter("colocation.name")
    private String name;

    @QueryParameter("colocation.ids")
    private List<Long> colocationIds;

    @QueryParameter("account.ids")
    private List<Long> accountIds;

    @QueryParameter("colocation.statuses")
    private List<Status> colocationStatuses;

    @QueryParameter("paging")
    private PagingSelector paging;

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

    public List<Status> getColocationStatuses() {
        return colocationStatuses;
    }

    public void setColocationStatuses(List<Status> colocationStatuses) {
        this.colocationStatuses = colocationStatuses;
    }

    @Override
    public PagingSelector getPaging() {
        return paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }
}
