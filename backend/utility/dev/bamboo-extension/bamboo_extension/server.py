#coding=utf-8
import os
import sys
import uuid
import time
from hashlib import md5


import tornado.ioloop
import tornado.web

from rest import JobHandler, PlanHandler
from html import HtmlHandler
from auth import LoginHandler, LogoutHandler


class Server(object):
    def __init__(self, config):
        self.config = config

    def load_application(self):
        application = tornado.web.Application(
            [
                (r'/static/(.*)', tornado.web.StaticFileHandler,
                    {'path': self.config.server.static_directory}),
                (r'/login', LoginHandler),
                (r'/logout', LogoutHandler),
                (r'/api/plan', PlanHandler),
                (r'/api/plan/([\-\w]+)', PlanHandler),
                (r'/api/plan/([\-\w]+?)/(status|result|children)',
                    PlanHandler),
                (r'/api/plan/([\-\w]+?)/(result)/(latest|\d+)', PlanHandler),
                (r'/api/plan/([\-\w]+)/job', JobHandler),
                (r'/api/plan/([\-\w]+)/job/([\-\w]+)', JobHandler),
                (r'/api/plan/([\-\w]+)/job/([\-\w]+)/(status|result)',
                    JobHandler),
                (r'/api/plan/([\-\w]+)/job/([\-\w]+)/(result)/(latest|\d+)',
                    JobHandler),
                (r'/(dtree|gantt)', HtmlHandler),
                (r"/", tornado.web.RedirectHandler, {"url": "/dtree"}),
            ],
            gzip=True,
            xsrf_cookies=True,
            static_path=self.config.server.static_directory,
            template_path=self.config.server.html_directory,
            login_url="/login",
            cookie_secret=md5(str(uuid.uuid1())).hexdigest(),
            config=self.config,
            memcached=self.config.memcached)
        return application

    def execute(self, action):
        return getattr(self, action)()

    def start(self):
        pid = os.fork()
        if not pid:
            os.setsid()
            os.chdir("/")
            pid = os.fork()
            if not pid:
                os.setpgid(0, 0)
                pid = os.fork()
                if not pid:
                    address, port = self.config.memcached.address.split(":")
                    os.execlp("memcached", "memcached", "-l", address, "-p", port)
                else:
                    time.sleep(2)
                    if not self.get_pid_status(pid):
                        with open(self.config.memcached.pid, 'w') as handler:
                            handler.write(str(pid))
                    pid = os.fork()
                    if not pid:
                        self.application = self.load_application()
                        self.application.listen(
                            self.config.server.port,
                            address=self.config.server.address,
                            ssl_options=self.config.server.ssl)
                        tornado.ioloop.IOLoop.instance().start()
                    else:
                        time.sleep(2)
                        if not self.get_pid_status(pid):
                            with open(self.config.server.pid, 'w') as handler:
                                handler.write(str(pid))
            else:
                os._exit(0)
        else:
            os._exit(0)

    def get_pids(self):
        if os.path.exists(self.config.server.pid):
            with open(self.config.server.pid) as handler:
                spid = int(handler.read())
        else:
            spid = None

        if os.path.exists(self.config.memcached.pid):
            with open(self.config.memcached.pid) as handler:
                mpid = int(handler.read())
        else:
            mpid = None
        return (spid, mpid)

    def get_pid_status(self, pid):
        result = 1
        if pid:
            try:
                os.kill(pid, 0)
                result = 0
            except OSError:
                result += 1
        return result

    def kill_pid(self, pid):
        try:
            os.kill(pid, 9)
        except OSError, error:
            if error.errno == 3:
                pass
            else:
                raise

    def stop(self):
        spid, mpid = self.get_pids()
        if spid:
            self.kill_pid(spid)
            os.remove(self.config.server.pid)
        if mpid:
            self.kill_pid(mpid)
            os.remove(self.config.memcached.pid)

    def status(self):
        spid, mpid = self.get_pids()
        if not self.get_pid_status(spid):
            print "Web server is working"
        else:
            print "Web server is down"
        if not self.get_pid_status(mpid):
            print "Memcached is working"
        else:
            print "Memcached is down"
