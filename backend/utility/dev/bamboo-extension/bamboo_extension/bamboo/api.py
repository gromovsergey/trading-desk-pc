# coding=utf-8
import json
import hashlib
import collections
from io import StringIO


from tornado.httpclient import HTTPClient, AsyncHTTPClient, HTTPResponse
from tornado.escape import json_decode
from tornado.httputil import HTTPHeaders

from plan import Plan
from utils import Sequence, return_data, cache_value, cache_values


class Api(object):

    OUTPUT_TYPE = {
        "response": "make_response",
        "data": "make_data",
        "json": "make_json"
    }

    class Client(object):
        def __init__(self, username, password, async=True):
            self.username = username
            self.password = password
            self.async = async

        def fetch(self, url, callback):
            kwargs = {
                "auth_username": self.username,
                "auth_password": self.password,
                "validate_cert": False
            }
            if self.async:
                AsyncHTTPClient().fetch(url, callback, **kwargs)
            else:
                result = HTTPClient().fetch(url, **kwargs)
                callback(result)

    def __init__(self, username, password, baseurl, cache=None,
                 async=True, response="data"):
        self.client = Api.Client(username, password, async)
        self.resturl = "%s/rest/api/latest" % baseurl
        self.baseurl = baseurl
        self.cache = cache
        self.api = self
        self._key = []
        self.output = getattr(self, self.OUTPUT_TYPE[response])

    @return_data
    @cache_value(Plan)
    def plan(self, request, key, callback=None):
        pass

    @return_data
    @cache_values(Plan)
    def plans(self, request, callback=None):
        pass

    def use_cache(self, request):
        return (self.cache and request.headers.get("If-None-Match"))

    def make_data(self, request, data):
        return data.data()

    def make_json(self, request, data):
        return json.dumps(data.data(), ensure_ascii=False)

    def make_response(self, request, data):
        headers = HTTPHeaders({"Content-Type": "application/json"})
        result = StringIO(json.dumps(data.data(), ensure_ascii=False))
        response = HTTPResponse(request, 200, headers, result,
                                effective_url=request.uri)
        return response
