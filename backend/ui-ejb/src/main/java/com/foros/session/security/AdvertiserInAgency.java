package com.foros.session.security;

import com.foros.jaxb.adapters.EntityLink;
import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.security.AccountRole;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlRootElement(name = "account")
@XmlType(propOrder = {
        "status",
        "id",
        "name",
        "commission",
        "role",
        "agency",
        "country",
        "currency",
        "test",
        "international"
})

@XmlAccessorType(XmlAccessType.NONE)
public class AdvertiserInAgency extends EntityBase {
    private String currency;
    private EntityLink agency;
    private Long id;
    private String name;
    private BigDecimal commission;
    private Status status;
    private AccountRole role;
    private Boolean test;
    private String country;
    private Boolean international;

    public AdvertiserInAgency() {
    }

    @XmlElement
    public Long getId() {
        return this.id;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    @XmlElement
    public BigDecimal getCommission() {
        return commission;
    }

    @XmlElement
    public Status getStatus() {
        return this.status;
    }

    @XmlElement
    public AccountRole getRole() {
        return this.role;
    }

    @XmlElement
    public Boolean getTest() {
        return this.test;
    }

    @XmlElement
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlElement
    public String getCountry() {
        return this.country;
    }

    @XmlElement
//    @XmlJavaTypeAdapter(AccountLinkXmlAdapter.class)
    public EntityLink getAgency() {
        return agency;
    }

    @XmlElement
    public Boolean getInternational() {
        return this.international;
    }

    public void setAgency(EntityLink agency) {
        this.agency = agency;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setInternational(Boolean international) {
        this.international = international;
    }
}
