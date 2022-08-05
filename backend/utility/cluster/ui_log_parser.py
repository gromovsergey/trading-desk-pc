#!/usr/bin/env python
import sys
import os
import re
import commands
import syslog
import ConfigParser
import shutil
import traceback
import optparse
import json
from commons import get_logger, get_config, PersistentStorage, FileLock
from commons import unthrowable

try:
    from glassfish_log_formatter import Inserter
    from glassfish_log_filter import LogParser
except ImportError:
    sys.path.append(os.path.dirname(os.path.abspath(__file__)) +
                    '/../dev/glassfish-log-filter')
    from glassfish_log_formatter import Inserter
    from glassfish_log_filter import LogParser


EXTRACT_PID_CMD = 'ps -ef ' + \
                  '| awk \'/bin\/java.*glassfish.*domain1/ {print $2}\' ' + \
                  '| grep "" -m 1'
CONFIG_FILENAME = '/opt/foros/ui/etc/conf/ui_log_parser'
LAST_LOG_FILENAME = 'server.log_9999-99-99T99-99-99'
SEVERITY_LEVELS = ['WARNING', 'SEVERE']


def load_rules(filename):
    with open(filename, 'r') as handler:
        data = handler.read()
    data = re.sub(re.compile("^#.*?\n", re.S | re.M), "\n", data)
    data = json.loads(data)
    config = {'ignore': [], 'rules': [], 'unsorted': {}, 'force': []}
    config["ignore"] = [x["pattern"] for x in data["IGNORE"]]
    config["rules"] += [(x["code"], x["pattern"], x["subj"], "ERROR")
                        for x in data["SEVERE"]]
    config["rules"] += [(x.get("code", ""), x["pattern"], x["subj"], "WARNING")
                        for x in data["WARNING"] if x.get("pattern")]
    unsorted = [x for x in data["WARNING"] if not x.get("pattern")][0]
    config["unsorted"]["SEVERE"] = (unsorted["code"], unsorted["subj"], "WARNING")
    config["unsorted"]["WARNING"] = (unsorted["code"], unsorted["subj"], "WARNING")
    return config


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option('-c', '--config', metavar='filename',
                      help='config filename, default %s' % CONFIG_FILENAME,
                      default=CONFIG_FILENAME)
    return parser.parse_args()[0]


def output_to_syslog(ident, log):
    SEVERITY = {'WARNING': syslog.LOG_WARNING,
                'ERROR': syslog.LOG_ERR}

    count = 0
    syslog.openlog(ident, 0, syslog.LOG_DAEMON)
    try:
        @unthrowable(logger, "could not write to syslog: %s")
        def write(msg):
            priority = SEVERITY.get(msg[1], syslog.LOG_ERR)
            line = msg[0]
            line = line.replace('\x00', r'\x00')
            if isinstance(line, unicode):
                line = line.encode('utf-8', 'strict')
            logger.debug("MESSAGE: %s: %s", priority, line)
            try:
                syslog.syslog(priority, line)
            except TypeError, e:
                raise TypeError(
                    "unexpected type: %s" % line.__class__.__name__)

        count = len(map(write, filter(lambda x: x, log)))

    finally:
        syslog.closelog()
    return count


def get_log_files(config, storage):
    if not os.path.isdir(config.main.glassfish_logs_folder):
        logger.warning('No such directory: %s',
                       config.main.glassfish_logs_folder)
        return []

    pattern = re.compile(r'server.log_\d{4}\-\d{2}\-\d{2}T\d{2}\-\d{2}\-\d{2}',
                         re.S)
    while True:
        file_list = filter(lambda x: pattern.match(x),
                           os.listdir(config.main.glassfish_logs_folder))
        shutil.copy(config.main.glassfish_logs_folder + '/server.log',
                    config.main.glassfish_logs_folder + '/' + LAST_LOG_FILENAME)
        approved_file_list = filter(lambda x: pattern.match(x),
                                    os.listdir(config.main
                                                     .glassfish_logs_folder))
        if approved_file_list == file_list:
            break
    file_list = filter(lambda x: x > storage['lastprocessed'], file_list)
    file_list.sort()
    return file_list


def process_log(parser, rules, storage, config, stream):
    def timestamp_filter(record):
        if record.timestamp <= storage['timestamp']:
            return False
        storage['timestamp'] = record.timestamp
        return True

    def force_it(record):
        record.severity = "SEVERE"
        return record

    log = parser.parse(stream)
    log = log.map_if_contains(force_it, *rules['force'])
    log = log.severity(*SEVERITY_LEVELS)
    log = log.filter(timestamp_filter)
    log = log.contains(*rules['ignore'], **({'negation': True}))
    log = log.map(Inserter(rules))
    ident = config.output.ident % commands.getoutput(EXTRACT_PID_CMD)
    msg_count = output_to_syslog(ident, log)
    logger.info('Found %d messages', msg_count)


def process_all_logs(config, storage):
    rules = load_rules(config.main.rules_file)
    parser = LogParser()
    lock = FileLock(config.main.lock_file)
    lock.acquire()
    try:
        for filename in get_log_files(config, storage):
            try:
                logger.info('Processing file "%s"', filename)
                fullfilename = os.path.join(
                    config.main.glassfish_logs_folder, filename)
                with open(fullfilename, 'r') as stream:
                    process_log(parser, rules, storage, config, stream)
            except Exception, e:
                logger.exception(e)
                logger.syslog(
                    "could not parse filename %s: %s" % (filename, e))
            if filename != LAST_LOG_FILENAME:
                storage['lastprocessed'] = filename
            storage.save()
    finally:
        lock.release()

if __name__ == '__main__':
    configfilename = parse_args().config
    config = get_config(configfilename)
    logger = get_logger(config.log.file, config.log.level, __file__,
                        console_format="%(asctime)-15s: %(message)s")
    logger.before(__file__, sys.argv[1:])
    try:
        storage = \
            PersistentStorage(config.state.file, config.state.holesize,
                              lastprocessed='server.log_0000-00-00T00-00-00',
                              timestamp='0000-00-00T00:00:00.000+0000')
        logger.info('Start timestamp: "%s"', storage['timestamp'])
        process_all_logs(config, storage)
    except Exception, error:
        logger.exception(error)
        logger.syslog(error)
        logger.exit(1)
    else:
        logger.exit(0)
