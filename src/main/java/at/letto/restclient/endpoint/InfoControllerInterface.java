package at.letto.restclient.endpoint;

import at.letto.dto.ServiceInfoDTO;
import at.letto.service.microservice.AdminInfoDto;
import org.springframework.context.ApplicationContext;

import java.util.jar.Manifest;

public interface InfoControllerInterface {

    /**
     * Setzt die notwendigen Werte im serviceInfoDTO
     * @param serviceInfoDTO hier werden die Werte gesetzt
     * @param admin          true wenn die Info für den Admin erzeugt wird
     */
    void setInfo(ServiceInfoDTO serviceInfoDTO, boolean admin);

    /**
     * Setzt die Ports im AdminInfoDto
     * @param adminInfoDto hier werden die Werte gesetzt
     */
    void setInfo(AdminInfoDto adminInfoDto);

    /** Liefert den Context der laufenden Anwendung */
    ApplicationContext getContext();

    /** Liefert das Manifest des verwendeten Webservers */
    Manifest getManifest();

    /** Liefert die Hauptklasse des Programmes in dem der Springboot-Server gestartet wird */
    Class getMainClass();

}
