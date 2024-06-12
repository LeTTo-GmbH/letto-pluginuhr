package at.open.letto.plugin.service;

import at.letto.plugins.restclient.BasePluginConnectionService;

public class PluginConnectionServiceIntern  extends BasePluginConnectionService {

    /** registriert alle Plugins welche mit diesem Service implementiert sind */
    public PluginConnectionServiceIntern() {
        registerPlugin("Uhr", "at.open.letto.plugins.uhr.PluginUhr");
    }

}
