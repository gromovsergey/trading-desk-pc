package com.foros.model.site;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.TagOptionValueAuditSerializer;
import com.foros.model.account.Account;
import com.foros.model.template.AbstractOptionValue;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "TAGOPTIONVALUE")
@Audit(serializer = TagOptionValueAuditSerializer.class)
public class TagOptionValue extends AbstractOptionValue {

    @EmbeddedId
    private TagOptionValuePK id;

    @ManyToOne
    @JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID", insertable = false, updatable = false)
    @ChangesInspection(type = InspectionType.NONE)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Tag tag;

    public TagOptionValue() {
    }

    public TagOptionValue(TagOptionValuePK id) {
        this.id = id;
    }

    public TagOptionValue(long tagId, long optionId) {
        this.id = new TagOptionValuePK(tagId, optionId);
    }

    @Override
    @Transient
    public Long getOptionId() {
        long optionId = getOption() == null ? getId().getOptionId(): getOption().getId();
        return optionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof TagOptionValue)) {
            return false;
        }

        TagOptionValue other = (TagOptionValue)object;
        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getTag(), other.getTag())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getOption(), other.getOption())) {
            return false;
        }

        return true;
    }

    public TagOptionValuePK getId() {
        return id;
    }

    public void setId(TagOptionValuePK id) {
        this.id = id;
        this.registerChange("id");
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        this.registerChange("tag");
    }

    @Override
    public String toString() {
        return "com.foros.model.site.TagOptionValue[id=" + getId() + "]";
    }

    @Override
    public Account getAccount() {
        return getTag() == null ? null : getTag().getAccount();
    }
}
