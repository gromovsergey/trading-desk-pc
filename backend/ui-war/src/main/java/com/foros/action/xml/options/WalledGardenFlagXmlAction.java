package com.foros.action.xml.options;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;

import javax.ejb.EJB;

public class WalledGardenFlagXmlAction extends AbstractXmlAction<Boolean> {
    @EJB
    private WalledGardenService walledGardenService;

    private String agencyPair = null;

    private String advertiserPair = null;

    private String publisherPair = null;

    @Override
    public Boolean generateModel() throws ProcessException {
        if (!StringUtil.isPropertyEmpty(getAgencyPair())) {
            Long agencyAccountId = fetchId(getAgencyPair());
            return walledGardenService.isAgencyWalledGarden(agencyAccountId);
        }
        if (!StringUtil.isPropertyEmpty(getAdvertiserPair())) {
            Long advertiserAccountId = fetchId(getAdvertiserPair());
            return walledGardenService.isAdvertiserWalledGarden(advertiserAccountId);
        }
        if (!StringUtil.isPropertyEmpty(getPublisherPair())) {
            Long pubAccountId = fetchId(getPublisherPair());
            return walledGardenService.isPublisherWalledGarden(pubAccountId);
        }
        return false;
    }

    public String getAgencyPair() {
        return agencyPair;
    }

    public void setAgencyPair(String agencyPair) {
        this.agencyPair = agencyPair;
    }

    public String getAdvertiserPair() {
        return advertiserPair;
    }

    public void setAdvertiserPair(String advertiserPair) {
        this.advertiserPair = advertiserPair;
    }

    public String getPublisherPair() {
        return publisherPair;
    }

    public void setPublisherPair(String publisherPair) {
        this.publisherPair = publisherPair;
    }

    private Long fetchId(String pair) {
        return StringUtil.isPropertyEmpty(pair) ? null : PairUtil.fetchId(pair);
    }
}
