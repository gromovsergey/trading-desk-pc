package com.foros.model.creative;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CreativeOptGroupStatePK implements Serializable {
    @Column(name = "OPTION_GROUP_ID", nullable = false)
    private long optionGroupId;

    @Column(name = "CREATIVE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private long creativeId;

    public CreativeOptGroupStatePK() {
    }

    public CreativeOptGroupStatePK(long optionGroupId, long creativeId) {
        this.optionGroupId = optionGroupId;
        this.creativeId = creativeId;
    }

    public long getOptionGroupId() {
        return optionGroupId;
    }

    public void setOptionGroupId(long optionGroupId) {
        this.optionGroupId = optionGroupId;
    }

    public long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(long creativeId) {
        this.creativeId = creativeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreativeOptGroupStatePK that = (CreativeOptGroupStatePK) o;

        if (optionGroupId != that.optionGroupId) return false;
        if (creativeId != that.creativeId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (optionGroupId ^ (optionGroupId >>> 32));
        result = 31 * result + (int) (creativeId ^ (creativeId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PK[optionGroupId=" + optionGroupId + ", creativeId=" + creativeId + ']';
    }
}