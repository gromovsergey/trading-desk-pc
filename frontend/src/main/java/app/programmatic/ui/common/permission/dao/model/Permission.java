package app.programmatic.ui.common.permission.dao.model;

import app.programmatic.ui.common.model.VersionEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "Policy")
public class Permission extends VersionEntityBase<Long> {
    @SequenceGenerator(name = "PolicyGen", sequenceName = "POLICY_PERM_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PolicyGen")
    @Column(name = "perm_id", updatable = false, nullable = false)
    private Long id;

    //@ManyToOne(fetch = FetchType.EAGER)
    @Column(name = "user_role_id", updatable = false, nullable = false)
    private Long userRoleId;

    @Column(name = "permission_type", updatable = false, nullable = false)
    private String type;

    @Column(name = "action_type", updatable = false, nullable = false)
    private String action;

    @Column(name = "parameter", updatable = false)
    private String parameter;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if(! (obj instanceof Permission)) {
            return false;
        }

        Permission other = (Permission)obj;

        return userRoleId.equals(other.userRoleId) &&
                type.equals(other.type) &&
                action.equals(other.action) &&
                (parameter == null ? other.parameter == null : parameter.equals(other.parameter));
    }

    @Override
    public int hashCode() {
        int base = 31;
        int result = userRoleId.hashCode();
        result = result * base + type.hashCode();
        result = result * base + action.hashCode();
        result = result * base + (parameter == null ? 0 : parameter.hashCode());

        return result;
    }
}
