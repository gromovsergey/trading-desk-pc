#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

[ -z "$DB_SOURCE" ] && DB_SOURCE=${bamboo__0_Source}
DB_ORACLE_SCHEMA=${bamboo__1_Schema}
DB_POSTGRES_SCHEMA=${bamboo__1_Database}
DB_SCHEMA_NO="${bamboo__1_SchemaNumber}"
[ -z "$DB_REFRESH_ORACLE" ] && DB_REFRESH_ORACLE=${bamboo__2_RefreshOracle}
[ -z "$DB_REFRESH_POSTGRES" ] && DB_REFRESH_POSTGRES=${bamboo__3_RefreshPostgres}
[ -z "$DB_PATCH_ORACLE" ] && DB_PATCH_ORACLE="${bamboo__4_PatchOracleTill}${bamboo__2_PatchTill}${bamboo__2_PatchOracleTill}"
[ -z "$DB_PATCH_POSTGRES" ] && DB_PATCH_POSTGRES="${bamboo__5_PatchPostgresTill}${bamboo__2_PatchTill}${bamboo__3_PatchPostgresTill}"
[ -z "$DB_PATCH_BI" ] && DB_PATCH_BI="${bamboo__5_PatchBiTill}${bamboo__2_PatchTill}${bamboo__9_PatchBiTill}"
DB_PATCH_ORACLE_REPLICATION="trunk"
DB_PATCH_POSTGRES_REPLICATION="trunk"

[ -z "$DB_REPLICATE_TABLES" ] && DB_REPLICATE_TABLES="${bamboo__6_ReplicateTables}${bamboo__4_ReplicateTables}"
DB_POSTGRES_FULL_COPY=${bamboo__8_UsePostgresFullCopy}

[ -n "$DB_ORACLE_SCHEMA" ] && [ "$DB_ORACLE_SCHEMA" != "skip" ] && DB_POSTGRES_SCHEMA="skip"
[ -n "$DB_POSTGRES_SCHEMA" ] && [ "$DB_POSTGRES_SCHEMA" != "skip" ] && DB_ORACLE_SCHEMA="skip"

case $DB_SOURCE in
  emergency)
    DB_ORACLE_SCHEMA="UI_DEV_$DB_SCHEMA_NO"
    DB_POSTGRES_SCHEMA="ui_dev_$DB_SCHEMA_NO"
    ;;
  test*)
    DB_ORACLE_SCHEMA="UI_DEV_$DB_SCHEMA_NO"
    DB_POSTGRES_SCHEMA="ui_dev_$DB_SCHEMA_NO"
    ;;
  nb)
    DB_ORACLE_SCHEMA="NB_COPY$DB_SCHEMA_NO"
    DB_POSTGRES_SCHEMA="nb_copy$DB_SCHEMA_NO"
    ;;
  unittest)
    DB_ORACLE_SCHEMA="UNITTEST_UI_$DB_SCHEMA_NO"
    DB_POSTGRES_SCHEMA="unittest_ui_$DB_SCHEMA_NO"
    ;;
  skip) # patching, replicating tasks
    DB_REFRESH_ORACLE="no"
    DB_REFRESH_POSTGRES="no"
    DB_SCHEMA_NO="`echo $DB_ORACLE_SCHEMA | tr -d '[:alpha:]_'`"
    [ -z "$DB_SCHEMA_NO" ] && DB_SCHEMA_NO="`echo $DB_POSTGRES_SCHEMA | tr -d '[:alpha:]_'`"
    [ -z "$DB_REPLICATE_TABLES" ] && DB_REPLICATE_TABLES="no"
    ;;
  *)
    echo "Unknown source '$DB_SOURCE'"
    exit 1
    ;;
esac

[ -z "$DB_POSTGRES_FULL_COPY" ] && DB_POSTGRES_FULL_COPY="no"

[ "$DB_POSTGRES_FULL_COPY" != "yes" ] && DB_POSTGRES_FULL_COPY="N"
[ "$DB_POSTGRES_FULL_COPY" = "yes" ] && DB_POSTGRES_FULL_COPY="Y"

check_globals DB_SOURCE \
              DB_ORACLE_SCHEMA DB_POSTGRES_SCHEMA \
              DB_SCHEMA_NO \
              DB_REFRESH_ORACLE DB_REFRESH_POSTGRES \
              DB_PATCH_ORACLE DB_PATCH_POSTGRES DB_PATCH_BI \
              DB_PATCH_ORACLE_REPLICATION \
              DB_PATCH_POSTGRES_REPLICATION \
              DB_REPLICATE_TABLES \
              DB_POSTGRES_FULL_COPY

[ -z $BAMBOO_SUBTASK ] &&  { echo "Undefined BAMBOO_SUBTASK"; exit 1; }
echo ": SUBTASK: $BAMBOO_SUBTASK"
case $BAMBOO_SUBTASK in
  REFRESHORACLE)
    [ "$DB_REFRESH_ORACLE" != "yes" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc refresh_oracle_from_source "$DB_SOURCE" "$DB_SCHEMA_NO" "$DB_PATCH_ORACLE"
    ;;
  REFRESHPOSTGRES)
    [ "$DB_REFRESH_POSTGRES" != "yes" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc refresh_postgres_from_source "$DB_SOURCE" "$DB_SCHEMA_NO" "$DB_POSTGRES_FULL_COPY" "$DB_PATCH_POSTGRES"
    ;;
  PATCHORACLEREPLICATION)
    [ "$DB_PATCH_ORACLE" = "skip" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc patch_oracle "$DB_ORACLE_SCHEMA" "S" "$DB_PATCH_ORACLE_REPLICATION" "no" "no"
    ;;
  PATCHORACLE)
    [ "$DB_PATCH_ORACLE" = "skip" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc patch_oracle "$DB_ORACLE_SCHEMA" "$DB_PATCH_ORACLE" "S" "no" "no"
    ;;
  PATCHPOSTGRESREPLICATION)
    doc patch_postgres "$DB_POSTGRES_SCHEMA" "S" "$DB_PATCH_POSTGRES_REPLICATION"
    ;;
  REPLICATETABLES)
    [ "$DB_REPLICATE_TABLES" != "yes" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc replicate_tables "stat-dev0.ocslab.com" 5432 "$DB_POSTGRES_SCHEMA"
    ;;
  PATCHPOSTGRES)
    [ "$DB_PATCH_POSTGRES" = "skip" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc patch_postgres "$DB_POSTGRES_SCHEMA" "$DB_PATCH_POSTGRES" "S"
    ;;
  PATCHBI)
    [ "$DB_PATCH_BI" = "skip" ] && { echo ": $SUBTASK: skipped"; exit 0; }
    doc patch_postgres "$DB_POSTGRES_SCHEMA" "S" "S" "$DB_PATCH_BI"
    ;;
  *)
    echo "Unknown subtask: $BAMBOO_SUBTASK"
    exit 1
    ;;
esac

exit 0
