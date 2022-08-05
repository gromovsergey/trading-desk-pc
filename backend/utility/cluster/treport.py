#!/usr/bin/env python

import os
import sys
import subprocess
import shlex
import shutil
import optparse
import ConfigParser
from StringIO import StringIO
import re
import time
import threading
import signal
import commons


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option("-c", "--config", dest="config", default='',
                      help="config filename, it's ini file")
    parser.add_option("-w", "--work_dir", dest="work_dir",
                      help="working directory")
    parser.add_option("-a", "--archive", dest="archive",
                      help="output archive filename")
    parser.add_option("-l", "--loglevel", dest="loglevel",
                      help="logging level, default 'INFO'", default="INFO")
    parser.add_option("-L", "--log", metavar="filename",
                      help="log filename, default: '__archive__.log'")
    parser.add_option("-t", "--timeout", dest="timeout",
                      help="timeout for ..., default '0'", default=0, type=int)
    parser.add_option("-g", "--groups", dest="groups", default="A",
                      help="commands groups for executing, default 'A'")
    args = parser.parse_args()[0]
    if not args.work_dir or not args.archive:
        parser.error("-w (work_dir) and -a (archive) required.")
    args.groups = args.groups.split(',')
    if not args.log:
        args.log = args.archive + '.log'
    return args


def get_config(filename=''):
    """
[A]
clusterStatus: ('/opt/foros/manager/bin/cmgr -f ui status', 'run'),
packageInfo: ('rpm -qa', 'run')
uptime: ('uptime', 'run')
date: ('date', 'run')
loggedUsers: ('who', 'run')
processesInfo: ('ps uax', 'run')
processesTree: ('pstree -p', 'run')
netStats: ('netstat -ntp', 'run')
openedFiles: ('/usr/sbin/lsof', 'run')
diskSpace: ('df -h', 'run')
jmxFetcher: ('/opt/foros/ui/bin/oui_jmx_status.sh', 'run')
dirsSync: ('/opt/foros/ui/bin/check_dirs_sync.sh', 'run')
serverLog: ('LOGS_DIR=/opt/foros/ui/var/log/domain1; find -L $LOGS_DIR\
  -type f -name server.log* | sort | tail -n 2;\
  echo $LOGS_DIR/server.log', 'fetch')
apacheLog: ('find -L /opt/foros/ui/var/log/apache \
  -name *_log* -ctime -7', 'fetch')
jvmStats: ('PID=`cat /opt/foros/ui/var/domains/domain1/config/pid` && \
  kill -QUIT $PID && sleep 60 >/dev/null && \
  echo "/opt/foros/ui/var/log/domain1/jvm.log"', 'fetch')

[B]
logFiles: ('find -L /opt/foros/ui/var/log/* -type d \
    ! -wholename *apache* ! -wholename *domain1* ! -wholename *treport*', 'fetch')
heapDumper: ('PID=`cat /opt/foros/ui/var/domains/domain1/config/pid` && \
  jmap -dump:file=/opt/foros/ui/var/tmp/jmap-$$.dump $PID >/dev/null && \
  echo "/opt/foros/ui/var/tmp/jmap-$$.dump"', 'fetch')
heapDumpFinder: ('find -L /opt/foros/ui/var/domains/domain1/logs/ \
  -name *hprof', 'fetch')

    """
    if filename and os.path.exists(filename):
        handler = open(filename, 'r')
        text = handler.read()
        handler.close()
        logger.debug('Using config file: %s', filename)
    else:
        text = get_config.__doc__
        logger.debug('Using __doc__ config')
    config = ConfigParser.RawConfigParser()
    config.optionxform = str
    config.readfp(StringIO(text))
    return config


def get_commands(config):
    commands = {}
    for group in config.sections():
        logger.debug('Group: %s' % group)
        for name, value in config.items(group):
            pair = re.findall(r"\('(.*?)', *?'(\w+)'\)", value)
            if not pair:
                raise Exception('Bad command format, must be: '
                                '(\'<command>\',\'<type>\')')
            command, type = pair[0]
            logger.debug('Name: %s; Type: %s; Command: %s',
                         name, type, command)
            commands.setdefault(group, []).append((name, type, command))
    return commands


class Report(object):
    class Spy(threading.Thread):
        def __init__(self, executing, timeout):
            super(Report.Spy, self).__init__()
            self.executing = executing
            self.timeout = timeout
            self.stopped = False

        def run(self):
            logger.debug('Spy started')
            while not self.stopped or len(self.executing):
                current_time = time.time()
                for name in self.executing.keys():
                    try:
                        command = self.executing[name]
                        return_code = command['child'].poll()
                        if return_code is None:
                            if not self.timeout:
                                continue
                            start_time = command['start_time']
                            if self.timeout < current_time - start_time:
                                os.kill(command['child'].pid, signal.SIGKILL)
                                logger.warning('TIMEOUT: command %s is killed!',
                                               name)
                                del self.executing[name]
                        elif return_code == 0:
                            logger.debug('Process for command %s return 0', name)
                            command['on_success'](name)
                            logger.info('Command %s finished successfully', name)
                            del self.executing[name]
                        else:
                            logger.info('Command %s failed', name)
                            del self.executing[name]
                    except Exception, error:
                        logger.error('Error with command "%s"', name)
                        logger.exception(error)
                        if name in self.executing:
                            del self.executing[name]
                time.sleep(1)



        def stop(self):
            self.stopped = True
            logger.debug('Waiting commands...')
            self.join()
            logger.info('All commands finished')

    def __init__(self, work_dir, archive, timeout):
        logger.debug('Working directory: %s', work_dir)
        self.work_dir = work_dir
        if os.path.exists(self.work_dir):
            shutil.rmtree(self.work_dir)
        os.makedirs(self.work_dir)
        self.timeout = timeout
        self.archive = archive
        self.types = {'run': self.run, 'fetch': self.fetch}
        self.logs_dir = os.path.join(self.work_dir, 'logs')
        self.output_dir = os.path.join(self.work_dir, 'output')
        os.makedirs(self.logs_dir)
        os.makedirs(self.output_dir)
        self.executing = {}
        self.spy = Report.Spy(self.executing, self.timeout)
        self.spy.start()

    def execute(self, name, type, command):

        err_output = os.path.join(self.logs_dir, name + '.err.log')
        std_output = os.path.join(self.logs_dir, name + '.std.log')
        command = '"(' + command + ') 1>' + std_output + ' 2>' +\
            err_output + '"'
        try:
            self.executing.setdefault(name, {})['child'] = \
                subprocess.Popen(shlex.split(command), shell=True)
            logger.info('Command %s: executing "%s"', name, command)
            self.executing[name]['start_time'] = time.time()
            self.executing[name]['on_success'] = \
                self.types.get(type, lambda x:
                               logger.warning('Unknown type %s for commmand "%s"',
                               type, x))
        except Exception, error:
            logger.exception(error)
            if name in self.executing:
                del self.executing[name]

    def run(self, name):
        shutil.copy(os.path.join(self.logs_dir, name + '.std.log'),
                    os.path.join(self.output_dir, name + '.log'))

    def fetch(self, name):
        handler = open(os.path.join(self.logs_dir, name + '.std.log'), 'r')
        data = handler.read()
        handler.close()
        destination_dir = os.path.join(self.output_dir, name)
        logger.debug('Command "%s": destination directory: "%s"', name,
                     destination_dir)
        os.makedirs(destination_dir)
        for filename in sorted(data.splitlines()):
            logger.debug('Command "%s": File "%s"', name, filename)
            if os.path.exists(filename):
                if os.path.isfile(filename):
                    logger.debug('Command "%s": %s is file', name, filename)
                    shutil.copy(filename, destination_dir)
                else:
                    logger.debug('Command "%s": %s is directory',
                                 name, filename)
                    destination_sub_dir = destination_dir + filename
                    if not os.path.exists(destination_sub_dir):
                        logger.debug('Command "%s": Subdir %s does not exist',
                                     name, destination_sub_dir)
                        if not os.path.exists(
                                os.path.dirname(destination_sub_dir)):
                            os.makedirs(os.path.dirname(destination_sub_dir))
                        shutil.copytree(filename, destination_sub_dir)
                    else:
                        logger.debug('Command "%s": Subdir %s already exists',
                                     name, destination_sub_dir)
            else:
                logger.debug('Command "%s": %s does not exist', name, filename)

    def make_archive(self):
        self.spy.stop()
        if not os.path.exists(os.path.dirname(self.archive)):
            os.makedirs(os.path.dirname(self.archive))
        if not commons.make_archive(self.archive, self.work_dir):
            logger.error('Can\'t generate archive %s ', self.archive)
        else:
            logger.info('Archive %s is generated ', self.archive)
            shutil.rmtree(self.work_dir)


if __name__ == '__main__':
    args = parse_args()
    logger = commons.get_logger(args.log, args.loglevel)
    try:
        config = get_config(args.config)
        commands = get_commands(config)
        report = Report(args.work_dir, args.archive, args.timeout)
        for group in args.groups:
            if not group in commands:
                logger.warning('Unknown group %s, skipping...', group)
                continue
            for name, type, command in commands[group]:
                logger.debug('Executing command %s' % name)
                report.execute(name, type, command)
        report.make_archive()
    except Exception, error:
        logger.exception(error)
        sys.exit(1)
