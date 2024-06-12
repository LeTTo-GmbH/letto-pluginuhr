#!/bin/bash

rev="$(head revision.txt -n1|sed 's/^ *//'|sed 's/[\n\r ]//g')"
pwd=$(pwd)

cd $pwd
mvn install -DinteractiveMode=false -DskipTests -Dmaven.test.skip=true
if ! [ $? -eq 0 ] ; then echo "Modul: $modul - Fehler - Abbruch!"; exit 1; fi
