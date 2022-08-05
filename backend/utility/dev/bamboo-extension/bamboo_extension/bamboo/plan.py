# coding=utf-8
import json

from tornado.escape import json_decode

from pyquery import PyQuery

from utils import Model, Sequence, return_data, cache_value, cache_values
from utils import check_response
from status import Status
from job import Job
from result import Result


class Children(Model):
    def __init__(self, api, key, children):
        self.api = api
        self.key = key
        self.children = children
        self._key = [key]

    @staticmethod
    def cache_key(key):
        return (str("bamboo-planchildren-%s" % key.lower()), 84600)

    @staticmethod
    def create(api, **kwargs):
        return Children(api, kwargs["key"], kwargs["children"])

    @staticmethod
    def get(api, key, callback):
        url = api.baseurl + \
            "/chain/admin/config/" + \
            "editChainDependencies.action?buildKey=%s" % key

        def on_response(response):
            check_response(response)
            q = PyQuery(response.body)
            children = []

            for element in q("ul").filter(".dependencies-child-plans"):
                for li in PyQuery(element).find('li'):
                    children.append(PyQuery(li).attr("data-id"))
            children = Children.create(api, key=key, children=sorted(children))
            children.save()
            callback(children)

        api.client.fetch(url, on_response)


class Plan(Model):
    def __init__(self, api, key, name="", short_name=""):
        self.api = api
        self.key = key
        self.name = name
        self.short_name = short_name
        self._key = [key]

    @staticmethod
    def cache_keys():
        return (str("bamboo-plans-keys"), 84600)

    @staticmethod
    def cache_key(key):
        return (str("bamboo-plan-%s" % key.lower()), 84600)

    @staticmethod
    def create(api, **kwargs):
        return Plan(api, kwargs["key"], kwargs["name"], kwargs["short_name"])

    @staticmethod
    def get(api, key, callback):
        url = api.resturl + "/plan/%s.json" % key

        def on_response(response):
            check_response(response)
            record = json_decode(response.body)
            plan = Plan.create(api, key=record["key"], name=record["name"],
                               short_name=record["shortName"])
            plan.save()
            status = Status.create(api, key=record["key"],
                                   is_active=record["isActive"],
                                   is_building=record["isBuilding"],
                                   enabled=record["enabled"])
            status.save()
            callback(plan)

        api.client.fetch(url, on_response)

    @staticmethod
    def get_all(api, callback):
        ckey, cexpires = Plan.cache_keys()
        data = Sequence([])
        index = 0
        baseurl = api.resturl + "/plan.json?start-index=%d"

        def on_response(response):
            check_response(response)
            meta = json_decode(response.body)["plans"]
            plans = meta["plan"]
            total = meta["size"]
            index = meta["start-index"] + meta["max-result"]
            for plan in plans:
                plan = Plan.create(api,
                                   key=plan["key"],
                                   name=plan["name"],
                                   short_name=plan["shortName"])
                plan.save()
                data.append(plan)
            if len(data) < total:
                api.client.fetch(baseurl % index, on_response)
            else:
                if api.cache:
                    api.cache.set(ckey,
                                  json.dumps(map(lambda x: x.key, data)),
                                  cexpires)
                callback(data)

        api.client.fetch(baseurl % index, on_response)

    @return_data
    @cache_value(Status)
    def status(self, request, callback=None):
        pass

    @return_data
    @cache_value(Job)
    def job(self, request, key, callback=None):
        pass

    @return_data
    @cache_values(Job)
    def jobs(self, request, callback=None):
        pass

    @return_data
    @cache_value(Result)
    def result(self, request, number, callback=None):
        pass

    @return_data
    @cache_values(Result)
    def results(self, request, callback=None):
        pass

    @return_data
    @cache_value(Children)
    def children(self, request, callback=None):
        pass
