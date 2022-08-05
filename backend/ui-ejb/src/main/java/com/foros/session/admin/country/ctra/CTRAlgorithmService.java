package com.foros.session.admin.country.ctra;

import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.session.NamedTO;

import java.util.Collection;
import javax.ejb.Local;

@Local
public interface CTRAlgorithmService {

    CTRAlgorithmData find(String countryCode);

    CTRAlgorithmData findByCountryId(Long countryId);

    void save(CTRAlgorithmData data, Collection<Long> advertiserExclusions, Collection<Long> campaignExclusions);

    Collection<NamedTO> displayAdvertisers(Collection<Long> ids);

    Collection<NamedTO> findAdvertisers(String name, String countryCode, int maxResults);

    Long findAdvertiserId(String name, String countryCode);
}
