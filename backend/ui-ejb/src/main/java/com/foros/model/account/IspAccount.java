package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.isp.Colocation;
import com.foros.security.AccountRole;
import com.foros.util.changes.ChangesSupportSet;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("3")
public class IspAccount extends AccountsPayableAccountBase {

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<Colocation> colocations = new LinkedHashSet<Colocation>();

    @Column(name = "hid_profile")
    private boolean household  = false;

    public IspAccount() {
    }

    public IspAccount(Long accountId) {
        super(accountId);
    }

    public IspAccount(Long id, String name) {
        super(id, name);
    }

    @Override
    public AccountRole getRole() {
        return AccountRole.ISP;
    }

    public Set<Colocation> getColocations() {
        return new ChangesSupportSet<Colocation>(this, "colocations", colocations);
    }

    public void setColocations(Set<Colocation> colocations) {
        this.colocations = colocations;
        this.registerChange("colocations");
    }

    public boolean isHousehold() {
        return household;
    }

    public void setHousehold(boolean household) {
        this.household = household;
        this.registerChange("household");
    }
}
