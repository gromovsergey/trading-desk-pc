#!/usr/bin/env python
import itertools
import json
import logging
import optparse
import os
import psycopg2
import re
import copy
import xml.etree.ElementTree as ET
from htmlentitydefs import codepoint2name

logging.basicConfig(
    level="INFO", filename="%s.log" % os.path.basename(__file__),
    format="%(asctime)s: %(levelname)s: %(name)s: %(message)s")
logger = logging.getLogger(__name__)

DEFAULT = {
    "description": "",
    "constraints": [],
    "columns": [],
    "aggregates": []
}


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option("--dbname", default="unittest_ui_12")
    parser.add_option("--user", default="foros")
    parser.add_option("--password", default="adserver")
    parser.add_option("--host", default="stat-dev0.ocslab.com")
    parser.add_option("--port", default="5432")
    parser.add_option("--tables-sql")
    parser.add_option("--columns-sql")
    parser.add_option("--url", default="https://confluence.ocslab.com")
    parser.add_option("--space", default="TDOC")
    return parser.parse_args()


def load_pages(filename):
    with open(filename) as h:
        return json.loads(h.read())


def save_pages(filename, pages):
    with open(filename, 'w') as h:
        h.write(json.dumps(pages, indent=2))


def load_sql(filename):
    with open(filename) as h:
        sql = h.read()
    return sql.replace("\pset", "-- \pset")


def get_connection(options):
    conn = "dbname=%(dbname)s user=%(user)s password=%(password)s " \
        " host=%(host)s port=%(port)s"
    conn = conn % options.__dict__
    return psycopg2.connect(conn)


def fix_entity_ref(content):
    if not content:
        return content
    content = content.replace("&amp;160;", "&nbsp;")
    content = content.replace("&amp;8212;", "&mdash;")
    content = content.decode('utf-8', 'ignore')
    return content


def restore_entity_ref(content):
    if not content:
        return content
    for code, key in sorted(codepoint2name.items(), key=lambda x: -x[0]):
        if key in ('lt', 'gt', 'amp', 'quot'):
            continue
        content = content.replace(unichr(code), u"&{0};".format(key))
    return content


def get_tag(tag, content):
    if not content:
        return ""
    pattern = r'\[\s*%s\s*:(.*?)\]' % tag
    sch = re.search(pattern, content, re.S | re.U | re.M)
    if sch:
        return sch.group(1)
    else:
        return ""


def get_table_description(connection, filename):
    rows = []
    with connection.cursor() as cursor:
        cursor.execute(load_sql(filename))
        for row in cursor:
            rows.append(row)
    rows.sort(key=lambda x: x[0])
    result = {}
    for key, group in itertools.groupby(rows, key=lambda x: x[0]):
        result[key] = copy.deepcopy(DEFAULT)
        for _, t, v in group:
            if t.lower().startswith("description"):
                result[key]["description"] = fix_entity_ref(v)
            else:
                result[key]["constraints"].append((t, v))
    for key, value in result.items():
        basetables = get_tag("Base tables", value["description"])
        if basetables:
            basetables = basetables.split(",")
            for table in basetables:
                table = table.strip()
                if table not in result:
                    result[table] = copy.deepcopy(DEFAULT)
                result[table]["aggregates"].append(key)
    return result


def get_column_description(connection, filename, tdesc):
    rows = []
    with connection.cursor() as cursor:
        cursor.execute(load_sql(filename))
        for row in cursor:
            strong, table, column, ctype, cont, check, description = row
            strong = bool(strong)
            constraint = ""
            if cont:
                constraint += cont
                if check:
                    constraint += "," + check
            else:
                if check:
                    constraint += check
            description = description or ""
            description = fix_entity_ref(description)
            if table not in tdesc:
                tdesc[table] = copy.deepcopy(DEFAULT)
            tdesc[table]["columns"].append(
                (strong, column, ctype, constraint, description))
    return tdesc


def get_immutable_content(tablename, pages):
    for child in pages.get("children", []):
        if child["tablename"] == tablename:
            content = child["content"]
            sch = re.search(r"<h\d+.*?>", content, re.S | re.M | re.U)
            if sch:
                return content[sch.start():]
            else:
                return ""
    return ""


def get_content(context):
    def get_cell(value, tag='td', strong=False):
        td = ET.Element(tag)
        p = ET.Element('p')
        if strong:
            strong = ET.Element('strong')
            strong.text = value
            p.append(strong)
        else:
            p.text = value
        td.append(p)
        return td

    root = ET.fromstring('<?xml version="1.0" encoding="utf-8"?><data />')
    desc = ET.Element('p')
    desc.text = context["description"]
    root.append(desc)
    ctable = ET.SubElement(root, 'table')
    cbody = ET.SubElement(ctable, 'tbody')
    header = ET.SubElement(cbody, 'tr')
    header.append(get_cell('Column', tag='th'))
    header.append(get_cell('Data type', tag='th'))
    header.append(get_cell('Constraints', tag='th'))
    header.append(get_cell('Description', tag='th'))
    for strong, column, ctype, constraint, desc in context["columns"]:
        row = ET.SubElement(cbody, 'tr')
        row.append(get_cell(column, strong=strong))
        row.append(get_cell(ctype))
        row.append(get_cell(constraint))
        row.append(get_cell(desc))
    if context['constraints']:
        cttable = ET.SubElement(root, 'table')
        ctbody = ET.SubElement(cttable, 'tbody')
        ctheader = ET.SubElement(ctbody, 'tr')
        ctheader.append(get_cell('Relation', tag='th'))
        ctheader.append(get_cell('Constraint', tag='th'))
        for relation, constraint in context["constraints"]:
            row = ET.SubElement(ctbody, 'tr')
            row.append(get_cell(relation))
            row.append(get_cell(constraint))
    content = ""
    for tag in root:
        content += ET.tostring(tag, encoding="utf-8")
    return content


def get_root_content(tdesc, url, space):

    def get_link(tablename):
        link = "PGTable+%s+%s" % tuple(tablename.split("."))
        link = "%s/display/%s/%s" % (url, space, link)
        a = ET.Element('a', attrib={"href": link})
        a.text = tablename
        return a

    header = """
    <p>This page and descendants are <strong>GENERATED BY <a
    href="https://bamboo.ocslab.com/browse/ODB-CONFLUENCEGENERATEPGPATCHTOCOMMENTCOLUMNS1">
    SCRIPT</a>.</strong> The
    only changes you permitted to do are:<br /><span style="font-size:
        10.0pt;line-height: 13.0pt;">1) Edit column's descriptions<br />2)
        Below table you can add any text you want. BUT this text must be under
        a header<br />3) Edit table description, add tag [Base tables:
        table,table] to define aggregates.</span></p>
    <br/>
    <strong>Table of contents</strong>
    """

    root = ET.fromstring(
        '<?xml version="1.0" encoding="utf-8"?><data>%s</data>' % header)
    current_schema = None
    schema_ul = None
    toc = ET.SubElement(root, "ul")
    for tablename, context in sorted(tdesc.items(), key=lambda x: x[0]):
        schema = tablename.split(".")[0]
        if schema != current_schema:
            schema_li = ET.Element("li")
            schema_li.text = schema
            schema_ul = ET.SubElement(schema_li, 'ul')
            toc.append(schema_li)
            current_schema = schema
        table_li = ET.SubElement(schema_ul, 'li')
        table_li.append(get_link(tablename))
        if context["aggregates"]:
            table_ul = ET.SubElement(table_li, 'ul')
            table_ul.text = "aggregates:"
            for aggregate in context["aggregates"]:
                aggr = ET.SubElement(table_ul, 'li')
                aggr.append(get_link(aggregate))
    content = ""
    for tag in root:
        content += ET.tostring(tag, encoding="utf-8")
    return content


def main(positional, options):
    pages, newpages = positional
    pages = load_pages(pages)
    connection = get_connection(options)
    tdesc = get_table_description(connection, options.tables_sql)
    tdesc = get_column_description(connection, options.columns_sql, tdesc)
    connection.close()
    children = []
    for tablename, context in sorted(tdesc.items(), key=lambda x: x[0]):
        schema = tablename.split(".")[0]
        title = "PGTable %s %s" % tuple(tablename.split("."))
        immutable = get_immutable_content(tablename, pages)
        content = get_content(context) + immutable.encode('utf-8')
        content = restore_entity_ref(content.decode('utf-8'))
        children.append({
            "schema": schema,
            "title": title,
            "tablename": tablename,
            "content": content
        })
    root = {
        "children": children,
        "content": get_root_content(tdesc, options.url, options.space)
    }
    save_pages(newpages, root)


if __name__ == '__main__':
    options, positional = parse_args()
    main(positional, options)
