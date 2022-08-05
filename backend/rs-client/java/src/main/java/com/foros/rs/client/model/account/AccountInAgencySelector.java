package com.foros.rs.client.model.account;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class AccountInAgencySelector implements PagingSelectorContainer {

    @QueryParameter("agency.id")
    private Long agencyId;

    @QueryParameter("statuses")
    private List<Status> statuses;

    @QueryParameter("paging")
    private PagingSelector paging;

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
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
