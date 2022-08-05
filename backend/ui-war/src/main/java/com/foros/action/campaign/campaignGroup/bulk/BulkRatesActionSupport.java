package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.RateType;
import com.foros.model.security.AccountType;

import java.math.BigDecimal;
import java.util.List;

public class BulkRatesActionSupport extends CcgEditBulkActionSupport {

    protected RateType rateType;
    protected BigDecimal rateValue;

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public void setRateValue(BigDecimal rateValue) {
        this.rateValue = rateValue;
    }

    public List<RateType> getRateTypes() {
        AccountType accountType = getAccount().getAccountType();
        Campaign campaign = getCampaign();
        return accountType.getAllowedRateTypes(CCGType.valueOf(campaign.getCampaignType()));
    }
}
