package at.letto.basespringboot.security;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.databaseclient.service.BaseLettoRedisDBService;
import at.letto.login.restclient.RestLoginService;
import at.letto.security.LettoToken;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    @Autowired private JwtTokenService jwtService;
    /*@Autowired public JwtAuthenticationProvider(JwtTokenService jwtService) {
        this.jwtService = jwtService;
    }*/
    @Autowired private BaseMicroServiceConfiguration baseMicroServiceConfiguration;

    /**
     * Initializes the JWT service with the secret and expiration time.     *
     * @param secret          The secret key used for signing the JWT.
     * @param jwtExpiration   The expiration time for the JWT in milliseconds.
     */
    public void init(String secret, long jwtExpiration, RestLoginService restLoginService) {
        jwtService.setExpiration(jwtExpiration);
        jwtService.setSecret(secret);
        jwtService.setRestLoginService(restLoginService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String token = (String) authentication.getCredentials();
            LettoToken lettoToken = jwtService.toLettoToken(token);
            String username = lettoToken.getUsername();

            return lettoToken.validateToken()
                    .map(aBoolean -> new JwtAuthenticatedProfile(username))
                    .orElseThrow(() -> new JwtAuthenticationException("JWT Token validation failed"));

        } catch (JwtException ex) {
            log.error(String.format("Invalid JWT Token: %s", ex.getMessage()));
            throw new JwtAuthenticationException("Failed to verify token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.equals(authentication);
    }
}