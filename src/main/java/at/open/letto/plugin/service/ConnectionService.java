package at.open.letto.plugin.service;

import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.plugins.restclient.BasePluginConnectionService;
import at.letto.plugins.restclient.BasePluginManagerService;
import at.letto.plugins.restclient.PluginConnectionService;
import at.letto.setup.dto.config.ConfigServiceDto;
import at.letto.setup.dto.config.RegisterServiceResultDto;
import at.letto.setup.restclient.RestSetupService;
import at.letto.tools.Datum;
import at.letto.tools.ServerStatus;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.config.MicroServiceConfiguration;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;

@Service
public class ConnectionService {

    private Logger logger = LoggerFactory.getLogger(ConnectionService.class);
    public BasePluginManagerService pm;

    private final MicroServiceConfiguration microServiceConfiguration;
    private final LettoService lettoService;
    private final ApplicationContext applicationContext;

    /** Server-interne URI welche an das Setup-service übergeben wird */
    @Getter private String uriIntern;
    /** Basis-URI ohne Endpoint für den externen Zugriff*/
    @Getter private String baseUriExtern;
    /** externe URI welche an das Setup-service übergeben wird */
    @Getter private String uriExtern;
    /** externe URI für das PluginDto */
    @Getter private String uriGetPluginDto;

    public ConnectionService(MicroServiceConfiguration microServiceConfiguration, LettoService lettoService, ApplicationContext applicationContext) {
        this.microServiceConfiguration = microServiceConfiguration;
        this.lettoService = lettoService;
        this.applicationContext = applicationContext;
        // Plugin-Manager starten
        pm = new BasePluginManagerService();
        // Plugin-Connection starten
        PluginConnectionService pIntern    = new PluginConnectionServiceIntern();
        // Plugin-Connection mit Manager verbinden
        pm.registerPluginConnectionService(pIntern);

        // Interne Uri welche ans Setup weitergegeben wird bestimmen
        uriIntern = System.getenv("letto.plugin.uri.intern");
        while (uriIntern!=null && uriIntern.endsWith("/")) uriIntern=uriIntern.substring(0,uriIntern.length()-1);
        if (uriIntern==null || uriIntern.trim().length()==0)
            uriIntern = "http://letto-pluginuhr.nw-letto:8080";
        uriIntern = uriIntern + Endpoint.LOCAL_API;

        // externe Uri welche ans Setup weitergegeben wird bestimmen
        baseUriExtern = System.getenv("letto.plugin.uri.extern");
        while (baseUriExtern!=null && baseUriExtern.endsWith("/")) baseUriExtern=baseUriExtern.substring(0,baseUriExtern.length()-1);
        if (baseUriExtern==null || baseUriExtern.trim().length()==0)
            baseUriExtern = "https://"+microServiceConfiguration.getServername();
        uriExtern = baseUriExtern + Endpoint.EXTERN_API;
        uriGetPluginDto = baseUriExtern + Endpoint.EXTERN_OPEN+ PluginConnectionEndpoint.reloadPluginDto;
    }

    /**
     * registriert das Plugin am Setup-Service, damit es verwendet werden kann.
     */
    public void registerPlugin() {
        RestSetupService setupService = lettoService.getSetupService();
        if (setupService==null) {
            logger.error("cannot connect to Setup-Service - Plugin is not registered!");
            return;
        }
        HashMap<String,String> params = new HashMap<>();

        ConfigServiceDto configServiceDto = new ConfigServiceDto(
                "Uhr",
                "1.0",
                "LeTTo GmbH",
                "opensource",
                ServerStatus.getBetriebssystem(),
                ServerStatus.getIP(),
                ServerStatus.getEncoding(),
                ServerStatus.getJavaVersion(),
                "letto-pluginuhr",
                "letto-pluginuhr",
                uriIntern,
                true,
                uriExtern,
                true,
                false,
                true,
                "user",
                microServiceConfiguration.getUserUserPassword(),
                false,
                Datum.toDateInteger(new Date(applicationContext.getStartupDate())),
                Datum.nowDateInteger(),
                params
        );
        RegisterServiceResultDto result = setupService.registerPlugin(configServiceDto);
        if (result==null) {
            logger.error("setup service cannot be reached at "+setupService.getBaseURI());
        } else if (!result.isRegistrationOK()) {
            logger.error("setup service cannot register this plugin! -> "+result.getMsg());
        } else {
            int count = result.getRegistrationCounter();
            boolean isnew = result.isNewRegistered();
            logger.info("Plugin registered in setup-Service "+
                        (isnew?"NEW":"UPDATED")+" "+
                        (count>1?", "+count+" instances":""));
        }
    }

    public BasePluginConnectionService getPluginConnectionService(String typ) {
        PluginConnectionService pluginConnectionService = pm.getPluginConnectionService(typ);
        if (pluginConnectionService instanceof BasePluginConnectionService) {
            return (BasePluginConnectionService) pluginConnectionService;
        }
        return null;
    }

}
