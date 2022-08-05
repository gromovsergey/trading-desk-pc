package com.foros.model.site;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TagOptionValuePK implements Serializable {

    @Column(name = "TAG_ID", nullable = false)
    private long tagId;

    @Column(name = "OPTION_ID", nullable = false)
    private long optionId;
    
    public TagOptionValuePK() {
    }

    public TagOptionValuePK(long tagId, long optionId) {
        this.tagId = tagId;
        this.optionId = optionId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) getTagId();
        hash += (int) getOptionId();
        
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TagOptionValuePK)) {
            return false;
        }
        
        TagOptionValuePK other = (TagOptionValuePK)object;
        if (this.getTagId() != other.getTagId()) {
            return false;
        }
        
        if (this.getOptionId() != other.getOptionId()) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.site.TagOptionValuePK[tagId=" + getTagId() + ", optionId=" + getOptionId() + "]";
    }
}
