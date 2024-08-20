# LeTTo Plugin Development Kit
## Demo Plugin "Uhr"
* Demo Plugin für LeTTo als einfache Uhr mit Analoganzeige und JS-Eingabe.
* Das Uhr Plugin ist fertig gebildet im LeTTo-Setup-Service installierbar.
* Weiters ist das Plugin als Sourcecode verfügbar der für eigene Plugins als Basis verwendet werden kann.
* URL: https://github.com/LeTTo-GmbH/letto-pluginuhr.git

## LeTTo Plugin-Tester
* Der Plugin-Tester ist direkt über das Setup-Service auf einem LeTTo-Server installierbar
* dockerhub: https://hub.docker.com/repository/docker/lettohub/letto-service-plugintester/general

# Build des Pluginservices
* Download der Sourcecode-Dateien von Git
* Build des Moduls plugin mit Maven-Build anhand der pom.xml
* Build des Docker-Containers anhand des Dockerfiles

# Funktionsweise der Plugin-Integration
* Das Plugin muss als Docker-Container gestartet werden (docker-service-pluginuhr.yml)
* Der Docker-Container muss im Reverse-Proxy eine Konfiguration für öffentliche Endpoints haben (pluginuhr.conf)
* Beim Start des Plugin-Docker-Containers muss sich das Plugin am Setup-Service (/config) registrieren und dort die Url und Grunddaten eintragen (StartupConfiguration.java - connectionService.registerPlugin();)
* Fremdplugins von externen Servern können (in Zukunft - jetzt noch nicht) im Setup-Service auch händisch hinzugefügt werden.
* LeTTo holt sich alle registrierten Plugins immer von Setup-Service welches notwendigerweise laufen muss.
* Jegliche Kommunikation zwischen LeTTo und dem Plugin erfolgt über REST-Endpoints welche im Demoplugin beschrieben sind (https://servername/pluginuhr/swagger-ui/index.html)

# Installation des Pluginservices
## Installation auf einem Entwicklungsrechner
* Installation einer Docker-Installation von LeTTo auf dem Entwicklungsrechner
* Installation des Plugin-Tester-Services
* kopieren der docker-service-pluginuhr.yml in das Verzeichnis
  /opt/letto/docker/compose/letto/
* Anlegen einer proxy-Konfiguration für einen öffentlichen Endpoint des Services mit einer .conf-Datei in /opt/letto/docker/proxy/ (siehe pluginuhr.conf) . Diese proxy-Konfiguration muss mit der yml-Datei zusammenpassen!
* falls noch nicht gebildet - Build des Pluginservices
* start des Pluginservices mit docker compose oder über das Setup-Service
* Plugin-Tester im Browser starten https://localhost/plugintester

# Dokumentation
## REST-Schnittstelle
* Swagger-Doku des Uhr Plugins https://build.letto.at/pluginuhr/swagger-ui/index.html
* Swagger-Doku des Setup-Services https://build.letto.at/config/swagger-ui/index.html
## Sourcecode
* Die notwendigen Controller für die REST-Kommunikation findet man im Verzeichnis **src/main/java/at/open/letto/plugin/controller**
 * info-controller: allgemeine Information welche jedes Service liefern muss (von extern erreichbar)
 * base-info-controller: allgemeine Information welche jedes Service liefern muss (nur aus dem Docker-Netzwerk erreichbar)
 * ping-controller: interner Ping für health-check (nur aus dem Docker-Netzwerk erreichbar)
 * api-extern-open-controller: offene Endpoints welche von extern erreichbar sein müssen für ajax und allgemeine Informationen (von extern erreichbar)
 * api-controller: die eigentliche Plugin-Schnittstelle zwischen LeTTo und Plugin (nur aus dem Docker-Netzwerk erreichbar)
 * api-extern-controller: für eine gesicherte Verbindung von einem externen LeTTo-Server wenn Plugin und LeTTo nicht auf dem gleichen Server liegen (noch nicht fertig implementiert) jedoch gleiche Funktion wie api-controller (von extern erreichbar)
* Den Rest-Client für die Registratur am Setup-Service findet man in der Klasse RestSetupService.class (Methode registerPlugin()) im Verzeichnis src/main/java/at/letto/setup/restclient
 * Im Uhr Plugin erledigt die Registrator das ConnectionService mit der Methode registerPluigin()
* DTO: Die Datentransfer-JSON-Klassen für die REST-Schnittstelle als dokumentierte Java-Klassen findet man im Verzeichnis **src/main/java/at/letto/plugins/**
