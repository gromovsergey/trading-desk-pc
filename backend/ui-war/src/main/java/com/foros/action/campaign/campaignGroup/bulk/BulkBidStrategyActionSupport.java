package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.model.campaign.BidStrategy;
import java.math.BigDecimal;

public class BulkBidStrategyActionSupport extends CcgEditBulkActionSupport {
    protected BidStrategy bidStrategy = BidStrategy.MAXIMISE_REACH;
    protected BigDecimal minCtrGoal = BigDecimal.ZERO;

    public BidStrategy getBidStrategy() {
        return bidStrategy;
    }

    public void setBidStrategy(BidStrategy bidStrategy) {
        this.bidStrategy = bidStrategy;
    }

    public BigDecimal getMinCtrGoal() {
        return minCtrGoal;
    }

    public void setMinCtrGoal(BigDecimal minCtrGoal) {
        this.minCtrGoal = minCtrGoal;
    }
}
