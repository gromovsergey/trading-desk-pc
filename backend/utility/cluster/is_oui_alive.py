#!/usr/bin/env python

import os
import threading
import sys
import commands
import subprocess
import re
import optparse
from commons import get_logger, get_config


ROLES = [
    'internal',
    'advertiser',
    'publisher',
    'isp',
    'cmp'
]
MONITORING_TYPE = {
    'GLASSFISH': '1',
    'BALANCER': '2'
}
CONFIG_FILENAME = '/opt/foros/ui/etc/conf/is_oui_alive'


def get_user_roles(etc_dir):
    available_roles = {}
    for role in ROLES:
        filename = etc_dir + '/' + role
        if os.path.exists(filename):
            credentials = open(filename, 'r').read().split(' ')
            available_roles[role] = UserRole(role,
                                             credentials[0], credentials[1])
    if len(available_roles) == 0:
        logger.warning('no available users found at "%s"', etc_dir)
        logger.warning('\tExpected files: %s. File format is "user password"',
                       ', '.join(ROLES))
    return available_roles


def get_ports(hosts_n_ports):
    available_ports = []
    current_host = commands.getoutput('hostname').strip()
    # property format: <host name1>:<port number1>,...,<port numberN>
    #                  <host name2>:<port number1>,...,<port numberM> ...
    #                  <host nameH>:<port number1>,...,<port numberL>
    for host_n_ports in hosts_n_ports.strip().split(' '):
        host, ports = host_n_ports.split(':')
        if host != current_host:
            continue
        for port in ports.split(','):
            if len(port.strip()) > 0 and int(port):
                available_ports.append(port)
        break
    if len(available_ports) == 0:
        raise Exception('No available listener ports found for "%s"' %
                        current_host)
    return available_ports


def generate_name(role, port):
    if port == '8181':
        return role + '_GLASSFISH_' + port + '_'
    else:
        return role + '_BALANCER_' + port + '_'


class UserRole:
    def __init__(self, name, login, password):
        self.name = name
        self.login = login.strip()
        self.password = password.strip()


class CommandRunner(threading.Thread):
    def __init__(self, port, prefix, login, password, timeout):
        threading.Thread.__init__(self)
        self.port = port
        self.name = generate_name(prefix, port)
        self.login = login
        self.password = password
        self.timeout = timeout
        self.out = ''
        self.err = ''

    def run(self):
        try:
            command = 'curl -w "' + self.name + 'HTTPCODE = %{http_code}\n' + \
                self.name + 'TIME = %{time_total}\n" ' + \
                '-m ' + self.timeout + ' -d "j_username=' + \
                self.login + '&j_password=' + self.password + \
                '" -Lskbc https://localhost:' + self.port + \
                '/login/j_spring_security_check -o /dev/null'
            logger.debug('Request "%s": "%s"', self.name, command)
            popen = subprocess.Popen(command, stderr=subprocess.STDOUT,
                                     stdout=subprocess.PIPE, shell=True)
            self.out, self.err = popen.communicate()
            logger.info('Request "%s" return code "%d"', self.name,
                        popen.returncode)
        except KeyboardInterrupt:
            pass
        except Exception, e:
            pass


def print_dummy_values(port, role):
    paramName = generate_name(role, port)
    print(paramName + 'HTTPCODE = 200\n' + paramName + 'TIME = 0')


def run_threads(available_roles, available_ports, timeout):
    THREADS = []

    for port in available_ports:
        for role in ROLES:
            if not role in available_roles.keys():
                print_dummy_values(port, role.upper())
                continue

            roleObj = available_roles[role]
            thread = CommandRunner(port, roleObj.name.upper(),
                                   roleObj.login, roleObj.password,
                                   timeout)
            THREADS.append(thread)
            thread.start()

    for thread in THREADS:
        thread.join()
        output = thread.out.strip(' \t\n\r')
        time = re.search(r'\w+TIME = (\d+.\d+)', output).group(1)
        result = (output.replace('TIME = ' + time,
                  'TIME = ' + str(int(float(time.replace(',', '.')) * 1000))))
        logger.multiline.info(result)
        print result


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option('-c', '--config', metavar='filename',
                      help='config filename, default %s' % CONFIG_FILENAME,
                      default=CONFIG_FILENAME)
    return parser.parse_args()[0]


if __name__ == '__main__':
    sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)

    args = parse_args()
    config = get_config(args.config)
    logger = get_logger(config.log.file, config.log.level)
    logger.before(__file__, sys.argv[1:])
    try:
        available_roles = get_user_roles(config.main.etc_dir)
        available_ports = get_ports(config.main.hosts_n_ports)
        run_threads(available_roles, available_ports, config.main.timeout)
    except Exception, error:
        logger.exception(error)
        logger.exit(1)
    logger.exit(0)

run_threads()
