package at.letto.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatusDto {

    /** Lizenzserver vom Host aus erreichbar */
    private boolean lettoLicenseHost=false;

    /** Login Service vom Host aus erreichbar */
    private boolean loginHost=false;

    /** Setup Service Docker  vom Host aus erreichbar */
    private boolean setupHost=false;

    /** Lizenzserver vom Docker-Container aus erreichbar */
    private boolean lettoLicense=false;

    /** Login Service vom Docker-Container aus erreichbar */
    private boolean login=false;

    /** Setup Service des Host vom Docker-Setup aus erreichbar */
    private boolean setup=false;

    /** Data-Services vom Docker-Setup aus erreichbar */
    private HashMap<String,Boolean> data = new HashMap<>();

    /** LTI-Service läuft */
    private boolean lti=false;

    /** Image-Service läuft */
    private boolean image=false;

    /** Question-Service läuft */
    private boolean question=false;

    /** Mail-Service läuft */
    private boolean mail=false;

    /** Allgemeines Plugin-Service läuft */
    private boolean plugin=false;

    /** Plugin-Sourcecode-Service läuft */
    private boolean pluginsourcecode=false;

    /** Mathe-Service läuft */
    private boolean math=false;

    /** Print-Service läuft */
    private boolean print=false;

    /** Export- Service läuft */
    private boolean export=false;

    /** Edit-Service läuft */
    private boolean edit=false;

    /** Main-Service läuft */
    private boolean main=false;

    /** alle notwendigen Services der Version 1.2 sind installiert als docker-service-...yml */
    private boolean v12ymlpresent=false;

    /** alle notwendigen Services der Version 1.2 laufen korrekt */
    private boolean v12servicesOk=false;

    /** services installiert */
    private HashMap<String,Boolean> serviceInstalled = new HashMap<>();

    /** services laufen korrekt */
    private HashMap<String,Boolean> serviceOk = new HashMap<>();

}
