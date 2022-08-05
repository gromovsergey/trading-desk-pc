package com.foros.session.campaign.bulk;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class CCGKeywordSelector extends CampaignCreativeGroupSelector {

    private List<Long> keywords;

    public List<Long> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Long> keywords) {
        this.keywords = keywords;
    }
}