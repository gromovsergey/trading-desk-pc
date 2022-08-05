package com.foros.model.site;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.IdLinkXmlAdapter;
import com.foros.model.EntityBase;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name = "thirdPartyCreative")
@XmlType(propOrder = {
        "creativeId",
        "thirdPartyCreativeId",
        "pendingThirdPartyApproval"
})
@Entity
@Table(name = "THIRDPARTYCREATIVE")
public class ThirdPartyCreative extends EntityBase implements Serializable {

    @EmbeddedId
    @ChangesInspection(type = InspectionType.NONE)
    private SiteCreativePK id = new SiteCreativePK();

    @Column(name = "THIRD_PARTY_CREATIVE_ID")
    @StringSizeConstraint(size = 255)
    private String thirdPartyCreativeId;

    @Column(name = "THIRD_PARTY_APPROVAL")
    private Boolean pendingThirdPartyApproval;

    @XmlTransient
    public SiteCreativePK getId() {
        return this.id;
    }

    public void setId(SiteCreativePK id) {
        this.id = id;
        this.registerChange("siteCreativePK");
    }

    @XmlElement(name = "creative")
    @XmlJavaTypeAdapter(IdLinkXmlAdapter.class)
    public Long getCreativeId() {
        return id.getCreativeId();
    }

    public void setCreativeId(Long creativeId) {
        setId(new SiteCreativePK(creativeId, id.getSiteId()));
    }

    @XmlTransient
    public Long getSiteId() {
        return id.getSiteId();
    }

    public void setSiteId(Long siteId) {
        setId(new SiteCreativePK(id.getCreativeId(), siteId));
    }

    public String getThirdPartyCreativeId() {
        return thirdPartyCreativeId;
    }

    public void setThirdPartyCreativeId(String thirdPartyCreativeId) {
        this.thirdPartyCreativeId = thirdPartyCreativeId;
        this.registerChange("thirdPartyCreativeId");
    }

    public Boolean getPendingThirdPartyApproval() {
        return pendingThirdPartyApproval;
    }

    public void setPendingThirdPartyApproval(Boolean pendingThirdPartyApproval) {
        this.pendingThirdPartyApproval = pendingThirdPartyApproval;
        this.registerChange("pendingThirdPartyApproval");
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (object == null || !(object instanceof ThirdPartyCreative)) {
            return false;
        }

        ThirdPartyCreative entity = (ThirdPartyCreative)object;
        return getId().equals(((ThirdPartyCreative) object).getId());
    }
}
