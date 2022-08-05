package com.foros.model.security;

import com.foros.annotations.Audit;
import com.foros.changes.inspection.changeNode.custom.CollectionStringValueChange;
import com.foros.model.IdNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.security.AccountRole;
import com.foros.util.FlagsUtil;
import com.foros.util.SQLUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import java.util.LinkedHashSet;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "USERROLE")
@NamedQueries({
    @NamedQuery(name = "UserRole.findAll", query = "SELECT a FROM UserRole a ORDER BY a.name"),
    @NamedQuery(name = "UserRole.findByAccountRole", query = "SELECT a FROM UserRole a WHERE a.accountRole = :accountRole ORDER BY a.name")
})
public class UserRole extends VersionEntityBase implements Serializable, IdNameEntity {
    public static long ACCOUNT_MANAGER_ADVERTISER = 0x01;
    public static long ACCOUNT_MANAGER_PUBLISHER = 0x02;
    public static long ACCOUNT_MANAGER_ISP = 0x04;
    public static long ACCOUNT_MANAGER_CMP = 0x08;
    public static long AUTO_GENERATED_FLAG = 0x10;

    @SequenceGenerator(name = "UserRoleGen", sequenceName = "USERROLE_USER_ROLE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserRoleGen")
    @Column(name = "USER_ROLE_ID", nullable = false)
    @IdConstraint
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ACCOUNT_ROLE_ID")
    @RequiredConstraint
    private AccountRole accountRole;

    @Column(name = "NAME", nullable = false)
    @NameConstraint
    @RequiredConstraint
    @StringSizeConstraint(size = 100)
    private String name = "";

    @Column(name = "FLAGS", nullable = false)
    private long flags;

    @Column(name = "LDAP_DN")
    @StringSizeConstraint(size = 2000)
    private String ldapDn = "";

    @OneToMany(mappedBy = "userRole", cascade = CascadeType.ALL)
    @OrderBy ("id ASC")
    private Set<PolicyEntry> policyEntries = new LinkedHashSet<PolicyEntry>();
    
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.security.InternalAccessType"),
            @Parameter(name = "identifierMethod", value = "getLetter"),
            @Parameter(name = "valueOfMethod", value = "byLetter") })
    @Column(name = "INTERNAL_ACCESS_TYPE")
    private InternalAccessType internalAccessType = InternalAccessType.USER_ACCOUNT;
    
    @CollectionOfElements(targetElement = java.lang.Long.class, fetch = FetchType.EAGER)
    @JoinTable(name = "USERROLEINTERNALACCESS", joinColumns = @JoinColumn(name = "USER_ROLE_ID"))
    @Column(name = "ACCOUNT_ID")
    @Audit(nodeFactory = CollectionStringValueChange.Factory.class)
    private Set<Long> accessAccountIds = new LinkedHashSet<Long>();

    public UserRole() {
    }

    public UserRole(Long userRoleId) {
        this.id = userRoleId;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long userRoleId) {
        this.id = userRoleId;
        this.registerChange("id");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public long getFlags() {
        return this.flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }
    
    public boolean isManagerOf(AccountRole accountRole) {
        switch (accountRole) {
            case ADVERTISER:
            case AGENCY:
                return isAdvertiserAccountManager();
            case PUBLISHER:
                return isPublisherAccountManager();
            case ISP:
                return isISPAccountManager();
            case CMP:
                return isCMPAccountManager();
            default:
                return false;
        }
    }
    
    public boolean isAccountManager() {
        return isAdvertiserAccountManager() ||
            isPublisherAccountManager() ||
            isISPAccountManager() ||
            isCMPAccountManager();
    }

    public boolean isAdvertiserAccountManager() {
        return FlagsUtil.get(getFlags(), ACCOUNT_MANAGER_ADVERTISER);
    }

    public void setAdvertiserAccountManager(boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), ACCOUNT_MANAGER_ADVERTISER, flag));
    }

    public boolean isPublisherAccountManager() {
        return FlagsUtil.get(getFlags(), ACCOUNT_MANAGER_PUBLISHER);
    }

    public void setPublisherAccountManager(boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), ACCOUNT_MANAGER_PUBLISHER, flag));
    }

    public boolean isISPAccountManager() {
        return FlagsUtil.get(getFlags(), ACCOUNT_MANAGER_ISP);
    }

    public void setISPAccountManager(boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), ACCOUNT_MANAGER_ISP, flag));
    }

    public boolean isCMPAccountManager() {
        return FlagsUtil.get(getFlags(), ACCOUNT_MANAGER_CMP);
    }

    public void setCMPAccountManager(boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), ACCOUNT_MANAGER_CMP, flag));
    }

    public boolean isAccountManager(AccountRole accountRole) {
        switch (accountRole) {
            case ISP:
                return isISPAccountManager();
            case AGENCY:
            case ADVERTISER:
                return isAdvertiserAccountManager();
            case PUBLISHER:
                return isPublisherAccountManager();
            case CMP:
                return isCMPAccountManager();
            default:
                return false;
        }
    }

    public AccountRole getAccountRole() {
        return this.accountRole;
    }

    public void setAccountRole(AccountRole role) {
        this.accountRole = role;
        this.registerChange("accountRole");
    }

    public String getLdapDn() {
        return this.ldapDn;
    }

    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
        this.registerChange("ldapDn");
    }

    public Set<PolicyEntry> getPolicyEntries() {
        return new ChangesSupportSet<PolicyEntry>(this, "policyEntries", policyEntries);
    }

    public void setPolicyEntries(Set<PolicyEntry> policyEntries) {
        this.policyEntries = policyEntries;
        this.registerChange("policyEntries");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserRole)) {
            return false;
        }
        UserRole other = (UserRole)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.security.UserRole[id=" + getId() + "]";
    }

    public InternalAccessType getInternalAccessType() {
        return internalAccessType;
    }

    public void setInternalAccessType(InternalAccessType internalAccessType) {
        this.internalAccessType = internalAccessType;
        this.registerChange("internalAccessType");
    }

    public Set<Long> getAccessAccountIds() {
        return new ChangesSupportSet<Long>(this, "accessAccountIds", accessAccountIds);
    }

    public void setAccessAccountIds(Set<Long> accessAccountIds) {
        this.accessAccountIds = accessAccountIds;
        this.registerChange("accessAccountIds");
    }
}
