package com.foros.model.ctra;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.CTRAlgorithmCampaignExclusionAuditSerializer;
import com.foros.model.EntityBase;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CTRALGCAMPAIGNEXCLUSION")
@Audit(serializer = CTRAlgorithmCampaignExclusionAuditSerializer.class)
public class CTRAlgorithmCampaignExclusion extends EntityBase implements Serializable {

    @EmbeddedId
    @ChangesInspection(type = InspectionType.NONE)
    private CTRAlgorithmCampaignExclusionPK pk;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false)
    @ManyToOne
    @ChangesInspection(type = InspectionType.NONE)
    private CTRAlgorithmData algorithmData;

    public CTRAlgorithmCampaignExclusion() {
    }

    public CTRAlgorithmCampaignExclusionPK getPk() {
        return pk;
    }

    public void setPk(CTRAlgorithmCampaignExclusionPK pk) {
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

        CTRAlgorithmCampaignExclusion that = (CTRAlgorithmCampaignExclusion) o;

        if (!pk.equals(that.pk)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pk.hashCode();
    }
}
