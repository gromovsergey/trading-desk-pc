package com.foros.cache.generic;

public class CacheRegionConfig {
    private ExpirationTimeCalculator expirationTimeCalculator;
    private String name;

    public CacheRegionConfig(String name, ExpirationTimeCalculator expirationTimeCalculator) {
        this.name = name;
        this.expirationTimeCalculator = expirationTimeCalculator;
    }

    public ExpirationTimeCalculator getExpirationTimeCalculator() {
        return expirationTimeCalculator;
    }

    public String getName() {
        return name;
    }
}
