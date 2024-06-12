#!/bin/bash

jar=pluginuhr.jar
logfile=/log/start.log
date=$(date)

if [ $debug == "true" ] ; then
  echo debug mode on
  echo "$date : Plugin-Service start in debugging-mode" >>$logfile
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5080 $JAVA_OPTS -jar $jar
else
  echo start $jar
  echo "$date : PLugin-Service start" >>$logfile
  java $JAVA_OPTS -jar $jar
fi

date=$(date)
echo "$date : Plugin-Service stopped" >>$logfile
echo $jar stopped - something went wrong!