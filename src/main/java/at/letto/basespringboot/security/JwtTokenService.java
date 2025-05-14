package at.letto.basespringboot.security;

import at.letto.databaseclient.service.BaseLettoRedisDBService;
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
    @Getter @Setter private boolean useSecret       = false;

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
                lettoToken = restLoginService.lettoTokenFromTokenString(token);
                if (lettoToken != null) {
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

    /**
     * Erzeugt einen neuen Token - Darf nur vom Login-Service verwendet werden!!
     * @param username  Benutzername
     * @param idSchule  id der Schule am Lizenzserver
     * @param roles     erzeuge mit Arrays.asList("a","b")
     * @return neuer Token

    public LettoToken generateLettoToken(String username,
                                         String  vorname,
                                         String  nachname,
                                         String  activDirectoryname,
                                         String  email,
                                         String  sprache,
                                         Integer id,
                                         Integer idSchule,
                                         String  school,
                                         String  lettoUri,
                                         String  serverRestkey,
                                         String  fingerprint,
                                         List<String> roles) {
        LettoToken lettoToken = new LettoToken(
                secret,
                expiration,
                username,
                vorname,
                nachname,
                activDirectoryname,
                email,
                sprache,
                id,
                idSchule,
                school,
                lettoUri,
                serverRestkey,
                fingerprint,
                roles
        );
        // Token im Redis-Server speichern
        baseLettoRedisDBService.putToken(lettoToken);
        return lettoToken;
    }

    public LettoToken refreshToken(String token) {
        LettoToken lettoToken = new LettoToken(token, secret);
        if (!lettoToken.isValid() || !lettoToken.isTokenNotExpired()) return null;
        LettoToken newToken = generateLettoToken(
                lettoToken.getUsername(),
                lettoToken.getVorname(),
                lettoToken.getNachname(),
                lettoToken.getActiveDirectoryName(),
                lettoToken.getEmail(),
                lettoToken.getSprache(),
                lettoToken.getId(),
                lettoToken.getIdSchule(),
                lettoToken.getSchool(),
                lettoToken.getLettoUri(),
                lettoToken.getServerRestkey(),
                lettoToken.getFingerprint(),
                lettoToken.getRoles()
        );
        return newToken;
    }

    public boolean tokenValidation(String token) {
        LettoToken lettoToken = new LettoToken(token, secret);
        return lettoToken.isValid();
    } */

}
