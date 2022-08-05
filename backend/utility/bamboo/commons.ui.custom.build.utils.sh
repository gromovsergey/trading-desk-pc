#!/bin/bash

### Add new folder "tag"
### In live FOROS System, this folder is served by AdServer
### (returns the requested html file)
### On a dev host, this file will be returned by Apache SCP
ui_update_static_content_provider() {
  local host=$1

  [ -z "$host" ] && { echo "Undefined host" ; exit 1 ; }

  echo ": $FUNCNAME: Adding '/tags/' alias"
  execute_remote_as "uiuser" $host <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    # Getting the SCP config location (different for 3.1.0)
    SCP_CONF_FILE=/opt/foros/ui/etc/apache/static-content-provider/conf.d/static-content-provider.conf
    [ ! -f "$SCP_CONF_FILE" ] && SCP_CONF_FILE=/opt/foros/ui/etc/apache/static-content-provider/default/conf.d/static-content-provider.conf

    # Adding the alias for /tag/
    ADDON="\\n
      # Not present in live FOROS System\\n
      # This is a mock for AdServer's service\\n
      Alias /tag/ \"/opt/foros/ui/var/www/tags/\"\\n
      <Directory \"/opt/foros/ui/var/www/tags/\">\\n
        Header unset Etag\\n
        Header set Cache-Control \\"max-age=0, must-revalidate\\"\\n
        Options FollowSymLinks\\n
        AllowOverride None\\n
        Order allow,deny\\n
        Allow from all\\n
      </Directory>\\n
    </VirtualHost>\\n"

    LINE_COUNT=`grep -n '</VirtualHost>' $SCP_CONF_FILE | awk -F ":" '{print $1}' | tail -n 1`
    head -n $((LINE_COUNT-1)) $SCP_CONF_FILE > $SCP_CONF_FILE.tmp
    echo -e $ADDON >> $SCP_CONF_FILE.tmp
    mv $SCP_CONF_FILE.tmp $SCP_CONF_FILE
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

ui_mount_distributed_fs() {
  local master_host="$1"; shift
  local hosts="$@"

  [ -z "$master_host" ] && { echo ": $FUNCNAME: undefined master_host" ; exit 1 ; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: nothing to do"; return 0; }

  local host
  for host in $hosts; do
    echo ": $FUNCNAME: mounting fs from $master_host to $host"
    execute_remote_ex "maint" $host "*.sh" $master_host <<-"EOF"
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  HOST=$1

  doc sudo -u uiuser -i mkdir -p /u01/foros/ui/var/sync
  doc sudo -u uiuser -i mkdir -p /u01/foros/ui/var/www
  doc sudo -u uiuser -i mkdir -p /u01/foros/ui/var/cache

  doc sudo mount -t fuse.sshfs uiuser@$HOST://u01/foros/ui/var/sync /u01/foros/ui/var/sync -o rw,auto,nosuid,nonempty,allow_other,nodev,max_read=65536,user=uiuser,IdentityFile=/home/maint/.ssh/uikey
  doc sudo mount -t fuse.sshfs uiuser@$HOST://u01/foros/ui/var/www /u01/foros/ui/var/www -o rw,auto,nosuid,nonempty,allow_other,nodev,max_read=65536,user=uiuser,IdentityFile=/home/maint/.ssh/uikey
  doc sudo mount -t fuse.sshfs uiuser@$HOST://u01/foros/ui/var/cache /u01/foros/ui/var/cache -o rw,auto,nosuid,nonempty,allow_other,nodev,max_read=65536,user=uiuser,IdentityFile=/home/maint/.ssh/uikey
EOF
  [ "$?" != "0" ] && exit 1
  done
}

ui_umount_distributed_fs() {
  local hosts="$1"
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts" ; exit 1 ; }

  local host
  for host in $hosts; do
    echo ": $FUNCNAME: umounting distibuted fs on $host"
    execute_remote_ex "maint" $host "*.sh" <<-"EOF"
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  sudo umount /u01/foros/ui/var/sync
  sudo umount /u01/foros/ui/var/www
  sudo umount /u01/foros/ui/var/cache
EOF
  done
}

ui_update_dir() {
  local dst_host=$1
  local dir=$2
  local db_config=$3

  [ -z "$dst_host" ] && { echo "Undefined dst_host" ; exit 1 ; }
  [ -z "$dir" ] && { echo "Undefined dir" ; exit 1 ; }
  [ -z "$db_config" ] && { echo "Undefined db_config" ; exit 1 ; }

  local src_host="voix0"
  local ssh_key_file="uikey-moscow-test-central"
  local isEmergency=`expr match "$db_config" 'EMERGENCY'`
  if [ "$isEmergency" != "0" ] ; then
    src_host="eoix"
    ssh_key_file="uikey-moscow-emergency-central"
  fi
  local isSelenium=`expr match "$db_config" 'TRUNK'`
  if [ "$isSelenium" != "0" ] ; then
    src_host="oui-nbmaster0"
    ssh_key_file="uikey-moscow-nb-master"
  fi

  local local_dir_base="/tmp/OUI-COMMON/directories/$src_host"
  local local_dir="$local_dir_base/$dir"
  if [ ! -d "$local_dir" ]; then
    mkdir -p "$local_dir"
    chmod -R a+rw "$local_dir_base" 2>/dev/null
  fi

  echo ": $FUNCNAME: Copying $src_host:$dir to $local_dir/*"
  docl sudo -u uiuser "rsync -e \"ssh -i /home/uiuser/.ssh/$ssh_key_file -o 'BatchMode yes'\" -avz $src_host:$dir/* $local_dir"

  echo ": $FUNCNAME: Copying $local_dir to $dst_host:$dir"
  docl sudo -u uiuser "rsync -e \"ssh -i /home/uiuser/.ssh/uikey-moscow-dev-ui -o 'BatchMode yes'\" -avz \"$local_dir/\"* $dst_host:$dir"
  echo ": $FUNCNAME: Directory '$dir' is updated successfully"
}

ui_stop() {
  local hosts="$@"
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }

  local host
  for host in ${hosts//,/ }; do
    stop_cluster $host "ui-moscow"
  done
}

ui_start() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  start_cluster $host "ui-moscow"
}

ui_remove_old_packages() {
  local branch="$1"; shift
  local config_branch="$1"; shift
  local hosts="$@"

  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ -z "$config_branch" ] && { echo ": $FUNCNAME: undefined config_branch "; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }

  if [ "$branch" != "none" ]; then
    local host
    for host in $hosts; do
      uninstall_packages $host `ui_get_package_list -c moscow-dev-ui*`
    done
  elif [ "$config_branch" != "none" ]; then
    local host
    for host in $hosts; do
      uninstall_packages $host `ui_get_package_list -c moscow-dev-ui* -C`
    done
  fi
}

ui_update() {
  local version="$1"; shift
  local migration="$1"; shift
  local hosts="$@"

  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }
  [ -z "$migration" ] && { echo ": $FUNCNAME: undefined migration "; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }

  if echo $notBuild | grep -q unixcommons; then
    docl rm -rf $WORKING_DIR/unixcommons
    docl mkdir -p $WORKING_DIR/unixcommons
    docl scp -q oui-nbmaster0:/opt/foros/ui/lib/*.so $WORKING_DIR/unixcommons
  fi

  if echo $notBuild | grep -q jspwiki; then
    docl rm -rf $WORKING_DIR/autodeploy
    docl mkdir -p $WORKING_DIR/autodeploy
    docl scp -q oui-nbmaster0:/opt/foros/ui/lib/autodeploy/JSPWiki.ear $WORKING_DIR/autodeploy
  fi

  local host
  for host in $hosts; do
    if is_package_installed "$host" "foros-ui" "$version"; then
      echo ": $FUNCNAME: package 'foros-ui = $version' is installed to '$host'"
      continue
    fi

    local package=`repo_get_packages -r local "foros-ui-$version*.rpm"`
    local java_version=`get_required_java_version $package`
    downgrade_packages "$host" java-1.7.0-oracle-$java_version java-1.7.0-oracle-devel-$java_version

    install_packages "$host" "foros-ui-$version*.rpm"

    if [ "$migration" = "yes" ]; then
      ui_skip_migration_backup "$host"
    else
      ui_skip_migration "$host"
    fi

    if echo $notBuild | grep -q unixcommons; then
      docl sudo -u uiuser "scp -q -i '/home/uiuser/.ssh/uikey-moscow-dev-ui' -o 'BatchMode yes' $WORKING_DIR/unixcommons/* $host:/opt/foros/ui/lib"
    fi

    if echo $notBuild | grep -q jspwiki; then
      docl ssh -o BatchMode=yes $host sudo chown uiuser:uiuser -R /opt/foros/ui/lib/autodeploy
      docl sudo -u uiuser "scp -q -i '/home/uiuser/.ssh/uikey-moscow-dev-ui' -o 'BatchMode yes' $WORKING_DIR/autodeploy/* $host:/opt/foros/ui/lib/autodeploy"
    fi
  done
}

ui_update_ear() {
  local filename="$1"; shift
  local hosts="$@"

  [ -z "$filename" ] && { echo ": $FUNCNAME: undefined filename "; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }

  local host
  for host in $hosts; do
    echo ": $FUNCNAME: installing foros-ui.ear to $host"
    doc ssh $host sudo chown uiuser:uiuser -R /opt/foros/ui/lib/autodeploy
    doc sudo -u uiuser "scp -q -i '/home/uiuser/.ssh/uikey-moscow-dev-ui' -o 'BatchMode yes' $filename $host:/opt/foros/ui/lib/autodeploy"
  done
}

ui_check_installed_version() {
  local branch="$1"; shift
  local hosts="$@"

  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ "$branch" = "none" ] && { echo ": $FUNCNAME: branch is none"; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }
  [ "$hosts" = "none" ] && { echo ": $FUNCNAME: hosts is none"; exit 1; }

  echo ": $FUNCNAME: checking for version on $hosts"
  local installed_version="none"
  local host
  for host in $hosts; do
    [ "X$installed_version" = "Xnone" ] && \
      installed_version="`ssh $host -- rpm -q --qf '%{version}' foros-ui`" && continue
    [ "X$installed_version" != "X`ssh $host -- rpm -q --qf '%{version}' foros-ui`" ] && \
      installed_version="none" && break
  done
  [ -z "$installed_version" ] && installed_version="none"
  echo ": $FUNCNAME: installed version: $installed_version"
  [ "$installed_version" = "none" ] && { echo "$FUNCNAME: foros-ui is not installed or versions are differ"; exit 1; }

  get_package_ui_branch_from_cache_file $installed_version
  echo ": $FUNCNAME: installed branch: $CACHE_PACKAGE_UI_BRANCH"
  [ "X$CACHE_PACKAGE_UI_BRANCH" != "Xforos/ui/$branch" ] &&
    { echo ": $FUNCNAME: you can not use 'RedeployEAROnly', branches are mismatched"; exit 1; }
  return 0
}

ui_skip_migration() {
  local host=$1

  [ -z "$host" ] && { echo ": $FUNCNAME: Undefined host" ; exit 1 ; }

  echo ": $FUNCNAME: Skipping OUI migration"
  ssh $host "sudo chown uiuser:uiuser /opt/foros/ui/bin/run-migrations.py"
  ssh $host "sudo -u uiuser sh -c 'echo -e \"#!/bin/bash \\necho \\\"automigration is skipped\\\" >&2\" > /opt/foros/ui/bin/run-migrations.py'"
  return 0
}

ui_skip_migration_backup() {
  local host=$1

  [ -z "$host" ] && { echo ": $FUNCNAME: Undefined host" ; exit 1 ; }

  echo ": $FUNCNAME: Skipping backup during FOROS UI migration"
  ssh $host "sudo chown uiuser:uiuser /opt/foros/ui/bin/run-migrations.py"
  ssh $host "sudo -u uiuser sed -i -e '/backup(self.config)/d' /opt/foros/ui/bin/run-migrations.py"
  return 0
}

