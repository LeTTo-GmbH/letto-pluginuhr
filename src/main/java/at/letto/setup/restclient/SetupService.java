package at.letto.setup.restclient;

import at.letto.setup.dto.ServiceSchuleDto;
import at.letto.setup.dto.ServiceStatusDto;
import at.letto.setup.dto.config.*;
import java.util.List;

public interface SetupService {

    /** @return Liefert alle Schulen die an dem Server installiert sind */
    List<ServiceSchuleDto> getSchulen();

    /**
     * @param school Schul-Kurzname
     * @return       Liefert die Schul-Konfiguration, wenn sie existiert
     */
    ServiceSchuleDto getSchule(String school);

    /** @return liefert den RestKey des Servers */
    String getRestKey();

    /**
     * Setzt die Lizenz einer Schule welche in den Konfigurationsdateien des Servers eingetragen wird
     * @param school  Kurzname der Schule mit der die Schule am Server registriert ist
     * @param restkey Restkey des Servers, wie er in der Schuldatenbank eingetagen ist
     * @param license Lizenz der Schule
     * @return Fehlermeldung wenn etwas nicht funktioniert hat
     */
    String setSchoolLicense(String school, String restkey, String license);

    /** Liefert den Servicestatus der verbundenen Services am Server */
    ServiceStatusDto checkServiceStatus();

    /** Liefert den Servicestatus der verbundenen Services wenn das Setup-Service am Host läuft */
    ServiceStatusDto checkServiceStatusLocal();

    /** @return Liefert eine Liste aller am Server registrierten Plugins */
    ConfigServicesDto getPlugins();

    /** @return Liefert eine Liste aller am Server registrierten Services */
    ConfigServicesDto getServices();

    /** registriert ein Plugin am Setup-Service */
    RegisterServiceResultDto registerPlugin(ConfigServiceDto configPluginDto);

    /** registriert ein Plugin am Setup-Service */
    RegisterServiceResultDto registerService(ConfigServiceDto configServiceDto);

    /** @return Liefert die Daten des angeforderten Services */
    ConfigServiceDto getService(String service) ;

    /** @return Liefert die notwendigen Verbindungsinformationen zu einer Schule */
    ConfigServiceDto getSchuleService(String schule);

}
