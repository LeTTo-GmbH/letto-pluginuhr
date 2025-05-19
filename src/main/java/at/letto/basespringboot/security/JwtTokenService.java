package at.letto.basespringboot.security;

import at.letto.databaseclient.service.BaseLettoRedisDBService;
import at.letto.login.dto.TokenLoginResult;
import at.letto.login.restclient.RestLoginService;
import at.letto.security.LettoToken;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * LeTTo Basis JWT-Token-Authentifikation
 */
@Component
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    /** Secret welches auch jedem weitern Service bekannt sein muss, welches den Token erhält */
    @Getter @Setter private String secret;
    /** Lebensdauer eines Tokens */
    @Getter @Setter private long expiration;
    /** Login-Service Client*/
    @Getter @Setter private RestLoginService restLoginService;
    /** Verbindung zum Redis-Server */
    @Autowired      private BaseLettoRedisDBService baseLettoRedisDBService;

    @Getter @Setter private boolean useRedis        = true;
    @Getter @Setter private boolean useLoginService = true;
    //FIXME Werner: Standardwert muss wieder auf false gesetzt werden wenn alles auf Stable umgestellt ist!!
    @Getter @Setter private boolean useSecret       = true;

    /** Macht aus einem Tokenstring einen LettoToken */
    public LettoToken toLettoToken(String token) {
        LettoToken lettoToken;
        if (useRedis) {
            try {
                // Suche den Token im Redis-Server
                lettoToken = baseLettoRedisDBService.getToken(token);
                if (lettoToken != null) {
                    // Token im Redis-Server gefunden
                    return lettoToken;
                }
            } catch (Exception e) {
                // Token nicht im Redis-Server
            }
        }
        if (useLoginService) {
            try {
                // Suche den Token im Login-Service
                TokenLoginResult result = restLoginService.lettoTokenFromTokenString(token);
                if (result!=null && result.getLettoToken() != null) {
                    lettoToken = result.getLettoToken();
                    // Token im Login-Service gefunden
                    baseLettoRedisDBService.putToken(lettoToken);
                    return lettoToken;
                }
            } catch (Exception e) {
                // Token nicht über das Login-Service validierbar
            }
        }
        if (useSecret) {
            lettoToken = new LettoToken(token, secret);
            return lettoToken;
        }
        log.info("Token not valid: "+token);
        return null;
    }

}
