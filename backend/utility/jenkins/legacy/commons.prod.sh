#!/bin/bash

prod_generate_ssh_config() {
  local username="$1"; shift
  local keyfile="$1"; shift

  [ -z "$username" ] && { echo ": $FUNCNAME: undefined username "; exit 1; }
  [ -z "$keyfile" ] && { echo ": $FUNCNAME: undefined keyfile "; exit 1; }

  [ ! -z "$SSH_CONFIG" ] && [ -r "$SSH_CONFIG" ] && return 0

  local tempfile=`mktemp`
  doc chmod 600 $tempfile

  cat >> $tempfile <<EOF
Host ru77*
User $username
StrictHostKeyChecking no
IdentityFile $keyfile
EOF
  SSH_CONFIG=$tempfile
}
