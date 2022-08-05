package com.foros.model.creative;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.CreativeOptionValueAuditSerializer;
import com.foros.jaxb.adapters.CreativeOptionValueXmlAdapter;
import com.foros.model.account.Account;
import com.foros.model.template.AbstractOptionValue;
import com.foros.model.template.OptionValueUtils;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.BatchSize;
@Entity
@Table(name = "CREATIVEOPTIONVALUE")
@Audit(serializer = CreativeOptionValueAuditSerializer.class)
@BatchSize(size = 50)
@XmlJavaTypeAdapter(CreativeOptionValueXmlAdapter.class)
public class CreativeOptionValue extends AbstractOptionValue {
    @EmbeddedId
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private CreativeOptionValuePK id;


    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CREATIVE_ID", referencedColumnName = "CREATIVE_ID", insertable = false, updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Creative creative;

    public CreativeOptionValue() {
    }

    public CreativeOptionValue(CreativeOptionValuePK id) {
        this.id = id;
    }

    public CreativeOptionValue(long creativeId, long creativeOptionId) {
        this.id = new CreativeOptionValuePK(creativeId, creativeOptionId);
    }

    public CreativeOptionValuePK getId() {
        return this.id;
    }

    public void setId(CreativeOptionValuePK id) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);

        return hash;
    }

    @Override
    public String getFileStripped() {
        return OptionValueUtils.getFileStripped(this);
    }

    @Override
    public boolean isFile() {
        return OptionValueUtils.isFile(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CreativeOptionValue)) {
            return false;
        }

        CreativeOptionValue other = (CreativeOptionValue)object;
        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getCreative(), other.getCreative())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getOption(), other.getOption())) {
            return false;
        }

        return true;
    }

    @Override
    @Transient
    public Long getOptionId() {
        return getOption() == null ? (Long) getId().getOptionId() : getOption().getId();
    }

    @Override
    public String toString() {
        return "CreativeOptionValue[id=" + getId() + ", value=" + getValue() + "]";
    }

    @Override
    public Account getAccount() {
        return getCreative() == null ? null : getCreative().getAccount();
    }
}
