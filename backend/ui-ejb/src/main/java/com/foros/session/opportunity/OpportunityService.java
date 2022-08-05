package com.foros.session.opportunity;

import com.foros.model.ExtensionProperty;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationSumTO;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.session.fileman.ContentSource;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface OpportunityService {
    ExtensionProperty<Collection> ADDED_IO_FILES = new ExtensionProperty<Collection>(Collection.class);
    ExtensionProperty<Collection> REMOVED_IO_FILES = new ExtensionProperty<Collection>(Collection.class);

    Opportunity find(Long id);

    Long create(Opportunity opportunity, Map<String, File> ioFiles);

    Opportunity update(Opportunity opportunity, Map<String, File> ioFiles);

    Opportunity view(Long id);

    Collection<Opportunity> findOpportunitiesForAccount(Long accountId);

    Set<String> getIOFileNames(Opportunity opportunity);

    ContentSource getIOFileContent(Long opportunityId, String fileName);

    Collection<Probability> getAvailableProbabilities(Long accountId, Probability existingProbability);

    Opportunity viewIO(Long id);

    List<CampaignAllocation> getCampaignAllocations(Long opportunityId);

    List<CampaignAllocationSumTO> findCampaignAllocationSum(Long opportunityId);
}
