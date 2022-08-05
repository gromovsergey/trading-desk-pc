package com.foros.web.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedAssetManagerWrapper implements AssetManager {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    private final Map<String, Map<String, Asset>> assets = new HashMap<String, Map<String, Asset>>();

    private AssetManager assetManager;

    public CachedAssetManagerWrapper(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void add(String type, AssetFactory factory) {
        assetManager.add(type, factory);
    }

    public Asset get(String type, String name, long version) {
        readLock.lock();
        try {
            Map<String, Asset> typedAssets = getTypedAssets(type);
            return getAsset(typedAssets, type, name, version);
        } finally {
            readLock.unlock();
        }
    }

    private Map<String, Asset> getTypedAssets(String type) {
        Map<String, Asset> typedAssets = assets.get(type);

        if (typedAssets == null) {
            typedAssets = createTypedAssets(type);
        }

        return typedAssets;
    }

    private Map<String, Asset> createTypedAssets(String type) {
        readLock.unlock();
        writeLock.lock();
        try {
            if (assets.containsKey(type)) {
                return assets.get(type);
            }

            Map<String, Asset> typedAssets = new HashMap<String, Asset>();

            assets.put(type, typedAssets);

            return typedAssets;
        } finally {
            writeLock.unlock();
            readLock.lock();
        }
    }

    private Asset getAsset(Map<String, Asset> typedAssets, String type, String name, long version) {
        Asset asset = typedAssets.get(name);

        if (asset == null) {
            asset = createAsset(typedAssets, type, name, version);
        }

        return asset;
    }

    private Asset createAsset(Map<String, Asset> typedAssets, String type, String name, long version) {
        readLock.unlock();
        writeLock.lock();
        try {
            if (typedAssets.containsKey(name)) {
                return typedAssets.get(name);
            }

            Asset asset = assetManager.get(type, name, version);

            if (asset != null) {
                typedAssets.put(name, asset);
                return asset;
            }

            return null;
        } finally {
            writeLock.unlock();
            readLock.lock();
        }

    }


}
