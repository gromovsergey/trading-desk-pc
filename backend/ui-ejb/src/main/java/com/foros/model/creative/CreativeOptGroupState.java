package com.foros.model.creative;


import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.VersionEntityBase;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupState;
import com.foros.validation.constraint.RequiredConstraint;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "CREATIVEOPTGROUPSTATE")
@NamedQueries({ @NamedQuery(name = "CreativeOptGroupState.findAllByOptionGroupId", query = "SELECT c FROM CreativeOptGroupState c WHERE c.id.optionGroupId = :og_id ") })
public class CreativeOptGroupState extends VersionEntityBase implements OptionGroupState {
    @EmbeddedId
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private CreativeOptGroupStatePK id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CREATIVE_ID", referencedColumnName = "CREATIVE_ID", insertable = false, updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Creative creative;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "OPTION_GROUP_ID", referencedColumnName = "OPTION_GROUP_ID", insertable = false, updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private OptionGroup group;

    @RequiredConstraint
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = true;

    @RequiredConstraint
    @Column(name = "COLLAPSED", nullable = false)
    private Boolean collapsed = false;

    public CreativeOptGroupStatePK getId() {
        return id;
    }

    public void setId(CreativeOptGroupStatePK id) {
        this.id = id;
        this.registerChange("id");
    }

    public Creative getCreative() {
        return this.creative;
    }

    public void setCreative(Creative creative) {
        this.creative = creative;
        this.registerChange("creative");
    }

    public OptionGroup getGroup() {
        return group;
    }

    public void setGroup(OptionGroup group) {
        this.group = group;
        this.registerChange("group");
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
        this.registerChange("enabled");
    }

    @Override
    public Boolean getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
        this.registerChange("collapsed");
    }

    @Override
    public Long getGroupId() {
        return getId().getOptionGroupId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreativeOptGroupState that = (CreativeOptGroupState) o;

        if (id != null) {
            return id.equals(that.id);
        }

        if (that.id != null) {
            return false;
        }

        if (!collapsed.equals(that.collapsed)) return false;
        if (!enabled.equals(that.enabled)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return com.foros.util.HashUtil.calculateHash(id, enabled, collapsed);
    }

    @Override
    public String toString() {
        return "CreativeOptGroupState[id=" + getId() + "]";
    }
}
