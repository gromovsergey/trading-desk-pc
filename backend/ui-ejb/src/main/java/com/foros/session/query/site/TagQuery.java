package com.foros.session.query.site;

import com.foros.session.query.PublisherEntityQuery;

import java.util.Collection;

public interface TagQuery extends PublisherEntityQuery<TagQuery> {

    TagQuery sites(Collection<Long> siteIds);

    TagQuery tags(Collection<Long> tagIds);
}
