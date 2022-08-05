#!/usr/bin/env python
import os
import json
import logging
import optparse
import xml.etree.ElementTree as ET
from htmlentitydefs import name2codepoint
import re

logging.basicConfig(
    level="INFO", filename="%s.log" % os.path.basename(__file__),
    format="%(asctime)s: %(levelname)s: %(name)s: %(message)s")
logger = logging.getLogger(__name__)


def parse_args():
    parser = optparse.OptionParser()
    return parser.parse_args()


def fix_entity_ref(content):
    content = content.replace("&amp;160;", "&nbsp;")
    content = content.replace("&amp;8212;", "&mdash;")
    content = content.replace("&amp;nbsp;", "&nbsp;")
    content = content.replace("&amp;mdash;", "&mdash;")
    for key, code in name2codepoint.items():
        if key in ('lt', 'gt', 'amp', 'quot'):
            continue
        content = content.replace("&%s;" % key, r"\u{0:04x}".format(code))
    return content


def get_content(treefilename):
    with open(treefilename) as h:
        data = json.loads(h.read())
    for child in data.get("children", []):
        tablename = child["tablename"]
        content = child.get("content", "").encode('utf-8')
        sch = re.search(r"<h\d+.*?>", content, re.S | re.M | re.U)
        if sch:
            content = content[:sch.start()]
        content = content.replace("\\", "\\\\")
        content = fix_entity_ref(content)
        yield (tablename, '<?xml version="1.0" encoding="utf-8"?><data>' +
               content + '</data>')


def save_output(filename, content):
    header = "do $do$ begin\n"
    footer = "\nend;$do$;"
    case = "exception when others then " + \
           "case current_setting('foros.comments_errors') " + \
           "when 'raise' then raise; " + \
           "when 'ignore' then end case"
    text = reduce(
        lambda x, y: x + "\n" + "begin execute $COMMENT$%s$COMMENT$; %s; end;" % (y, case),
        content, "")
    with open(filename, "w") as h:
        h.write(header + text + footer)


def get_quoted(comment):
    comment = comment.replace("'", "''").replace("\n", r"\n")
    return "E'%s'" % comment


def main(positional, options):
    treefilename, outfilename = positional
    output = []
    for tablename, content in get_content(treefilename):
        root = ET.fromstring(content)
        description = ""
        if root[0].tag == "p":
            description = root[0].text or description
        if description:
            output.append(
                "comment on table %s is %s" %
                (tablename, get_quoted(description)))
        tables = root.findall('table')
        for i, row in enumerate(tables[0].find('tbody').findall('tr')):
            if i == 0:
                continue
            values = []
            for td in row.findall('td'):
                value = reduce(lambda x, y: x + y,
                               map(lambda x: x.text or "", td.getiterator()),
                               td.text or "")
                values.append(value)
            column, dtype, contr, desc = values
            desc = desc.strip()
            if not desc:
                continue
            output.append(
                "comment on column %s.%s is %s" %
                (tablename, column, get_quoted(desc)))
    save_output(outfilename, output)


if __name__ == '__main__':
    options, positional = parse_args()
    main(positional, options)
