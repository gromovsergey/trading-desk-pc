package com.foros.session.site;

import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Result;
import com.foros.session.status.Approvable;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

@Local
public interface SiteService extends Approvable {

    Long create(Site site);

    void update(Site site);

    void delete(Long id);

    void undelete(Long id);

    Site view(Long id);

    Site find(Long id);

    List<Site> getByAccount(Long accountId, boolean excludeNoTagsSite);

    Site viewSiteFetched(Long accountId);

    List<EntityTO> findUsedAccounts();

    List<EntityTO> getIndex(Long accountId);

    List<EntityTO> search(Long accountId);

    List<EntityTO> searchByName(String name, int maxResults);

    Collection<CreativeSize> getAccountSizes(Long accountId);

    Collection<Site> findDuplicated(Collection<Site> sites, Long accountId);

    List<SiteCreativeCategoryExclusion> getCategoryExclusions(Long siteId);

    void validateAll(Collection<Site> sites);

    void createOrUpdateAll(List<Site> sites);

    boolean isDuplicateSite(Site site);

    List<Site> fetchSitesForCsvDownload(Collection<Long> accountIds, final int maxResults);

    Result<Site> get(SiteSelector build);
}
