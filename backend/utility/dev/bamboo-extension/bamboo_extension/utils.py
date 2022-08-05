# coding=utf-8
import os
import logging
from logging.handlers import RotatingFileHandler


def init_logging(config):
    default_format = "%(asctime)-15s: %(levelname)s: %(message)s"
    default_level = "INFO"
    default_directory = "."
    default_maxbytes = 2097152
    default_backupcount = 10
    root = logging.getLogger()
    if config.logging.root:
        root_directory = config.logging.root.get(
            "directory", default_directory)
        filename = os.path.join(
            root_directory, config.logging.root.get("name", "root"))
        root_format = config.logging.root.get(
            "format", default_format)
        formatter = logging.Formatter(root_format)
        root_maxbytes = config.logging.root.get("rotate", {})\
                                      .get("maxbytes", default_maxbytes)
        root_backupcount = config.logging.root.get("rotate", {})\
                                 .get("backupcount", default_backupcount)
        handler = RotatingFileHandler(filename, maxBytes=root_maxbytes,
                                      backupCount=root_backupcount)
        root_level = config.logging.root.get("level", default_level)
        handler.setLevel(getattr(logging, root_level))
        handler.setFormatter(formatter)
        root.addHandler(handler)
        root.setLevel(getattr(logging, root_level))

    else:
        root_directory = default_directory
        root_backupcount = default_backupcount
        root_maxbytes = default_maxbytes
        root_level = default_level
        root_format = default_format

    for key, value in config.logging.loggers.items():
        log = logging.getLogger(key)
        filename = os.path.join(value.get("directory", root_directory),
                                value.get("name", key))
        format = value.get("format", root_format)
        formatter = logging.Formatter(format)
        maxbytes = value.get("rotate", {}).get("maxbytes", root_maxbytes)
        backupcount = value.get("rotate", {}).get("backupcount",
                                                  root_backupcount)
        handler = RotatingFileHandler(filename, maxBytes=maxbytes,
                                      backupCount=backupcount)
        level = value.get("level", root_level)
        handler.setLevel(getattr(logging, level))
        handler.setFormatter(formatter)
        log.addHandler(handler)
        log.setLevel(getattr(logging, level))


class IsAjaxMixin(object):
    def is_ajax(self):
        key = "X-Requested-With"
        value = "XMLHttpRequest"
        return self.request.headers.get(key) == value
