package app.programmatic.ui.user.dao.model;

import static app.programmatic.ui.user.dao.model.UserRoleOpts.ACCOUNT_MANAGER_ADVERTISER;
import static app.programmatic.ui.user.dao.model.UserRoleOpts.ACCOUNT_MANAGER_CMP;
import static app.programmatic.ui.user.dao.model.UserRoleOpts.ACCOUNT_MANAGER_ISP;
import static app.programmatic.ui.user.dao.model.UserRoleOpts.ACCOUNT_MANAGER_PUBLISHER;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.tool.EnumSetHelper;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
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
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
public class UserRole extends VersionEntityBase<Long> {

    @SequenceGenerator(name = "UserRoleGen", sequenceName = "userrole_user_role_id_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserRoleGen")
    @Column(name = "user_role_id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "account_role_id", updatable = false, nullable = false)
    private AccountRole accountRole;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "name", updatable = false, nullable = false) // updatable = false because new ui must not update user roles
    private String name;

    @NotNull
    @Column(name = "flags", updatable = false, nullable = false) // updatable = false because new ui must not update user roles
    private long flags;

    @Size(max = 2000)
    @Column(name = "ldap_dn", updatable = false) // updatable = false because new ui must not update user roles
    private String ldapDn;

    @Enumerated(EnumType.STRING)
    @Column(name = "internal_access_type", updatable = false) // updatable = false because new ui must not update user roles
    private InternalAccessType internalAccessType;

    @ElementCollection(fetch = FetchType.EAGER) // Seems that EAGER is acceptable for set of ids (NOT entities)
    @CollectionTable(name="userroleinternalaccess", joinColumns=@JoinColumn(name="user_role_id"))
    @Column(name = "account_id", updatable = false) // updatable = false because new ui must not update user roles
    private Set<Long> accessAccountIds = Collections.emptySet();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public EnumSet<UserRoleOpts> getFlagsSet() {
        return EnumSetHelper.bitsToEnumSet(UserRoleOpts.class, flags);
    }

    public void setFlagsSet(EnumSet<UserRoleOpts> flags) {
        this.flags = EnumSetHelper.enumSetToBits(UserRoleOpts.class, flags);
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
    }

    public InternalAccessType getInternalAccessType() {
        return internalAccessType;
    }

    public void setInternalAccessType(InternalAccessType internalAccessType) {
        this.internalAccessType = internalAccessType;
    }

    public Set<Long> getAccessAccountIds() {
        return accessAccountIds;
    }

    public void setAccessAccountIds(Set<Long> accessAccountIds) {
        this.accessAccountIds = accessAccountIds;
    }

    public boolean isAccountManager() {
        EnumSet<UserRoleOpts> opts = getFlagsSet();
        return opts.contains(ACCOUNT_MANAGER_ADVERTISER) ||
                opts.contains(ACCOUNT_MANAGER_PUBLISHER) ||
                opts.contains(ACCOUNT_MANAGER_ISP) ||
                opts.contains(ACCOUNT_MANAGER_CMP);
    }

    public boolean isAdvertiserAccountManager() {
        EnumSet<UserRoleOpts> opts = getFlagsSet();
        return opts.contains(ACCOUNT_MANAGER_ADVERTISER);
    }

    public boolean isPublisherAccountManager() {
        EnumSet<UserRoleOpts> opts = getFlagsSet();
        return opts.contains(ACCOUNT_MANAGER_PUBLISHER);
    }
}
