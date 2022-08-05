package com.foros.monitoring;

import com.foros.cache.generic.Cache;
import com.foros.cache.generic.CacheProviderService;
import com.foros.cache.generic.CacheRegionStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;
import org.glassfish.gmbal.ManagedObject;

@Singleton
@Startup
@ManagedObject
@LocalBean
@AMXMetadata(type = "MemcachedMonitoring")
public class MemcachedMonitoring {

    private static final Logger logger = Logger.getLogger(MemcachedMonitoring.class.getName());

    @EJB
    private MemcachedMonitoring self;

    @EJB
    private CacheProviderService cacheProviderService;

    @EJB
    private MonitoringManager monitoringManager;

    private Collection<String> regions;

    private Collection<Object> unregisterQueue;

    @PostConstruct
    public void init()  {
        Cache cache = cacheProviderService.getCache();
        regions = cache.getRegionNames();
        unregisterQueue = new ArrayList<>(regions.size() + 1);

        registerMBean(self, "root");
        for (String region : regions) {
            registerMBean(new RegionCacheStatisticsMBean(cache, region), region);
        }

        logger.log(Level.INFO, "MemcachedMonitoring bean is started");
    }

    private void registerMBean(Object obj,  String region) {
        try {
            monitoringManager.registerMBean(obj, region);
            unregisterQueue.add(obj);
        } catch (MonitorActivationException e) {
            logger.log(Level.SEVERE, "Failed to register MBean : " + region, e);
        }
    }

    @PreDestroy
    public void destroy() {
        for (Object obj : unregisterQueue) {
            try {
                monitoringManager.unregisterMBean(obj);
            } catch (MonitorActivationException e) {
                logger.log(Level.SEVERE, "Failed to register MBean : " + obj);
            }
        }
        logger.log(Level.INFO, "MemcachedMonitoring bean is stopped");
    }

    @ManagedAttribute
    public Collection<String> getRegions() {
        return new ArrayList<>(regions);
    }


    @ManagedObject
    @AMXMetadata(type = "MemcachedMonitoring")
    public static class RegionCacheStatisticsMBean {
        private final Cache cache;
        private final String region;
        private StatsCopy prev = StatsCopy.zero();

        public RegionCacheStatisticsMBean(Cache cache, String region) {
            this.cache = cache;
            this.region = region;
        }


        @ManagedAttribute
        public StatsCopy getStatistics() {
            return getStatisticsInternal();
        }

        @ManagedAttribute
        public synchronized StatsCopy getStatisticsDelta() {
            StatsCopy current = getStatisticsInternal();
            StatsCopy res = StatsCopy.delta(prev, current);
            prev = current;
            return res;
        }

        private StatsCopy getStatisticsInternal() {
            return StatsCopy.copy(cache.getStatistics(region));
        }

        @Override
        public String toString() {
            return "RegionCacheStatisticsMBean[" + region +"]";
        }
    }

    @ManagedData
    public static class StatsCopy extends CompositeDataSupport implements CacheRegionStatistics {
        private static final CompositeType PAGE_STATISTIC_TYPE = createCompositeType();

        private static CompositeType createCompositeType() {
            try {
                return new CompositeType(
                        "CacheRegionStatistics",
                        "Cache Region Statistics",
                        new String[]{"hits", "misses"},
                        new String[]{"Cache hits", "Cache misses"},
                        new OpenType<?>[]{SimpleType.LONG, SimpleType.LONG}
                );
            } catch (OpenDataException e) {
                throw new RuntimeException(e);
            }
        }


        private final long hits;
        private final long misses;

        public StatsCopy(long hits, long misses) throws OpenDataException {
            super(PAGE_STATISTIC_TYPE, new String[]{"hits", "misses"}, new Object[]{hits, misses});
            this.hits = hits;
            this.misses = misses;
        }

        @ManagedAttribute
        @Override
        public long getHits() {
            return hits;
        }

        @ManagedAttribute
        @Override
        public long getMisses() {
            return misses;
        }

        public static StatsCopy delta(CacheRegionStatistics prev, CacheRegionStatistics current) {
            return newInstance(current.getHits() - prev.getHits(), current.getMisses() - prev.getMisses());
        }

        public static StatsCopy copy(CacheRegionStatistics statistics) {
            return newInstance(statistics.getHits(), statistics.getMisses());
        }

        public static StatsCopy zero() {
            return newInstance(0, 0);
        }

        private static StatsCopy newInstance(long hits, long misses) {
            try {
                return new StatsCopy(hits, misses);
            } catch (OpenDataException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
