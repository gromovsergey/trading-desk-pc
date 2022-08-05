package com.foros.rs.client.model.advertising.channel;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;
import com.foros.rs.client.model.operation.PagingSelector;

import java.lang.Long;
import java.lang.String;
import java.util.List;


@QueryEntity
public class ChannelSelector implements PagingSelectorContainer {

    @QueryParameter("name")
    private String name;

    @QueryParameter("countryCode")
    private String countryCode;

    @QueryParameter("type")
    private ChannelType type;

    @QueryParameter("visibility")
    private Visibility visibility;

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("account.ids")
    private List<Long> accountIds;

    @QueryParameter("channel.ids")
    private List<Long> channelIds;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public ChannelType getType() {
        return this.type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

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
}