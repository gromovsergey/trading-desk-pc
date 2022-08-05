\pset format unaligned
\pset fieldsep ;
\pset tuples_only on
with tbl as (
    select nspname || '.' || relname nsprelname,
           t.oid
    from pg_namespace s
    join pg_class t on s.oid = relnamespace
    where nspname !~ '^pg_' and nspname <> 'information_schema' -- not from system catalogs
      and relkind = 'r' -- tables, not indexes etc.
      and t.oid not in (select inhrelid from pg_inherits) -- not a partition
)
-- description
select nsprelname,
       'Description',
       obj_description(tbl.oid, 'pg_class')
from tbl
where obj_description(tbl.oid, 'pg_class') is not null
  union all
-- unique
select nsprelname,
       indrelid::regclass::text,
       coalesce((select pg_get_constraintdef(c.oid, true) from pg_constraint c where conindid = i.indexrelid), pg_get_indexdef(i.indexrelid)) -- get UK or index description
from tbl
join pg_index i on indrelid = tbl.oid and indisunique and not indisprimary
where array_length(indkey, 1) > 1 or indkey[1] = 0
  union all
-- check
select nsprelname,
       conname,
       pg_get_constraintdef(c.oid, true)
from tbl
join pg_constraint c on conrelid = tbl.oid and contype in ('f', 'c', 'x')
where (contype = 'x' or array_length(conkey, 1) > 1);

