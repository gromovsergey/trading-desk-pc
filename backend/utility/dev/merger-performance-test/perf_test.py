#! /usr/bin/env python2
import atexit
import glob
import os
import psycopg2
import re
import shutil
import sys
import tempfile
import time
import argparse

from datetime import datetime
from itertools import ifilter, imap
from math import log10


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "perf_db_name", type=str,
        help="target database")
    parser.add_argument(
        "result_db_connection", type=str,
        help="connection string to database to store test results")
    parser.add_argument(
        "incoming_dir", type=str,
        help="merger incoming directory")
    parser.add_argument(
        "failure_dir", type=str,
        help="merger failure direcotry")
    parser.add_argument(
        "log_file", type=str,
        help="merger log filename")
    parser.add_argument(
        "row_count", type=int,
        help="count of rows")
    parser.add_argument(
        "num_of_files", type=int,
        help="number of files")
    parser.add_argument(
        "timeout", type=int,
        help="timeout in seconds")
    return parser.parse_args()


def timestamp():
    datetime_format = "%Y%m%d%H%M%S"
    return time.strftime(datetime_format, time.localtime())


def count_files(directory):
    assert os.path.isdir(directory), "'%s' isn't a directory" % directory
    filenames = \
        imap(lambda f: os.path.join(directory, f), os.listdir(directory))
    return len([x for x in filenames if os.path.isfile(x)])


def move_files(src, dst):
    """:param src: source directory
       :param dst: destination directory
    """
    assert os.path.isdir(src), "'%s' isn't a directory" % src
    assert os.path.isdir(dst), "'%s' isn't a directory" % dst

    print "Moving file from %s to %s" % (src, dst)
    for f in os.listdir(src):
        shutil.move(os.path.join(src, f), os.path.join(dst, f))


def wait_for_files(incoming_dir, timeout):
    pause = 10  # seconds
    files_processed = False
    for _ in xrange(timeout / pause):
        time.sleep(pause)
        if not count_files(incoming_dir):
            files_processed = True
            break
    return files_processed


def validate_generated_csv_files(temp_dir, table, has_header):
    failed = False
    for filename in glob.glob("%s/%s*.csv" % (temp_dir, table)):
        with open(filename) as h:
            count = len([x for x in h.read().splitlines() if x.strip()])
            if has_header and count <= 1:
                print "File %s contains not enough rows: %d" % \
                    (filename, count)
                failed = True
            elif not has_header and count == 0:
                print "File %s is empty" % filename
                failed = True
    if failed:
        raise RuntimeError("Invalid CSV files")


def generate_csv_files(conn, temp_dir, row_count, num_of_files):
    cursor = conn.cursor()
    cursor.callproc(
        "test_performance.populate_temp_tables", (row_count, ))

    filler = conn.cursor()
    cursor.callproc("test_performance.get_perf_tables")
    for table, has_header in [(x[0], x[1] if len(x) > 1 else True) for x in cursor]:
        print "Creating CSV files for %s..." % table

        start = time.time()
        ts_tmpl = "%%s-%%0%dd" % (int(log10(num_of_files)) + 1)
        for f in imap(lambda i: ts_tmpl % (timestamp(), i), xrange(num_of_files)):
            filler.callproc(
                "test_performance.fill_csv", (table, f, temp_dir, row_count))
        validate_generated_csv_files(temp_dir, table, has_header)

        print "CSV files for %s have been created in %.3f s." % \
            (table, time.time() - start)


def process_log_file(conn, log_file, started):
    """Extracts entries from :param log_file: and puts them into the database via :param conn:.
       Doesn't process entries with timestamp earlier than :param started:.
    """
    cursor = conn.cursor()
    cursor.callproc(
        "create_session",
        ("Load Stats to Postgres DB", "Load Stats to Postgres DB"))
    session_id = cursor.fetchone()[0]
    print "Session_ID: %d" % int(session_id)

    # The RE was taken from merger
    filename_re = re.compile(
        r"(?P<source>[^-]+)(-(?P<version>[^_]+))?_(?P<id>[-\d]+).csv")
    time_re = re.compile(r"^\d+")
    for num, line in enumerate(open(log_file, "r")):
        pos = line.find("PERF:")
        if pos == -1:
            continue

        pos += 6  # len("PERF: ")
        m = filename_re.match(line, pos)
        if not m:
            print "WARNING: line %d possibly has wrong format." % num
            continue

        ts = m.group("id").split("-")[0]
        if ts < started:
            continue

        # Parsing parameters formatted as following: param1=value1, param2=value2, ..., paramN=valueN
        begin = line.find("(", m.end(0) + 1) + 1
        end = line.find(")", begin + 1)
        params = dict(imap(lambda t: (t[0].strip(), t[1].strip()),
                           imap(lambda s: s.split("="),
                                line[begin:end].split(","))))
        params["time"] = int(time_re.match(params["time"]).group(0))

        print "INFO:", line[m.start(0):end]

        descr = "Number of rows - %s; Bytes - %s" % (params["rows"], params["bytes"])
        # Second argument should be a table name
        cursor.callproc(
            "log_test",
            (params["result"], m.group("source"),
             "Load Stats to Postgres DB", params["time"], session_id, descr))


def main():
    args = parse_args()

    print "Starting PGDB performance testing at %(perf_db_name)s\n\n" \
        "Parameters:\n" \
        "  perf db name:        %(perf_db_name)s\n" \
        "  result db string:    %(result_db_connection)s\n" \
        "  merger incoming dir: %(incoming_dir)s\n" \
        "  merger failure dir:  %(failure_dir)s\n" \
        "  merger log file:     %(log_file)s\n" \
        "  row count:           %(row_count)d\n" \
        "  number of files:     %(num_of_files)d\n" \
        "  timeout:             %(timeout)d\n\n" % dict(args._get_kwargs())

    started = timestamp()
    with psycopg2.connect(database=args.perf_db_name) as conn:
        temp_dir = tempfile.mkdtemp(prefix="perf_test-")
        atexit.register(shutil.rmtree, temp_dir)
        generate_csv_files(conn, temp_dir, args.row_count, args.num_of_files)

    # Copy files and wait until all files have been processed
    if count_files(args.incoming_dir):
        print "WARNING: %s isn't empty." % args.incoming_dir

    move_files(temp_dir, args.incoming_dir)
    if not wait_for_files(args.incoming_dir, args.timeout):
        print ("ERROR: files haven't been processed in %d seconds, "
               "something seems to be wrong.") % args.timeout
        sys.exit(2)

    if count_files(args.failure_dir):
        print "ERROR: failure dir is not empty:"
        for filename in os.listdir(args.failure_dir):
            print "\t", filename
        sys.exit(3)

    if args.result_db_connection != "--do-not-save-results":
        with psycopg2.connect(args.result_db_connection) as conn:
            process_log_file(conn, args.log_file, started)

if __name__ == "__main__":
    main()
