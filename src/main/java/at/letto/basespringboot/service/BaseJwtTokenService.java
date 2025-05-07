package at.letto.basespringboot.service;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.security.LettoToken;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Basis für die LeTTo Basis JWT-Token-Authentifikation<br>
 * Extende davon im JwtTokenService des jeweiligen Microservices
 */
public class BaseJwtTokenService {

    /** Secret welches auch jedem weitern Service bekannt sein muss, welches den Token erhält */
    @Getter @Setter private String secret;
    /** Lebensdauer eines Tokens */
    @Getter @Setter private long expiration;


//    public BaseJwtTokenService(String secret, long jwtExpiration) {
//        this.secret = secret;
//        this.expiration = jwtExpiration;
//    }

    public LettoToken toLettoToken(String token) {
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