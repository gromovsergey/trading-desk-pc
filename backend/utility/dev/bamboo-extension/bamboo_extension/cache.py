# coding-utf-8
import hashlib
import logging
import tornado.web

logger = logging.getLogger("tornado.cache")


class CacheHandlerMixin(object):
    def _get_cache_key(self, etag):
        path = hashlib.md5(self.request.path).hexdigest()
        key = "path-%s-etag-%s" % (path, etag[1:-1])
        logger.debug("PATH: %s, KEY: %s", self.request.path, key)
        return key

    def _get_from_cache(self, key):
        logger.debug("GET: %s", key)
        return self._get_cache_client().get(key)

    def _put_to_cache(self, key, value, expires=86400):
        self._get_cache_client().set(key, value, expires)
        logger.debug("PUT: %s", key)

    def prepare(self):
        super(CacheHandlerMixin, self).prepare()
        etag = self.request.headers.get("If-None-Match")
        if etag is None:
            logger.debug("PREPARE: no etag -> refreshing")
            return
        key = self._get_cache_key(etag)
        data = self._get_from_cache(key)
        if data:
            self.write(data)
            self.finish()
        else:
            logger.debug("PREPARE: no data -> refreshing")

    def compute_etag(self):
        etag = super(CacheHandlerMixin, self).compute_etag()
        data = reduce(lambda x, y: x + y, self._write_buffer)
        key = self._get_cache_key(etag)
        self._put_to_cache(key, data)
        return etag


class MemcachedClientMixin(object):
    MEMCACHED_CLIENT = None

    def _get_cache_client(self):
        if MemcachedClientMixin.MEMCACHED_CLIENT is None:
            logger.debug("MEMCACHED: creating client")
            import memcache
            self.require_setting("memcached")
            config = self.application.settings["memcached"]
            mc = memcache.Client([config.address], debug=config.debug)
            MemcachedClientMixin.MEMCACHED_CLIENT = mc
        else:
            mc = MemcachedClientMixin.MEMCACHED_CLIENT
        if (len(mc.get_stats()) == 0):
            raise tornado.web.HTTPError(
                503, reason="Could not connect to Memcached")
        return mc


class MemcachedHandlerMixin(MemcachedClientMixin, CacheHandlerMixin):
    pass
