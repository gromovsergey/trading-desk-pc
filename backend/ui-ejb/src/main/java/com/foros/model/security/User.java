package com.foros.model.security;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.Audit;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.audit.serialize.serializer.entity.UserAuditSerializer;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.site.Site;
import com.foros.security.AccountRole;
import com.foros.security.AuthenticationType;
import com.foros.util.EntityUtils;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.EmailConstraint;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "USERS")
@NamedQueries({
        @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
        @NamedQuery(name = "User.findByAcc", query = "SELECT u FROM User u WHERE u.account.id = :accId"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.authType = :authType AND u.status = 'A' AND u.email = :email AND u.account.status = 'A' order by u.id desc")
})
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@Audit(serializer = UserAuditSerializer.class)
public class User extends StatusEntityBase implements DisplayStatusEntity, Serializable, OwnedStatusable, Identifiable {
    public static final long ADV_LEVEL_ACCESS_FLAG = 0x1;
    public static final long IS_DELETED_OBJECTS_VISIBLE = 0x2;
    public static final long SITE_LEVEL_ACCESS_FLAG = 0x4;

    private static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "user.displaystatus.live");
    private static final DisplayStatus INACTIVE = new DisplayStatus(2L, DisplayStatus.Major.INACTIVE, "user.displaystatus.inactive");
    private static final DisplayStatus DELETED = new DisplayStatus(3L, DisplayStatus.Major.DELETED, "user.displaystatus.deleted");

    @SequenceGenerator(name = "UserGen", sequenceName = "USERS_USER_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserGen")
    @Column(name = "USER_ID", nullable = false)
    @IdConstraint
    private Long id;

    @RequiredConstraint
    @NameConstraint
    @Column(name = "FIRST_NAME")
    private String firstName;

    @RequiredConstraint
    @NameConstraint
    @Column(name = "LAST_NAME")
    private String lastName;

    @StringSizeConstraint(size = 400)
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @StringSizeConstraint(size = 4000)
    @Column(name = "LDAP_DN")
    private String dn;

    @RequiredConstraint
    @EmailConstraint
    @Column(name = "EMAIL")
    private String email;

    @RequiredConstraint
    @ByteLengthConstraint(length = 80)
    @Column(name = "PHONE")
    private String phone;

    @JoinColumn(name = "USER_ROLE_ID", referencedColumnName = "USER_ROLE_ID")
    @OneToOne
    @NotNullConstraint
    private UserRole role;

    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @NotNullConstraint
    private Account account;

    @Column(name = "FLAGS", nullable = false)
    private long flags;

    @JoinTable(name = "USERADVERTISER",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @OrderBy("name ASC")
    private Set<AdvertiserAccount> advertisers = new LinkedHashSet<AdvertiserAccount>();

    @JoinTable(name = "USERSITE",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "SITE_ID", referencedColumnName = "SITE_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @OrderBy("name ASC")
    private Set<Site> sites = new LinkedHashSet<Site>();

    @Column(name = "LANGUAGE")
    @Enumerated(EnumType.STRING)
    @NotNullConstraint
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTH_TYPE")
    @NotNullConstraint
    private AuthenticationType authType;

    @JoinColumn(name = "USER_CREDENTIAL_ID", referencedColumnName = "USER_CREDENTIAL_ID")
    @ManyToOne(cascade = CascadeType.ALL)
    private UserCredential userCredential;

    @Column(name = "PREPAID_AMOUNT_LIMIT")
    @RangeConstraint(min = "0", max = "4999999999")
    @FractionDigitsConstraint(2)
    private BigDecimal maxCreditLimit;

    @Transient
    private boolean mailSent = false;

    @Transient
    private String newPassword;

    public static DisplayStatus getDisplayStatus(Status status) {
        switch (status) {
            case ACTIVE:
                return LIVE;
            case INACTIVE:
                return INACTIVE;
            case DELETED:
                return DELETED;
            default:
                return null;
        }
    }

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.registerChange("firstName");
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.registerChange("lastName");
    }

    public String getFullName() {
        StringBuilder nameBuilder = new StringBuilder();

        if (getFirstName() != null) {
            nameBuilder.append(getFirstName());
        }

        nameBuilder.append(" ");

        if (getLastName() != null) {
            nameBuilder.append(getLastName());
        }

        return nameBuilder.toString();
    }

    public String getNameWithStatusSuffix() {
        return EntityUtils.appendStatusSuffix(firstName + " " + lastName, getStatus());
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        this.registerChange("jobTitle");
    }

    public String getDn() {
        return this.dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
        this.registerChange("dn");
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.registerChange("email");
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.registerChange("phone");
    }

    @Override
    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
        this.registerChange("account");
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (id == null || !id.equals(user.getId())) return false;

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.security.User[id=" + getId() + "]";
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
        this.registerChange("role");
    }

    public boolean isMailSent() {
        return mailSent;
    }

    public void setMailSent(boolean mailSent) {
        this.mailSent = mailSent;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    public Set<AdvertiserAccount> getAdvertisers() {
        return new ChangesSupportSet<AdvertiserAccount>(this, "advertisers", advertisers);
    }

    public void setAdvertisers(Set<AdvertiserAccount> advertisers) {
        this.advertisers = advertisers;
        this.registerChange("advertisers");
    }

    public boolean isAdvLevelAccessFlag() {
        return (getFlags() & ADV_LEVEL_ACCESS_FLAG) != 0;
    }

    public Set<Site> getSites() {
        return new ChangesSupportSet<Site>(this, "sites", sites);
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
        this.registerChange("sites");
    }

    public boolean isSiteLevelAccessFlag() {
        return (getFlags() & SITE_LEVEL_ACCESS_FLAG) != 0;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
        this.registerChange("language");
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(getStatus());
    }

    public Locale getLocale() {
        String language = getLanguage().getIsoCode();
        String country = getAccount().getCountry().getCountryCode();
        return new Locale(language, country);
    }

    @Override
    public Status getParentStatus() {
        return getAccount().getInheritedStatus();
    }

    public AuthenticationType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthenticationType authType) {
        this.authType = authType;
        this.registerChange("authType");
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
        this.registerChange("userCredential");
    }

    public BigDecimal getMaxCreditLimit() {
        return maxCreditLimit;
    }

    public void setMaxCreditLimit(BigDecimal maxCreditLimit) {
        this.maxCreditLimit = maxCreditLimit;
        this.registerChange("maxCreditLimit");
    }

    public boolean isDeletedObjectsVisible() {
        return getAccount().getRole() == AccountRole.INTERNAL && (getFlags() & IS_DELETED_OBJECTS_VISIBLE) != 0;
    }

}
