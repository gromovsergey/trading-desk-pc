#!/usr/bin/env python
import os
import sys
import re
import glob
import zipfile
import shutil
import fnmatch
import psycopg2
import optparse
from datetime import datetime
from subprocess import Popen, STDOUT, PIPE
from commons import *
from commands import getoutput

EXTENSIONS = ('.sh', '.py')
CONFIG_FILENAME = '/opt/foros/ui/etc/conf/migration'


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option('-c', '--config', metavar='filename',
                      help='Config filename, default: %s' %
                      CONFIG_FILENAME, default=CONFIG_FILENAME)
    parser.add_option('-t', '--test', action='store_true',
                      help='Run self tests and exit')
    parser.add_option('-s', '--simulate', action='store_true',
                      help='Simulate without DB access and running patches')
    parser.add_option('-a', '--autorestart', action='store_true',
                      help='Enable autorestart mode')
    return parser.parse_args()[0]


def get_foros_ui_version():
    return getoutput("rpm -q --qf %{Version} foros-ui").strip()


def backup(config):
    logger.info('Start to backup working directory')
    version = get_foros_ui_version()

    existing = glob.glob(
        "%s/forosui-%s_*.tar.gz" % (config.main.backups_folder, version))
    if existing:
        logger.console("Backup already exists: %s", existing)
        return

    timestamp = datetime.now().strftime('%Y.%m.%d.%H-%M')
    filename = os.path.join(
        config.main.backups_folder, 'forosui-%s_%s.tar.gz' % (version, timestamp))
    tempfilename = os.path.join(
        config.main.backups_folder, '.temp-forosui-%s_%s.tar.gz' % (version, timestamp))

    logger.info("Creating backup: %s", tempfilename)
    folder = config.main.backup_target_folder
    if make_archive(tempfilename, folder, '--ignore-failed-read'):
        logger.info("Moving %s to %s", tempfilename, filename)
        os.rename(tempfilename, filename)
        logger.info('Backup completed successfully')
    else:
        logger.error('Can\'t generate backup archive %s' % filename)
        logger.exit(1)


def unpack_ear(config):
    tmp_dir = config.main.tmp_folder
    lib_dir = os.path.join(config.main.tmp_folder, 'lib')
    if os.path.isdir(lib_dir):
        logger.info('Removing folder %s', lib_dir)
        shutil.rmtree(lib_dir)
    logger.info('Unpacking foros-ui.ear')
    archive = zipfile.ZipFile(config.main.ear_path)
    files = fnmatch.filter(archive.namelist(), 'lib/*.jar')
    archive.extractall(tmp_dir, files)
    archive.extractall(lib_dir, ['foros-ui-ejb.jar'])
    archive.close()


class PgConnection:
    def __init__(self, parameters):
        logger.info("Pg DB: host=%(host)s port=%(port)s "
                    "database=%(database)s user=%(user)s" % parameters)
        credentials = {}
        credentials.update(parameters)
        del credentials['table']
        self.connection = None
        self.connection = psycopg2.connect(**credentials)

    def __del__(self):
        if self.connection:
            self.connection.close()


class PatchFinder:
    DIR_REGEX = '^([1-9][\.0-9]*)$'
    FILE_REGEX = '^([1-9][\.0-9]*)_.*$'

    def __init__(self, xdir):
        self.xdir = xdir

    def find_all_patches(self):
        "Find all patches on disk"
        patches = []

        for root, dirs, files in os.walk(self.xdir):
            for xdir in dirs:
                if not self._need_process_dir(xdir):
                    dirs.remove(xdir)

            for xfile in files:
                if self._need_process_file(xfile):
                    patch = os.path.join(root, xfile)
                    patches.append(patch)

        def recursive_compare(seq1, seq2, el_cmp):
            "Compare sequences recursively using el_cmp for comparing elements"

            if len(seq1) == 0 and len(seq2) == 0:
                return 0
            if len(seq1) == 0:
                return -1
            if len(seq2) == 0:
                return 1

            compare_first = el_cmp(seq1[0], seq2[0])
            if compare_first != 0:
                return compare_first

            return recursive_compare(seq1[1:], seq2[1:], el_cmp)

        def num_cmp(d1, d2):
            "Compare strings as numbers if possible, or as strings if not"
            try:
                n1 = int(d1)
                n2 = int(d2)

                return cmp(n1, n2)
            except ValueError:
                return cmp(d1, d2)

        def numseq_cmp(d1, d2):
            "Compare strings which contains numbers separated by dots"

            return recursive_compare(d1.split('.'), d2.split('.'), num_cmp)

        def extract_num(xlist):
            "Extract number and dots from file or directory name"
            xlistlen = len(xlist)
            if xlistlen == 1:
                return re.match(PatchFinder.FILE_REGEX, xlist[0]).groups()[0]
            else:
                return xlist[0]

        def split_cmp(s1, s2):
            "Compare splitted file or directory names"
            if not s1 and not s2:
                return 0
            if not s1:
                return -1
            if not s2:
                return 1

            if s1[0] != s2[0]:
                x1snum = extract_num(s1)
                x2snum = extract_num(s2)

                return numseq_cmp(x1snum, x2snum)
            else:
                return split_cmp(s1[1:], s2[1:])

        def patch_cmp(x1, x2):
            "Compare full patch names"
            x1s = x1.split(os.path.sep)
            x2s = x2.split(os.path.sep)

            return split_cmp(x1s, x2s)

        patches.sort(cmp=patch_cmp)

        return patches

    def _need_process_dir(self, xstr):
        """Match directories which needs to be processed.

        The directory is processed
        if the name consists only of digits divided by dot.

        >>> p = PatchFinder('')
        >>> p._need_process_dir('1')
        True
        >>> p._need_process_dir('1a')
        False
        >>> p._need_process_dir('01.1.1')
        False
        >>> p._need_process_dir('1.31.11')
        True
        >>> p._need_process_dir('1.31.11a')
        False
        >>>
        """
        return re.match(PatchFinder.DIR_REGEX, xstr) is not None

    def _need_process_file(self, xstr):
        """Match files which needs to be processed.

        The file is processed if the name matches the following template:
        <Number>([.Number])*_JIRA_ISSUE.<EXT>.

        >>> p = PatchFinder('')
        >>> p._need_process_file('abc')
        False
        >>> p._need_process_file('1.2.3')
        False
        >>> p._need_process_file('1.2.3_')
        False
        >>> p._need_process_file('1.02.3_OUI-1234.py')
        True
        >>> p._need_process_file('1_OUI-1234.sh')
        True
        >>>
        """

        for ext in EXTENSIONS:
            if xstr.endswith(ext):
                return re.match(PatchFinder.FILE_REGEX, xstr) is not None

        return False


class Patch:
    OK = "ok"
    FAIL = "fail"
    PROGRESS = "progress"

    BACKUP_REQUIRED = re.compile(r'^#\s+BACKUP_WWW_FOLDER\s*$', re.M | re.S)
    BACKUP_NOT_REQUIRED = re.compile(r'^#\s+DO_NOT_BACKUP\s*$', re.M | re.S)

    def __init__(self, config, path, conn, performed_patches, is_simulation):
        self.config = config
        self.path = path
        self.pg = conn
        self.performed_patches = performed_patches
        self.is_simulation = is_simulation

        name = os.path.basename(self.path)
        self.issue = Patch.extract_issue(name)

        self.start_date = None
        self.status = None

        if self.issue in self.performed_patches:
            self.start_date, self.status = performed_patches[self.issue]

        if self.status != self.OK:
            self.backup_required = self.read_backup_tag()
        else:
            self.backup_required = False

    def read_backup_tag(self):
        required = None
        with open(self.path) as h:
            data = h.read()
        if self.BACKUP_REQUIRED.search(data):
            required = True
        if self.BACKUP_NOT_REQUIRED.search(data):
            if required is not None:
                logger.error("There are too many backup tags in the file: %s", self.path)
                logger.exit(1)
            required = False
        if required is None:
            logger.error("There is no one backup tag in the file: %s", self.path)
            logger.exit(1)
        return required

    @staticmethod
    def extract_issue(name):
        return re.match('[\.0-9]+_(.*)\.', name).groups()[0]

    def run(self):
        self.logger = get_logger(os.path.join(self.config.log.folder,
                                 self.issue + '.log'), self.config.log.level,
                                 name=self.issue)

        self.logger.before(self.path)
        self.logger.info('APPLYING PATCH %s' % self.issue)

        error_output = []
        if not self.is_simulation:
            process = Popen([self.path], stderr=STDOUT,
                            stdout=PIPE, shell=True)
            while True:
                line = process.stdout.readline().rstrip()
                if len(line) == 0:
                    break
                self.logger.info(line)
                error_output.append(line)
            return_status = process.wait()
        else:
            return_status = 0

        self.logger.after(return_status)

        if return_status == 0:
            self.status = Patch.OK
        else:
            self.status = Patch.FAIL
            logger.multiline.console('\n'.join(error_output))

        del self.logger

    def mark_started(self):
        if self.is_simulation:
            return

        cursor = self.pg.connection.cursor()

        if self.status is None:
            query = 'insert into ' + self.config.pg.table + \
                ' (patch_name, status) values (%s, %s)'
            cursor.execute(query, (self.issue, Patch.PROGRESS))
        else:
            query = 'update ' + self.config.pg.table + \
                ' set status = %s where patch_name=%s'
            cursor.execute(query, (Patch.PROGRESS, self.issue))

        query = 'update ' + self.config.pg.table + \
            ' set start_date = current_timestamp where patch_name=%s'
        cursor.execute(query, (self.issue,))

        self.pg.connection.commit()
        cursor.close()

    def mark_complete(self):
        if self.is_simulation:
            return

        cursor = self.pg.connection.cursor()
        query = 'update ' + self.config.pg.table + \
            ' set status = %s,' + \
            ' end_date = current_timestamp where patch_name=%s'

        cursor.execute(query, (self.status, self.issue))

        self.pg.connection.commit()
        cursor.close()


class Migration:
    def __init__(self, config, is_simulation):
        self.is_simulation = is_simulation
        self.config = config
        self.is_migration_actual = False
        self.is_backuped = False

        if not is_simulation:
            self.pg = PgConnection(config.pg.__dict__)
        else:
            logger.info('Simulation performed')
            self.pg = None

    def read_performed_patches(self):
        if self.is_simulation:
            return {}

        patches = {}

        cursor = self.pg.connection.cursor()
        query = 'SELECT patch_name, start_date, status FROM ' + \
                self.config.pg.table
        cursor.execute(query)
        for name, start_date, status in cursor:
            patches[name] = (start_date, status)
        cursor.close()

        return patches

    def prestart(self):
        unpack_ear(self.config)

    def poststop(self):
        pass

    def main(self):
        try:
            self.run()
        finally:
            if self.is_migration_actual:
                self.poststop()

    def run(self):
        performed_patches = self.read_performed_patches()

        all_patches = PatchFinder(self.config.main.migrations_folder).\
            find_all_patches()

        for path in all_patches:
            patch = Patch(self.config, path, self.pg,
                          performed_patches, self.is_simulation)

            if patch.status == Patch.PROGRESS:
                logger.error('Migration aborted as there are unfinished patches. ' +
                             'Name: %s, started: %s',
                             patch.issue, patch.start_date)
                logger.exit(1)
            if patch.status == Patch.FAIL:
                logger.warning('Patch "%s" was failed at %s. Applying it again',
                               patch.issue, patch.start_date)
            elif patch.status == Patch.OK:
                logger.console('Skipping patch %s', patch.issue)
                continue
            elif patch.status is not None:
                logger.error('Migration is aborted as there are patches ' +
                             'in unknown status. Name: %s', patch.issue)
                logger.exit(3)

            if not self.is_backuped and patch.backup_required:
                backup(self.config)
                self.is_backuped = True

            if not self.is_migration_actual:
                self.is_migration_actual = True
                self.prestart()

            logger.info('Marking patch %s as started', patch.issue)
            patch.mark_started()
            logger.info('done')

            logger.console('Starting patch %s', patch.issue)

            patch.run()

            logger.info('Marking patch %s as complete. Status: %s',
                        patch.issue, patch.status)
            patch.mark_complete()
            logger.info('done')

            logger.console('Patch %s completed with status %s',
                           patch.issue, patch.status)

            if patch.status != Patch.OK:
                logger.error('Patch failed. Abort.')
                logger.exit(1)


def test():
    import doctest
    doctest.testmod()
    os._exit(0)


def check_issue_uniqueness(folder):
    logger.info('Checking uniqueness')
    all_patches = PatchFinder(folder).find_all_patches()
    all_issues = {}
    for path in all_patches:
        name = os.path.basename(path)
        issue = Patch.extract_issue(name)
        if issue in all_issues:
            logger.error('Duplicate migrations found: ' +
                         '"%s" and "%s". Issue: "%s"',
                         all_issues[issue], path, issue)
            logger.exit(1)
        all_issues[issue] = path

if __name__ == '__main__':
    args = parse_args()
    if args.test:
        test()
    config = get_config(args.config)
    logger = get_logger(config.log.file, config.log.level, __file__)
    logger.before(__file__, sys.argv[1:])
    if args.autorestart:
        logger.console('Autorestart mode detected, Migration skipped')
        logger.exit(0, 'Skipped')
    check_issue_uniqueness(config.main.migrations_folder)
    try:
        runner = Migration(config, args.simulate)
        runner.main()
    except Exception, error:
        logger.exception(error)
        logger.exit(1)
    logger.exit(0, 'Successfully')
