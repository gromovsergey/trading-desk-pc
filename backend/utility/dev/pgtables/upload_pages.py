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
    parser.add_option("--force", action="store_true")
    return parser.parse_args()


def load_pages(filename):
    with open(filename) as h:
        return json.loads(h.read())


def pages(oldpages, newpages, space, parentId, force):
    old = sorted(oldpages['children'], key=lambda x: x['tablename'])
    new = sorted(newpages['children'], key=lambda x: x['tablename'])
    o = n = 0

    while o < len(old) and n < len(new):
        opage = old[o]
        npage = new[n]
        if opage['tablename'] == npage['tablename']:
            logger.info("updating page: %s", opage['tablename'])
            o += 1
            n += 1
            if opage['content'] == npage['content'] and not force:
                logger.info("skipping, same content")
                continue
            yield {
                "title": "PGTable %s" % opage['tablename'].replace(".", " "),
                "space": space,
                "id": opage["id"],
                "content": npage["content"],
                "version": opage["version"],
                "parentId": parentId,
            }
        elif opage['tablename'] > npage['tablename']:
            logger.info("creating page: %s", npage['tablename'])
            n += 1
            yield {
                "title": "PGTable %s" % npage['tablename'].replace(".", " "),
                "space": space,
                "content": npage['content'],
                "parentId": parentId,
            }
        else:  # opage['tablename'] < npage['tablename']
            logger.info("deleting page: %s", opage['tablename'])
            o += 1
            yield {
                "title": "[DELETED]PGTable %s" %
                    opage['tablename'].replace(".", " "),
                "space": space,
                "content": opage['content'],
                "version": opage['version'],
                "id": opage['id'],
                "parentId": parentId,
            }
    while o < len(old):
        logger.info("deleting page: %s", opage['tablename'])
        o += 1
        yield {
            "title": "[DELETED]PGTable %s" %
                opage['tablename'].replace(".", " "),
            "space": space,
            "content": opage['content'],
            "version": opage['version'],
            "id": opage['id'],
            "parentId": parentId,
        }

    while n < len(new):
        logger.info("creating page: %s", npage['tablename'])
        n += 1
        yield {
            "title": "PGTable %s" % npage['tablename'].replace(".", " "),
            "space": space,
            "content": npage['content'],
            "parentId": parentId,
        }


def main(positional, options):
    confluence = Confluence(options.url, options.user, options.passfile)
    oldpages, newpages = positional
    oldpages = load_pages(oldpages)
    newpages = load_pages(newpages)
    parentId = oldpages['id']
    root = {
        "title": "PGTables",
        "id": oldpages['id'],
        "version": oldpages['version'],
        "space": options.space,
        "content": newpages["content"],
        "parentId": oldpages["parentId"]
    }
    for page in pages(oldpages, newpages,
                      options.space, parentId, options.force):
        print page["title"]
        try:
            confluence.storePage(page)
        except Exception, e:
            logger.exception(e)
    print root["title"]
    try:
        confluence.storePage(root)
    except Exception, e:
        logger.exception(e)


if __name__ == '__main__':
    options, positional = parse_args()
    main(positional, options)
