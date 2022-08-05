# coding=utf-8
import json

from tornado.escape import json_decode

from utils import Model, Sequence, return_data, check_response
from status import Status
from result import Result


class Job(Model):
    def __init__(self, api, plan_key, key, name="", short_name="", stage=""):
        self.api = api
        self.plan_key = plan_key
        self.key = key
        self.name = name
        self.short_name = short_name
        self.stage = stage
        self._key = [plan_key, key]

    @staticmethod
    def cache_key(plan_key, key):
        return (str("bamboo-job-%s-%s" % (plan_key.lower(), key.lower())),
                84600)

    @staticmethod
    def cache_keys(plan_key):
        return (str("bamboo-job-%s-keys" % plan_key.lower()), 84600)

    @staticmethod
    def create(api, **kwargs):
        return Job(api, kwargs["plan_key"], kwargs["key"], kwargs["name"],
                   kwargs["short_name"], kwargs["stage"])

    @staticmethod
    def get(api, plan_key, key, callback):
        url = api.resturl + "/plan/%s-%s.json" % (plan_key, key)

        def on_response(response):
            check_response(response)
            record = json_decode(response.body)
            plan_key = record["projectKey"] + "-" + record["parentKey"]
            job = Job.create(api, plan_key=plan_key,
                             key=record["shortKey"],
                             name=record["name"],
                             short_name=record["shortName"],
                             stage=record["stageName"])
            job.save()
            status = Status.create(api, key=record["key"],
                                   is_active=record["isActive"],
                                   is_building=record["isBuilding"])
            status.save()
            callback(job)

        api.client.fetch(url, on_response)

    @staticmethod
    def get_all(api, plan_key, callback):
        ckey, cexpires = Job.cache_keys(plan_key)
        data = Sequence([])
        url = api.resturl + ("/plan/%s.json" % plan_key) + \
            "?expand=stages.stage.plans"

        def on_response(response):
            check_response(response)
            meta = json_decode(response.body)
            stages = meta["stages"]["stage"]
            for stage in stages:
                plans = stage["plans"]["plan"]
                for plan in plans:
                    job = Job.create(api,
                                     plan_key=meta["key"],
                                     key=plan["shortKey"],
                                     name=plan["name"],
                                     short_name=plan["shortName"],
                                     stage=stage["name"])
                    job.save()
                    data.append(job)
            if api.cache:
                api.cache.set(
                    ckey, json.dumps(map(lambda x: [x.plan_key, x.key], data)),
                    cexpires)
            callback(data)

        api.client.fetch(url, on_response)

    @return_data
    def status(self, request, callback=None):
        key = self.plan_key + "-" + self.key
        if self.api.use_cache(request):
            data = Status.load(self.api, key)
            if data:
                callback(data)
                return
        Status.get(self.api, key, callback)

    @return_data
    def result(self, request, number, callback=None):
        key = "-".join(self._key)
        if self.api.use_cache(request):
            data = Result.load(self.api, key, number)
            if data:
                callback(data)
                return
        Result.get(self.api, key, number, callback)

    @return_data
    def results(self, request, callback=None):
        key = "-".join(self._key)
        if self.api.use_cache(request):
            data = Result.load_all(self.api, key)
            if data:
                callback(data)
                return
        Result.get_all(self.api, key, callback)
