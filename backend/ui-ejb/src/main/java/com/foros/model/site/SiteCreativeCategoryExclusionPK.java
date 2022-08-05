package com.foros.model.site;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary Key class SiteCreativeCategoryExclusionPK for entity class SiteCreativeCategoryExclusion
 * 
 * @author Andrey Chernyshov
 */
@Embeddable
public class SiteCreativeCategoryExclusionPK implements Serializable {
    @Column(name = "SITE_ID", nullable = false)
    private long siteId;
    
    @Column(name = "CREATIVE_CATEGORY_ID", nullable = false)
    private long creativeCategoryId;

    /** 
     * Creates a new instance of SiteCreativeCategoryExclusionPK 
     */
    public SiteCreativeCategoryExclusionPK() {
    }

    /**
     * Creates a new instance of SiteCreativeCategoryExclusionPK with the specified values.
     * @param creativeCategoryId the creativeCategoryId of the SiteCreativeCategoryExclusionPK
     * @param siteId the siteId of the SiteCreativeCategoryExclusionPK
     */
    public SiteCreativeCategoryExclusionPK(long creativeCategoryId, long siteId) {
        this.creativeCategoryId = creativeCategoryId;
        this.siteId = siteId;
    }

    /**
     * Gets the siteId of this SiteCreativeCategoryExclusionPK.
     * @return the siteId
     */
    public long getSiteId() {
        return this.siteId;
    }

    /**
     * Sets the siteId of this SiteCreativeCategoryExclusionPK to the specified value.
     * @param siteId the new siteId
     */
    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getCreativeCategoryId() {
        return creativeCategoryId;
    }

    public void setCreativeCategoryId(long creativeCatedoryId) {
        this.creativeCategoryId = creativeCatedoryId;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) getCreativeCategoryId();
        hash += (int) getSiteId();
        return hash;
    }

    /**
     * Determines whether another object is equal to this SiteCreativeCategoryExclusionPK.  The result is 
     * <code>true</code> if and only if the argument is not null and is a SiteCreativeCategoryExclusionPK object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiteCreativeCategoryExclusionPK)) {
            return false;
        }
        SiteCreativeCategoryExclusionPK other = (SiteCreativeCategoryExclusionPK)object;
        if (this.getCreativeCategoryId() != other.getCreativeCategoryId()) {
            return false;
        }
        if (this.getSiteId() != other.getSiteId()) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.site.SiteCreativeCategoryExclusionPK[creativeCategoryId=" + getCreativeCategoryId() + ", siteId=" + getSiteId() + "]";
    }
}
