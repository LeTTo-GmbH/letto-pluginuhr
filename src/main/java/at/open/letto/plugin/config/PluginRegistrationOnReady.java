package at.open.letto.plugin.config;

import at.open.letto.plugin.service.ConnectionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PluginRegistrationOnReady {

    private final ConnectionService connectionService;

    public PluginRegistrationOnReady(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Hier wird das Plugin am Setup-Service registriert
     */
    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        connectionService.registerPlugin();
    }

}