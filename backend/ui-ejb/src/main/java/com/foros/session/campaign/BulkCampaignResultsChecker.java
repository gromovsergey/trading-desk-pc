package com.foros.session.campaign;

import javax.ejb.Local;

@Local
public interface BulkCampaignResultsChecker {
    public void proceed();
}