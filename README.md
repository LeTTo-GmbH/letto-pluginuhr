# LeTTo Plugin Development Kit
* URL: https://github.com/LeTTo-GmbH/letto-plugin-development-kit.git

Demo Plugin für LeTTo als einfache Uhr mit Analoganzeige und JS-Eingabe.

# LeTTo Plugin-Tester
* Der Plugin-Tester ist direkt über das Setup-Service auf einem LeTTo-Server installierbar
* dockerhub: https://hub.docker.com/repository/docker/lettohub/letto-service-plugintester/general

# Build des Pluginservices
* Download der Sourcecode-Dateien von Git
* Build des Moduls plugin mit Maven-Build anhand der pom.xml
* Build des Docker-Containers anhand des Dockerfiles

# Installation des Pluginservices
## Installation auf einem Entwicklungsrechner
* Installation einer Docker-Installation von LeTTo auf dem Entwicklungsrechner
* kopieren der docker-service-pluginuhr.yml in das Verzeichnis
  /opt/letto/docker/compose/letto/
* falls noch nicht gebildet - Build des Pluginservices
* start des Pluginservices mit docker compose oder über das config-Service

# 
