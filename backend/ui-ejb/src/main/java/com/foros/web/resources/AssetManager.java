package com.foros.web.resources;

public interface AssetManager {

    void add(String type, AssetFactory factory);

    Asset get(String type, String name, long version);

}
