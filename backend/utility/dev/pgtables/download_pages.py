#!/usr/bin/env python
import os
import optparse
import logging
import shutil
import sys
import json
from confluence import Confluence

logging.basicConfig(
    level="INFO", filename="%s.log" % os.path.basename(__file__),
    format="%(asctime)s: %(levelname)s: %(name)s: %(message)s")
logger = logging.getLogger(__name__)


def parse_args():
    parser = optparse.OptionParser()
    parser.add_option("--url", default="https://confluence.ocslab.com")
    parser.add_option("--user", default="oix.project.coordinator")
    parser.add_option("--passfile", default=".confluencepass")
    parser.add_option("--space", default="TDOC")
    parser.add_option("--root", default="PGTables")
    parser.add_option("--methods", action="store_true")
    return parser.parse_args()


def print_methods(methods):
    for name, inp, outp in methods:
        print name
        print "  input:"
        for p in inp:
            print "    ", p.name, p.type[1]
        print "  output:"
        for p in outp:
            print "    ", p.name, p.type[1]


def serialize(page):
    data = {}
    for key, value in vars(page).items():
        if key.startswith("_"):
            continue
        data[key] = value
    return data


def set_tablename(data):
    title = data.get("title", "")
    if title.startswith("PGTable"):
        schema, tablename = title[8:].strip().split(" ")
        data["schema"] = schema
        data["tablename"] = "%s.%s" % (schema, tablename)
    return data


def main(positional, options):
    confluence = Confluence(options.url, options.user, options.passfile)
    if options.methods:
        methods = confluence.getMethods()
        print_methods(methods)
        exit(0)
    outfilename = positional[0]
    logger.info("downloading root page: %s/%s", options.space, options.root)
    root = confluence.getPage(options.space, options.root)
    data = serialize(root)
    logger.info("getting list of children")
    children = confluence.getChildren(root.id)
    data["children"] = []
    for child in children:
        logger.info("downloading child: %s/%s", child.space, child.title)
        if child.title.find("[DELETED]") >= 0:
            continue
        page = confluence.getPageById(child.id)
        data["children"].append(set_tablename(serialize(page)))
    logging.info("saving structure")
    with open(outfilename, "w") as h:
        h.write(json.dumps(data, indent=2))
    logging.info("done")

if __name__ == '__main__':
    options, positional = parse_args()
    main(positional, options)
