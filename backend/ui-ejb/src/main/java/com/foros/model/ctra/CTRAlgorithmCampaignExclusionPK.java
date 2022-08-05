package com.foros.model.ctra;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CTRAlgorithmCampaignExclusionPK implements Serializable{
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "CAMPAIGN_ID")
    private Long campaignId;

    public CTRAlgorithmCampaignExclusionPK() {
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CTRAlgorithmCampaignExclusionPK that = (CTRAlgorithmCampaignExclusionPK) o;

        if (!campaignId.equals(that.campaignId)) return false;
        if (!countryCode.equals(that.countryCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + campaignId.hashCode();
        return result;
    }
}
