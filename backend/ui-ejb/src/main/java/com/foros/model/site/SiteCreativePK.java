package com.foros.model.site;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SiteCreativePK implements Serializable {
    @Column(name = "SITE_ID", nullable = false)
    private long siteId;

    @Column(name = "CREATIVE_ID", nullable = false)
    private long creativeId;

    public SiteCreativePK() {
    }

    public SiteCreativePK(long creativeId, long siteId) {
        this.creativeId = creativeId;
        this.siteId = siteId;
    }

    public long getSiteId() {
        return this.siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(long creativeId) {
        this.creativeId = creativeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) getCreativeId();
        hash += (int) getSiteId();
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiteCreativePK)) {
            return false;
        }
        SiteCreativePK other = (SiteCreativePK)object;
        if (this.getCreativeId() != other.getCreativeId()) {
            return false;
        }
        if (this.getSiteId() != other.getSiteId()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "creativeId=" + getCreativeId() + ", siteId=" + getSiteId();
    }
}
