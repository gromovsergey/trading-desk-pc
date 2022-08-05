#!/usr/bin/env python
import glassfish_log_filter
import re


class Inserter(object):
    def __init__(self, config, replace=True):
        self.expressions = glassfish_log_filter.RegularExpressionsContainer
        self.rules = config
        self.format = '%s %s: %s'
        self.replace = replace

    def _get_message(self, record):
        message = ''
        for part in record.stack:
            message += part['exception']
            message += part['comment']
            foros_finded = False
            for i in xrange(len(part['at'])):
                if part['at'][i].startswith('\tat com.foros.'):
                    foros_finded = True
                    message += ''.join(part['at'][:i + 1])
                    message += '...\n'
                    break
            if not foros_finded:
                message += ''.join(part['at'])
        if self.replace:
            message = message.replace('\n', ' <- \\\\ ')
        record.__dict__['message'] = message
        result = ("%(timestamp)s|%(severity)s|%(version)s|%(classname)s|" +
                  "_ThreadID=%(thread_id)s;_ThreadName=%(thread_name)s;|" +
                  "%(message)s\n") % record.__dict__
        return result.decode('utf-8')

    def _get_pattern(self, expression):
        pattern = None
        if expression not in self.expressions:
            try:
                pattern = re.compile(expression, re.S | re.U)
                self.expressions[expression] = pattern
            except re.error, v:
                raise Exception("%s: %s" % (v, expression))
        else:
            pattern = self.expressions[expression]
        return pattern

    def __call__(self, record):
        for part in reversed(record.stack):
            for code, regex, msg, severity in self.rules['rules']:
                pattern = self._get_pattern(regex)
                if pattern.search(part['exception'] + part['comment']):
                    message = self.format % (
                        code, msg, self._get_message(record))
                    return (message.encode('utf-8', 'strict'), severity)
        code, msg, severity = self.rules['unsorted'][record.severity]
        message = self.format % (code, msg, self._get_message(record))
        return (message.encode('utf-8', 'strict'), severity)


if __name__ == '__main__':
    import sys

    from ui_log_parser import load_rules

    SEVERITY_LEVELS = ['WARNING', 'SEVERE']

    def parse_args():

        description = """
examples:
    - parse server.log with rules
      glassfish_log_formatter.py -f server.log /opt/foros/ui/lib/ui_log_parser/rules.json
        """

        try:
            import argparse
            parser = argparse.ArgumentParser(
                epilog=description,
                formatter_class=argparse.RawDescriptionHelpFormatter)
            add = parser.add_argument
            add('config', metavar='config',
                help='config filename')
        except ImportError:
            import optparse

            class RawEpilogFormatter(optparse.IndentedHelpFormatter):
                def format_epilog(self, text):
                    return "\n" + text + "\n"

            usage = "usage: %prog [options] config"
            parser = optparse.OptionParser(
                usage=usage, epilog=description,
                formatter=RawEpilogFormatter())
            add = parser.add_option

        add('-o', '--output', metavar='output',
            help='output filename, default=stdout')
        add('-f', '--filename', metavar='filename',
            help='log filename, default=stdin')
        add('-r', '--replace', action='store_true',
            help='replace "\\n" by " <- \\\\ "')

        result = parser.parse_args()
        if result.__class__.__name__ == 'tuple':
            if len(result[1]) != 1:
                parser.error('config is required')
            setattr(result[0], 'config', result[1][0])
            return result[0]
        else:
            return result

    def output_to_file(filename, log):
        handler = open(filename, 'a')
        for message in log:
            handler.write(message[1] + '#' + message[0] + '\n')
        handler.close()

    def output_to_stdout(log):
        import sys
        stream = sys.stdout
        for message in log:
            stream.write(message[1] + '#' + message[0] + '\n')

    args = parse_args()
    config = load_rules(args.config)
    if args.filename:
        stream = open(args.filename, 'r')
    else:
        stream = sys.stdin
    parser = glassfish_log_filter.LogParser()
    log = parser.parse(stream)
    log = log.severity(*SEVERITY_LEVELS)
    log = log.contains(*config['ignore'], **({'negation': True}))
    log = log.map(Inserter(config, args.replace))
    if args.output:
        output_to_file(args.output, log)
    else:
        output_to_stdout(log)
