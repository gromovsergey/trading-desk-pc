package com.foros.model.site;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class WDTagOptionValuePK implements Serializable {
    @Column(name = "OPTION_ID", nullable = false)
    private long optionId;

    @Column(name = "WDTAG_ID", nullable = false)
    private long wdTagId;

    public WDTagOptionValuePK() {
    }

    public WDTagOptionValuePK(long wdTagId, long optionId) {
        this.wdTagId = wdTagId;
        this.optionId = optionId;
    }

    public long getOptionId() {
        return this.optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public long getWdTagId() {
        return this.wdTagId;
    }

    public void setWdTagId(long wdTagId) {
        this.wdTagId = wdTagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof WDTagOptionValuePK)) {
            return false;
        }

        WDTagOptionValuePK that = (WDTagOptionValuePK) o;

        if (optionId != that.optionId) {
            return false;
        }

        if (wdTagId != that.wdTagId) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (optionId ^ (optionId >>> 32));
        result = 31 * result + (int) (wdTagId ^ (wdTagId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "com.foros.model.site.WDTagOptionValuePK[wdTagId=" + getWdTagId() + ", optionId=" + getOptionId() + "]";
    }
}
