# coding=utf-8
import tornado.web
import tornado.template
import copy

from auth import UserHandlerMixin


class HtmlHandler(UserHandlerMixin,
                  tornado.web.RequestHandler):
    def get_context(self):
        userinfo = copy.deepcopy(self.current_user.__dict__)
        del userinfo["password"]
        return userinfo

    @tornado.web.authenticated
    @tornado.web.asynchronous
    def get(self, filename):
        self.render(filename + ".html", **self.get_context())
