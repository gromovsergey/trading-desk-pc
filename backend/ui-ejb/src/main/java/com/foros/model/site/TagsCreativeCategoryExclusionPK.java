package com.foros.model.site;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary Key class TagsCreativeCategoryExclusionPK for entity class TagsCreativeCategoryExclusion
 * 
 * @author Denis Korneev
 */
@Embeddable
public class TagsCreativeCategoryExclusionPK implements Serializable {
    @Column(name = "TAG_ID", nullable = false)
    private long tagId;
    
    @Column(name = "CREATIVE_CATEGORY_ID", nullable = false)
    private long creativeCategoryId;

    /** 
     * Creates a new instance of TagsCreativeCategoryExclusionPK 
     */
    public TagsCreativeCategoryExclusionPK() {
    }

    /**
     * Creates a new instance of TagsCreativeCategoryExclusionPK with the specified values.
     * @param creativeCategoryId the creativeCategoryId of the TagsCreativeCategoryExclusionPK
     * @param tagId the tagId of the TagsCreativeCategoryExclusionPK
     */
    public TagsCreativeCategoryExclusionPK(long creativeCategoryId, long tagId) {
        this.creativeCategoryId = creativeCategoryId;
        this.tagId = tagId;
    }

    /**
     * Gets the tagId of this TagsCreativeCategoryExclusionPK.
     * @return the tagId
     */
    public long getTagId() {
        return this.tagId;
    }

    /**
     * Sets the tagId of this TagsCreativeCategoryExclusionPK to the specified value.
     * @param tagId the new tagId
     */
    public void setTagId(long tagId) {
        this.tagId = tagId;
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
        hash += (int) getTagId();
        return hash;
    }

    /**
     * Determines whether another object is equal to this TagsCreativeCategoryExclusionPK.  The result is 
     * <code>true</code> if and only if the argument is not null and is a TagsCreativeCategoryExclusionPK object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TagsCreativeCategoryExclusionPK)) {
            return false;
        }
        TagsCreativeCategoryExclusionPK other = (TagsCreativeCategoryExclusionPK)object;
        if (this.getCreativeCategoryId() != other.getCreativeCategoryId()) {
            return false;
        }
        if (this.getTagId() != other.getTagId()) {
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
        return "com.foros.model.site.TagsCreativeCategoryExclusionPK[creativeCategoryId=" + getCreativeCategoryId() + ", tagId=" + getTagId() + "]";
    }
}
