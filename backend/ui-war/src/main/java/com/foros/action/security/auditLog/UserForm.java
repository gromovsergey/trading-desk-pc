package com.foros.action.security.auditLog;

import com.foros.action.IdNameBean;
import com.foros.model.Status;
import com.foros.model.security.AccountType;
import com.foros.security.AuthenticationType;
import com.foros.model.security.Language;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.action.IdNameForm;

public class UserForm extends IdNameForm<String> {
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String dn;
    private String authType;
    private String email;
    private String phone;
    private Status status;
    private IdNameBean account = new IdNameBean();
    private IdNameBean role;
    private AccountRole accountRole;
    private AccountType accountType;
    private long flags;
    private String[] selectedAdvertisers;
    private Language language;
    private String maxCreditLimit;
    private String currencyCode = "USD";

    public UserForm() {
        super();
    }

    public String getName() {
        return this.firstName+" "+this.lastName;
    }

    public void setName(String name) {
    }

    public String getNonLoginInfo() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public char getStatusId() {
        return status == null ? '\u0000' : status.getLetter();
    }

    public void setStatusId(char statusId) {
        this.status = Status.valueOf(statusId);
    }

    public IdNameBean getAccount() {
        return account;
    }

    public void setAccount(IdNameBean account) {
        this.account = account;
    }

    public IdNameBean getRole() {
        return role;
    }

    public void setRole(IdNameBean role) {
        this.role = role;
    }

    public String getRoleId() {
        return role == null ? null : String.valueOf(role.getId());
    }

    public void setRoleId(String id) {
        role = StringUtil.isPropertyEmpty(id) ? null : new IdNameBean(id);
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole value) {
        this.accountRole = value;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public boolean getAdvLevelAccessFlag() {
        return (flags & User.ADV_LEVEL_ACCESS_FLAG) != 0;
    }

    public void setAdvLevelAccessFlag(boolean flag) {
        if (flag) {
            flags |= User.ADV_LEVEL_ACCESS_FLAG;
        } else {
            flags &= ~User.ADV_LEVEL_ACCESS_FLAG;
        }
    }

    public boolean getSiteLevelAccessFlag() {
        return (flags & User.SITE_LEVEL_ACCESS_FLAG) != 0;
    }

    public void setSiteLevelAccessFlag(boolean flag) {
        if (flag) {
            flags |= User.SITE_LEVEL_ACCESS_FLAG;
        } else {
            flags &= ~User.SITE_LEVEL_ACCESS_FLAG;
        }
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String[] getSelectedAdvertisers() {
        return selectedAdvertisers;
    }

    public void setSelectedAdvertisers(String[] selectedAdvertisers) {
        this.selectedAdvertisers = selectedAdvertisers;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLanguageIsoCode(String language) {
        if (StringUtil.isPropertyEmpty(language)) {
            this.language = null;
        } else {
            this.language = Language.valueOfCode(language);
        }
    }

    public String getLanguageIsoCode() {
        if (language != null) {
            return language.getIsoCode();
        } else {
            return null;
        }
    }

    public String getLdapOrPassword() {
        return (AuthenticationType.LDAP.getName().equals(authType))
                    ? StringUtil.getLocalizedString("InternalUser.ldap")
                    : StringUtil.getLocalizedString("InternalUser.password");
    }

    public String getMaxCreditLimit() {
        return maxCreditLimit;
    }

    public void setMaxCreditLimit(String maxCreditLimit) {
        this.maxCreditLimit = maxCreditLimit;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getMaxLimit() {
        return NumberUtil.maxCurrencyLimit(getCurrencyCode());
    }

}
