package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.validation.constraint.RequiredConstraint;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue("0")
public class InternalAccount extends Account {
    @RequiredConstraint(message = "internalAccount.advContact")
    @JoinColumn(name = "ADV_CONTACT_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    private User advContact;

    @RequiredConstraint(message = "internalAccount.pubContact")
    @JoinColumn(name = "PUB_CONTACT_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    private User pubContact;

    @RequiredConstraint(message = "internalAccount.ispContact")
    @JoinColumn(name = "ISP_CONTACT_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    private User ispContact;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private AccountAuctionSettings auctionSettings;

    public InternalAccount() {
    }

    public InternalAccount(Long accountId) {
        super(accountId);
    }

    public InternalAccount(Long id, String name) {
        super(id, name);
    }

    @Override
    public boolean isInternational() {
        return true;
    }

    @Override
    public AccountRole getRole() {
        return AccountRole.INTERNAL;
    }

    public User getAdvContact() {
        return advContact;
    }

    public void setAdvContact(User advContact) {
        this.advContact = advContact;
        this.registerChange("advContact");
    }

    public User getPubContact() {
        return pubContact;
    }

    public void setPubContact(User pubContact) {
        this.pubContact = pubContact;
        this.registerChange("pubContact");
    }

    public User getIspContact() {
        return ispContact;
    }

    public void setIspContact(User ispContact) {
        this.ispContact = ispContact;
        this.registerChange("ispContact");
    }

    public AccountAuctionSettings getAuctionSettings() {
        return auctionSettings;
    }

    public void setAuctionSettings(AccountAuctionSettings auctionSettings) {
        this.auctionSettings = auctionSettings;
    }
}
