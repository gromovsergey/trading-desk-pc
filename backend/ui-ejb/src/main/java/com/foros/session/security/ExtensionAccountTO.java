package com.foros.session.security;

import com.foros.jaxb.adapters.AccountLinkXmlAdapter;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.security.AccountRole;
import com.foros.session.NamedTO;
import com.foros.util.FlagsUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "account")
@XmlType(propOrder = {
        "status",
        "id",
        "name",
        "role",
        "agency",
        "countryCode",
        "currencyCode",
        "testFlag",
        "internationalFlag"
})

@XmlAccessorType(XmlAccessType.NONE)
public class ExtensionAccountTO extends AccountTO {

    private String currencyCode;

    private NamedTO agency;

    public ExtensionAccountTO() {
    }

    public ExtensionAccountTO(Long id, String name, char status, AccountRole role, String countryCode, Long displayStatusId, long flags, String currencyCode, Long agencyId) {
        super(id, name, status, role, countryCode, displayStatusId, flags);
        this.currencyCode = currencyCode;
        this.agency = new NamedTO(agencyId, null);
    }

    @Override
    @XmlElement
    public Long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @Override
    @XmlElement
    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    @XmlElement
    public AccountRole getRole() {
        return super.getRole();
    }

    @Override
    @XmlElement(name = "test")
    public boolean isTestFlag() {
        return super.isTestFlag();
    }

    @XmlElement(name = "currency")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    @XmlElement(name = "country")
    public String getCountryCode() {
        return super.getCountryCode();
    }

    @XmlElement
    @XmlJavaTypeAdapter(AccountLinkXmlAdapter.class)
    public NamedTO getAgency() {
        return agency;
    }

    @XmlElement(name = "international")
    public boolean isInternationalFlag() {
        return FlagsUtil.get(flags, Account.INTERNATIONAL);
    }

    public void setAgency(NamedTO agency) {
        this.agency = agency;
    }

}
