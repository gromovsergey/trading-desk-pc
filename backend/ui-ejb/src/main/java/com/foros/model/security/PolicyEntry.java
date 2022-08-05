package com.foros.model.security;

import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.entity.PolicyEntryAuditSerializer;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "POLICY")
@NamedQueries({
    @NamedQuery(name = "PolicyEntry.findPermissionsByUserRole", query = "SELECT p FROM PolicyEntry p WHERE p.userRole.id = :userRoleId"),
    @NamedQuery(name = "PolicyEntry.findPolicy", query = "select p from PolicyEntry p where p.userRole.id = :userRoleId and p.type = :type and p.action = :action"),
    @NamedQuery(name = "PolicyEntry.findPolicyFor", query = "select p from PolicyEntry p where p.userRole.id = :userRoleId and p.type = :type and p.action = :action and p.parameter = :parameter")
})
@Audit(serializer = PolicyEntryAuditSerializer.class)
public class PolicyEntry extends VersionEntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "PolicyGen", sequenceName = "POLICY_PERM_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PolicyGen")
    @Column(name = "PERM_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "USER_ROLE_ID", referencedColumnName = "USER_ROLE_ID", updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserRole userRole;

    @Column(name = "PERMISSION_TYPE", nullable = false, updatable = false)
    private String type;

    @Column(name = "ACTION_TYPE", nullable = false, updatable = false)
    private String action;

    @Column(name = "PARAMETER", updatable = false)
    private String parameter;

    public PolicyEntry() {
    }

    public PolicyEntry(String type, String action) {
        this(type, action, null);
    }

    public PolicyEntry(String type, String action, String parameter) {
        this.type = type;
        this.action = action;
        this.parameter = parameter;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
        this.registerChange("userRole");
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
        this.registerChange("action");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.registerChange("type");
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
        this.registerChange("parameter");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PolicyEntry)) {
            return false;
        }

        PolicyEntry that = (PolicyEntry) o;

        if (!ObjectUtils.equals(this.id, that.id)) {
            return false;
        }

        if (!ObjectUtils.equals(this.type, that.type)) {
            return false;
        }

        if (!ObjectUtils.equals(this.action, that.action)) {
            return false;
        }

        if (!ObjectUtils.equals(this.parameter, that.parameter)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PolicyEntry{" +
                "id=" + id +
                ", userRole=" + userRole +
                ", type='" + type + '\'' +
                ", action='" + action + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
