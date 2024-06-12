package at.open.letto.plugin.config;

import at.letto.basespringboot.config.BaseLoggingConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration extends BaseLoggingConfiguration {

    private MicroServiceConfiguration microServiceConfiguration;

    public LoggingConfiguration(MicroServiceConfiguration microServiceConfiguration) {

    }

}
