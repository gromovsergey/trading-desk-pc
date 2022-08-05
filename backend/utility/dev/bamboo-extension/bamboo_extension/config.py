# coding=utf-8
import os
import sys
import json


class Config(object):
    class Server(object):
        def __init__(self, address, port, directory, pid, ssl):
            self.address = address
            self.port = port
            self.html_directory = os.path.join(directory, "html")
            self.static_directory = os.path.join(directory, "static")
            self.pid = pid
            self.ssl = ssl

    class Bamboo(object):
        def __init__(self, base_url, check_page):
            self.base_url = base_url
            self.check_url = "%s%s" % (base_url, check_page)

    class LDAP(object):
        def __init__(self, **kwargs):
            for key, value in kwargs.items():
                setattr(self, key, value)

    class Memcached(object):
        def __init__(self, address, debug, pid):
            self.address = address
            self.debug = debug
            self.pid = pid

    class Logging(object):
        def __init__(self, loggers):
            self.root = loggers.get("root")
            if self.root:
                del loggers["root"]
            self.loggers = loggers

    def __init__(self, filename):
        if filename and os.path.exists(filename):
            settings = self.load_settings(filename)
        else:
            settings = {}
        self.initialize(settings)

    def initialize(self, settings):
        directory = settings.get("server", {})\
                            .get("directory", os.path.join(
                                 sys.prefix,
                                 "bamboo_extension/data"))
        port = settings.get("server", {}).get("port", 8888)
        address = settings.get("server", {}).get("address", "127.0.0.1")
        pid = settings.get("server", {}).get("pid", "server.pid")
        ssl = settings.get("server", {}).get("ssl")
        self.server = self.Server(address, port, directory, pid, ssl)

        base_url = settings.get("bamboo", {})\
                           .get("baseurl", "https://bamboo.ocslab.com")
        check_page = settings.get("bamboo", {})\
                             .get("checkpage", "/admin/administer.action")
        self.bamboo = self.Bamboo(base_url, check_page)

        address = settings.get("ldap", {}).get("address", "ldap.ocslab.com")
        port = settings.get("ldap", {}).get("port", 389)
        dn = settings.get("ldap", {})\
                     .get("dn", "uid=%s,ou=Moscow,ou=People,dc=phorm,dc=com")
        filter = settings.get("ldap", {}).get("filter", "(active=TRUE)")
        displayname = settings.get("ldap", {}).get("displayname",
                                                   "displayName")
        mail = settings.get("ldap", {}).get("mail", "mail")
        self.ldap = self.LDAP(address=address, port=port, dn=dn,
                              filter=filter, displayname=displayname,
                              mail=mail)

        address = settings.get("memcached", {}) \
                          .get("address", "127.0.0.1:11211")
        debug = settings.get("memcached", {}).get("debug", 0)
        pid = settings.get("memcached", {}).get("pid", "memcached.pid")
        self.memcached = self.Memcached(address, debug, pid)

        self.logging = self.Logging(settings.get("logging"))

    def load_settings(self, filename):
        with open(filename) as handler:
            data = json.loads(handler.read())
        return data
