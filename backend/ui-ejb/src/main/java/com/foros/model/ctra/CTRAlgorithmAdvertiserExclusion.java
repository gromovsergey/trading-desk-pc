package com.foros.model.ctra;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.CTRAlgorithmAdvertiserExclusionAuditSerializer;
import com.foros.model.EntityBase;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CTRALGADVERTISEREXCLUSION")
@Audit(serializer = CTRAlgorithmAdvertiserExclusionAuditSerializer.class)
public class CTRAlgorithmAdvertiserExclusion extends EntityBase implements Serializable {

    @EmbeddedId
    @ChangesInspection(type = InspectionType.NONE)
    private CTRAlgorithmAdvertiserExclusionPK pk;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false)
    @ManyToOne
    @ChangesInspection(type = InspectionType.NONE)
    private CTRAlgorithmData algorithmData;

    public CTRAlgorithmAdvertiserExclusion() {
    }

    public CTRAlgorithmAdvertiserExclusionPK getPk() {
        return pk;
    }

    public void setPk(CTRAlgorithmAdvertiserExclusionPK pk) {
        this.pk = pk;
        registerChange("pk");
    }

    public CTRAlgorithmData getAlgorithmData() {
        return algorithmData;
    }

    public void setAlgorithmData(CTRAlgorithmData algorithmData) {
        this.algorithmData = algorithmData;
        registerChange("algorithmData");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CTRAlgorithmAdvertiserExclusion that = (CTRAlgorithmAdvertiserExclusion) o;

        if (!pk.equals(that.pk)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pk.hashCode();
    }
}
