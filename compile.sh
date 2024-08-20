#!/bin/bash

rev="$(head revision.txt -n1|sed 's/^ *//'|sed 's/[\n\r ]//g')"
pwd=$(pwd)

cd $pwd
mvn javadoc:javadoc

rm src/main/resources/static/pluginuhr/open/javadoc -rf
mkdir src/main/resources/static/pluginuhr/open/javadoc -p
cp -r target/site/apidocs/* src/main/resources/static/pluginuhr/open/javadoc

mvn install -DinteractiveMode=false -DskipTests -Dmaven.test.skip=true
if ! [ $? -eq 0 ] ; then echo "Modul: $modul - Fehler - Abbruch!"; exit 1; fi

if ! [ $? -eq 0 ] ; then echo "Modul: $modul - JAVADOC-Fehler!"; fi
