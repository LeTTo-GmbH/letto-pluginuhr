package at.letto.basespringboot.security;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.basespringboot.service.BaseJwtTokenService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService extends BaseJwtTokenService {

    public JwtTokenService() {
        super();
    }

}
