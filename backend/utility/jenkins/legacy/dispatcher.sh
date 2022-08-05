#!/bin/bash

### Start ssh-agent and add identity
start_ssh_agent() {
  local storage="/tmp/OUI-COMMON/ssh-agent"
  mkdir -p $storage || { echo ": Could not create $storage"; exit 1; }

  if test -f "$storage/agent.pid" && ps --pid `cat $storage/agent.pid 2>/dev/null` > /dev/null; then
    eval `cat $storage/agent.vars 2>/dev/null` >/dev/null
    if [ -S "$SSH_AUTH_SOCK" ]; then
      echo ": SSH agent already running. PID = $SSH_AGENT_PID"
      return 0;
    fi
  fi

  echo `ssh-agent 2>&1` > "$storage/agent.vars"
  eval `cat $storage/agent.vars 2>/dev/null` >/dev/null
  [ -z "$SSH_AGENT_PID" ] && { echo ": Could not run ssh-agent. See $storage/agent.vars"; exit 1; }
  ssh-add 2>/dev/null || { kill $SSH_AGENT_PID; echo ": Could not add identity"; exit 1; }
  echo "$SSH_AGENT_PID" > "$storage/agent.pid"
  echo ": SSH agent was run. PID = $SSH_AGENT_PID"
}

TASK="$1"
SUBTASK="$2"
echo [`whoami`@`hostname` `pwd`]"$ $TASK"
start_ssh_agent

echo ": $BAMBOO_USER / $EMAIL_RECIPIENT has started this build ('$buildKey' / '$buildNumber')" ; echo

export BAMBOO_SUBTASK=$SUBTASK
eval "./$TASK 2>&1"
exit $?
