package at.letto.setup.dto.config;

import at.letto.service.microservice.AdminInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service welches im Setup-Service registriert ist
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConfigServiceDto {

    /** Name des Services */
    private String  name;

    /** Version des Services */
    private String  version="";

    /** Information über den Autor des Services */
    private String  author="";

    /** Information über die Lizenz des Services */
    private String  license="";

    /** Betriebssystem auf dem das Service läuft */
    private String  bs="";

    /** IP des Services */
    private String  ip="";

    /** Zeichen-Encoding */
    private String  encoding="";

    /** Programmiersprache in der das Service Programmiert wurde */
    private String  programmingLanguage="";

    /** Adresse innerhalb des Docker-Netzwerkes nw-letto, wenn das Service dort direkt erreichbar ist */
    private String  nwLettoAddress;

    /** Name des Docker-Containers, dieser muss eindeutig sein!! <br>
     *  Bei externen Services auf anderen Servcern gibt es keinen dockerName, dann muss die
     *  externe URI eindeutig sein */
    private String  dockerName;

    /** interne URI mit der auf das Service ohne Authentifizierung zugegriffen werden kann.<br>
     *  die URI muss protokoll://adresse:port/basisendpunkt enthalten woran dann die Standard-Plugin-Endpoints
     *  angehängt werden.<br>
     *  Ist die uriIntern nicht gesetzt dann wird wenn extern=true ist auf der uriExtern verbunden. <br>
     *  Läuft das Service also auf einem Fremdserver muss Benutzername und Passwort angegeben sein
     *  um sich am Fremdserver zu authentifizieren oder alle Endpunkte müssen offen sein.
     * */
    private String  uriIntern;

    /** Service ist von Extern (Browser) direkt erreichbar */
    private boolean extern;

    /** externe URI mit der vom Browser auf das Service zugegriffen werden kann (wenn extern=true)  <br>
     *  Hier muss die gesamte absolute Basis-URI angegeben werden unter der die Plugin-Endpoints liegen
     *  */
    private String  uriExtern;

    /** Gibt an ob es sich bei dem Service um ein Plugin handelt */
    private boolean plugin;

    /** Gibt an ob das Service skalierbar (mehrfach vorkommen kann) ist */
    private boolean scalable;

    /** Gibt an ob das Service nur Stateless-Endpoints hat */
    private boolean stateless;

    /** Benutzername wenn das Service mit einer User-Authentifizierung am Plugin anmelden muss */
    private String  username;

    /** Passwort wenn das Service mit einer User-Authentifizierung am Plugin anmelden muss */
    private String  password;

    /** Wenn hier true steht, dann muss für das Plugin ein Token verwendet werden,
     *  der in der Schule gespeichert ist. Dieser Token muss für die Authentifizierung
     *  am Plugin verwendet werden. - Ist noch nicht implementiert.
     */
    private boolean usePluginToken=false;

    /** Datum und Uhrzeit an der das Service gestartet wurde als DateInteger */
    private long    serviceStartTime;

    /** Datum und Uhrzeit der letzten Service-Registratur */
    private long    lastRegistrationTime;

    /** zusätzliche nicht weiter definierte Parameter des Plugins */
    private HashMap<String,String> params;

}
