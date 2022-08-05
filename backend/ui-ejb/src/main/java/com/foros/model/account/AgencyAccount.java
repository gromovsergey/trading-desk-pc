package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.security.AccountRole;
import com.foros.util.changes.ChangesSupportSet;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
@DiscriminatorValue("4")
public class AgencyAccount extends AdvertisingAccountBase {

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "agency", fetch = FetchType.LAZY)
    @OrderBy("name ASC")
    private Set<AdvertiserAccount> advertisers = new LinkedHashSet<AdvertiserAccount>();

    public AgencyAccount() {
    }

    public AgencyAccount(Long accountId) {
        super(accountId);
    }

    public AgencyAccount(Long id, String name) {
        super(id, name);
    }

    @Override
    public boolean isStandalone() {
        return true;
    }

    @Override
    public AccountRole getRole() {
        return AccountRole.AGENCY;
    }

    public Set<AdvertiserAccount> getAdvertisers() {
        return new ChangesSupportSet<AdvertiserAccount>(this, "advertisers", advertisers);
    }

    public void setAdvertisers(Set<AdvertiserAccount> advertisers) {
        this.advertisers = advertisers;
        this.registerChange("advertisers");
    }

    public boolean isFinancialFieldsPresent() {
        return getAccountType().isAgencyFinancialFieldsFlag();
    }

}
