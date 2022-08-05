# coding=utf-8
import ldap
import json
import memcache
import binascii
import logging
from hashlib import md5

from pyquery import PyQuery
from Crypto.Cipher import AES

import tornado.httpclient
import tornado.web
import tornado.gen

from cache import MemcachedClientMixin
from utils import IsAjaxMixin

logger = logging.getLogger("tornado.auth")


class User(object):
    def __init__(self, username, password, displayname, mail):
        self.displayname = displayname
        self.mail = mail
        self.username = username
        self.password = password

    @staticmethod
    def random(count):
        try:
            from Crypto import Random
            Random.atfork()
            return Random.new().read(count)
        except ImportError:
            #  for pycrypto 2.0.1 compatibility
            from Crypto.Util import randpool
            return randpool.RandomPool().get_bytes(count)

    def serialize(self):
        salt = 32
        align = 16

        data = json.dumps(self.__dict__)
        sentinels = align - len(data) % align
        data = data + " " * sentinels

        ckey = "user-%s" % \
            md5(self.username + binascii.b2a_base64(
                User.random(salt))).hexdigest()
        mkey = md5(data + binascii.b2a_base64(User.random(salt))).hexdigest()

        cipher = AES.new(mkey, AES.MODE_ECB)
        mdata = cipher.encrypt(data).encode('hex')
        return (ckey, mkey, mdata)

    @staticmethod
    def deserialize(mkey, mdata):
        try:
            cipher = AES.new(mkey, AES.MODE_ECB)
            data = cipher.decrypt(binascii.unhexlify(mdata))
            obj = User("", "", "", "")
            obj.__dict__ = json.loads(data)
            return obj
        except Exception, error:
            logger.exception(error)
            return None


class UserHandlerMixin(MemcachedClientMixin):
    def get_current_user(self):
        uid = self.get_secure_cookie("__uid")
        gid = self.get_secure_cookie("__gid")
        if not (uid and gid):
            return None
        data = self._get_cache_client().get(uid)
        if data is None:
            return None
        user = User.deserialize(gid, data)
        if user:
            logger.debug("USER: %s, PATH: %s",
                         user.username, self.request.path)
        return user


class LogoutHandler(UserHandlerMixin,
                    tornado.web.RequestHandler):
    @tornado.web.authenticated
    def get(self):
        uid = self.get_secure_cookie("__uid")
        if uid:
            self._get_cache_client().delete(uid)
        self.clear_all_cookies()
        logging.info("User %s logged out", self.current_user.username)
        self.redirect("/")


class LoginHandler(MemcachedClientMixin, IsAjaxMixin,
                   tornado.web.RequestHandler):
    def initialize(self):
        self.config = self.application.settings["config"]

    def get(self):
        if not self.is_ajax():
            self.render("login.html", error="")
        else:
            self.set_status(403)
            self.write({"data": "Authentication required",
                        "redirect": self.get_login_url()})

    def ldap_auth(self, username, password):
        try:
            conn = ldap.open(self.config.ldap.address, self.config.ldap.port)
            conn.protocol_version = ldap.VERSION3

            dn = self.config.ldap.dn % username
            attrs = [str(self.config.ldap.displayname),
                     str(self.config.ldap.mail)]

            info = conn.search_s(dn, ldap.SCOPE_BASE,
                                 self.config.ldap.filter, attrs)[0][1]
            if not info:
                raise tornado.web.HTTPError(
                    403, reason="Invalid username or password")

            conn.bind_s(dn, password)
            displayname = info[self.config.ldap.displayname][0]
            mail = info[self.config.ldap.mail][0]
            return User(username, password, displayname, mail)
        except ldap.INVALID_CREDENTIALS:
            logger.warning("LDAP: Invalid credentials for %s", username)
            raise tornado.web.HTTPError(
                403, reason="Invalid username or password")
        except ldap.NO_SUCH_OBJECT:
            logger.warning("LDAP: No such object: %s", username)
            raise tornado.web.HTTPError(
                403, reason="Invalid username or password")
        except ldap.SERVER_DOWN:
            logger.error("LDAP: Server down")
            raise tornado.web.HTTPError(
                503, reason="Could not connect to LDAP")

    def bamboo_auth(self, username, password):
        http_client = tornado.httpclient.HTTPClient()
        response = http_client.fetch(self.config.bamboo.check_url,
                                     auth_username=username,
                                     auth_password=password,
                                     validate_cert=False)
        q = PyQuery(response.body)
        if not q.find("#admin"):
            logger.info("User %s not in bamboo-admin group", username)
            raise tornado.web.HTTPError(
                403, reason="You have not rights to do this. "
                            "Please, contact administrator")

    def post(self):
        user = None
        try:
            username = self.get_argument("username")
            password = self.get_argument("password")
            self.check_xsrf_cookie()
            user = self.ldap_auth(username, password)
            self.bamboo_auth(username, password)
        except tornado.web.MissingArgumentError, error:
            logger.warning("Incorrect request!")
            logger.exception(error)
            raise tornado.web.HTTPError(
                405, reason="Could not connect to LDAP")
        except tornado.web.HTTPError, error:
            raise
        except Exception, error:
            logger.exception(error)
            raise tornado.web.HTTPError(
                500, reason="Something goes wrong")
        else:
            expires = 86400  # 60s * 60m * 24h = 1 day
            uid, gid, data = user.serialize()
            self._get_cache_client().set(uid, data, expires)
            self.set_secure_cookie("__uid", uid, expires)
            self.set_secure_cookie("__gid", gid, expires)
            url = self.get_argument('next', '/')
            logger.info("User %s is logged in", user.username)
            self.redirect(url)

    def write_error(self, status_code, **kwargs):
        if "exc_info" in kwargs:
            error = kwargs["exc_info"][1]
            if hasattr(error, "reason"):
                message = error.reason
            else:
                message = repr(error)
        else:
            message = ""
        self.set_status(status_code)
        self.render("login.html", error=message)
