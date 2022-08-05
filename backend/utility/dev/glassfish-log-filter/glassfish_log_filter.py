#!/usr/bin/env python
# coding=utf-8
import logging
import re
import itertools
from cStringIO import StringIO
from commons import unthrowable

logger = logging.getLogger()


class StackTraceParser(object):
    def __init__(self):
        self.is_at = re.compile(r'\A\W+(at |\.\.\.)', re.U | re.S)
        self.is_exception = re.compile(r'Exception', re.U | re.S)

    def _parse_out(self, line, record, result):
        in_record = False
        if self.is_at.search(line):
            record['at'].append(line)
            in_record = True
        elif self.is_exception.search(line):
            record['exception'] = line
        else:
            record['comment'] = record.get('comment', '') + line
        return [record, in_record]

    def _parse_in(self, line, record, result):
        in_record = True
        if self.is_at.search(line):
            record['at'].append(line)
        else:
            result.append(record)
            record = {'exception': '',
                      'comment': '',
                      'at': []}
            in_record = False
            [record, in_record] = self._parse_out(line, record, result)
        return [record, in_record]

    def parse(self, data):
        result = []
        record = {'exception': '',
                  'comment': '',
                  'at': []}
        in_record = False
        for line in StringIO(data):
            if not in_record:
                record, in_record = self._parse_out(line, record, result)
            else:
                record, in_record = self._parse_in(line, record, result)
        if record:
            result.append(record)
        return result


RegularExpressionsContainer = {}


class Log(object):
    def __init__(self, records, root=None):
        if root:
            self._root = root
        else:
            self._root = self
            self._expressions = RegularExpressionsContainer
        self._records = records
        self._filter = {False: itertools.ifilter,
                        True: itertools.ifilterfalse}

    def _regexp(self, expression, text):
        pattern = None
        if expression in self._root._expressions:
            pattern = self._root._expressions[expression]
        else:
            try:
                pattern = re.compile(expression, re.U | re.S | re.M)
                self._root._expressions[expression] = pattern
            except re.error, v:
                raise Exception("%s: %s" % (v, expression))
        return not pattern.search(text) is None

    def __iter__(self):
        for record in self._records:
            if record:
                yield record

    def severity(self, *severity, **kwargs):
        """
        Filter records in log by severity.
        Tool examples: -r severity=WARNING,SEVERE
                       -r not:severity=INFO
        """
        negation = kwargs.get('negation', False)
        return Log(self._filter[negation](
            unthrowable(logger)(lambda x: x.severity in severity),
            self._records), self._root)

    def contains(self, *expressions, **kwargs):
        """
        Filter records in log by messages. Search expressions in message.
        Tool example: -r contains='ORA-\d+'
                      -r not:contains='Invalid path was requested','NAKACK'
        """
        @unthrowable(logger)
        def filt(record):
            for expression in expressions:
                if self._regexp(expression, record.message):
                    return True
            return False
        negation = kwargs.get('negation', False)
        return Log(self._filter[negation](filt, self._records), self._root)

    def classname(self, *expressions, **kwargs):
        """
        Filter records in log by classname. Search expressions in classname.
        """
        @unthrowable(logger)
        def filt(record):
            for expression in expressions:
                if self._regexp(expression, record.classname):
                    return True
            return False
        negation = kwargs.get('negation', False)
        return Log(self._filter[negation](filt, self._records), self._root)

    def filter(self, predicate):
        return Log(itertools.ifilter(unthrowable(logger)(predicate),
                   self._records), self._root)

    def map(self, function):
        return itertools.imap(unthrowable(logger)(function), self._records)

    def map_if_contains(self, function, *expressions):
        @unthrowable(logger)
        def mapper(record):
            for expression in expressions:
                if self._regexp(expression, record.message):
                    return function(record)
            return record
        return Log(itertools.imap(mapper, self._records), self._root)

    def execute(self, function):
        for record in sefl._records:
            unthrowable(logger)(function)(record)

    def unique(self, *keys, **kwargs):
        """
        Return unique records by key, where key in concatenation of keys fields
        Tool example: -r unique=classname,severity
            returns all records, which have unique pair classname and severity
        WARNING:
            Slow method, it should be last method in rules sequence
        """
        result = {}

        def collector(record):
            key = ''
            for name in keys:
                key += getattr(record, name).__str__()
            if not key in result:
                result[key] = record

        map(collector, self)
        return Log(result.values())


class LogRecord(object):
    def __init__(self, entries):
        self.__dict__.update(entries)

    def __repr__(self):
        return self.__dict__.__repr__()

    def __str__(self):
        return ("%(severity)s|%(classname)s:\n" + "=" * 40 +
                "\n%(message)s\n" + "=" * 40 + "\n\n") % self.__dict__


class LogParser(object):
    def __init__(self):
        pattern_str = r"^\|" +\
            r"(?P<timestamp>\d{4}\-\d{2}\-\d{2}T" + \
            r"\d{2}:\d{2}:\d{2}\.\d{3}\+\d{4})" +\
            r"\|(?P<severity>[A-Z]+)" +\
            r"\|(?P<version>.*?)" +\
            r"\|(?P<classname>.*?)\|" +\
            r"_ThreadID=(?P<thread_id>\d+);" + \
            r"_ThreadName=(?P<thread_name>.*?);" +\
            r"\|(?P<message>.*?)\|$"
        self.entities = re.compile(pattern_str, re.U | re.M | re.S)
        self.record = re.compile(r'^\[#(.*?)#\]$', re.S | re.U | re.M)
        self.stk_parser = StackTraceParser()

    def get_records(self, stream, max_message_length=1024):
        """
        >>> from StringIO import StringIO
        >>> parser = LogParser()
        >>> stream = StringIO("[#|2012-08-13T11:13:29.486+0000|\
INFO|glassfish3.0.1|com.foros.session.LoggingInterceptor|\
_ThreadID=76;_ThreadName=Thread-1;|blah)|#]")
        >>> filter(None, parser.get_records(stream))
        [{'thread_name': 'Thread-1', \
'severity': 'INFO', 'timestamp': '2012-08-13T11:13:29.486+0000', \
'classname': 'com.foros.session.LoggingInterceptor', 'thread_id': '76', \
'version': 'glassfish3.0.1', 'message': 'blah)', \
'stack': [{'comment': 'blah)', 'exception': '', 'at': []}]}]
        """
        data = stream.read()
        for record in self.record.findall(data):
            message = record[:max_message_length].rstrip('|') + '|'
            match = self.entities.match(message)
            if not match:
                raise Exception(message)
            result = match.groupdict()
            result.update({'stack': self.stk_parser.parse(result['message'])})
            yield result

    def parse(self, stream, max_message_length=1024):
        return Log(tuple([LogRecord(x) for x in self.get_records(
                          stream, max_message_length)]))


if __name__ == '__main__':
    def parse_args():

        description = """
examples:
    - search for messages contains expression
      glassfish_log_filter.py -f server.log -r contains='.*?Broken pipe'

    - search for messages not contains expression
      glassfish_log_filter.py -f server.log -r not:contains='.*?Broken pipe'

    - search for all warnings and errors
      glassfish_log_filter.py -f server.log -r severity=WARNING,SEVERE

    - search for all warnings from org.springframework.web.context.ContextLoader
      glassfish_log_filter.py -f server.log -r severity=WARNING -r classname=org.springframework.web.context.ContextLoader
        """

        try:
            import argparse
            parser = argparse.ArgumentParser(
                epilog=description,
                formatter_class=argparse.RawDescriptionHelpFormatter)
            add = parser.add_argument
        except ImportError:
            import optparse

            class RawEpilogFormatter(optparse.IndentedHelpFormatter):
                def format_epilog(self, text):
                    return "\n" + text + "\n"

            parser = optparse.OptionParser(
                epilog=description,
                formatter=RawEpilogFormatter())
            add = parser.add_option
        add('-t', '--test', action='store_true',
            help='run self tests, before processing file')
        add('-l', '--loglevel', metavar='loglevel', type=str,
            default='INFO', help='logging verbosity level',
            nargs='?')
        add('-f', '--filename', metavar='filename', type=str,
            help='filename of glassfish\'s log or use stdin')
        add('-r', '--rules', metavar='rules', type=str, action='append',
            help='filtering rules: [contains, classname, severity, unique]')
        add('-o', '--output', metavar='output', type=str,
            help='output format')
        result = parser.parse_args()
        #return result[0] if result.__class__.__name__ == 'tuple' else result
        if result.__class__.__name__ == 'tuple':
            return result[0]
        else:
            return result

    def run_tests():
        import doctest
        doctest.testmod()

    def print_native(record):
        print ("[#|%(timestamp)s|%(severity)s|%(version)s|%(classname)s|" +
               "_ThreadID=%(thread_id)s;_ThreadName=%(thread_name)s;|" +
               "%(message)s|#]\n") % record.__dict__

    def print_format(format, record):
        print (format % record.__dict__)

    args = parse_args()
    if args.test:
        run_tests()
    if args.filename:
        stream = open(args.filename, 'r')
    else:
        import sys
        stream = sys.stdin
    parser = LogParser()
    log = parser.parse(stream)
    if args.rules:
        for rule in args.rules:
            name, params = rule.split('=')
            negation = False
            pair = name.split(':')
            if len(pair) == 2:
                name = pair[1]
                if pair[0] == 'not':
                    negation = True
                else:
                    raise Exception('Unknown key "%s"' % pair[0])
            kwargs = {'negation': negation}
            log = getattr(log, name)(*params.split(','), **kwargs)
    for record in log:
        if args.output:
            print_format(args.output, record)
        else:
            print_native(record)
