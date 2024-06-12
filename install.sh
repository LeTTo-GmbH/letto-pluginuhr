#!/bin/bash

rev="$(head revision.txt -n1|sed 's/^ *//'|sed 's/[\n\r ]//g')"
pwd=$(pwd)

installok=true
nocache=off
# Aufruf: install.sh nocache=$nocache
for i in $@ ; do
  if   [[ $i == "nocache="* ]] ; then
    nocache=${i:8}
  fi
done

# pluginuhr
cd $pwd/plugin
image=lettohub/letto-service-pluginuhr
docker rmi $image
if [ $nocache == "on" ] ; then
  docker build --no-cache -t $image .
  if ! [ $? -eq 0 ] ; then installok=false; fi
else
  docker build -t $image .
  if ! [ $? -eq 0 ] ; then installok=false; fi
fi


if [ $installok == "false" ] ; then
  exit 1
fi


