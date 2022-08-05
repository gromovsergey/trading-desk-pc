package com.foros.model.site;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.SiteCreativeCategoryExclusionAuditSerializer;
import com.foros.model.EntityBase;
import com.foros.model.creative.CreativeCategory;

import com.foros.validation.annotation.CascadeValidation;
import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SITECREATIVECATEGORYEXCLUSION")
@NamedQueries(
{
  @NamedQuery(name = "SiteCreativeCategoryExclusion.findSiteByCategory", query = "SELECT s.site FROM SiteCreativeCategoryExclusion s WHERE s.creativeCategory = :category ORDER BY s.site.account.name ASC, s.site.name ASC")
})
@Audit(serializer = SiteCreativeCategoryExclusionAuditSerializer.class)
public class SiteCreativeCategoryExclusion extends EntityBase implements Serializable {
    /**
     * EmbeddedId primary key field
     */
    @ChangesInspection(type = InspectionType.NONE)
    @EmbeddedId
    private SiteCreativeCategoryExclusionPK siteCreativeCategoryExclusionPK;
    
    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "SITE_ID", insertable = false, updatable = false)
    @ManyToOne
    private Site site;

    @CascadeValidation
    @JoinColumn(name = "CREATIVE_CATEGORY_ID", referencedColumnName = "CREATIVE_CATEGORY_ID", insertable = false, updatable = false)
    @ManyToOne
    private CreativeCategory creativeCategory;
    
    @Column(name = "APPROVAL", nullable = false)
    private char approval;

    /** 
     * Creates a new instance of SiteCreativeCategoryExclusion 
     */
    public SiteCreativeCategoryExclusion() {
    }

    /**
     * Creates a new instance of SiteCreativeCategoryExclusion with the specified values.
     * @param siteCreativeCategoryExclusionPK the siteCreativeCategoryExclusionPK of the SiteCreativeCategoryExclusion
     */
    public SiteCreativeCategoryExclusion(SiteCreativeCategoryExclusionPK siteCreativeCategoryExclusionPK) {
        this.siteCreativeCategoryExclusionPK = siteCreativeCategoryExclusionPK;
    }

    /**
     * Creates a new instance of SiteCreativeCategoryExclusionPK with the specified values.
     * @param creativeCategoryId the creativeCategoryId of the SiteCreativeCategoryExclusionPK
     * @param siteId the siteId of the SiteCreativeCategoryExclusionPK
     */
    public SiteCreativeCategoryExclusion(long creativeCategoryId, long siteId) {
        this(creativeCategoryId, siteId, CategoryExclusionApproval.ACCEPT);
    }

    public SiteCreativeCategoryExclusion(long creativeCategoryId, long siteId, CategoryExclusionApproval approval) {
        this.siteCreativeCategoryExclusionPK = new SiteCreativeCategoryExclusionPK(creativeCategoryId, siteId);
        this.approval = approval.getLetter();
    }

    /**
     * Gets the SiteCreativeCategoryExclusionPK of this SiteCreativeCategoryExclusion.
     * @return the SiteCreativeCategoryExclusionPK
     */
    public SiteCreativeCategoryExclusionPK getSiteCreativeCategoryExclusionPK() {
        return this.siteCreativeCategoryExclusionPK;
    }

    /**
     * Sets the SiteCreativeCategoryExclusionPK of this SiteCreativeCategoryExclusion to the specified value.
     *
     * @param siteCreativeCategoryExclusionPK
     *         the new SiteCreativeCategoryExclusionPK
     */
    public void setSiteCreativeCategoryExclusionPK(SiteCreativeCategoryExclusionPK siteCreativeCategoryExclusionPK) {
        this.siteCreativeCategoryExclusionPK = siteCreativeCategoryExclusionPK;
        this.registerChange("siteCreativeCategoryExclusionPK");
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getSiteCreativeCategoryExclusionPK() != null ? this.getSiteCreativeCategoryExclusionPK().hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this SiteCreativeCategoryExclusion.  The result is 
     * <code>true</code> if and only if the argument is not null and is a SiteCreativeCategoryExclusion object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof SiteCreativeCategoryExclusion)) {
            return false;
        }

        SiteCreativeCategoryExclusion other = (SiteCreativeCategoryExclusion)object;

        if (this.getSiteCreativeCategoryExclusionPK() == null || other.getSiteCreativeCategoryExclusionPK() == null) {
            return false;
        }

        if (!ObjectUtils.equals(this.getSiteCreativeCategoryExclusionPK(), other.getSiteCreativeCategoryExclusionPK())) {
            return false;
        }

        return true;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
        this.registerChange("site");
    }

    public CreativeCategory getCreativeCategory() {
        return creativeCategory;
    }

    public void setCreativeCategory(CreativeCategory creativeCategory) {
        this.creativeCategory = creativeCategory;
        this.registerChange("creativeCategory");
    }

    public CategoryExclusionApproval getApproval() {
        return CategoryExclusionApproval.valueOf(approval);
    }

    public void setApproval(CategoryExclusionApproval approval) {
        this.approval = approval.getLetter();
        this.registerChange("approval");
    }

    public SiteCreativeCategoryExclusionPK getId() {
        return this.siteCreativeCategoryExclusionPK;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.site.SiteCreativeCategoryExclusion[siteCreativeCategoryExclusionPK=" + getSiteCreativeCategoryExclusionPK() + "]";
    }
}
