package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.fileman.FileInfo;
import com.foros.model.security.User;
import com.foros.util.FlagsUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public abstract class ExternalAccount<T extends AccountFinancialSettings> extends Account {
    @JoinColumn(name = "ACCOUNT_MANAGER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    private User accountManager;

    @RequiredConstraint
    @HasIdConstraint
    @JoinColumn(name = "INTERNAL_ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @ManyToOne
    private InternalAccount internalAccount;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToOne(targetEntity = AccountFinancialSettings.class)
    @JoinColumn(name = "ACCOUNT_ID")
    private T financialSettings;

    @Transient
    private Set<FileInfo> terms = new LinkedHashSet<FileInfo>();

    public Set<FileInfo> getTerms() {
        return new ChangesSupportSet<FileInfo>(this, "terms", terms);
    }

    public void setTerms(Set<FileInfo> terms) {
        this.terms = terms;
        this.registerChange("terms");
    }

    public ExternalAccount() {
    }

    public ExternalAccount(Long accountId) {
        super(accountId);
    }

    public ExternalAccount(Long id, String name) {
        super(id, name);
    }

    @Override
    public boolean isInternational() {
        return FlagsUtil.get(getFlags(), INTERNATIONAL);
    }

    public void setInternational(boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), INTERNATIONAL, flag));
    }

    public User getAccountManager() {
        return this.accountManager;
    }

    public void setAccountManager(User accountManager) {
        this.accountManager = accountManager;
        this.registerChange("accountManager");
    }

    public InternalAccount getInternalAccount() {
        return this.internalAccount;
    }

    public void setInternalAccount(InternalAccount account) {
        this.internalAccount = account;
        this.registerChange("internalAccount");
    }

    public T getFinancialSettings() {
        return financialSettings;
    }

    public void setFinancialSettings(T financialSettings) {
        this.financialSettings = financialSettings;
        this.registerChange("financialSettings");
    }
}
