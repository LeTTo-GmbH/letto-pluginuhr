package at.letto.basespringboot.security;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.basespringboot.service.BaseJwtTokenService;
import at.letto.databaseclient.service.BaseLettoRedisDBService;
import at.letto.login.restclient.RestLoginService;
import at.letto.security.LettoToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * LeTTo Basis JWT-Token-Authentifikation
 */
@Component
public class JwtTokenService{

    /** Secret welches auch jedem weitern Service bekannt sein muss, welches den Token erhält */
    @Getter @Setter private String secret;
    /** Lebensdauer eines Tokens */
    @Getter @Setter private long expiration;
    /** Login-Service Client*/
    @Getter @Setter private RestLoginService restLoginService;
    /** Verbindung zum Redis-Server */
    @Autowired      private BaseLettoRedisDBService baseLettoRedisDBService;

    /** Macht aus einem Tokenstring einen LettoToken */
    public LettoToken toLettoToken(String token) {
        //FIXME Werner Token aus Redis holen oder Token validieren am Login-Service
        return new LettoToken(token,secret);
    }

    /**
     * Erzeugt einen neuen Token
     * @param username  Benutzername
     * @param idSchule  id der Schule am Lizenzserver
     * @param roles     erzeuge mit Arrays.asList("a","b")
     * @return neuer Token
     */
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
    }

}
