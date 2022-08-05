package com.foros.cache.generic;

import com.foros.cache.generic.hasher.Hasher;
import com.foros.cache.generic.hasher.Md5Hasher;
import com.foros.cache.generic.implementor.CacheImplementorFactory;
import com.foros.cache.generic.serializer.ProtostuffSerializer;
import com.foros.cache.generic.serializer.Serializer;

import java.util.ArrayList;
import java.util.Collection;

public class CacheFactory {

    private Serializer serializer;
    private Hasher hasher;
    private Collection<CacheRegionConfig> regionConfigs;
    private CacheImplementorFactory implementorFactory;

    public CacheFactory(CacheImplementorFactory implementorFactory) {
        this.implementorFactory = implementorFactory;
        this.serializer = new ProtostuffSerializer();
        this.hasher = new Md5Hasher(serializer);
        this.regionConfigs = new ArrayList<>();
    }

    public CacheFactory addRegionConfig(CacheRegionConfig regionConfig) {
        this.regionConfigs.add(regionConfig);
        return this;
    }

    public CacheFactory withRegionConfig(Collection<CacheRegionConfig> regionConfigs) {
        this.regionConfigs = regionConfigs;
        return this;
    }

    public Cache create() {
        return new CacheImpl(
                implementorFactory.create(serializer),
                hasher,
                regionConfigs
        );
    }

}
