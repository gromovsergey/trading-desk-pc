package com.foros.cache.generic;


import com.foros.cache.generic.implementor.CacheImplementorFactory;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.persistence.hibernate.EvictCacheHibernateInterceptor;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.util.PersistenceUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.ExcludeDefaultInterceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.impl.SessionImpl;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

@Startup
@Singleton(name = "CacheProviderService")
@ExcludeDefaultInterceptors
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class CacheProviderServiceBean implements CacheProviderService {

    private static final Collection<CacheRegionConfig> REGION_CONFIGS = Arrays.asList(
            // generally regions named after stored procedures/functions
            new CacheRegionConfig("statqueries.triggers", new ConstInterval(5 * 60)),
            new CacheRegionConfig("report.channel_triggers", new ConstInterval(5 * 60)),
            new CacheRegionConfig("report.TargetingStats", new NextGMTMidnight())
    );

    private static final Logger logger = Logger.getLogger(CacheProviderServiceBean.class.getName());

    @EJB
    private ConfigService configService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    private Cache cache;

    @PostConstruct
    private void initCache() {
        CacheImplementorFactory implementorFactory = createCacheImplementorFactory();
        cache = new CacheFactory(implementorFactory).withRegionConfig(REGION_CONFIGS).create();
    }

    private CacheImplementorFactory createCacheImplementorFactory() {
        List<String> hosts = configService.get(ConfigParameters.MEMCACHED_HOSTS_LIST);
        if (hosts.isEmpty()) {
            logger.warning(ConfigParameters.MEMCACHED_HOSTS_LIST.getName() + " parameter contains empty host list, memory cache will be disabled!");
            return CacheImplementorFactory.nullCache();
        }
        return CacheImplementorFactory.memcached(hosts);
    }

    @Override
    public Cache getCache() {
        return cache;
    }

    @Override
    public void touchTag(Object tag) {
        SessionImpl sessionImpl = (SessionImpl) PersistenceUtils.getHibernateSession(em);
        EvictCacheHibernateInterceptor interceptor = ((HibernateInterceptor) sessionImpl.getInterceptor()).getEvictCacheInterceptor();
        if (!interceptor.isInitialized()) {
            interceptor.initialize(sessionImpl, cache);
        }
        interceptor.touchTag(tag);
    }

    static class ConstInterval implements ExpirationTimeCalculator {
        private ReadableDuration expireTime;

        public ConstInterval(long expireTime) {
            this.expireTime = Duration.standardSeconds(expireTime);
        }

        @Override
        public ReadableDuration getExpirationTime() {
            return expireTime;
        }
    }

    static class NextGMTMidnight implements ExpirationTimeCalculator {
        static final DateTimeZone GMT = DateTimeZone.forID("GMT");

        @Override
        public ReadableDuration getExpirationTime() {
            DateMidnight nextMidnight = new DateMidnight(GMT).plusDays(1);
            DateTime now = new DateTime(GMT);
            return new Duration(now, nextMidnight);
        }
    }
}
