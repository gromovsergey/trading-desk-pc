# coding=utf-8
import json
from datetime import datetime

from tornado.escape import json_decode

from utils import Model, Sequence, check_response


class Result(Model):
    def __init__(self, api, key, number,
                 state="", starttime="", stoptime=None, link=None):
        self.api = api
        self.key = key
        self.number = str(number)
        self.state = state.lower()
        self.starttime = starttime
        self.stoptime = stoptime
        self._key = [key, number]
        if not link is None:
            self.link = link
        else:
            self.link = api.baseurl + "/browse/" + key + "-" + str(number)

    @staticmethod
    def cache_key(key, number):
        return (str("bamboo-result-%s-%s" % (key.lower(), number)), 84600 * 14)

    @staticmethod
    def cache_keys(key):
        return (str("bamboo-result-%s-keys" % key.lower()), 3600)

    @staticmethod
    def create(api, **kwargs):
        return Result(api, kwargs["key"], kwargs["number"],
                      kwargs["state"], kwargs["starttime"],
                      kwargs["stoptime"], kwargs.get("link"))

    @staticmethod
    def get(api, key, number, callback):
        url = api.resturl + "/result/%s-%s.json" % (key, number)

        def on_response(response):
            check_response(response)
            record = json_decode(response.body)
            result = Result.create(api, key=key,
                                   number=record["number"],
                                   state=record["state"],
                                   starttime=record["buildStartedTime"],
                                   stoptime=record.get("buildCompletedTime"))
            result.save()
            callback(result)

        api.client.fetch(url, on_response)

    @staticmethod
    def get_all(api, key, callback):
        ckey, cexpires = Result.cache_keys(key)
        data = Sequence([])
        url = api.resturl + ("/result/%s.json" % key) + \
            "?expand=results.result"

        def on_response(response):
            check_response(response)
            results = json_decode(response.body)["results"]["result"]
            for record in results:
                result = Result.create(api, key=key,
                                       number=record["number"],
                                       state=record["state"],
                                       starttime=record["buildStartedTime"],
                                       stoptime=record["buildCompletedTime"])
                result.save()
                data.append(result)
            if api.cache:
                api.cache.set(
                    ckey, json.dumps(map(lambda x: [x.key, x.number], data)),
                    cexpires)
            callback(data)

        api.client.fetch(url, on_response)
