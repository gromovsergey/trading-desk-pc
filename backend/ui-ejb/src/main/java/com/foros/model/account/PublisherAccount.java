package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.site.Site;
import com.foros.security.AccountRole;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("2")
public class PublisherAccount extends AccountsPayableAccountBase {

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<Site> sites = new LinkedHashSet<Site>();

    @Column(name = "PASSBACK_BELOW_FOLD")
    private Boolean passbackBelowFold;

    @RequiredConstraint
    @Column(name = "CREATIVE_REAPPROVAL")
    private Boolean creativesReapproval;

    @Column(name = "USE_PUB_PIXEL", nullable = false)
    private boolean usePubPixel;

    @StringSizeConstraint(size = 4000)
    @Column(name = "PUB_PIXEL_OPTIN")
    private String pubPixelOptIn;

    @StringSizeConstraint(size = 4000)
    @Column(name = "PUB_PIXEL_OPTOUT")
    private String pubPixelOptOut;

    public PublisherAccount() {
    }

    public PublisherAccount(Long accountId) {
        super(accountId);
    }

    public PublisherAccount(Long id, String name) {
        super(id, name);
    }

    public Set<Site> getSites() {
        return new ChangesSupportSet<Site>(this, "sites", sites);
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
        this.registerChange("sites");
    }

    @Override
    public AccountRole getRole() {
        return AccountRole.PUBLISHER;
    }

    public Boolean getPassbackBelowFold() {
        return passbackBelowFold;
    }

    public void setPassbackBelowFold(Boolean passbackBelowFold) {
        this.passbackBelowFold = passbackBelowFold;
        this.registerChange("passbackBelowFold");
    }

    public boolean isUsePubPixel() {
        return usePubPixel;
    }

    public void setUsePubPixel(boolean usePubPixel) {
        this.usePubPixel = usePubPixel;
        this.registerChange("usePubPixel");
    }

    public String getPubPixelOptIn() {
        return pubPixelOptIn;
    }

    public void setPubPixelOptIn(String pubPixelOptIn) {
        this.pubPixelOptIn = pubPixelOptIn;
        this.registerChange("pubPixelOptIn");
    }

    public String getPubPixelOptOut() {
        return pubPixelOptOut;
    }

    public void setPubPixelOptOut(String pubPixelOptOut) {
        this.pubPixelOptOut = pubPixelOptOut;
        this.registerChange("pubPixelOptOut");
    }

    public Boolean isCreativesReapproval() {
        return creativesReapproval;
    }

    public void setCreativesReapproval(Boolean creativesReapproval) {
        this.creativesReapproval = creativesReapproval;
        this.registerChange("creativesReapproval");
    }
}
