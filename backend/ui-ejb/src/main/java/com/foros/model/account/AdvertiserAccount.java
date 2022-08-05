package com.foros.model.account;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.Status;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.security.AccountType;
import com.foros.model.security.TextAdservingMode;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.StringSizeConstraint;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("1")
public class AdvertiserAccount extends AdvertisingAccountBase {
    @JoinColumn(name = "AGENCY_ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @ManyToOne
    private AgencyAccount agency;

    @StringSizeConstraint(size = 50)
    @Column(name = "CONTACT_NAME")
    private String contactName;

    @StringSizeConstraint(size = 50)
    @Column(name = "BUSINESS_AREA")
    private String businessArea;

    @StringSizeConstraint(size = 50)
    @Column(name = "SPECIFIC_BUSINESS_AREA")
    private String specificBusinessArea;

    @JoinTable(name = "CREATIVECATEGORY_ACCOUNT",
            joinColumns = {@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CREATIVE_CATEGORY_ID", referencedColumnName = "CREATIVE_CATEGORY_ID")})
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<CreativeCategory> categories = new LinkedHashSet<CreativeCategory>();

    @JoinColumn(name = "TNS_ADVERTISER_ID", referencedColumnName = "TNS_ADVERTISER_ID")
    @ManyToOne
    private TnsAdvertiser tnsAdvertiser;

    @JoinColumn(name = "TNS_BRAND_ID", referencedColumnName = "TNS_BRAND_ID")
    @ManyToOne
    private TnsBrand tnsBrand;

    public AdvertiserAccount() {
    }

    public AdvertiserAccount(Long accountId) {
        super(accountId);
    }

    public TnsAdvertiser getTnsAdvertiser() {
        return tnsAdvertiser;
    }

    public void setTnsAdvertiser(TnsAdvertiser tnsAdvertiser) {
        this.tnsAdvertiser = tnsAdvertiser;
        this.registerChange("tnsAdvertiser");
    }

    public TnsBrand getTnsBrand() {
        return tnsBrand;
    }

    public void setTnsBrand(TnsBrand tnsBrand) {
        this.tnsBrand = tnsBrand;
        this.registerChange("tnsBrand");
    }

    public AdvertiserAccount(Long id, String name) {
        super(id, name);
    }

    @Override
    public AccountRole getRole() {
        return AccountRole.ADVERTISER;
    }

    public AgencyAccount getAgency() {
        return agency;
    }

    public void setAgency(AgencyAccount agency) {
        this.agency = agency;
        this.registerChange("agency");
    }

    public boolean isInAgencyAdvertiser() {
        return agency != null;
    }

    @Override
    public TextAdservingMode getTextAdservingMode() {
        if (isInAgencyAdvertiser()) {
            return getAgency().getTextAdservingMode();
        } else {
            return super.getTextAdservingMode();
        }
    }

    @Override
    public void setTextAdservingMode(TextAdservingMode textAdservingMode) {
        if (isInAgencyAdvertiser()) {
            throw new UnsupportedOperationException();
        }
        super.setTextAdservingMode(textAdservingMode);
    }

    @Override
    public boolean isStandalone() {
        return !isInAgencyAdvertiser();
    }

    @Override
    public User getAccountManager() {
        if (isInAgencyAdvertiser()) {
            return getAgency().getAccountManager();
        }
        else {
            return super.getAccountManager();
        }
    }

    @Override
    public void setAccountManager(User accountManager) {
        if (isInAgencyAdvertiser()) {
            throw new UnsupportedOperationException();
        }
        super.setAccountManager(accountManager);
    }

    @Override
    public AccountType getAccountType() {
        if (isInAgencyAdvertiser()) {
            return getAgency().getAccountType();
        }
        else {
            return super.getAccountType();
        }
    }

    @Override
    public void setAccountType(AccountType accountType) {
        if (isInAgencyAdvertiser()) {
            throw new UnsupportedOperationException();
        }
        super.setAccountType(accountType);
    }

    @Override
    public long getFlags() {
        if (isInAgencyAdvertiser()) {
            return getAgency().getFlags();
        }
        else {
            return super.getFlags();
        }
    }

    @Override
    public void setFlags(long flags) {
        if (isInAgencyAdvertiser()) {
            throw new UnsupportedOperationException();
        }
        super.setFlags(flags);
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
        this.registerChange("contactName");
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
        this.registerChange("businessArea");
    }

    public String getSpecificBusinessArea() {
        return specificBusinessArea;
    }

    public void setSpecificBusinessArea(String specificBusinessArea) {
        this.specificBusinessArea = specificBusinessArea;
        this.registerChange("specificBusinessArea");
    }

    @Override
    public boolean isFinancialFieldsPresent() {
        return isStandalone() || !getAccountType().isAgencyFinancialFieldsFlag();
    }

    @Override
    public Status getParentStatus() {
        if (isStandalone()) {
            return Status.ACTIVE;
        } else {
            return agency.getInheritedStatus();
        }
    }

    @Override
    public boolean isTestFlag() {
        return isStandalone() ? super.isTestFlag() : getAgency().isTestFlag();
    }

    @Override
    public boolean getTestFlag() {
        return this.isTestFlag();
    }

    public Set<CreativeCategory> getCategories() {
        return new ChangesSupportSet<CreativeCategory>(this, "categories", categories);
    }

    public void setCategories(Set<CreativeCategory> categories) {
        this.categories = categories;
        this.registerChange("categories");
    }

    @Override
    public boolean isSelfServiceFlag() {
        return isInAgencyAdvertiser() ? agency.isSelfServiceFlag() : super.isSelfServiceFlag();
    }
}
