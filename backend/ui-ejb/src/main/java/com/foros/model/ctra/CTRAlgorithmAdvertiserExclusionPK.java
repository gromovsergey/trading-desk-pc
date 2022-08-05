package com.foros.model.ctra;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CTRAlgorithmAdvertiserExclusionPK implements Serializable{
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "ADV_ACCOUNT_ID")
    private Long advertiserId;

    public CTRAlgorithmAdvertiserExclusionPK() {
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CTRAlgorithmAdvertiserExclusionPK that = (CTRAlgorithmAdvertiserExclusionPK) o;

        if (!advertiserId.equals(that.advertiserId)) return false;
        if (!countryCode.equals(that.countryCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + advertiserId.hashCode();
        return result;
    }
}
