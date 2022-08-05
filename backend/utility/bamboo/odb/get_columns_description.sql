\pset format unaligned
\pset fieldsep ;
\pset tuples_only on
select case when attnum = any(pk.conkey) then '*' end,
       nspname || '.' || relname,
       attname,
       format_type(atttypid, atttypmod),
       array_to_string(
         array[
             case when attnum = any(pk.conkey) then 'PK' when attnotnull then 'not null' end,
             case when exists (select 1
                               from pg_index ui
                               where ui.indrelid = t.oid
                                 and ui.indisunique
                                 and not ui.indisprimary
                                 and ui.indkey = array[attnum]::int2vector) then 'unique' end,
             case when pg_get_expr(adbin, adrelid) like 'nextval(%)' then 'uses sequence' else 'default ' || pg_get_expr(adbin, adrelid) end
         ] || (
           select array_agg('FK(' || fk.confrelid::regclass || ')')
           from pg_constraint fk
           where fk.conrelid = t.oid
             and fk.contype = 'f'
             and fk.conkey = array[attnum]
         ),
         ', '
       ),
       array_to_string(
           (select array_agg(ck.conname || ' ' || pg_get_constraintdef(ck.oid, true))
            from pg_constraint ck
            where ck.conrelid = t.oid
              and ck.contype = 'c'
              and ck.conkey = array[attnum]
           ),
           ', '
       ),
       col_description(attrelid, attnum)
from pg_namespace s
join pg_class t on s.oid = relnamespace
join pg_attribute c on t.oid = attrelid
left join pg_attrdef d on adrelid = attrelid and adnum = attnum and atthasdef
left join pg_constraint pk on pk.conrelid = t.oid and pk.contype = 'p'
where nspname !~ '^pg_' and nspname <> 'information_schema' -- not from system catalogs
  and relkind = 'r' -- tables, not indexes etc.
  and attnum > 0  -- visible columns
  and not attisdropped -- not dropped
  and t.oid not in (select inhrelid from pg_inherits) -- not a partition
order by nspname, relname, attnum;

