package com.foros.model.account;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.IdNameStatusEntity;
import com.foros.model.Status;
import com.foros.model.Timezone;
import com.foros.model.currency.Currency;
import com.foros.model.security.AccountAddress;
import com.foros.model.security.AccountType;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.util.FlagsUtil;
import com.foros.util.NumberUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "ACCOUNT")
@Inheritance
@DiscriminatorColumn(name = "ROLE_ID", discriminatorType= DiscriminatorType.INTEGER)
@NamedQueries({
    @NamedQuery(name = "Account.findForDiscovery", query =
        "SELECT NEW com.foros.session.security.ManagerAccountTO(a.id, a.name, a.status, a.role, a.flags) FROM Account a " +
        " WHERE a.class = InternalAccount"),

    @NamedQuery(name = "Account.findByName", query =
            "SELECT a FROM Account a WHERE a.name = :name AND a.agency IS NULL"),

    @NamedQuery(name = "Account.findUsers", query =
        "SELECT NEW com.foros.session.security.UserByAccountTO(u.id, u.firstName, u.lastName, u.status) FROM User u " +
        " WHERE u.account.id = :id order by u.firstName, u.lastName"),

    @NamedQuery(name = "Account.findUserTOById", query =
        "SELECT NEW com.foros.session.security.UserByAccountTO(u.id, u.firstName, u.lastName, u.status) FROM User u " +
        " WHERE u.id = :id"),

    @NamedQuery(name = "Account.findCountByAccountType", query =
        "SELECT count(a.id) FROM Account a WHERE a.accountType.id = :id" ),

    @NamedQuery(name = "Account.findAccountByAccountType", query =
        "SELECT NEW com.foros.session.EntityTO(a.id, a.name ,a.status) FROM Account a WHERE a.accountType.id = :id" )
})
@AllowedStatuses(values = { Status.ACTIVE, Status.INACTIVE, Status.DELETED } )
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class Account extends DisplayStatusEntityBase implements OwnedStatusable<Account>, IdNameStatusEntity, Serializable {
    public static final long TEST_FLAG = 0x01;
    public static final long INTERNATIONAL = 0x02;
    public static final long CMP_CONTACT_SHOW_PHONE = 0x08;
    public static final long PUB_ADVERTISING_REPORT_FLAG = 0x10;
    public static final long REFERRER_REPORT_FLAG = 0x20;
    public static final long SITE_TARGETING_FLAG = 0x40;
    public static final long PUB_CONVERSION_REPORT_FLAG = 0x80;
    public static final int SELF_SERVICE_FLAG = 0x100;

    @SequenceGenerator(name = "AccountGen", sequenceName = "ACCOUNT_ACCOUNT_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountGen")
    @Column(name = "ACCOUNT_ID", nullable = false)
    @IdConstraint
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ROLE_ID", insertable=false, updatable=false)
    private AccountRole role;

    @ExpressionSymbolsOnlyConstraint
    @StringSizeConstraint(size = 100)
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @RequiredConstraint
    @ByteLengthConstraint(length = 200)
    @Column(name = "LEGAL_NAME")
    private String legalName;

    @Column(name = "FLAGS")
    private long flags;

    @RequiredConstraint
    @HasIdConstraint
    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE")
    @ManyToOne
    private Country country;

    @RequiredConstraint
    @HasIdConstraint
    @JoinColumn(name = "ACCOUNT_TYPE_ID", referencedColumnName = "ACCOUNT_TYPE_ID")
    @ManyToOne
    private AccountType accountType;

    @RequiredConstraint
    @HasIdConstraint
    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID")
    @ManyToOne
    private Currency currency;

    @StringSizeConstraint(size = 4000)
    @Column(name = "NOTES")
    private String notes;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @OrderBy("email ASC")
    private Set<User> users = new LinkedHashSet<User>();

    @JoinColumn(name = "BILLING_ADDRESS_ID", referencedColumnName = "ADDRESS_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AccountAddress billingAddress;

    @JoinColumn(name = "LEGAL_ADDRESS_ID", referencedColumnName = "ADDRESS_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AccountAddress legalAddress;

    @RequiredConstraint
    @HasIdConstraint
    @JoinColumn(name = "TIMEZONE_ID", referencedColumnName = "TIMEZONE_ID")
    @ManyToOne
    private Timezone timezone;

    @StringSizeConstraint(size = 50)
    @Column(name = "COMPANY_REGISTRATION_NUMBER")
    private String companyRegistrationNumber;

    @JoinColumn(name = "CMP_CONTACT_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    private User cmpContact;

    @Column(name = "MESSAGE_SENT")
    private int messageSent;

    @Column(name = "SELF_SERVICE_COMMISSION")
    private BigDecimal selfServiceCommission;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "account.displaystatus.live");
    public static final DisplayStatus NOT_LIVE = new DisplayStatus(2L, DisplayStatus.Major.NOT_LIVE, "account.displaystatus.not_live");
    public static final DisplayStatus INACTIVE = new DisplayStatus(3L, DisplayStatus.Major.INACTIVE, "account.displaystatus.inactive");
    public static final DisplayStatus DELETED = new DisplayStatus(4L, DisplayStatus.Major.DELETED, "account.displaystatus.deleted");
    public static final DisplayStatus NO_BILLING_CONTACT = new DisplayStatus(5L, DisplayStatus.Major.NOT_LIVE, "account.displaystatus.no_billing_contact");

    public static Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
        LIVE,
        NOT_LIVE,
        INACTIVE,
        DELETED,
        NO_BILLING_CONTACT
    );

    public abstract AccountRole getRole();

    @Override
    public Account getAccount() {
        return this;
    }

    public static Collection<DisplayStatus> getAvailableDisplayStatuses() {
        return displayStatusMap.values();
    }

    public static DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(displayStatusId);
    }

    public Account() {
    }

    protected Account(Long accountId) {
        this.id = accountId;
    }

    protected Account(Long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public static Account newInstance(AccountRole accountRole) {
        switch (accountRole) {
            case INTERNAL: {
                return new InternalAccount();
            }
            case ADVERTISER: {
                return new AdvertiserAccount();
            }
            case PUBLISHER: {
                return new PublisherAccount();
            }
            case ISP: {
                return new IspAccount();
            }
            case AGENCY: {
                return new AgencyAccount();
            }
            case CMP: {
                return new CmpAccount();
            }
            default: {
                break;
            }
        }

        throw new IllegalStateException("Can't create an account for the role " + accountRole.toString());
    }

    @Override
    @XmlAttribute
    public Long getId() {
        return this.id;
    }

    @DenyAll
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @PermitAll
    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
        this.registerChange("legalName");
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.registerChange("notes");
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.registerChange("country");
    }

    public Set<User> getUsers() {
        return new ChangesSupportSet<User>(this, "users", users);
    }

    public void setUsers(Set<User> users) {
        this.users = users;
        this.registerChange("users");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }

        Account other = (Account)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.registerChange("currency");
    }

    public long getFlags() {
        return flags;
    }

    public boolean isTestFlag() {
        return FlagsUtil.get(getFlags(), TEST_FLAG);
    }

    public void setFlags(long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    public boolean getTestFlag() {
        return this.isTestFlag();
    }

    public abstract boolean isInternational();

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
        this.registerChange("accountType");
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
        this.registerChange("timezone");
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.registerChange("companyRegistrationNumber");
    }

    @Override
    public String toString() {
        return  getClass().getSimpleName() + "[id=" + getId() + "]";
    }

    @Override
    public Status getParentStatus() {
        return Status.ACTIVE;
    }

    public AccountAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AccountAddress billingAddress) {
        this.billingAddress = billingAddress;
        this.registerChange("billingAddress");
    }

    public AccountAddress getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(AccountAddress legalAddress) {
        this.legalAddress = legalAddress;
        this.registerChange("legalAddress");
    }

    public User getCmpContact() {
        return cmpContact;
    }

    public void setCmpContact(User cmpContact) {
        this.cmpContact = cmpContact;
        this.registerChange("cmpContact");
    }

    public boolean isCmpContactShowPhone() {
        return FlagsUtil.get(getFlags(), CMP_CONTACT_SHOW_PHONE);
    }

    public boolean isPubAdvertisingReportFlag() {
        return FlagsUtil.get(getFlags(), PUB_ADVERTISING_REPORT_FLAG);
    }

    public boolean isPubConversionReportFlag() {
        return FlagsUtil.get(getFlags(), PUB_CONVERSION_REPORT_FLAG);
    }

    public boolean isSiteTargetingFlag() {
        return FlagsUtil.get(getFlags(), SITE_TARGETING_FLAG);
    }

    public boolean isReferrerReportFlag() {
        return FlagsUtil.get(getFlags(), REFERRER_REPORT_FLAG);
    }

    public int getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(int messageSent) {
        this.messageSent = messageSent;
        this.registerChange("messageSent");
    }

    public boolean isSelfServiceFlag() {
        return FlagsUtil.get(getFlags(), SELF_SERVICE_FLAG);
    }

    public BigDecimal getSelfServiceCommission() {
        return selfServiceCommission;
    }

    public void setSelfServiceCommission(BigDecimal selfServiceCommission) {
        this.selfServiceCommission = selfServiceCommission;
        this.registerChange("selfServiceCommission");
        this.registerChange("selfServiceFlag");
    }

    public BigDecimal getSelfServiceCommissionPercent() {
        return NumberUtil.toPercents(getSelfServiceCommission());
    }

    public void setSelfServiceCommissionPercent(BigDecimal selfServiceCommissionPercent) {
        setSelfServiceCommission(NumberUtil.fromPercents(selfServiceCommissionPercent));
    }
}
