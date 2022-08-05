package com.foros.model.site;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.entity.TagsCreativeCategoryExclusionAuditSerializer;
import org.apache.commons.lang.ObjectUtils;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.EntityBase;
import com.foros.model.creative.CreativeCategory;


@Entity
@Table(name = "TAGSCREATIVECATEGORYEXCLUSION")
@NamedQueries(
{
  @NamedQuery(name = "TagsCreativeCategoryExclusion.findTagByCategory", query = "SELECT t.tag FROM TagsCreativeCategoryExclusion t WHERE t.creativeCategory = :category")
})
@Audit(serializer = TagsCreativeCategoryExclusionAuditSerializer.class)
public class TagsCreativeCategoryExclusion extends EntityBase implements Serializable {
    /**
     * EmbeddedId primary key field
     */
    @ChangesInspection(type = InspectionType.NONE)
    @EmbeddedId
    private TagsCreativeCategoryExclusionPK tagsCreativeCategoryExclusionPK;
    
    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID", insertable = false, updatable = false)
    @ManyToOne
    private Tag tag;
    
    @JoinColumn(name = "CREATIVE_CATEGORY_ID", referencedColumnName = "CREATIVE_CATEGORY_ID", insertable = false, updatable = false)
    @ManyToOne
    private CreativeCategory creativeCategory;
    
    @Column(name = "APPROVAL", nullable = false)
    private char approval;

    /** 
     * Creates a new instance of SiteCreativeCategoryExclusion 
     */
    public TagsCreativeCategoryExclusion() {
    }

    /**
     * Creates a new instance of SiteCreativeCategoryExclusion with the specified values.
     * @param tagsCreativeCategoryExclusionPK the tagsCreativeCategoryExclusionPK of the SiteCreativeCategoryExclusion
     */
    public TagsCreativeCategoryExclusion(TagsCreativeCategoryExclusionPK tagsCreativeCategoryExclusionPK) {
        this.tagsCreativeCategoryExclusionPK = tagsCreativeCategoryExclusionPK;
    }

    /**
     * Creates a new instance of TagsCreativeCategoryExclusionPK with the specified values.
     * @param creativeCategoryId the creativeCategoryId of the TagsCreativeCategoryExclusionPK
     * @param tagId the tagId of the TagsCreativeCategoryExclusionPK
     */
    public TagsCreativeCategoryExclusion(long creativeCategoryId, long tagId) {
        this(creativeCategoryId, tagId, CategoryExclusionApproval.ACCEPT);
    }

    public TagsCreativeCategoryExclusion(long creativeCategoryId, long tagId, CategoryExclusionApproval approval) {
        this.tagsCreativeCategoryExclusionPK = new TagsCreativeCategoryExclusionPK(creativeCategoryId, tagId);
        this.approval = approval.getLetter();
    }

    /**
     * Gets the TagsCreativeCategoryExclusionPK of this SiteCreativeCategoryExclusion.
     * @return the TagsCreativeCategoryExclusionPK
     */
    public TagsCreativeCategoryExclusionPK getTagsCreativeCategoryExclusionPK() {
        return this.tagsCreativeCategoryExclusionPK;
    }

    /**
     * Sets the TagsCreativeCategoryExclusionPK of this SiteCreativeCategoryExclusion to the specified value.
     *
     * @param tagsCreativeCategoryExclusionPK
     *         the new TagsCreativeCategoryExclusionPK
     */
    public void setTagsCreativeCategoryExclusionPK(TagsCreativeCategoryExclusionPK tagsCreativeCategoryExclusionPK) {
        this.tagsCreativeCategoryExclusionPK = tagsCreativeCategoryExclusionPK;
        this.registerChange("tagsCreativeCategoryExclusionPK");
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += 31*(this.getTagsCreativeCategoryExclusionPK() != null ? this.getTagsCreativeCategoryExclusionPK().hashCode() : 0);
        hash += 31*(this.getApproval() != null ? this.getApproval().hashCode() : 0);
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

        if (!(object instanceof TagsCreativeCategoryExclusion)) {
            return false;
        }

        TagsCreativeCategoryExclusion other = (TagsCreativeCategoryExclusion)object;

        if (this.getTagsCreativeCategoryExclusionPK() == null || other.getTagsCreativeCategoryExclusionPK() == null) {
            return false;
        }
        
        if (!ObjectUtils.equals(this.getTagsCreativeCategoryExclusionPK(), other.getTagsCreativeCategoryExclusionPK())) {
            return false;
        }
        
        if (!ObjectUtils.equals(this.getApproval(), other.getApproval())) {
            return false;
        }

        return true;
    }

    public Tag getTag() {
        return tag;
    }

    public void setSite(Tag tag) {
        this.tag = tag;
        this.registerChange("tag");
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

    public TagsCreativeCategoryExclusionPK getId() {
        return this.tagsCreativeCategoryExclusionPK;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.site.TagsCreativeCategoryExclusion[tagsCreativeCategoryExclusionPK=" + getTagsCreativeCategoryExclusionPK() + "]";
    }

}
