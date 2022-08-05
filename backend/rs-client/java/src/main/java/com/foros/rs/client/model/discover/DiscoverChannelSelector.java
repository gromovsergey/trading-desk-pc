package com.foros.rs.client.model.discover;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;
import com.foros.rs.client.model.operation.PagingSelector;

import java.lang.Long;
import java.lang.String;
import java.util.List;


@QueryEntity
public class DiscoverChannelSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("account.ids")
    private List<Long> accountIds;

    @QueryParameter("channel.ids")
    private List<Long> channelIds;

    @QueryParameter("name")
    private List<String> names;

    @QueryParameter("countryCode")
    private List<String> countryCode;

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<Long> getAccountIds() {
        return this.accountIds;
    }

    public void setAccountIds(List<Long> accountIds) {
        this.accountIds = accountIds;
    }

    public List<Long> getChannelIds() {
        return this.channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<String> getNames() {
        return this.names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(List<String> countryCode) {
        this.countryCode = countryCode;
    }
}
