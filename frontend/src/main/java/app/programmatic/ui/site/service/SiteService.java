package app.programmatic.ui.site.service;

import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.common.model.OwnedStatusable;
import app.programmatic.ui.site.dao.model.Site;
import app.programmatic.ui.site.dao.model.SiteStat;

import java.util.List;

public interface SiteService {
    List<Site> findSitesByCountry(String countryCode, int limit);

    List<Site> findSitesByCountryAndIds(String countryCode, List<Long> ids, int limit);

    List<SiteStat> getStatsByLineItemId(Long lineItemId);

    List<Long> filterActive(List<Long> ids);

    OwnedStatusable findSiteUnchecked(Long siteId);

    List<IdName> findSitesByAccountId(Long accountId);

    List<IdName> findTagsBySiteId(Long siteId);
}
