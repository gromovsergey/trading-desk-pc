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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TAGOPTGROUPSTATE")
@NamedQueries({ @NamedQuery(name = "TagOptGroupState.findAllByOptionGroupId", query = "SELECT c FROM TagOptGroupState c WHERE c.id.optionGroupId = :og_id ") })
public class TagOptGroupState extends VersionEntityBase implements OptionGroupState {
    @EmbeddedId
    private TagOptGroupStatePK id;

    @RequiredConstraint
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = true;

    @RequiredConstraint
    @Column(name = "COLLAPSED", nullable = false)
    private Boolean collapsed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID", insertable = false, updatable = false)
    @ChangesInspection(type = InspectionType.NONE)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Tag tag;

    public TagOptGroupStatePK getId() {
        return id;
    }

    public void setId(TagOptGroupStatePK id) {
        this.id = id;
        this.registerChange("id");
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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        this.registerChange("tag");
    }

    @Override
    public Long getGroupId() {
        return getId().getOptionGroupId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagOptGroupState that = (TagOptGroupState) o;

        if (!id.equals(that.id)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
