package com.foros.session.campaign.bulk;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.validation.constraint.IdCollectionConstraint;

import java.util.List;

public class ChannelSelector implements Selector<Channel> {
    
    @IdCollectionConstraint
    private List<Long> channelIds;
    @IdCollectionConstraint
    private List<Long> accountIds;
    private String name;
    private String countryCode;
    private Paging paging;
    private String content;
    private List<ChannelVisibility> visibility;
    private List<AdvertisingChannelType> types;

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
    
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<Long> accountIds) {
        this.accountIds = accountIds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChannelVisibility> getVisibility() {
        return visibility;
    }

    public void setVisibility(List<ChannelVisibility> visibility) {
        this.visibility = visibility;
    }

    public List<AdvertisingChannelType> getTypes() {
        return types;
    }

    public void setTypes(List<AdvertisingChannelType> types) {
        this.types = types;
    }
}
