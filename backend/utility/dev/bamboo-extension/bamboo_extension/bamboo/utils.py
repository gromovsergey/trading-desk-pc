# coding=utf-8
import json
import copy
import logging

import tornado.web

logger = logging.getLogger("tornado.cache")


def return_data(func):
    def wrapper(this, request, *args, **kwargs):
        def on_data(data):
            kwargs["callback"](this.api.output(request, data))
        nkwargs = copy.deepcopy(kwargs)
        nkwargs["callback"] = on_data
        return func(this, request, *args, **nkwargs)
    return wrapper


def cache_value(klass):
    def decorator(func):
        def wrapper(this, request, *args, **kwargs):
            nargs = this._key + list(args)
            callback = kwargs["callback"]
            if this.api.use_cache(request):
                logger.debug("WITH CACHE: %s", request.uri)
                data = klass.load(this.api, *nargs)
                if data:
                    callback(data)
                    return
            nargs += [callback]
            klass.get(this.api, *nargs)
        return wrapper
    return decorator


def cache_values(klass):
    def decorator(func):
        def wrapper(this, request, *args, **kwargs):
            nargs = this._key + list(args)
            callback = kwargs["callback"]
            if this.api.use_cache(request):
                logger.debug("WITH CACHE: %s", request.uri)
                data = klass.load_all(this.api, *nargs)
                if data:
                    callback(data)
                    return
            nargs += [callback]
            klass.get_all(this.api, *nargs)
        return wrapper
    return decorator


class Sequence(list):
    def traverse(self, method, *args, **kwargs):
        return [getattr(x, method)(*args, **kwargs) for x in self]

    def __getattr__(self, key):
        if key in dir(super(Sequence, self)):
            return getattr(super(Sequence, self), key)
        else:
            return lambda *a, **kw: self.traverse(key, *a, **kw)


class Model(object):
    def data(self):
        data = dict(self.__dict__.items())
        if "api" in data:
            del data["api"]
        for key in data.keys():
            if key.startswith("_"):
                del data[key]
                continue
            if isinstance(data[key], (Model, Sequence)):
                data[key] = data[key].data()
        return data

    def serialize(self):
        return json.dumps(self.data())

    def save(self):
        if self.api.cache:
            key, expires = self.__class__.cache_key(*self._key)
            value = self.serialize()
            self.api.cache.set(key, value, expires)
            logger.debug("SAVE: %s -> %s / %s", key, value, expires)

    @classmethod
    def load(klass, api, *args):
        if not api.cache:
            return
        key, expires = klass.cache_key(*args)
        value = api.cache.get(key)
        logger.debug("LOAD: %s -> %s", key, value)
        if value:
            data = json.loads(value)
            return klass.create(api, **data)

    @classmethod
    def load_all(klass, api, *args):
        if not api.cache:
            return
        keys, _ = klass.cache_keys(*args)
        keys = api.cache.get(keys)
        logger.debug("ALL: %s -> %s" % (args, keys))
        if not keys:
            return
        result = Sequence()
        for key in json.loads(keys):
            logger.debug("TRY: %s" % key)
            if not isinstance(key, (str, unicode)):
                value = klass.load(api, *key)
            else:
                value = klass.load(api, key)
            logger.debug("   VALUE: %s" % value)
            if not value:
                return
            result.append(value)
        return result


def check_response(response):
    if response.error:
        raise tornado.web.HTTPError(
            response.code,
            reason="%s: %s" % (response.request.url, response.reason))
