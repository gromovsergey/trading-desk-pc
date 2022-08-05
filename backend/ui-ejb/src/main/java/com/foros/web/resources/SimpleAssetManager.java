package com.foros.web.resources;

import java.util.HashMap;
import java.util.Map;

public class SimpleAssetManager implements AssetManager {

    private final Map<String, AssetFactory> factories = new HashMap<String, AssetFactory>();

    private final long timestamp;

    public SimpleAssetManager(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void add(String type, AssetFactory factory) {
        factories.put(type, factory);
    }

    @Override
    public Asset get(String type, String name, long version) {
        if (version != timestamp) {
            return null;
        }

        return getAsset(type, name, version);
    }

    private Asset getAsset(String type, String name, long version) {
        AssetFactory factory = factories.get(type);

        if (factory == null) {
            return null;
        }

        return factory.createAsset(name, version);
    }

}
