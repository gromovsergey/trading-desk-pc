package com.foros.cache.generic;

import java.util.Collection;

public interface Cache {

    Collection<String> getRegionNames();

    CacheRegion getRegion(String name);

    void removeByTags(Collection<Object> tags);

    void clear();

    CacheRegionStatistics getStatistics(String region);
}
