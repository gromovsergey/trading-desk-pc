package com.foros.cache.generic;

import com.foros.cache.generic.hasher.Hasher;
import com.foros.cache.generic.implementor.CacheImplementor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

public class CacheImpl implements Cache {
    private static final Logger logger = Logger.getLogger(CacheImpl.class.getName());
    private static final ReadableDuration TAG_EXPIRATION = Duration.standardDays(30); // almost forever

    private final CacheImplementor cache;
    private final Hasher hasher;
    private final Map<String, CacheRegionImpl> regions;
    private final Map<String, CacheRegionStatistics> regionStatistics;

    public CacheImpl(CacheImplementor cache, Hasher hasher, Collection<CacheRegionConfig> regionConfigs) {
        this.cache = cache;
        this.hasher = hasher;

        HashMap<String, CacheRegionImpl> regions = new HashMap<>();
        Map<String, CacheRegionStatistics> regionStatistics = new HashMap<>();
        for (CacheRegionConfig config : regionConfigs) {
            CacheRegionStatisticsImpl statistics = new CacheRegionStatisticsImpl();
            CacheRegionImpl region = new CacheRegionImpl(config, statistics);
            String name = config.getName().toLowerCase();
            regionStatistics.put(name, statistics);
            regions.put(name, region);
        }
        this.regions = regions;
        this.regionStatistics = regionStatistics;
    }

    @Override
    public Collection<String> getRegionNames() {
        return regions.keySet();
    }

    @Override
    public CacheRegion getRegion(String name) {
        CacheRegionImpl region = regions.get(name.toLowerCase());
        if (region == null) {
            throw new IllegalArgumentException("name: " + name);
        }
        return region;
    }

    @Override
    public void removeByTags(Collection<Object> tags) {
        if (tags.isEmpty()) {
            return;
        }

        try {
            cache.incrementAll(hash(tags), currentVersion(), TAG_EXPIRATION);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Can't remove entries by tags from cache.", e);
        }
    }

    @Override
    public void clear() {
        try {
            cache.clear();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Can't clean the cache", e);
        }
    }

    @Override
    public CacheRegionStatistics getStatistics(String name) {
        CacheRegionStatistics stats = regionStatistics.get(name.toLowerCase());
        if (stats == null) {
            throw new IllegalArgumentException("name: " + name);
        }
        return stats;
    }

    private List<String> hash(Collection<?> tags) {
        ArrayList<String> result = new ArrayList<String>();
        for (Object tag : tags) {
            result.add(hasher.hash(tag));
        }
        return result;
    }

    private long currentVersion() {
        return System.currentTimeMillis();
    }

    public static class TaggedEntry<T> {

        private T value;

        private Map<String, Long> tags;

        public TaggedEntry() {
        }

        public TaggedEntry(T value, Map<String, Long> tags) {
            this.value = value;
            this.tags = tags;
        }

        public T getValue() {
            return value;
        }

        public List<String> getTags() {
            return tags != null ? new ArrayList<String>(tags.keySet()) : null;
        }

        public boolean hasSameTagsVersions(Map<String, Long> tags) {
            if (tags == null && this.tags == null) {
                return true;
            }

            return tags != null && this.tags.equals(tags);
        }

    }

    private class CacheRegionImpl implements CacheRegion {
        private final ExpirationTimeCalculator expirationTimeCalculator;
        private final String name;
        private final CacheRegionStatisticsImpl statistics;

        private CacheRegionImpl(CacheRegionConfig config, CacheRegionStatisticsImpl statistics) {
            expirationTimeCalculator = config.getExpirationTimeCalculator();
            name = config.getName();
            this.statistics = statistics;
        }

        @Override
        public <T, K> T get(K key) {
            try {
                return getImpl(key);
            } catch (Exception e) {
                addMiss();
                logger.log(Level.WARNING, "Can't get entry by key from the cache", e);
                if (key != null) {
                    logger.log(Level.WARNING, "Key = " + key.toString() + ", class = " + key.getClass().getName());
                }
                return null;
            }
        }

        @Override
        public <T, K> T get(K key, CreateValueCallback<T, K> callback) {
            return get(key, Collections.emptyList(), callback);
        }

        @Override
        @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
        public <T, K> T get(K key, Collection<?> tags, CreateValueCallback<T, K> callback) {
            T res = get(key);
            if (res != null) {
                return res;
            }

            // copy to be sure collection is writable
            tags = new ArrayList<>(tags);


            res = callback.create(key, tags);

            set(key, res, tags.isEmpty() ? null : tags);

            return res;
        }

        private <T> T getImpl(Object key) {
            String entryKey = getKeyHash(key);

            //noinspection unchecked
            TaggedEntry<T> entry = cache.get(TaggedEntry.class, entryKey);

            if (entry == null) {
                addMiss();
                return null;
            }

            Map<String, Long> storedTags = entry.getTags() != null ? cache.getAll(Long.class, entry.getTags()) : null;
            if (!entry.hasSameTagsVersions(storedTags)) {
                addMiss();
                return null;
            }

            addHit();
            return entry.getValue();
        }

        @Override
        public <T> void set(Object key, T value) {
            set(key, value, null);
        }

        @Override
        public <T> void set(Object key, T value, Collection<?> tags) {
            try {
                cache.set(
                        getKeyHash(key),
                        new TaggedEntry<T>(value, getStoredTags(tags)),
                        expirationTimeCalculator.getExpirationTime()
                );
            } catch (Exception e) {
                logger.log(Level.WARNING, "Can't put a value to the cache", e);
                if (key != null) {
                    logger.log(Level.WARNING, "Key = " + key.toString() + ", class = " + key.getClass().getName());
                }
            }
        }

        @Override
        public void remove(Object key) {
            try {
                cache.remove(getKeyHash(key));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Can't remove entry by key from the cache", e);
                if (key != null) {
                    logger.log(Level.WARNING, "Key = " + key.toString() + ", class = " + key.getClass().getName());
                }
            }
        }

        private String getKeyHash(Object key) {
            return hasher.hash(new Holder(name, key));
        }

        private Map<String, Long> getStoredTags(Collection<?> tags) {
            try {
                return tags != null ? cache.getOrSetAll(Long.class, hash(tags), currentVersion(), TAG_EXPIRATION) : null;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Can't fetch stored tags from cache.", e);
                return null;
            }
        }

        private void addHit() {
            statistics.hits.incrementAndGet();
        }

        private void addMiss() {
            statistics.misses.incrementAndGet();
        }

    }

    /** @noinspection UnusedDeclaration */
    private static class Holder {
        private final String name;
        private final Object key;

        public Holder(String name, Object key) {
            this.name = name;
            this.key = key;
        }
    }

    public final class CacheRegionStatisticsImpl implements CacheRegionStatistics {
        private final AtomicLong hits = new AtomicLong();
        private final AtomicLong misses = new AtomicLong();

        @Override
        public long getHits() {
            return hits.get();
        }

        @Override
        public long getMisses() {
            return misses.get();
        }
    }
}
