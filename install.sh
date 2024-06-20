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
cd $pwd
image=lettohub/letto-service-pluginuhr
docker rmi $image
if [ $nocache == "on" ] ; then
  docker build -f Dockerfile-prebuilt -t $image .
  if ! [ $? -eq 0 ] ; then installok=false; fi
else
  docker build -t $image .
  if ! [ $? -eq 0 ] ; then installok=false; fi
fi


if [ $installok == "false" ] ; then
  exit 1
fi


