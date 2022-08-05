# coding=utf-8
import logging

import tornado.web
import tornado.gen

from auth import UserHandlerMixin
from cache import MemcachedClientMixin
from bamboo.api import Api
from bamboo.plan import Plan
from bamboo.job import Job


logger = logging.getLogger()


class PlanHandler(UserHandlerMixin,
                  MemcachedClientMixin,
                  tornado.web.RequestHandler):

    @tornado.web.authenticated
    @tornado.web.asynchronous
    @tornado.gen.engine
    def get(self, key=None, attr=None, number=None):
        cache = self._get_cache_client()
        baseurl = self.application.settings["config"].bamboo.base_url
        api = Api(self.current_user.username, self.current_user.password,
                  baseurl, cache)
        if key:
            if attr == "status":
                response = yield tornado.gen.Task(Plan(api, key).status,
                                                  self.request)
            elif attr == "result":
                if not number is None:
                    response = yield tornado.gen.Task(Plan(api, key).result,
                                                      self.request, number)
                else:
                    response = yield tornado.gen.Task(Plan(api, key).results,
                                                      self.request)
            elif attr == "children":
                response = yield tornado.gen.Task(Plan(api, key).children,
                                                  self.request)
            else:
                response = yield tornado.gen.Task(api.plan, self.request, key)
        else:
            response = yield tornado.gen.Task(api.plans, self.request)
        self.write({"data": response})
        self.finish()

    def write_error(self, status_code, **kwargs):
        if "exc_info" in kwargs:
            error = kwargs["exc_info"][1]
            if hasattr(error, "reason"):
                message = {"data": error.reason}
            else:
                message = {"data": repr(error)}
        else:
            message = {"data": ""}
        self.set_status(status_code)
        self.write(message)
        self.finish()


class JobHandler(UserHandlerMixin,
                 MemcachedClientMixin,
                 tornado.web.RequestHandler):

    @tornado.web.authenticated
    @tornado.web.asynchronous
    @tornado.gen.engine
    def get(self, plan_key, key=None, attr=None, number=None):
        cache = self._get_cache_client()
        baseurl = self.application.settings["config"].bamboo.base_url
        api = Api(self.current_user.username, self.current_user.password,
                  baseurl, cache)
        if key:
            if attr == "status":
                response = yield tornado.gen.Task(
                    Job(api, plan_key, key).status, self.request)
            elif attr == "result":
                if not number is None:
                    response = yield tornado.gen.Task(
                        Job(api, plan_key, key).result, self.request, number)
                else:
                    response = yield tornado.gen.Task(
                        Job(api, plan_key, key).results, self.request)
            else:
                response = yield tornado.gen.Task(
                    Plan(api, plan_key).job, self.request, key)
        else:
            response = yield tornado.gen.Task(
                Plan(api, plan_key).jobs, self.request)
        self.write({"data": response})
        self.finish()

    def write_error(self, status_code, **kwargs):
        if "exc_info" in kwargs:
            error = kwargs["exc_info"][1]
            if hasattr(error, "reason"):
                message = {"data": error.reason}
            else:
                message = {"data": repr(error)}
        else:
            message = {"data": ""}
        self.set_status(status_code)
        self.write(message)
        self.finish()
