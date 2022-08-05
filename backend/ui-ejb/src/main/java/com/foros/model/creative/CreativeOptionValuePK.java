package com.foros.model.creative;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CreativeOptionValuePK implements Serializable {
    @Column(name = "OPTION_ID", nullable = false)
    private long optionId;
    
    @Column(name = "CREATIVE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private long creativeId;

    public CreativeOptionValuePK() {
    }

    public CreativeOptionValuePK(long creativeId, long optionId) {
        this.creativeId = creativeId;
        this.optionId = optionId;
    }

    public long getOptionId() {
        return this.optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public long getCreativeId() {
        return this.creativeId;
    }

    public void setCreativeId(long creativeId) {
        this.creativeId = creativeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) getCreativeId();
        hash += (int) getOptionId();
        
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CreativeOptionValuePK)) {
            return false;
        }
        
        CreativeOptionValuePK other = (CreativeOptionValuePK)object;
        if (this.getCreativeId() != other.getCreativeId()) {
            return false;
        }
        
        if (this.getOptionId() != other.getOptionId()) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "PK[creativeId=" + getCreativeId() + ", creativeOptionId=" + getOptionId() + "]";
    }
}
