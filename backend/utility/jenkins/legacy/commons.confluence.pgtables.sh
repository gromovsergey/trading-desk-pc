#!/bin/bash

# create file with password
# set PASSFILE variable
pgtables_create_passfile() {
  echo ": $FUNCNAME: "
  local password="$1"
  PASSFILE=$WORKING_DIR/confluence.password
  echo "${password}" > $PASSFILE
  chmod 600 $PASSFILE
}

# delete PASSFILE
pgtables_delete_passfile() {
  echo ": $FUNCNAME: "
  rm -f $PASSFILE $@
}


# checkout pgtables utils
# set UTILS_FOLDER variable
pgtables_checkout_utils() {
  echo ": $FUNCNAME: "
  svn_export_folder "svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/dev/pgtables" || return 1
  UTILS_FOLDER=$CHECKOUT_FOLDER
  return 0
}

# download current version of pgtables from confluence
#   and store it to file pages
# use UTILS_FOLDER variable
pgtables_download_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local root="$2"
  [ ! -z "$root" ] && root=" --root $root "
  $UTILS_FOLDER/download_pages.py $root --passfile $PASSFILE $pages || return 1
  return 0
}


# update pages file with information from postgres
#  and store updated data to newpages files
pgtables_update_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local newpages="$2"
  $UTILS_FOLDER/update_pages.py \
    --tables-sql $WORKING_DIR/get_tables_description.sql \
    --columns-sql $WORKING_DIR/get_columns_description.sql \
    $pages $newpages || return 1
  return 0
}

impalatables_update_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local newpages="$2"
  $UTILS_FOLDER/update_impala_pages.py $pages $newpages || return 1
  return 0
}

# upload pages to confluence
pgtables_upload_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local newpages="$2"
  local root="$3"
  [ ! -z "$root" ] && root=" --root $root "
  $UTILS_FOLDER/upload_pages.py $root --passfile $PASSFILE $pages $newpages || return 1
  return 0
}

generate_pgtables() {
  local password="$1"
  local hostname="oix-dev7"

  pgtables_create_passfile "$password"

  execute_remote_ex "maint" "$hostname" '*.sh *.password *.sql' $PASSFILE <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh
    . $WORKING_DIR/commons.confluence.pgtables.sh

    PASSFILE=$1

    trap "pgtables_delete_passfile $PASSFILE" EXIT

    doc pgtables_checkout_utils

    PAGES=$WORKING_DIR/pages.json
    doc pgtables_download_pages $PAGES

    NEWPAGES=$WORKING_DIR/newpages.json
    doc pgtables_update_pages $PAGES $NEWPAGES
    doc pgtables_upload_pages $PAGES $NEWPAGES
    doc pgtables_delete_passfile

    exit 0
EOF
  local result=$?
  pgtables_delete_passfile $PASSFILE

  scp $hostname:$WORKING_DIR/pages.json .
  scp $hostname:$WORKING_DIR/newpages.json .
  scp $hostname:$WORKING_DIR/*.log .

  return $result
}


generate_impalatables() {
  local password="$1"
  local hostname="oix-dev7"

  pgtables_create_passfile "$password"

  execute_remote_ex "maint" "$hostname" '*.sh *.password *.sql' $PASSFILE <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh
    . $WORKING_DIR/commons.confluence.pgtables.sh

    PASSFILE=$1

    trap "pgtables_delete_passfile $PASSFILE" EXIT

    doc pgtables_checkout_utils

    PAGES=$WORKING_DIR/pages.json
    doc pgtables_download_pages $PAGES ImpalaTables

    NEWPAGES=$WORKING_DIR/newpages.json
    doc impalatables_update_pages $PAGES $NEWPAGES
    doc pgtables_upload_pages $PAGES $NEWPAGES ImpalaTables
    doc pgtables_delete_passfile

    exit 0
EOF
  local result=$?
  pgtables_delete_passfile $PASSFILE

  scp $hostname:$WORKING_DIR/pages.json .
  scp $hostname:$WORKING_DIR/newpages.json .
  scp $hostname:$WORKING_DIR/*.log .

  return $result
}
