# coding=utf-8
from tornado.escape import json_decode

from utils import Model, Sequence, check_response
from result import Result


class Status(Model):
    def __init__(self, api, key, is_active,
                 is_building, enabled,
                 current=None, starttime=None, state=None, link=None):
        self.api = api
        self.key = key
        self.is_building = is_building
        self.is_active = is_active
        self.enabled = enabled
        if not link is None:
            self.link = link
        else:
            self.link = api.baseurl + "/browse/" + key + "-"
        self._key = [key]
        self.current = current
        self.state = state
        self.starttime = starttime

    @staticmethod
    def cache_key(key):
        return (str("bamboo-planstatus-%s" % key.lower()), 60)

    @staticmethod
    def create(api, **kwargs):
        return Status(api, kwargs["key"],
                      kwargs["is_active"],
                      kwargs["is_building"],
                      kwargs["enabled"],
                      kwargs.get("current"),
                      kwargs.get("starttime"),
                      kwargs.get("state"),
                      kwargs.get("link"))

    @staticmethod
    def get(api, key, callback):
        url = api.resturl + "/plan/%s.json" % key

        def on_response(response):
            check_response(response)
            record = json_decode(response.body)
            status = Status.create(api, key=record["key"],
                                   is_active=record["isActive"],
                                   is_building=record["isBuilding"],
                                   enabled=record["enabled"])
            if status.is_building:
                url = api.resturl + \
                    ("/result/%s.json?" % key) + \
                    "includeAllStates&buildstate=Unknown&expand=results.result"
            else:
                url = api.resturl + \
                    "/result/%s-latest.json" % key

            def on_response_result(response):
                check_response(response)
                meta = json_decode(response.body)
                if "results" in meta:
                    record = meta["results"]["result"][0]
                    status.current = record["number"]
                else:
                    record = meta
                status.link += str(record["number"])
                result = Result.create(
                    api, key=key, number=record["number"],
                    state=record["state"],
                    starttime=record["buildStartedTime"],
                    stoptime=record.get("buildCompletedTime"))
                status.state = result.state
                status.starttime = result.starttime
                status.save()
                callback(status)

            api.client.fetch(url, on_response_result)

        api.client.fetch(url, on_response)
