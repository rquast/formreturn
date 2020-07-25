#!/bin/bash

FRM_HOME=.
COMMAND_PATH=`echo ${0} | sed -e "s/\(.*\)\/.*$/\1/g"`
cd ${COMMAND_PATH}

if [ -z $JAVA_HOME ]; then
  JAVA_COMMAND=`which java`
  if [ "$?" = "1" ]; then
    echo "No executable java found. Please set JAVA_HOME variable";
    exit;
  fi
else
  JAVA_COMMAND=$JAVA_HOME/bin/java
fi
if [ ! -x $JAVA_COMMAND ]; then
  echo "$JAVA_COMMAND is not executable. Please check the permissions."
  exit
fi
$JAVA_COMMAND -cp "$FRM_HOME/lib/formreturn.jar:$FRM_HOME/lib/*" -Xmx512m com.ebstrada.formreturn.server.ServerGUI
