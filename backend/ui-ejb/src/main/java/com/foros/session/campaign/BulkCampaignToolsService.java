package com.foros.session.campaign;

import com.foros.model.ExtensionProperty;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.TGTType;
import com.foros.session.TooManyRowsException;
import com.foros.session.campaign.bulk.BulkParseResult;

import java.util.Collection;

import javax.ejb.Local;

@Local
public interface BulkCampaignToolsService {

    ExtensionProperty<CampaignStatsTO> CAMPAIGN_STATS = new ExtensionProperty<CampaignStatsTO>(CampaignStatsTO.class);

    Collection<Campaign> findForExport(Long accountId, TGTType tgtType, Collection<Long> ids, int maxResultSize) throws TooManyRowsException;

    ValidationResultTO validateAll(Long accountId, TGTType tgtType, BulkParseResult result);

    BulkParseResult getValidatedResults(Long accountId, String validationResultId);

    void createOrUpdateAll(Long accountId, String validationResultId);

    void deleteObsoleteValidatedResults();
}
