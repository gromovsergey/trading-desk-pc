package com.foros.jaxb.adapters;

import com.foros.model.account.AdvertiserAccount;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AdvertiserAgencyAccountXmlAdapter extends XmlAdapter<AdvertiserLink, AdvertiserAccount> {

    @Override
    public AdvertiserAccount unmarshal(AdvertiserLink v) throws Exception {
        AdvertiserAccount account = new AdvertiserAccount();
        account.setId(v.getId());
        return account;
    }

    @Override
    public AdvertiserLink marshal(AdvertiserAccount v) throws Exception {
        if (v == null) {
            return null;
        }
        AdvertiserLink advertiserLink = new AdvertiserLink(v.getId());
        if (v.getAgency() != null) {
            advertiserLink.setAgency(new EntityLink(v.getAgency().getId()));
        }
        return advertiserLink;
    }

}
