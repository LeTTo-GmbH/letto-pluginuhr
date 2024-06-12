package at.letto.service.interfaces;

import at.letto.dto.ServiceInfoDTO;
import at.letto.service.microservice.AdminInfoDto;

/**
 * Methoden welche ein jedes Microservice realisieren sollte
 */
public interface MicroService {

    /** Schickt eine Ping an das Service */
    public boolean ping();

    /** Liefert die Version des Microservices als String */
    public String version();

    /** Liefert einen allgemeinen Informationsstring zu dem Microservice */
    public String info();

    /** Liefert Information über das Rest-Service */
    public AdminInfoDto admininfo();

}
