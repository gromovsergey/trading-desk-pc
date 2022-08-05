package com.foros.session.channel.service;

import com.foros.model.Country;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.session.channel.PlacementsBlacklistValidationResultTO;
import com.foros.util.jpa.DetachedList;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PlacementsBlacklistService {

    DetachedList<PlacementBlacklist> getPlacementsBlacklist(String url, Country country, int from, int count);

    PlacementsBlacklistValidationResultTO validateAll(List<PlacementBlacklist> placements, Country country);

    void createOrDropAll(String validationResultId);

    void dropAll(Country country, Collection<PlacementBlacklist> placementsBlacklist);

    Collection<PlacementBlacklist> getValidatedResults(String validationResultId);
}
