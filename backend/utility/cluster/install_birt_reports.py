#!/usr/bin/env python
import optparse
import logging
import os
import sys
import itertools
import shutil
import traceback
import psycopg2
from datetime import datetime
from commands import getoutput
import ConfigParser
from commons import get_logger, get_config, make_archive
from commons import get_platform_description

CONFIG_FILENAME = '/opt/foros/ui/etc/conf/install_birt_reports'


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option('-c', '--config', metavar='filename',
                      help='Config filename, default: %s' %
                      CONFIG_FILENAME, default=CONFIG_FILENAME)
    return parser.parse_args()[0]


def get_foros_ui_version():
    return getoutput("rpm -q --qf %{Version} foros-ui").strip()


def check_verison(destination):
    logger.console('Checking version')
    version_file = os.path.join(destination, '.version')
    if os.path.exists(version_file):
        fh = open(version_file, 'r')
        report_version = fh.read().strip()
        fh.close()
        rpm_version = get_foros_ui_version().strip()
        logger.info('RPM: %s; Reports: %s', rpm_version, report_version)
        return rpm_version != report_version
    else:
        logger.warning('File "%s" does not exists', version_file)
        return True


def store_version(destination):
    version_file = os.path.join(destination, '.version')
    fh = open(version_file, 'w')
    fh.write(get_foros_ui_version().strip())
    fh.close()


def clear_version(destination):
    version_file = os.path.join(destination, '.version')
    if os.path.exists(version_file):
        os.unlink(version_file)


def get_auditlog_update_description(report_id, report_name):
    """
<auditRecord version="1.1" type="Update">
  <entity class="com.foros.model.report.birt.BirtReport"
    id="%(report_id)d"
    name="%(report_name)s">
    <property
      name="Automatic Migration"
      changeType="UPDATE">%(oui_version)s</property>
  </entity>
</auditRecord>
    """
    return get_auditlog_update_description.__doc__ % \
        {'report_id': report_id,
         'report_name': report_name,
         'oui_version': get_foros_ui_version()
         }


def get_auditlog_create_description(report_id, report_name):
    """
<auditRecord version="1.1" type="Create">
  <entity class="com.foros.model.report.birt.BirtReport"
    id="%(report_id)d"
    name="%(report_name)s">
    <property
      name="Automatic Migration"
      changeType="ADD">%(oui_version)s</property>
    <property
      name="version"
      changeType="ADD">%(date)s</property>
    <property
      name="name"
      changeType="ADD">%(report_name)s</property>
  </entity>
</auditRecord>
    """
    return get_auditlog_create_description.__doc__ % \
        {'report_id': report_id,
         'report_name': report_name,
         'oui_version': get_foros_ui_version(),
         'date': datetime.now().__str__()
         }


class DB(object):
    def __init__(self, **kwargs):
        self.connection = psycopg2.connect(**kwargs)
        self.cursor = self.connection.cursor()

    def __del__(self):
        self.cursor.close()
        self.connection.close()

    def new_auditlog_id(self):
        query = "select nextval('auditlog_log_id_seq')"
        logger.debug('Query: "%s"', query)
        self.cursor.execute(query)
        return self.cursor.fetchone()[0]

    def id_exists(self, id):
        query = "select 1 from birtreport where birt_report_id = %(id)s"
        logger.debug('Query: "%s"; Args: name = "%s"', query, id)
        self.cursor.execute(query, {'id': id})
        record = self.cursor.fetchone()
        return bool(record)

    def name_exists(self, name):
        query = "select 1 from birtreport WHERE name = %(name)s"
        logger.debug('Query: "%s"; Args: name = "%s"', query, name)
        self.cursor.execute(query, {'name': name})
        record = self.cursor.fetchone()
        return bool(record)

    def _execute_and_commit(self, statement, parameters):
        self.cursor.execute(statement, parameters)
        self.connection.commit()

    def _insert_record_to_auditlog(self, id, type, description):
        statement = "insert into auditlog (log_id, log_date, " + \
            "object_type_id, object_id, action_type_id, ip, success, " + \
            "action_descr) values (%(log_id)s, current_timestamp, 43, %(report_id)s, " + \
            "%(type)s, '127.0.0.1', true, %(description)s)"
        parameters = {'log_id': self.new_auditlog_id(),
                      'report_id': id,
                      'type': int(type),
                      'description': description}
        logger.debug('Query: "%s"; Args: "%s"', statement,
                     parameters.__str__())
        self._execute_and_commit(statement, parameters)

    def insert(self, id, name):
        statement = "insert into birtreport (birt_report_id, name," + \
            " version) values (%s, %s, current_timestamp)"
        parameters = (id, name)
        logger.debug('Query: "%s"; Args: "%s"', statement,
                     parameters.__str__())
        self._execute_and_commit(statement, parameters)
        msg = get_auditlog_create_description(id, name)
        self._insert_record_to_auditlog(id, 0, msg)
        logger.info('NEW REPORT WAS ADDED: "%s"' % name)

    def update(self, id, name):
        statement = "update birtreport SET " + \
            "version = current_timestamp, name = %s " + \
            "WHERE birt_report_id = %s"
        parameters = (name, id)
        logger.debug('Query: "%s"; Args: "%s"', statement,
                     parameters.__str__())
        self._execute_and_commit(statement, parameters)
        msg = get_auditlog_update_description(id, name)
        self._insert_record_to_auditlog(id, 1, msg)

    def move(self, id, name):
        statement = "update birtreport SET " + \
            "version = current_timestamp, birt_report_id = %s " + \
            "WHERE name = %s"
        parameters = (id, name)
        logger.debug('Query: "%s"; Args: "%s"', statement,
                     parameters.__str__())
        self._execute_and_commit(statement, parameters)
        msg = get_auditlog_update_description(id, name)
        self._insert_record_to_auditlog(id, 1, msg)


def backup(folder, destination):
    logger.info('Backuping directory "%s"', folder)
    t = datetime.now()
    archive_name = \
        "%s/birt_reports_backup.%d-%02d-%02dT%02d%02d%02d.%06d.tar.gz" % \
        (destination, t.year, t.month, t.day, t.hour, t.minute,
         t.second, t.microsecond)
    if not make_archive(archive_name, folder):
        raise Exception('Can\'t generate "%s" file' % archive_name)
    logger.info('Backuping completed, file "%s"' % archive_name)


def install_all(reports, destination_dir, database):
    logger.info('Installing reports')
    source_dir = os.path.dirname(reports)
    handler = open(reports, 'r')
    lines = itertools.imap(lambda x: x.strip(), handler)
    lines = itertools.ifilter(lambda x: x and not x.startswith('#'), lines)
    triples = itertools.imap(lambda x: x.split('#'), lines)
    for report_id, filename, name in triples:
        report_id = int(report_id.strip())
        filename = os.path.join(source_dir, filename.strip())
        name = name.strip()
        logger.debug("Report's ID: %d", report_id)
        logger.debug('Filename: %s', filename)
        logger.debug('Report\'s name: %s', name)
        logger.info('Installing report "%s"(%d) with name "%s"',
                    filename[len(source_dir) + 1:], report_id, name)

        if not os.path.exists(filename):
            raise Exception('Report "%s" does not exists' % filename)

        destination_filename = os.path.join(destination_dir,
                                            'rpt_%d.rptdesign' % report_id)
        if not database.id_exists(report_id):
            if database.name_exists(name):
                logger.info('Report "%s" already exists, moving' % name)
                action = database.move
            else:
                action = database.insert
        else:
            if os.path.exists(destination_filename):
                logger.info('Destination file "%s" already exists, updating' %
                            destination_filename)
            action = database.update

        shutil.copy(filename, destination_filename)
        action(report_id, name)
        logger.info('Report "%s" was copied to "%s"',
                    filename[len(source_dir) + 1:], destination_filename)

    logger.console('Installing Birt reports completed')


if __name__ == '__main__':
    config = get_config(parse_args().config)
    logger = get_logger(config.log.file, config.log.level)
    logger.before(__file__, sys.argv[1:])
    try:
        if not check_verison(config.main.reports_dir):
            logger.console('Birt reports latest version already installed, '
                           'skipping')
            logger.exit(0)
        backup(config.main.reports_dir, config.main.archives_dir)
        clear_version(config.main.reports_dir)
        database = DB(**config.db.__dict__)
        install_all(config.main.reports_list, config.main.reports_dir,
                    database)
        store_version(config.main.reports_dir)
    except Exception, error:
        logger.exception(error)
        logger.console('See log "%s" for more details' % config.log.file)
        logger.console('Completed unsuccessfully')
        logger.exit(1)
    logger.console('Completed successfully')
    logger.exit(0)
