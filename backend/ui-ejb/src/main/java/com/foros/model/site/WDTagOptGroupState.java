package com.foros.model.site;


import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.VersionEntityBase;
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
@Table(name = "WDTAGOPTGROUPSTATE")
@NamedQueries({ @NamedQuery(name = "WDTagOptGroupState.findAllByOptionGroupId", query = "SELECT c FROM WDTagOptGroupState c WHERE c.id.optionGroupId = :og_id ") })
public class WDTagOptGroupState extends VersionEntityBase implements OptionGroupState {
    @EmbeddedId
    private WDTagOptGroupStatePK id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "WDTAG_ID", referencedColumnName = "WDTAG_ID", insertable = false, updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private WDTag tag;

    @RequiredConstraint
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = true;

    @RequiredConstraint
    @Column(name = "COLLAPSED", nullable = false)
    private Boolean collapsed = false;

    public WDTagOptGroupStatePK getId() {
        return id;
    }

    public void setId(WDTagOptGroupStatePK id) {
        this.id = id;
        this.registerChange("id");
    }

    public WDTag getTag() {
        return this.tag;
    }

    public void setTag(WDTag tag) {
        this.tag = tag;
        this.registerChange("tag");
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
        this.registerChange("enabled");
    }

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

        WDTagOptGroupState that = (WDTagOptGroupState) o;

        if (!collapsed.equals(that.collapsed)) return false;
        if (!enabled.equals(that.enabled)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return com.foros.util.HashUtil.calculateHash(id, enabled, collapsed);
    }
}
