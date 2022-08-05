package com.foros.model.site;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.WDTagOptionValueAuditSerializer;
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
@Table(name = "WDTAGOPTIONVALUE")
@Audit(serializer = WDTagOptionValueAuditSerializer.class)
public class WDTagOptionValue extends AbstractOptionValue {
    @EmbeddedId
    private WDTagOptionValuePK id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "WDTAG_ID", referencedColumnName = "WDTAG_ID", insertable = false, updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private WDTag tag;

    public WDTagOptionValue() {
    }

    public WDTagOptionValue(WDTagOptionValuePK id) {
        this.id = id;
    }

    public WDTagOptionValue(long wdTagId, long optionId) {
        this.id = new WDTagOptionValuePK(wdTagId, optionId);
    }

    public WDTagOptionValuePK getId() {
        return this.id;
    }

    public void setId(WDTagOptionValuePK id) {
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

    @Override
    @Transient
    public Long getOptionId() {
        long optionId = getOption() == null || getOption().getId() == null ? getId().getOptionId() : getOption().getId();
        return optionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        if (this.getId() != null) {
            hash += this.getId().hashCode();
        } else {
            // On web layer combined PK is not initialized, need to calculate hash code explicitly
            long optionId = this.getOption() != null ? getOption().getId() : 0;
            long tagId = this.getTag() != null ? (getTag().getId() != null ? getTag().getId() : 0) : 0;

            int result = (int) (optionId ^ (optionId >>> 32));
            result = 31 * result + (int) (tagId ^ (tagId >>> 32));

            hash += result;
        }

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof WDTagOptionValue)) {
            return false;
        }

        WDTagOptionValue other = (WDTagOptionValue) object;

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

    @Override
    public String toString() {
        return "com.foros.model.site.WDTagOptionValue[id=" + getId() + "]";
    }

    @Override
    public Account getAccount() {
        return getTag() == null ? null : getTag().getAccount();
    }
}
