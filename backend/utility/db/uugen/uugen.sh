#!/bin/bash
#
# what does this script do:
#
# install:
#  1. creates 100 users
#  2. creates user-site pairs randomly (p=0.5)
#
# daily:
#  1. removes old users (for every user p=0.0005)
#  2. removes old user-site pairs (for every user-site pair p=0.01)
#  3. creates new users (10 * p=0.005)
#  4. creates new user-site pairs (p=0.01)
#  5. for every user
#    1. calculate if he/she is active today (p=0.1-0.5, depends on user)
#    2. for every active user
#      1. for every user-site pair calculate if he/she is going to visit this site today (p=0.1-0.5, depends on user-site pair)
#      2. for every user-site pair that is active
#         1. for every cc on this site calculate if the group will be used (p=0.1-0.5, depends on user-site-cc group)
#         1. for every active user-site-cc make one visit
#  6. saves results to DB

CONF_DIR=$(dirname $(readlink -f $0))/conf
TEMP_DIR=$(dirname $(readlink -f $0))/tmp
WORK_DIR=$(dirname $(readlink -f $0))/var
INITIAL_USERS=100
P_USER_PRESENT_ON_SITE=2
P_DELETE_USER=200
P_DELETE_USER_FROM_SITE=100
P_ADD_USER=20
ADD_USER_COUNT=10
P_ADD_USER_TO_SITE=100
SLEEP=0.3
SQL_TPL="perform uugen.write('__uguid__', '__uid__', __cc_id__, __colo_id__, __tag_id__);"
SQL_CONNECT_STRING="-h stat-test.ocslab.com -d stat_test -U test_ui -w"
ADDITIONAL_URL_PARAMS="&require-debug-info=body&debug.ccg=304242"
REQUESTS_DEBUG_TIME_START="12:00:00"
REQUESTS_DEBUG_TIME_INTERVAL=2 #minute
REQUESTS_DEBUG_TIME_MAX_COUNT=360

TODAY=$(date -d "now" +"%Y-%m-%d")

gen_random_string () {
  head -c $1 /dev/urandom|md5sum|head -c $1
}

gen_random_number () {
  echo $(($(hexdump -n 4 -e '/4 "%u"' /dev/urandom) % $1))
}

shall_we_go_on () {
  local RND=$(gen_random_number $1)
  [ $RND = 0 ];
}

create_user () {
  local USER_ID=$(gen_random_string 8)
  echo create user $USER_ID
  mkdir -p $WORK_DIR/users/$USER_ID/sites
  : > $WORK_DIR/users/$USER_ID/cookies
  for i in $SITES; do
    if shall_we_go_on $P_USER_PRESENT_ON_SITE; then
      add_user_to_site $USER_ID $i
    fi
  done
}

add_user_to_site () {
  echo add user $1 to site $2
  : > $WORK_DIR/users/$1/sites/$2
}

# 1-5
get_user_ratio () {
  local HASH=$(echo -n $1 | md5sum | head -c 15)
  echo $((1 + $(printf "%d" 0x$HASH) % 5))
}

get_user_colo () {
  local HASH=$(echo -n $1 | md5sum | head -c 15)
  local COLO_ROWNUM=$((1 + $(printf "%d" 0x$HASH) % $COLOS_COUNT))
  head -n $COLO_ROWNUM < $CONF_DIR/colos | tail -n 1
}

get_next_today_date () {
  [ ! -x $WORK_DIR/times/ ] && mkdir $WORK_DIR/times/
  if [ -f "$WORK_DIR/times/$TODAY" ]; then
    local REQUEST_NO=$(cat "$WORK_DIR/times/$TODAY")
  else
    local REQUEST_NO=0
  fi
  local TOTAL_INTERVAL=$(($REQUESTS_DEBUG_TIME_INTERVAL * $REQUEST_NO))
  REQUEST_NO=$((REQUEST_NO + 1))
  if [ "$REQUEST_NO" -gt "$REQUESTS_DEBUG_TIME_MAX_COUNT" ]; then
    echo too many requests today >&2
    exit 5
  fi
  rm -f "$WORK_DIR"/times/*
  echo $REQUEST_NO > "$WORK_DIR/times/$TODAY"
  TIME="$TOTAL_INTERVAL minute $TODAY $REQUESTS_DEBUG_TIME_START"
  date -d "$TIME" "+%d-%m-%Y:%H-%M-%S"
}

install () {
  if [ -x $WORK_DIR/users/ ]; then
    echo remove $WORK_DIR/users dir to clean up the previous install >&2
    exit 2
  fi
#  for s in $SITES; do
#    local CCS=$(ls $WORK_DIR/sites/$s)
#    eval SITE_$s="'$CCS'";
#    for c in SITES_$s; do
#      local URL=$(cat $WORK_DIR/sites/$s/$c)
#      eval SITE_"$s"_CC_"$c"="'$URL'"
#    done
#  done
  for i in $(seq 1 $INITIAL_USERS); do
    create_user
  done
}

save () {
  echo -e "end;\n\$\$" >> $TEMP_DIR/tmp.sql
  if [ "$1" != local ]; then
    local pgpass=`mktemp`
    chmod 600 $pgpass
    echo "*:*:*:*:adserver" > $pgpass
    export PGPASSFILE=$pgpass
    if psql $SQL_CONNECT_STRING -v ON_ERROR_STOP=1 -f $TEMP_DIR/tmp.sql; then
      echo "Results are saved to DB";
      rm $TEMP_DIR/tmp.sql
    fi
  else
    echo "Warning! Results are not saved to DB. "
    echo "Try to run \`psql $SQL_CONNECT_STRING -v ON_ERROR_STOP=1 -f $TEMP_DIR/tmp.sql' manually"
    echo file follows
    cat "$TEMP_DIR/tmp.sql"
    exit 5
  fi;
}

daily () {
  if [ ! -x $WORK_DIR/users/ ]; then
    echo install first >&2
    exit 2
  fi
  for i in $(ls $WORK_DIR/users); do
    # remove user randomly
    if shall_we_go_on $P_DELETE_USER; then
      echo remove user $i
      rm -rf $WORK_DIR/users/$i
    else
      # remove user's sites randomly
      for j in $(ls $WORK_DIR/users/$i/sites); do
        if shall_we_go_on $P_DELETE_USER_FROM_SITE; then
          echo remove user $i from site $j
          rm -rf $WORK_DIR/users/$i/sites/$j
        fi
      done
      # add user's sites randomly
      for j in $SITES; do
        if shall_we_go_on $P_ADD_USER_TO_SITE; then
          add_user_to_site $i $j
        fi
      done
    fi
  done

  # add users randomly
  for i in $(seq 1 $ADD_USER_COUNT); do
    if shall_we_go_on $P_ADD_USER; then
      create_user
    fi;
  done

  # visit sites
  local HAVE_FAILED_WGET=0
  echo -e "do \$\$\nbegin" > $TEMP_DIR/tmp.sql
  for i in $(ls $WORK_DIR/users); do
    if [ $(get_user_ratio $i) -ge $(gen_random_number 10) ]; then
      local COLO_ID=$(get_user_colo $i)
      for j in $(ls $WORK_DIR/users/$i/sites); do
        if [ $(get_user_ratio $i $j) -ge $(gen_random_number 10) ]; then
          for k in $(ls $CONF_DIR/sites/$j); do
            if [ $(get_user_ratio $i $j $k) -ge $(gen_random_number 10) ]; then
              local URL="$(head -1 $CONF_DIR/sites/$j/$k)"
              local REFERER="$(head -2 $CONF_DIR/sites/$j/$k | tail -n +2)"
              if [ "$REFERER" != "" ]; then
                REFERER="--header \"Referer: http://$REFERER\"";
              fi;
              local DATE
              DATE=$(get_next_today_date)
              local RESULT="$?";
              echo "$RESULT"
              if [ "$RESULT" = 5 ]; then
                save $1
              fi;
              if [ "$RESULT" != 0 ]; then
                exit $RESULT
              fi;
              URL=$(echo $URL | sed -e "s/__colo_id__/$COLO_ID/")"$ADDITIONAL_URL_PARAMS""&debug-time="$DATE
              local COOKIE_FILE=$WORK_DIR/users/$i/cookies
              [ -f "$COOKIE_FILE" ];
              local COOKIE_FILE_PRESENT_EC=$?
              local WGET_CMD="wget -O - -q --load-cookies $COOKIE_FILE --save-cookies $COOKIE_FILE $REFERER \"$URL\""
              if [ "$1" = local ]; then
                local WGET_RESULT_CC_ID=$k
                local UID_COOKIE=$(awk '{if ($0 !~ "^#|^$" && $6 = "uid") print $7}' < $COOKIE_FILE)
              else
                local WGET_RESULT=$(eval "$WGET_CMD")
                echo "--- adserver answer for $URL"
                echo $WGET_RESULT
                echo "--- end adserver answer"
                local WGET_RESULT_CC_ID=$(echo "$WGET_RESULT" | awk '{if($0 ~ "^=== .* ===$")on=0;if ($0 == "=== Creative selection ===")on=1;if(on==1)print $0}' | grep '^ccid = [0-9]*;' | grep -o '[0-9]\+')
                local UID_COOKIE=$(awk '{if ($0 !~ "^#|^$" && $6 = "uid") print $7}' < $COOKIE_FILE)
                if [ "$COOKIE_FILE_PRESENT_EC" = 0 -a "$WGET_RESULT_CC_ID" != $k -a "$UID_COOKIE" != "PPPPPPPPPPPPPPPPPPPPPA.." ]; then
                  echo "Warning! Expected CC_ID=$k, received CC_ID=$WGET_RESULT_CC_ID, url follows:"
                  HAVE_FAILED_WGET=1
                fi;
                #if echo "$WGET_RESULT" | grep -q "track_pixel_url = ;"; [ $? = 1 ]; then
                #  echo "Warning! Expected no track_pixel_url, got track_pixel_url, url follows:"
                #  WGET_RESULT_CC_ID="-$WGET_RESULT_CC_ID/*not tracked*/"
                #  HAVE_FAILED_WGET=1
                #fi;
                sleep $SLEEP
              fi;
              if [ "$UID_COOKIE" = "" ]; then
                UID_COOKIE="no_cookie_set"
              fi;
              local TAG_ID=$(echo $URL | sed -e 's/.*[?&]tid=\([^&]*\).*/\1/')
              echo "uid=$UID_COOKIE visited cc=$WGET_RESULT_CC_ID exp_cc=$k cmd=$WGET_CMD"
              local WGET_RESULT_CC_ID_4SED=$(echo "$WGET_RESULT_CC_ID"|sed -e 's/\//\\\//g') #escape slash
              if [ "$1" = local ] || [ "$WGET_RESULT_CC_ID" != "" ]; then
                local SQL=""
                SQL=$(echo "$SQL_TPL"|sed -e "s/__colo_id__/$COLO_ID/g" -e "s/__tag_id__/$TAG_ID/g" -e "s/__cc_id__/$WGET_RESULT_CC_ID_4SED/g" -e "s/__uid__/$UID_COOKIE/g" -e "s/__uguid__/$i/g")
                echo "$SQL" >> $TEMP_DIR/tmp.sql
              fi;
              echo;
             fi;
          done
        fi;
      done;
    fi;
  done

  save $1 || exit "$?"

  if [ "$HAVE_FAILED_WGET" = 1 ]; then
    echo "Warning! Some adrequests gave us unexpected cc_ids"
    exit 4
  fi;
}


if [ ! -d $CONF_DIR/sites/ ]; then
  echo fill $CONF_DIR/sites dir by directories named by site names and files containing urls >&2
  exit 1
fi
if [ ! -f $CONF_DIR/colos ]; then
  echo fill $CONF_DIR/colos file by colo ids >&2
  exit 1
fi
SITES=$(ls $CONF_DIR/sites)
COLOS_COUNT=$(cat $CONF_DIR/colos | wc -w)
case "$1" in
  install)
    install
    ;;
  daily)
    shift;
    daily $@
    ;;
  *)
    echo "Usage: $0 {install|daily [local]}" >&2
    exit 3
    ;;
esac

