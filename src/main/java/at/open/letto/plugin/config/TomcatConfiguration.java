package at.open.letto.plugin.config;

import lombok.Getter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Konfiguration des Servers für einen zusätzlichen AJP und HTTP-Connector
 */
@Configuration
public class TomcatConfiguration {

    private MicroServiceConfiguration microServiceConfiguration;
    @Getter private int httpPort;
    @Getter private int ajpPort;

    public TomcatConfiguration(MicroServiceConfiguration microServiceConfiguration) {
        this.microServiceConfiguration = microServiceConfiguration;
        this.httpPort  = 8080;
        this.ajpPort   = 7080;
    }

    // --------------------------------------------------------------------------
    //              Configuriere HTTP-Server Port
    // --------------------------------------------------------------------------
    @Configuration
    public class CustomContainer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        public void customize(ConfigurableServletWebServerFactory factory){
            factory.setPort(httpPort);
        }
    }

    // --------------------------------------------------------------------------
    //              Configuriere HTTPS-Server Port und AJP
    // --------------------------------------------------------------------------
    @Bean
    public ServletWebServerFactory servletHTTPContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(
                microServiceConfiguration.createAjpConnector(ajpPort));
        return tomcat;
    }

    // --------------------------------------------------------------------------
    //              Configuriere Messages für die Spracheinstellung
    // --------------------------------------------------------------------------
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setCacheSeconds(10); //reload messages every 10 seconds
        return messageSource;
    }

}
