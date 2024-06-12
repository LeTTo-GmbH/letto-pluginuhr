package at.open.letto.plugin.config;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.basespringboot.security.WebSecurityConfig;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicroServiceConfiguration extends BaseMicroServiceConfiguration {

    @Value("${servername:}")
    @Getter
    private String servername;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Getter private Endpoint endpoints;

    @PostConstruct
    public void init() {
        endpoints = new Endpoint();
        webSecurityConfig.init(this, endpoints);
    }

}
