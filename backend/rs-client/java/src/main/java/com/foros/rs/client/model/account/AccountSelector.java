package com.foros.rs.client.model.account;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class AccountSelector implements PagingSelectorContainer {

    @QueryParameter("ids")
    private List<Long> accountIds;

    @QueryParameter("roles")
    private List<AccountRole> roles;

    @QueryParameter("statuses")
    private List<Status> statuses;

    @QueryParameter("countryCodes")
    private List<String> countryCodes;

    @QueryParameter("paging")
    private PagingSelector paging;

    public List<Long> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<Long> accountIds) {
        this.accountIds = accountIds;
    }

    public List<AccountRole> getRoles() {
        return roles;
    }

    public void setRoles(List<AccountRole> roles) {
        this.roles = roles;
    }

    public List<com.foros.rs.client.model.entity.Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<com.foros.rs.client.model.entity.Status> statuses) {
        this.statuses = statuses;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = countryCodes;
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
