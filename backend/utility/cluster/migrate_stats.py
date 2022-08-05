#! /usr/bin/env python

import atexit
import os
import os.path
import psycopg2
import stat
import sys
import time
import types

try:
	import json
except ImportError:
	import simplejson as json

from itertools import ifilter, imap
from subprocess import Popen, PIPE
from tempfile import mkstemp


class Table(object):
	__slots__ = ("name", "columns", "constraints", "indexes",
		"pre_queries", "insert_query", "post_queries", "ora_name",
		"full_query")

	class Column(object):
		__slots__ = ("name", "datatype", "ora_name")

		def __init__(self, name, datatype, ora_name = None):
			self.name, self.datatype, self.ora_name = name, datatype, ora_name or name

	def __init__(self, name, **kwargs):
		""":param kwargs:

		   :param columns: a sequence of Table.Column.

		   :param pre_queries: a sequence of SQL queries are executed after the table
		      has been cleaned and before to copy data from corresponding Oracle table.
		      Every item must be a well formed SQL query ended with semicolon (;).

		   :param post_queries: a sequence of SQL queries are executed after the data
		      copying is completed. Every item must be a well formed SQL query ended
		      with semicolon (;).
		"""
		self.name = name

		if "full_query" in kwargs:
			if len(kwargs) != 1:
				raise ValueError, "%s: full_query cannot be used with any other parameter" % name
			self.full_query = kwargs["full_query"]
			return

		if "columns" not in kwargs:
			raise ValueError, "%s: parameter \"columns\" is missed" % name

		self.columns = map(lambda t: Table.Column(*t), kwargs["columns"])
		self.constraints = kwargs.get("constraints", [])
		self.indexes = kwargs.get("indexes", [])
		self.pre_queries = kwargs.get("pre_queries", [])
		self.insert_query = kwargs.get("insert_query")
		self.post_queries = kwargs.get("post_queries", [])
		self.ora_name = kwargs.get("ora_name", name)
		self.full_query = None

		for idx in self.indexes:
			if "unique" not in idx:
				idx["unique"] = True


class Credentials(object):
	__slots__ = ("host", "port", "database", "user", "password")

	Options = { "host": "host", "port": "port", "database": "dbname", "user": "username" }

	def _non_empty_attrs(self, items):
		return ifilter(lambda attr: getattr(self, attr, None) != None, items)

	def to_dict(self):
		"""Returns dict suitable to be passed to psycopg2.connect()"""
		return dict(imap(lambda attr: (attr, getattr(self, attr)), self._non_empty_attrs(Credentials.__slots__)))

	def to_cmdline(self):
		"""Returns list of options suitable to be passed to psql"""
		return map(lambda attr: "--%s=%s" % (Credentials.Options[attr], getattr(self, attr)),
			self._non_empty_attrs(Credentials.Options))


class Task(object):
	__slots__ = ("table", "stage", "command", "process")

	Initial, Truncate, Copy, Final = 0, 1, 2, 3

	make_psql_command = None
	data_source_id = None

	@staticmethod
	def _initialize():
		creds = Task._credentials()
		args = ["psql"]
		args.extend(creds.to_cmdline())
		args.append("-c")
		args = tuple(args)
		Task.make_psql_command = types.MethodType(lambda self, stmt, options = tuple(): args + (stmt, ) + options, Task)

		Task._create_pgpassfile(creds)
		Task.data_source_id = Task._data_source_id(creds)

	@staticmethod
	def _get_colocation_property(key):
		utility = "/opt/foros/ui/bin/get_colocation_property.sh"
		p = Popen((utility, key), stdout=PIPE)
		if p.wait() != 0:
			print >> sys.stderr, "Failed to obtain parameter '%s'" % key
			sys.exit(1)
		value = p.communicate()[0].strip() or None
		return value

	@staticmethod
	def _credentials():
		mapping = { "pg_host": "host", "pg_port": "port", "pg_database": "database",
			"pg_user_foros": "user", "pg_password_foros": "password" }

		creds = Credentials()
		for src, dst in mapping.iteritems():
			value = Task._get_colocation_property(src)
			setattr(creds, dst, value)

		if creds.password == None:
			creds.password = ""

		return creds

	@staticmethod
	def _create_pgpassfile(creds):
		pgpass_dir = Task._get_colocation_property("oui_tmp_folder")
		if not os.path.isdir(pgpass_dir):
			os.mkdir(pgpass_dir)
		pgpassfile = mkstemp(dir=pgpass_dir)[1]
		atexit.register(os.remove, pgpassfile)

		open(pgpassfile, "w") # Just create new file
		os.chmod(pgpassfile, stat.S_IRUSR | stat.S_IWUSR)

		f = open(pgpassfile, "w")
		f.write(":".join(imap(lambda attr: getattr(creds, attr, "*"), ("host", "port", "database", "user"))))
		print >> f, ":%s" % creds.password
		f.close()

		os.environ["PGPASSFILE"] = pgpassfile

	@staticmethod
	def _data_source_id(creds):
		args = creds.to_dict()
		conn = psycopg2.connect(**args)
		cursor = conn.cursor()
		cursor.execute("select data_source_id from dbi_link.dbi_connection where lower(user_name) not like 'replication%'")
		return cursor.next()[0]


	def __init__(self, table):
		if not Task.make_psql_command:
			Task._initialize()

		self.table = table
		self.stage = Task.Initial
		self.command = None

	def next(self):
		if self.stage == Task.Initial:
			self.command = self.make_truncate_command()
			self.stage = Task.Truncate
		elif self.stage == Task.Truncate:
			self.command = self.make_copy_command()
			self.stage = Task.Copy
		elif self.stage == Task.Copy:
			self.command = None
			self.stage = Task.Final
		return self.stage

	def run(self):
		if not self.command:
			raise RuntimeError, "nothing to run"
		self.process = Popen(self.command, env=os.environ)

	def make_truncate_command(self):
		return Task.make_psql_command("truncate table " + self.table.name)

	def make_copy_command(self):
		tab = self.table

		if tab.full_query:
			stmt = tab.full_query
		else:
			params = {
				"table": tab.name,
				"columns": ", ".join(imap(lambda c: c.name, tab.columns)),
				"definintion": ", ".join(imap(lambda c: "\"%s\" %s" % (c.ora_name.upper(), c.datatype), tab.columns)),
				"d_id": Task.data_source_id,
				"ora_table": tab.ora_name,
				"ora_columns": ", ".join(imap(lambda c: c.ora_name, tab.columns))
			}

			stmt = "".join(imap(lambda q: q + "\n", tab.pre_queries))

			stmt += """do language plpgsql $$
declare
	constr text;
begin
	for constr in select conname from pg_constraint where conrelid = '%(table)s'::regclass loop
		execute 'alter table %(table)s drop constraint ' || constr;
	end loop;
"""
			if tab.indexes:
				stmt += """	for constr in select relname from pg_index
		join pg_class on (indexrelid = pg_class.oid) where indrelid = '%(table)s'::regclass
	loop
		execute 'drop index ' || constr;
	end loop;
"""
			stmt += 	"end;\n$$;\n"

			if tab.insert_query:
				stmt += tab.insert_query + ";\n"
			else:
				stmt += "insert into %(table)s (%(columns)s) " \
				"select * from dbi_link.remote_select(%(d_id)d, 'select %(ora_columns)s from %(ora_table)s') "\
				"as (%(definintion)s);\n"
			stmt %= params

			stmt += "".join(imap(lambda c: "alter table %s add %s;\n" % (tab.name, c), tab.constraints))
			for i in tab.indexes:
				params = (tab.name, i["expression"])
				if i["unique"]:
					stmt += "create unique index on %s (%s);\n" % params
				else:
					stmt += "create index on %s (%s);\n" % params

			stmt += "".join(imap(lambda q: q + "\n", tab.post_queries))

		return self.make_psql_command(stmt, ("--single-transaction", ))


PAUSE = 2 # seconds


def parse(filename):
	data = json.load(open(filename, "r"))
	res = []
	for item in data:
		name = item["table"]
		del item["table"]
		res.append(Table(name, **item))
	return res

def max_processes():
	return os.sysconf("SC_NPROCESSORS_ONLN")


def main():
	if len(sys.argv) != 2:
		print >> sys.stderr, "Usage: %s FILE" % os.path.basename(sys.argv[0])
		sys.exit(1)

	tables = parse(sys.argv[1])

	tables = iter(tables)
	tasks = []
	max_proc = max_processes()
	retval = 0
	action_map = { Task.Truncate: "truncate", Task.Copy: "copy" }
	while True:
		if tasks:
			alive = []
			for task in tasks:
				res = task.process.poll()
				if res == None:
					alive.append(task)
					continue

				if res == 0:
					if task.next() != Task.Final:
						task.run()
						alive.append(task)
					continue

				print "Failed to %s %s: %d" % (action_map[task.stage], task.table.name, res)
				tables = None
				retval = 255

			tasks = alive

		if tables:
			while len(tasks) < max_proc:
				try:
					table = tables.next()
					task = Task(table)
					task.next()
					task.run()
					tasks.append(task)

				except StopIteration:
					tables = None
					break

		if not tasks and not tables:
			break

		time.sleep(PAUSE)

	sys.exit(retval)

if __name__ == "__main__":
	main()
