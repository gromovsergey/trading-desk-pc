#!/usr/bin/env python

import os
import threading
import sys
import subprocess
import time
import signal
import syslog
from commons import get_logger, get_config
from datetime import datetime
import optparse

CONFIG_FILE = '/opt/foros/ui/etc/conf/get_monitoring_values'
MONITORING_COMMANDS = [
    '/opt/foros/ui/bin/oui_jmx_status.sh',
    '/opt/foros/ui/bin/is_oui_alive.py'
]


class CommandRunner(threading.Thread):

    def __init__(self, command, timeout):
        threading.Thread.__init__(self)
        self.command = command
        self.timeout = timeout
        self.out = ''
        self.err = ''

    def run(self):
        start_time = time.time()

        child = subprocess.Popen(self.command,
                                 stderr=subprocess.PIPE,
                                 stdout=subprocess.PIPE, shell=True,
                                 preexec_fn=os.setsid)

        timeout = self.timeout + 2
        return_code = None
        while timeout > time.time() - start_time:
            time.sleep(1)
            return_code = child.poll()
            if not return_code is None:
                break

        if return_code is None:
            os.killpg(child.pid, signal.SIGKILL)
            logger.error('Command "%s" killed at %s. Timeout exceeded.',
                         self.command,
                         str(datetime.fromtimestamp(time.time())))

        self.out, self.err = child.communicate()
        if self.err:
            logger.warning('Command "%s":', self.command)
            logger.multiline.warning(self.err)
        logger.info('Command "%s" returns %d', self.command, child.returncode)


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option('-c', '--config', metavar='filename',
                      help='config filename, default: %s' % CONFIG_FILE,
                      default=CONFIG_FILE)
    return parser.parse_args()[0]


def run_threads(timeout):
    THREADS = []

    for command in MONITORING_COMMANDS:
        thread = CommandRunner(command, timeout)
        THREADS.append(thread)
        thread.start()

    for thread in THREADS:
        thread.join()
        print(thread.out.strip(' \t\n\r'))


def check_processes(processes):
    for name, pattern in processes.items():
        ret = subprocess.call(
            "pgrep -f -u `whoami` '%s' &>/dev/null" % pattern, shell=True)
        if ret != 0:
            message = "Process %s(%s) is not running" % (name, pattern)
            logger.error(message)
            syslog.openlog("FOROS.UI", 0, syslog.LOG_DAEMON)
            try:
                syslog.syslog(syslog.LOG_ERR, message)
            finally:
                syslog.closelog()


if __name__ == '__main__':
    sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)

    args = parse_args()
    config = get_config(args.config)
    logger = get_logger(config.log.file, config.log.level)
    logger.before(__file__, sys.argv[1:])
    try:
        run_threads(int(config.base.timeout))
        check_processes(config.processes.__dict__)
    except Exception, error:
        logger.exception(error)
        logger.exit(1)
    logger.exit(0)
