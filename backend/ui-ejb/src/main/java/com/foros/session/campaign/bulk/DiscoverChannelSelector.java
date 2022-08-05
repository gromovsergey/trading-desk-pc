package com.foros.session.campaign.bulk;


import com.foros.model.channel.DiscoverChannel;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;
import com.foros.validation.constraint.IdCollectionConstraint;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class DiscoverChannelSelector  implements Selector<DiscoverChannel> {

    @IdCollectionConstraint
    private List<Long> channelIds;
    @IdCollectionConstraint
    private List<Long> accountIds;
    private String name;
    private String countryCode;
    private Paging paging;

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

    @XmlElement(name = "paging")
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
