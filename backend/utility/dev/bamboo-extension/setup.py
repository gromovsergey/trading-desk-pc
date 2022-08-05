#!/usr/bin/env python

"""
distutils/setuptools install script.
"""

import os
import sys
import glob


try:
    from setuptools import setup
except ImportError:
    from distutils.core import setup


setup(
    name="bamboo_extension",
    version="0.1.0.1",
    description='Some GUI extensions for Bamboo service',
    long_description=open('README').read(),
    author="Kirill Goldshtein",
    author_email='kirill_goldshtein@ocslab.com',
    packages=['bamboo_extension', "bamboo_extension.bamboo"],
    requires=['pyquery', 'tornado', 'Crypto', 'memcache', 'ldap'],
    install_requires=['pyquery >= 1.2.6', 'tornado >= 3.1.1',
                      'lxml >= 2.2.3', 'cssselect >= 0.9.1'],
    scripts=['bin/bx'],
    data_files=[
        ("bamboo_extension/data/html",
            glob.glob("bamboo_extension/data/html/*.html")),
        ("bamboo_extension/data/static",
            glob.glob("bamboo_extension/data/static/*"))],
    include_package_data=True,
    classifiers=(
        'Development Status :: 1 - Planning',
        'Intended Audience :: System Administrators',
        'Natural Language :: English',
        'Programming Language :: Python',
        'Programming Language :: Python :: 2.7',
        'Topic :: System :: Networking',
        'Topic :: Utilities',
        'Operating System :: Unix',
    ),
)
