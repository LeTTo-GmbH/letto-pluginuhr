#!/bin/bash

# ----------------------------------------------------------------------------------
# Parameter verarbeiten
# ----------------------------------------------------------------------------------
rev="$(head revision.txt -n1|sed 's/^ *//'|sed 's/[\n\r ]//g')"
tag=rev$rev
stable=off
daily=off
beta=off
version=
# Aufruf: deploy.sh rev=$rev tag=$tag stable=$stable daily=$daily beta=$beta version=$VERSION
for i in $@ ; do  
  if   [[ $i == "rev="* ]] ; then
    rev=${i:4}
  fi  
  if   [[ $i == "tag="* ]] ; then
    tag=${i:4}
  fi
  if   [[ $i == "version="* ]] ; then
    version=${i:8}
  fi
  for j in {daily,stable,beta} ; do
    if [ $i = "$j=off" ] ; then eval $j=off ; fi
    if [ $i = "$j" ]     ; then eval $j=on  ; fi
    if [ $i = "$j=on" ]  ; then eval $j=on  ; fi
  done
done

pwd=$(pwd)

finishok=true

if [[ $VERSION == "1.3" ]] ; then
  echo LeTTo Version 1.3
  # Docker Container am Build-Server neu starten!
  if [[ $daily == "on" || $stable == "on" || $beta == "on" ]] ; then
    echo restart daily-Version on build-server
    cd /opt/letto/docker/compose/letto

    ymlfile="docker-service-pluginuhr.yml"
    docker compose -f $ymlfile down

    # yml-Datei anpassen !
    downloadfile="/download/install-1.3/yml/$ymlfile"
    if [[ -s "$downloadfile" ]]; then
      # cp $downloadfile $ymlfile
      ./replacetag.sh $ymlfile $downloadfile
    else
      echo "Error: File '$downloadfile' does not exist or is empty."
    fi

    docker compose -f $ymlfile up -d

  fi
fi

if [ $finishok == "false" ] ; then
  exit 1
fi


