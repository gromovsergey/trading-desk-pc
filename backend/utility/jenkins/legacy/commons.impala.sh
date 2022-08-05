#!/bin/bash

impala_get_replicated_tables() {
  local colo="$1"
  ssh -o BatchMode=yes hadoop0 -- \
    xml_grep --cond=coordinator/configuration/property/value \
             --cond=coordinator/configuration/property/name \
             /opt/foros/datastore/lib/$colo/oozie/jobs/synctable/bundle.xml | \
    sed -n -e 's|<name>schema</name><value>[a-z]\+</value>||g' \
           -e 's|<name>frequency</name><value>[0-9]\+</value>||g' \
           -e 's|<name>start</name><value>[0-9]\+-[0-9]\+-[0-9]\+T[0-9]\+:[0-9]\+Z</value>||g' \
           -e 's|<name>tableName</name><value>\([a-z_]\+\)</value>|\1 |gp'
  return $?
}

impala_refresh_database() {
  local src="$1"; shift;
  local dst="$1"; shift;
  local partitions="$1"; shift;
  local excludes="$@"

  [ -z "$src" ] && { echo ": $FUNCNAME: undefined src "; exit 1; }
  [ -z "$dst" ] && { echo ": $FUNCNAME: undefined dst "; exit 1; }
  [ -z "$partitions" ] && { echo ": $FUNCNAME: undefined partitions "; exit 1; }

  local user="$dst"
  local impalad="hadoop1.ocslab.com:21050"

  local cmdline="-L DEBUG -i $impalad -s $src -d $dst -u $user"
  if [ "$partitions" = "0" ]; then
    cmdline="$cmdline --empty"
  else
    cmdline="$cmdline -p $partitions"
    local exclude
    for exclude in $excludes; do
      cmdline="$cmdline -X $exclude"
    done
  fi

  local attempt="0"
  local max_attempts="4"

  while [ "$attempt" -lt "$max_attempts" ]; do
    echo ": $FUNCNAME: attempt $attempt"
    execute_remote hadoop2 $cmdline <<-"EOF"
      #!/bin/bash
      WORKING_DIR=`pwd`
      . $WORKING_DIR/commons.sh

      CMDLINE=$@
      SVNPATH="svn+ssh://svn.ocslab.com/home/svnroot/oix/datastore-adm/trunk/utility/impala/tools"

      svn_export_folder $SVNPATH
      export PYTHONPATH=$CHECKOUT_FOLDER

      doc $CHECKOUT_FOLDER/bin/copy-database $CMDLINE

      exit 0
EOF
    local result=$?
    [ "$result" = "0" ] && break
    ((attempt+= 1))
    sleep 30
  done
  return $result
}

impala_drop_database() {
  local db="$1"
  [ -z "$db" ] && { echo ": $FUNCNAME: undefined db "; exit 1; }

  local user="$db"
  local impalad="hadoop1.ocslab.com:21050"
  local cmdline="-L DEBUG -i $impalad -d $db -u $user"

  execute_remote hadoop2 $cmdline <<-"EOF"
      #!/bin/bash
      WORKING_DIR=`pwd`
      . $WORKING_DIR/commons.sh

      CMDLINE=$@
      SVNPATH="svn+ssh://svn.ocslab.com/home/svnroot/oix/datastore-adm/trunk/utility/impala/tools"

      svn_export_folder $SVNPATH
      export PYTHONPATH=$CHECKOUT_FOLDER

      doc $CHECKOUT_FOLDER/bin/drop-database $CMDLINE

      exit 0
EOF
  local result=$?
  return $result
}

impala_create_empty_database() {
  local db="$1"
  [ -z "$db" ] && { echo ": $FUNCNAME: undefined db "; exit 1; }

  local user="$db"
  local impalad="hadoop1.ocslab.com"

  ssh -o BatchMode=yes datastore@hadoop1 -- \
    "impala-shell -i $impalad -d default -u $user -q \"create database $db\""
  return $?
}

update_hadoop_applied_patches() {
  local src="$1"
  local dst="$2"

  [ -z "$src" ] && { echo ": $FUNCNAME: undefined src "; exit 1; }
  [ -z "$dst" ] && { echo ": $FUNCNAME: undefined dst "; exit 1; }

  ssh -o BatchMode=yes datastore@hadoop0 -- \
    "psql -c \"delete from hadoop_applied_patches where username = '$dst';\
               insert into hadoop_applied_patches\
                 (select patch_name, '$dst' username, status, start_date, end_date\
                   from hadoop_applied_patches where username = '$src');\
               select * from hadoop_applied_patches where username='$dst';\""
  return $?
}

clean_hadoop_applied_patches() {
  local db="$1"
  [ -z "$db" ] && { echo ": $FUNCNAME: undefined db "; exit 1; }

  ssh -o BatchMode=yes datastore@hadoop0 -- \
    "psql -c \"delete from hadoop_applied_patches where username = '$db';\""
  return $?
}
