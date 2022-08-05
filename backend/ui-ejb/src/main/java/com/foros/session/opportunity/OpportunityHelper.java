package com.foros.session.opportunity;

import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;

public class OpportunityHelper {

    public static boolean canCreateIOFile(Opportunity opportunity) {
        return opportunity.getProbability() != Probability.LOST && isIOFileRequired(opportunity);
    }

    public static boolean canUpdateIOFiles(Opportunity opportunity) {
        return opportunity.getProbability().ordinal() < Probability.AWAITING_GO_LIVE.ordinal();
    }

    public static boolean isIOFileRequired(Opportunity opportunity) {
        return opportunity.getProbability().ordinal() >= Probability.IO_SIGNED.ordinal();
    }
}
