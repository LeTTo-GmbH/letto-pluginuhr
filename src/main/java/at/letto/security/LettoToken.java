package at.letto.security;

import at.letto.tools.ENCRYPT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

/**
 * Ein JWT-Token welcher in LeTTo verwendet wird als Objekt mit Methoden zur
 * Analyse des Tokens
 */
public class LettoToken {

    /** globaler Administrator */
    public static final String ROLE_GLOBAL          = "global";

    /** Externer Benutzer */
    public static final String ROLE_EXTERN          = "extern";

    /** Wird für Abos benutzt - MAYT fragen wozu */
    public static final String ROLE_CHANGE_ABOS     = "changeabos";

    /** mehrfacher Login zulässig */
    public static final String ROLE_MULTIPLE_LOGIN  = "multiplelogin";

    /** zahlender Student für LeTTo-Private */
    public static final String ROLE_PAYINGSTUDENT   = "payingstudent";

    /** Student der Schule */
    public static final String ROLE_STUDENT         = "student";

    /** Lehrer der Schule */
    public static final String ROLE_TEACHER         = "teacher";

    /** Administrator der Schule */
    public static final String ROLE_ADMIN           = "admin";

    /** Benutzer ist deaktiviert */
    public static final String ROLE_DISABLED        = "disabled";

    /** MAYT fragen */
    public static final String ROLE_USER_ABOS_CATEGORY  = "useraboscategory";

    /** MAYT fragen */
    public static final String ROLE_USER_ABOS_USER      = "userabosusers";

    /** nur das aktuelle Schuljahr darf benutzt werden */
    public static final String ROLE_USE_CURRENT_YEAR    = "usecurrentyear";

    /** Prefix für den Benutzernamen des Originalbenutzers wenn sich ein Lehrer/Admin als anderer User verkleidet hat */
    public static final String ROLE_PREFIX_ORIGINUSER   = "originuser";

    /** Rolle für die Authentifizierung eines Server-Tokens, der an Lehrer/Admins anderer Schulen weitergegeben wird */
    public static final String ROLE_SERVER              = "server";

    /** ein Externer Benutzer darf nur dann eine User-Category anlegen, wenn ROLE_CREATE_CATEGORY aktiv ist */
    public static final String ROLE_CREATE_CATEGORY     = "createcategory";

    // JWT-Token als String
    @Getter private  final String token;
    // Secret welches für die Prüfung des Tokens verwendet wird
    private final String secret;
    //private final Key keyJwt;

    // Gibt an ob ein Token angelegt (false) oder empfangen und geprüft wird (true)
    private final boolean created;
    // Claims
    private Claims claims = null;
    // JWT
    private Jwt jwt = null;

    private String checkSecret(String secret) {
        if (secret==null || secret.length()==0)
            secret = SecurityConstants.JWT_SECRET;
        if (secret.length()<72) {
            while (secret.length()<72) secret += secret;
            secret= ENCRYPT.base64Encode(secret);
        }
        return secret;
    }

    public LettoToken(String token, String secret) {
        this.token   = token;
        this.secret=checkSecret(secret);
        this.created = false;
        try {
            this.claims = getAllClaimsFromToken();
        } catch (Exception ex) {

        }
    }

    public LettoToken(String  secret,
                      Long    expiration,
                      String  username,
                      String  vorname,
                      String  nachname,
                      String  activDirectoryname,
                      String  email,
                      String  sprache,
                      Integer idUser,
                      Integer idSchule,
                      String  school,
                      String  lettoUri,
                      String  serverRestkey,
                      List<String> roles) {
        this(  secret, SecurityConstants.TOKEN_ISSUER, SecurityConstants.TOKEN_AUDIENCE,
                expiration,username,vorname,nachname,activDirectoryname,
                email,sprache,idUser,idSchule,school,lettoUri,serverRestkey,roles
        );
    }

    public LettoToken(String  secret,
                      String  issuer,
                      String  audience,
                      Long    expiration,
                      String  username,
                      String  vorname,
                      String  nachname,
                      String  activDirectoryname,
                      String  email,
                      String  sprache,
                      Integer idUser,
                      Integer idSchule,
                      String  school,
                      String  lettoUri,
                      String  serverRestkey,
                      List<String> roles) {
        this(secret, issuer, audience, expiration, username, vorname, nachname, activDirectoryname, email,
             sprache, idUser, idSchule, school, lettoUri, serverRestkey, roles, null);
    }

    public LettoToken(String  secret,
                      String  issuer,
                      String  audience,
                      Long    expiration,
                      String  username,
                      String  vorname,
                      String  nachname,
                      String  activDirectoryname,
                      String  email,
                      String  sprache,
                      Integer idUser,
                      Integer idSchule,
                      String  school,
                      String  lettoUri,
                      String  serverRestkey,
                      List<String> roles,
                      HashMap<String,String> payload) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, expiration);
        this.secret=checkSecret(secret);
        this.created = true;
        this.token   = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                .setIssuer(issuer)
                .setAudience(audience)
                .signWith(SignatureAlgorithm.HS512, this.secret)
                .claim("idschule",idSchule)
                .claim("roles",roles)
                .claim("vorname",vorname)
                .claim("nachname",nachname)
                .claim("ADname",activDirectoryname)
                .claim("email",email)
                .claim("sprache",sprache)
                .claim("id",idUser)
                .claim("school",school)
                .claim("lettoUri",lettoUri)
                .claim("serverRestkey",serverRestkey)
                .claim("payload",payload)
                .compact();
        this.claims = getAllClaimsFromToken();
    }

    /**
     * @param secret     JWT-Secret
     * @param expiration Lebensdauer in ms
     * @return Liefert einen neuen Token mit neuer Gültigkeitsdauer und sonst gleichen Daten */
    public LettoToken refreshToken(String secret, long expiration) {
        LettoToken newLettoToken = new LettoToken(
            secret,
            getIssuer(),
            getAudience(),
            expiration,
            getUsername(),
            getVorname(),
            getNachname(),
            getActiveDirectoryName(),
            getEmail(),
            getSprache(),
            getIdUser(),
            getIdSchule(),
            getSchool(),
            getLettoUri(),
            getServerRestkey(),
            getRoles(),
            getPayload()
        );
        return newLettoToken;
    }

    @Override
    public String toString() {
        return this.getToken();
    }

    private Date calculateExpirationDate(Date createdDate, Long expiration) {
        if (expiration==null || expiration<1) expiration = SecurityConstants.EXPIRATION_TIME;
        return new Date(createdDate.getTime() + expiration);
    }

    /** @return gibt an wie lange der Token noch gültig ist */
    public long getValidMillis() {
        try {
            long ms = getExpirationDate().getTime() - (new Date()).getTime();
            return ms;
        } catch (Exception ex) {}
        return -1;
    }

    /** @return Liefert den Benutzernamen welcher im Token gespeichert wurde */
    public String getUsername() {
        try {
            return claims.getSubject();
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert das Datum an dem der Token abläuft */
    public Date getExpirationDate() {
        try {
            return claims.getExpiration();
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert das Datum an dem der Token erzeugt wurde */
    public Date getCreatedDate() {
        try {
            return claims.getIssuedAt();
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert die id eines Users  */
    public Integer getIdUser() {
        return getId();
    }

    /** @return Liefert die id der Schule  */
    public Integer getIdSchule() {
        try {
            return claims.get("idschule", Integer.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert die id des Users  */
    public Integer getId() {
        try {
            return claims.get("id", Integer.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Vornamen des Users  */
    public String getVorname() {
        try {
            return claims.get("vorname", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Nachnamen des Users  */
    public String getNachname() {
        try {
            return claims.get("nachname", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Activ-Directory-Namen des Users  */
    public String getActiveDirectoryName() {
        try {
            return claims.get("ADname", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert die Email-Adresse des Users  */
    public String getEmail() {
        try {
            return claims.get("email", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert die Spracheinstellung des Users  */
    public String getSprache() {
        try {
            return claims.get("sprache", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Kurznamen der Schule welcher auch in der URI zur Identifikation verwendet wird */
    public String getSchool() {
        try {
            return claims.get("school", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert die URI der Schule */
    public String getLettoUri() {
        try {
            return claims.get("lettoUri", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Restkey des Servers */
    public String getServerRestkey() {
        try {
            return claims.get("serverRestkey", String.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Erzeuger des Tokens  */
    public String getIssuer() {
        try {
            return claims.getIssuer();
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert den Zuständigkeitsbereich des Tokens */
    public String getAudience() {
        try {
            return claims.getAudience();
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert eine Liste aller Rollen des Benutzers */
    public List<String> getRoles() {
        try {
            return claims.get("roles", List.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert eine Hashmap des Payloads */
    public HashMap<String,String> getPayload() {
        try {
            return claims.get("payload", HashMap.class);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert eine Hashmap eines Wertes des Payloads */
    public String getPayload(String key) {
        try {
            return getPayload().get(key);
        } catch (Exception ex) {}
        return null;
    }

    /** @return Liefert alle Rollen des Benutzers als Array von Strings */
    public String[] getRolesArray() {
        List<String> roles = getRoles();
        if (roles!=null) {
            String[] ret = new String[roles.size()];
            for (int i=0;i<roles.size();i++)
                ret[i] = roles.get(i);
            return ret;
        }
        return new String[0];
    }

    private <T> T getClaimFromToken(Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken();
        return claimsResolver.apply(claims);
    }



    private Claims getAllClaimsFromToken() {
        if (claims==null)
            claims = Jwts.parserBuilder().setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        return claims;
    }

    /** @return Liefert eine Java-Web-Token-Darstellung des LeTTo-Tokens */
    public Jwt getJwt() {
        if (jwt==null)
//            jwt = Jwts.parser().setSigningKey(secret).parse(token);
             jwt = Jwts.parserBuilder().setSigningKey(secret).build().parse(token);
        return jwt;
    }

    /** @return gibt an ob der Token noch gültig ist */
    public boolean isTokenNotExpired() {
        try {
            final Date expiration = getExpirationDate();
            return expiration.after(new Date());
        } catch (Exception ex) {}
        return false;
    }

    /** @return gibt an ob der Token gültige Inhalte hat, aber nicht ob er noch nicht abgelaufen ist ( siehe isTokenNotExpired ) */
    public boolean isValid() {
        return claims!=null;
    }

    /** @return true wenn der Token noch nicht abgelaufen ist */
    public Optional<Boolean> validateToken() {
        return  isTokenNotExpired() ? Optional.of(Boolean.TRUE) : Optional.empty();
    }

    /**
     * Prüft ob der Token eine angegebenen Rolle hat
     * @param role Rolle die geprüft werden soll
     * @return     true wenn der Token der Rolle entspricht
     */
    public boolean hasRole(String role) {
        for (String r : getRolesArray()) {
            if (r.trim().equalsIgnoreCase(role.trim()))
                return true;
        }
        return false;
    }

    /** @return Wenn der Token ein Alias-Token ist, der Benutzername welcher sich ursrpünglich eingeloggt hat, andernfalls ein Leerstring. */
    public String getOriginUser() {
        for (String r : getRolesArray()) {
            if (r.trim().startsWith(ROLE_PREFIX_ORIGINUSER))
                return r.trim().substring(ROLE_PREFIX_ORIGINUSER.length());
        }
        return "";
    }
    /** @return true wenn der Benutzer nicht disabled ist und die Admin-Rolle hat */
    public boolean isAdmin()         { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_ADMIN); }
    /** @return true wenn der Benutzer nicht disabled ist und die Global-Rolle hat */
    public boolean isGlobal()        { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_GLOBAL); }
    /** @return true wenn der Benutzer nicht disabled ist und die Teacher-Rolle hat */
    public boolean isTeacher()       { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_TEACHER); }
    /** @return true wenn der Benutzer nicht disabled ist und die Student-Rolle hat */
    public boolean isStudent()       { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_STUDENT); }
    /** @return true wenn der Benutzer nicht disabled ist und die Paying-Student-Rolle hat */
    public boolean isPayingStudent() { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_PAYINGSTUDENT); }
    /** @return true wenn der Benutzer nicht disabled ist und die Multiple-Login-Rolle hat */
    public boolean isMultipleLogin() { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_MULTIPLE_LOGIN); }
    /** @return true wenn der Benutzer nicht disabled ist und die Extern-Rolle hat */
    public boolean isExtern()        { return !hasRole(ROLE_DISABLED) && hasRole(ROLE_EXTERN); }
    /** @return true wenn es sich um einen Alias-Benutzer handelt - d.h. wenn sich ein Lehrer oder Admin als anderer User verkleidet hat */
    public boolean isAlias()         { return getOriginUser().length()>0; }

}
