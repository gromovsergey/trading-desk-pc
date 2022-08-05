package com.foros.model.site;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class WDTagOptGroupStatePK implements Serializable {
    @Column(name = "OPTION_GROUP_ID", nullable = false)
    private long optionGroupId;

    @Column(name = "WDTAG_ID", nullable = false)
    private long wdTagId;

    public WDTagOptGroupStatePK() {
    }

    public WDTagOptGroupStatePK(long optionGroupId, long wdTagId) {
        this.optionGroupId = optionGroupId;
        this.wdTagId = wdTagId;
    }

    public long getOptionGroupId() {
        return optionGroupId;
    }

    public void setOptionGroupId(long optionGroupId) {
        this.optionGroupId = optionGroupId;
    }

    public long getWdTagId() {
        return wdTagId;
    }

    public void setWdTagId(long wdTagId) {
        this.wdTagId = wdTagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WDTagOptGroupStatePK that = (WDTagOptGroupStatePK) o;

        if (optionGroupId != that.optionGroupId) return false;
        if (wdTagId != that.wdTagId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (optionGroupId ^ (optionGroupId >>> 32));
        result = 31 * result + (int) (wdTagId ^ (wdTagId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "WDTagOptGroupStatePK[" +
                "optionGroupId=" + optionGroupId +
                ", wdTagId=" + wdTagId +
                ']';
    }
}