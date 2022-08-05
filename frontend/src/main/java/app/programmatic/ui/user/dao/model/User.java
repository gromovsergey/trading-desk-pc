package app.programmatic.ui.user.dao.model;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

import app.programmatic.ui.authentication.model.AuthenticationType;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.OwnedStatusable;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.tool.EnumSetHelper;
import app.programmatic.ui.common.tool.foros.StatusHelper;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "USERS")
public class User extends VersionEntityBase<Long> implements OwnedStatusable {

    @SequenceGenerator(name = "UserGen", sequenceName = "users_user_id_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserGen")
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 2000)
    @Column(name = "ldap_dn", updatable = false)
    private String ldapDn;

    @NotNull
    @Size(min = 1, max = 400)
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Size(max = 80)
    @Column(name = "phone", nullable = false, updatable = false)
    private String phone = "";

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_role_id", referencedColumnName = "user_role_id", nullable = false)
    private UserRole userRole;

    @NotNull
    @Column(name = "account_id", updatable = false, nullable = false)
    private Long accountId;

    @NotNull
    @Column(name = "flags", nullable = false)
    private long flags;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, updatable = false)
    private Language language = Language.valueOfCode(LOCALE_RU.getLanguage());

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, updatable = false)
    private AuthenticationType authenticationType = AuthenticationType.PSWD;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "4999999999")
    @Digits(integer=12, fraction=2)
    @Column(name = "prepaid_amount_limit", nullable = false, updatable = false)
    private BigDecimal maxCreditLimit = BigDecimal.valueOf(4999999999l);

    @NotNull
    @Column(name = "status", nullable = false)
    private Character status;

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_credential_id", referencedColumnName = "user_credential_id", updatable = false, nullable = false)
    private UserCredential userCredential;

    @ElementCollection(fetch = FetchType.EAGER) // Seems that EAGER is acceptable for set of ids (NOT entities)
    @CollectionTable(name="useradvertiser", joinColumns=@JoinColumn(name="user_id"))
    @Column(name="account_id")
    private List<Long> advertiserIds;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLdapDn() {
        return ldapDn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public EnumSet<UserOpts> getFlagsSet() {
        return EnumSetHelper.bitsToEnumSet(UserOpts.class, flags);
    }

    public void setFlagsSet(EnumSet<UserOpts> flags) {
        this.flags = EnumSetHelper.enumSetToBits(UserOpts.class, flags);
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public MajorDisplayStatus getMajorStatus() {
        return StatusHelper.getMajorStatusByObjectStatus(getStatus());
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }

    public List<Long> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }
}
