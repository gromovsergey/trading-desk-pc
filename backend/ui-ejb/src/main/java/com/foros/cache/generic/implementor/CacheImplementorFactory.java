package com.foros.cache.generic.implementor;

import com.foros.cache.generic.implementor.memcached.MemcachedCacheImplementor;
import com.foros.cache.generic.serializer.Serializer;

import java.io.IOException;
import java.util.List;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;

public abstract class CacheImplementorFactory {

    public abstract CacheImplementor create(Serializer serializer);

    public static CacheImplementorFactory memcached(List<String> hosts) {
        return new MemcachedCacheImplementorFactory(hosts);
    }

    public static CacheImplementorFactory nullCache() {
        return new CacheImplementorFactory() {
            @Override
            public CacheImplementor create(Serializer serializer) {
                return new NullCacheImplementor();
            }
        };
    }

    private static class MemcachedCacheImplementorFactory extends CacheImplementorFactory {

        private ConnectionFactory connectionFactory;
        private List<String> hosts;

        public MemcachedCacheImplementorFactory(List<String> hosts) {
            this.hosts = hosts;
            this.connectionFactory = createConnectionFactory();
        }

        private ConnectionFactory createConnectionFactory() {
            return new ConnectionFactoryBuilder()
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                    .setFailureMode(FailureMode.Retry)
                    .build();
        }

        @Override
        public CacheImplementor create(Serializer serializer) {
            return new MemcachedCacheImplementor(createMemcachedClient(), serializer);
        }

        private MemcachedClient createMemcachedClient() {
            try {
                return new MemcachedClient(connectionFactory, AddrUtil.getAddresses(hosts));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
