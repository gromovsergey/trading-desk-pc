package com.foros.session.query.site;

import com.foros.session.query.PublisherEntityQuery;

import java.util.Collection;

public interface SiteQuery extends PublisherEntityQuery<SiteQuery> {

    SiteQuery sites(Collection<Long> siteIds);

}
