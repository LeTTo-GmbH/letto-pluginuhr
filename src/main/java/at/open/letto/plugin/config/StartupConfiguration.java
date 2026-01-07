package at.open.letto.plugin.config;

import at.letto.login.restclient.RestLoginService;
import at.letto.setup.restclient.RestSetupService;
import at.open.letto.plugin.service.ConnectionService;
import at.open.letto.plugin.service.LettoService;
import org.springframework.context.annotation.Configuration;

/**
 * Wird beim Systemstart nach den Constructoren der Services geladen und darf dann in keinem Service injiziert werden!!!
 * Wird gestartet bevor der die Filter-Chain des Webservers und der TomEE gestartet werden!
 */
@Configuration
public class StartupConfiguration {

    private final LettoService lettoService;
    private final LoggingConfiguration loggingConfiguration;
    private final MicroServiceConfiguration microServiceConfiguration;
    private final ConnectionService connectionService;

    public StartupConfiguration(LettoService lettoService,
                                LoggingConfiguration loggingConfiguration,
                                MicroServiceConfiguration microServiceConfiguration,
                                ConnectionService connectionService) {
        this.lettoService = lettoService;
        this.loggingConfiguration = loggingConfiguration;
        this.microServiceConfiguration = microServiceConfiguration;
        this.connectionService = connectionService;
        // Verbindung zu Setup und Login checken
        RestSetupService setupService = lettoService.getSetupService();
        RestLoginService loginService = lettoService.getLoginService();
        // Plugin intern verbinden

        // Das Plugin wird im Component PluginRegistrationOnRead am Setup registriert
        // hier bitte nicht: connectionService.registerPlugin();
    }
}
