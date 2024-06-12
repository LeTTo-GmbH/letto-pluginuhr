package at.open.letto.plugin.service;

import at.letto.login.restclient.RestLoginService;
import at.letto.setup.restclient.RestSetupService;
import at.open.letto.plugin.config.MicroServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Verbindungen zu allen anderen Microservices der LeTTo Installation
 */
@Component
public class LettoService {

    private Logger logger = LoggerFactory.getLogger(LettoService.class);

    @Autowired private MicroServiceConfiguration microServiceConfiguration;

    private RestLoginService loginService = null;
    private RestSetupService setupService = null;

    public RestLoginService getLoginService() {
        if (loginService==null) {
            try {
                //Loginservice starten
                loginService = new RestLoginService(
                    microServiceConfiguration.getLoginServiceUri()
                );
                if (loginService.ping()==false)
                    logger.error("Loginservice mit Ping nicht erreichbar auf "+loginService.getBaseURI());
                else
                    logger.info("Loginservice connected at "+loginService.getBaseURI());
            }catch(Exception ex) {
                logger.error("Loginservice kann nicht geladen werden von "+microServiceConfiguration.getLoginServiceUri());
            }
        }
        return loginService;
    }

    public RestSetupService getSetupService() {
        if (setupService==null) {
            try {
                //Setupservice starten
                setupService = new RestSetupService(
                    microServiceConfiguration.getSetupServiceUri(),
                    "user",
                    microServiceConfiguration.getUserUserPassword()
                );
                if (setupService.ping()==false)
                    logger.error("Setupservice mit Ping nicht erreichbar!!");
                else
                    logger.info("Setupservice connected!");
            }catch(Exception ex) {
                logger.error("Setupservice kann nicht geladen werden von "+
                        microServiceConfiguration.getSetupServiceUri());
            }
        }
        return setupService;
    }

}
