package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.*;
import com.foros.session.bulk.BulkOperation;

import java.math.BigDecimal;

public class SetBidStrategyOperation implements BulkOperation<CampaignCreativeGroup> {
    private BidStrategy bidStrategy;
    private BigDecimal minCtrGoal;

    public SetBidStrategyOperation(BidStrategy bidStrategy, BigDecimal minCtrGoal) {
        this.bidStrategy = bidStrategy;
        this.minCtrGoal = minCtrGoal;
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        toUpdate.setBidStrategy(bidStrategy);
        toUpdate.setMinCtrGoal(minCtrGoal);
    }

    public BigDecimal getMinCtrGoal() {
        return minCtrGoal;
    }
}
