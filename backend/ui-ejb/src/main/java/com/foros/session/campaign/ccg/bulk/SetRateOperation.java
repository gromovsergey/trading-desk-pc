package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.session.bulk.BulkOperation;

public class SetRateOperation implements BulkOperation<CampaignCreativeGroup> {
    private CcgRate rate;

    public SetRateOperation(CcgRate rate) {
        this.rate = rate;
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        if (existing.getCcgRate().compareFields(rate)) {
            return;
        }

        CcgRate newRate = new CcgRate();
        newRate.setRateType(rate.getRateType());
        switch (rate.getRateType()) {
            case CPM:
                newRate.setCpm(rate.getCpm());
                break;
            case CPC:
                newRate.setCpc(rate.getCpc());
                break;
            case CPA:
                newRate.setCpa(rate.getCpa());
                break;
        }

        toUpdate.setCcgRate(newRate);
    }

    public CcgRate getRate() {
        return rate;
    }
}
