package app.programmatic.ui.account.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.OwnedStatusable;

public class Account implements OwnedStatusable {
    private Long id;
    private AccountRole role;
    private String displayStatus;
    private String countryCode;
    private Long internalAccountId;
    private Long accountManagerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getInternalAccountId() {
        return internalAccountId;
    }

    public void setInternalAccountId(Long internalAccountId) {
        this.internalAccountId = internalAccountId;
    }

    public Long getAccountManagerId() {
        return accountManagerId;
    }

    public void setAccountManagerId(Long accountManagerId) {
        this.accountManagerId = accountManagerId;
    }

    @Override
    public MajorDisplayStatus getMajorStatus() {
        return displayStatus == null ? null : AccountDisplayStatus.findByName(displayStatus).getMajorStatus();
    }

    @Override
    public Long getAccountId() {
        return getId();
    }
}
