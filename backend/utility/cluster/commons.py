#!/usr/bin/env python
"""
This file containes common functions and classes for cluster utilities.
"""

__all__ = ['get_config',
           'get_logger',
           'make_archive',
           'PersistentStorage',
           'FileLock',
           'get_platform_description',
           'unthrowable']

import os
import sys


def get_platform_description():
    import os
    oper = "OS: %s; Host: %s; Version: %s; %s; %s" % os.uname()
    proc = "PROC: pid: %s; ppid: %s; pgrp: %s; pwd: %s" % (
        os.getpid(), os.getppid(), os.getpgrp(), os.getcwd())
    user = "USER: uid: %s(%s); gid: %s(%s)" % (
        os.getuid(), os.geteuid(), os.getgid(), os.getegid())
    env = "ENV:\n%s" % "\n".join(map(lambda x: "  %s: %s" % x,
                                     os.environ.items()))
    return "%s\n%s\n%s\n%s\n" % (oper, proc, user, env)


def exception_hook(type, value, traceback, logger=None):
    import logging
    import traceback as tb
    description = get_platform_description()
    if logger is None:
        logger = logging.getLogger("foros")
    if logger and hasattr(logger, "multiline"):
        logger.multiline.critical(description)
        logger.multiline.critical(
            "".join(tb.format_exception(type, value, traceback)))

    sys.stderr.write(description)
    return sys.__excepthook__(type, value, traceback)


sys.excepthook = exception_hook


LOG_FORMAT = "%(asctime)-15s: %(levelname)s: %(message)s"
LOG_CONSOLE_FORMAT = "%(message)s"


def _log_before(logger, filename, args=[]):
    filename = os.path.abspath(filename)
    message = "STARTED: %s %s" % (filename, ' '.join(args))
    logger.info(message)


def _log_after(logger, result, message=''):
    message = "DONE: %s result=%d\n" % (message, result)
    logger.info(message)


def _log_exit(logger, result, message=''):
    _log_after(logger, result, message)
    os._exit(result)


def _log_exception(logger, exception=None):
    type, value, traceback = sys.exc_info()
    exception_hook(type, value, traceback, logger)


class LogMultiline(object):
    def __init__(self, logger):
        methods = ['console', 'critical', 'debug', 'error',
                   'exception', 'fatal', 'info', 'warning']
        for method in methods:
            setattr(self, method, self(logger, method))

    def __call__(self, logger, method):
        method = getattr(logger, method)

        def level(msg, *args, **kwargs):
            for line in msg.splitlines():
                method(line, *args, **kwargs)

        return level


def get_logger(filename, level, name='foros', format=LOG_FORMAT,
               console_format=LOG_CONSOLE_FORMAT):
    """This function returns instance of logger class.

    Instance contains addtional severity level 'console'.
    It is lower than 'warning' but higher 'info'.
    All message with severity higher or equal 'console' printed to stdout.

    Instance contains methods 'before' and 'after' for logging 'start'
    and 'stop' moments respectively. Also it contains method 'exit',
    same as 'after', but stops application.

    :param filename: Log's filename.
    :type filename: str
    :param level: Severity level for logging in file.
    :type level: str
    :param name: Unique name of logger.
    :type name: str
    :param format: Record's format for logging in file.
    :type format: str
    :returns: Logger instance

    >>> import tempfile, os, shutil
    >>> test_dir = tempfile.mkdtemp(prefix='doctest')
    >>> log_file = os.path.join(test_dir, 'log.log')
    >>> logger = get_logger(log_file, 'DEBUG', \
                            format='%(levelname)s: %(message)s')
    >>> logger.before('/file')
    >>> logger.debug('debug message')
    >>> logger.info('info message')
    >>> logger.console('console message')
    >>> logger.warning('warning message')
    >>> logger.error('error message')
    >>> logger.critical('critical message')
    >>> logger.multiline.info('very very\\nlong message')
    >>> logger.after(0)
    >>> open(log_file, 'r').read()
    'INFO: STARTED: /file: \\n\
DEBUG: debug message\\n\
INFO: info message\\n\
INFO: console message\\n\
WARNING: warning message\\n\
ERROR: error message\\n\
CRITICAL: critical message\\n\
INFO: very very\\n\
INFO: long message\\n\
INFO: DONE:  result=0\\n\\n'
    >>> shutil.rmtree(test_dir)
    """

    import logging
    import logging.handlers
    CONSOLE_LEVEL = 25
    SYSLOG_LEVEL = 60
    MAX_BYTES = 1048576
    BACKUP_COUNT = 10

    if not os.path.exists(os.path.dirname(filename)):
        os.makedirs(os.path.dirname(filename))

    if os.path.isfile(name):
        name, _ = os.path.splitext(os.path.basename(name))

    log = logging.getLogger()
    if hasattr(log, 'console'):
        return log

    level = getattr(logging, level)
    logging.addLevelName(CONSOLE_LEVEL, 'INFO')
    logging.addLevelName(SYSLOG_LEVEL, 'CRITICAL')

    log_formatter = logging.Formatter(format)
    cls_formatter = logging.Formatter(console_format)
    sys_formatter = logging.Formatter('FOROS.UI: ' + name + ': %(message)s')

    log_handler = logging.handlers.RotatingFileHandler(
        filename, maxBytes=MAX_BYTES, backupCount=BACKUP_COUNT)
    log_handler.setLevel(level)
    log_handler.setFormatter(log_formatter)
    log.addHandler(log_handler)

    cls_handler = logging.StreamHandler(sys.stderr)
    cls_handler.setLevel(CONSOLE_LEVEL)
    cls_handler.setFormatter(cls_formatter)
    log.addHandler(cls_handler)

    sys_handler = logging.handlers.SysLogHandler(
        address="/dev/log", facility="cron")
    sys_handler.setLevel(SYSLOG_LEVEL)
    sys_handler.setFormatter(sys_formatter)
    log.addHandler(sys_handler)

    log.setLevel(level)

    setattr(log, 'console', lambda *args: log.log(CONSOLE_LEVEL, *args))
    setattr(log, 'syslog', lambda *args: log.log(SYSLOG_LEVEL, *args))
    setattr(log, 'before', lambda *args, **kwargs: _log_before(log, *args,
                                                               **kwargs))
    setattr(log, 'after', lambda *args, **kwargs: _log_after(log, *args,
                                                             **kwargs))
    setattr(log, 'exit', lambda *args, **kwargs: _log_exit(log, *args,
                                                           **kwargs))
    setattr(log, 'multiline', LogMultiline(log))
    setattr(log, 'exception', lambda *args: _log_exception(log, *args))
    return log


class ReadOnlyConfig(object):
    class Dict(dict):
        def __init__(self, *data):
            super(ReadOnlyConfig.Dict, self).__init__(*data)

        def __setitem__(self, key, value):
            raise TypeError

        def __delitem__(self, key):
            raise TypeError

        def update(self, dict):
            raise TypeError

    def __init__(self, *values):
        self.__dict__ = ReadOnlyConfig.Dict(*values)

    def __repr__(self):
        return self.__dict__.__repr__()


def _get_ini_config(filename):
    import ConfigParser
    config = ConfigParser.ConfigParser()
    config.read(filename)
    result = {}
    for section in config.sections():
        result[section] = ReadOnlyConfig(config.items(section))
    config = ReadOnlyConfig(result)
    return config


def _get_shell_config(filename):
    def _parse_shell_variables(text):
        import re
        return re.findall(r'^(\w+)=[\"\']?(.*?)[\"\']?$', text, re.M)

    import commands
    output = commands.getoutput("source \"%s\"; set" % filename)
    pairs = _parse_shell_variables(output)
    base = dict(_parse_shell_variables(commands.getoutput("set")))
    pairs = filter(lambda x: (not x[0] in base), pairs)
    return ReadOnlyConfig(pairs)


def get_config(filename, format='ini'):
    """Parse configuration file and returns simple object with configuration
    structure.

    :param filename: Configuration filename.
    :type filename: str
    :param format: Configuration file format.
    :type format: str
    :returns: Configuration object
    :raises: IOError, TypeError

    >>> import tempfile, os, shutil
    >>> test_dir = tempfile.mkdtemp(prefix='doctest')
    >>> ini_file = os.path.join(test_dir, 'config.ini')
    >>> open(ini_file, 'w').write('\
[first]\\n\
param1=value1\\n\
param2=value2\\n\
[second]\\n\
param3=value3\\n')
    >>> ini_config = get_config(ini_file)
    >>> ini_config.first.param1
    'value1'
    >>> ini_config.first.param2
    'value2'
    >>> ini_config.second.param3
    'value3'
    >>> sh_file = os.path.join(test_dir, 'config.sh')
    >>> open(sh_file, 'w').write('\
param1=value1\\n\
param2=value2\\n\
param3=value3$param1$param2\\n\
param4=`echo $param1`')
    >>> sh_config = get_config(sh_file, 'shell')
    >>> sh_config.param1
    'value1'
    >>> sh_config.param2
    'value2'
    >>> sh_config.param3
    'value3value1value2'
    >>> sh_config.param4
    'value1'
    >>> get_config('unknown file')
    Traceback (most recent call last):
    ...
    IOError: Configuration not found
    >>> get_config(ini_file, 'unknown')
    Traceback (most recent call last):
    ...
    TypeError: Unknown configuration format "unknown"
    >>> shutil.rmtree(test_dir)
    """
    if not os.path.exists(filename):
        raise IOError('Configuration not found')
    if format == 'ini':
        return _get_ini_config(filename)
    elif format == 'shell':
        return _get_shell_config(filename)
    else:
        raise TypeError('Unknown configuration format "%s"' % format)


class PersistentStorage(dict):
    def __init__(self, filename, holesize, **kwargs):
        self.__dict__ = self
        self._filename = filename
        self._properties = kwargs
        self._check_filename
        self._initialize()
        self._holesize = holesize
        self._count = 0
        self.__del__ = self.save()

    def _check_filename(self):
        dirname = os.path.dirname(self._filename)
        if not os.path.exists(dirname):
            os.makedirs(dirname)

    def _initialize(self):
        self.update(self._properties)
        if not os.path.exists(self._filename):
            return
        handler = open(self._filename, 'r')
        for line in handler:
            key, value = line.split('=')
            key = key.strip()
            if not key in self._properties:
                continue
            if value.strip().isdigit():
                value = int(value.strip())
            else:
                value = value.strip()
            self[key] = value
        handler.close()

    def __setitem__(self, key, value):
        if '_count' in self and key in self._properties:
            super(PersistentStorage, self).__setitem__(key, value)
            self._count += 1
            if self._count == self._holesize:
                self.save()
        elif not '_count' in self or key in self:
            super(PersistentStorage, self).__setitem__(key, value)
        else:
            raise KeyError('Forbidden key "%s"' % key)

    def save(self):
        self._count = 0
        data = ''
        for pair in filter(lambda x: (not x[0].startswith('_')), self.items()):
            data += '%s=%s\n' % pair
        handler = open(self._filename, 'w')
        handler.write(data)
        handler.close()


class FileLock(object):
    def __init__(self, filename):
        self._filename = filename

    def acquire(self):
        if os.path.isfile(self._filename):
            raise IOError('FileLock: already locked')

        try:
            open(self._filename, 'w').close()
        except IOError, error:
            raise IOError("FileLock: can't create lock file '%s'." %
                          self._filename)

    def release(self):
        try:
            if os.path.isfile(self._filename):
                os.unlink(self._filename)
        except IOError, error:
            raise IOError("FileLock: can't remove lock file '%s'. Remove it." %
                          self._filename)


def make_archive(filename, folder, params=''):
    """Making tar.gz archive of defined folder with defined filename.

    :param filename: Resulting archive filename
    :type filename: str
    :param folder: Target folder
    :type folder: str
    :param params: Addtional parameters
    :type params: str
    :returns: True if archiving completed successfully.
    :raises: IOError, OSError
    """
    if not os.path.exists(os.path.dirname(filename)):
        raise IOError('Can\' create file "%s". Folder "%s" does not exists' %
                      (filename, os.path.dirname(filename)))
    if not os.path.exists(folder):
        raise IOError('Target folder "%s" does not exists' % folder)
    from subprocess import call
    command = "tar -C %s -phczf %s %s ./ >/dev/null" % (folder, filename,
                                                        params)
    retcode = call(command, shell=True)
    return retcode == 0


def unthrowable(logger=None, msg=None):
    if msg is None:
        msg = "%s"

    def decorate(func):
        def wrapper(*args, **kwargs):
            try:
                return func(*args, **kwargs)
            except Exception, e:
                if logger:
                    logger.syslog(msg, e)
                    logger.exception(e)
        return wrapper
    return decorate


if __name__ == '__main__':
    import doctest
    doctest.IGNORE_EXCEPTION_DETAIL = True
    doctest.ELLIPSIS = True
    doctest.testmod()
