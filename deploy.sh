#!/bin/bash

# ----------------------------------------------------------------------------------
# Parameter verarbeiten
# ----------------------------------------------------------------------------------
rev="$(head revision.txt -n1|sed 's/^ *//'|sed 's/[\n\r ]//g')"
tag=rev$rev
stable=off
daily=off
beta=off
# Aufruf: deploy.sh rev=$rev tag=$tag stable=$stable daily=$daily beta=$beta
for i in $@ ; do  
  if   [[ $i == "rev="* ]] ; then
    rev=${i:4}
  fi  
  if   [[ $i == "tag="* ]] ; then
    tag=${i:4}
  fi 
  for j in {daily,stable,beta} ; do
    if [ $i = "$j=off" ] ; then eval $j=off ; fi
    if [ $i = "$j" ]     ; then eval $j=on  ; fi
    if [ $i = "$j=on" ]  ; then eval $j=on  ; fi
  done
done

pwd=$(pwd)

deployok=true

binary=pluginuhr-1.2.jar
# pluginservice
if [ -e $pwd/target/$binary ] ; then
  cp $pwd/target/$binary /deploy/pluginuhr.jar
else
  echo error ! binary $binary not found!
  deployok=false
fi

if [ $deployok == "false" ] ; then
  exit 1
fi


