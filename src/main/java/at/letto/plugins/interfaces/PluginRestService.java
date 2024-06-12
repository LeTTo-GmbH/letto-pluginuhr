package at.letto.plugins.interfaces;

import at.letto.plugins.dto.PluginGeneralInfo;

/**
 * Plugin-Schnittstelle als Rest-Service
 */
public interface PluginRestService {

    /** @param typ Typ des Plugins
     *  @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin */
    PluginGeneralInfo getPluginGeneralInfo(String typ);


}
