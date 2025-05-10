package at.letto.security;

import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LettoTokenDto {

    /** Der Token als String */
    private String  token;
    /** Der Token als JWT-WebToken */
    private Jwt jwt;
    /** Ersteller des Tokens */
    private String  issuer;
    /** Token für welche Anwendung der Token gültig ist */
    private String  audience;
    /** Zeitpunkt an dem der Token ausgestellt wurde */
    private Date    issuedAt;
    /** Zeitpunkt ab dem der Token nicht mehr gültig ist */
    private Date    expirationDate;
    /** Benutzername */
    private String  username;
    /** Vorname */
    private String  vorname;
    /** Nachname */
    private String  nachname;
    /** Name des Benutzers am Active Directory */
    private String  activDirectoryname;
    /** E-Mail Adresse */
    private String  email;
    /** Sprache der Oberfläche für den Benutzer */
    private String  sprache;
    /** ID des Benutzers */
    private Integer idUser;
    /** ID der Schule */
    private Integer idSchule;
    /** Kurzezeichnung der Schule welche auch in der URL und der Datenbank verwendet wird */
    private String  school;
    /** URI des LeTTo-Servers */
    private String  lettoUri;
    /** RestKey des Servers */
    private String  serverRestkey;
    /** Fingerprint des Browsers auf dem der Benutzer arbeitet */
    private String  fingerprint;
    /** Liste der Rollen des Benutzers */
    private List<String> roles;
    /** sonstige Nutzdaten welche über den Token mitgesendet werden */
    private HashMap<String,String> payload;

    public LettoTokenDto(LettoToken lettoToken) {
        this.token = lettoToken.getToken();
        this.issuer = lettoToken.getIssuer();
        this.audience = lettoToken.getAudience();
        this.issuedAt = lettoToken.getCreatedDate();
        this.expirationDate = lettoToken.getExpirationDate();
        this.username = lettoToken.getUsername();
        this.vorname = lettoToken.getVorname();
        this.nachname = lettoToken.getNachname();
        this.activDirectoryname = lettoToken.getActiveDirectoryName();
        this.email = lettoToken.getEmail();
        this.sprache = lettoToken.getSprache();
        this.idUser = lettoToken.getIdUser();
        this.idSchule = lettoToken.getIdSchule();
        this.school = lettoToken.getSchool();
        this.lettoUri = lettoToken.getLettoUri();
        this.serverRestkey = lettoToken.getServerRestkey();
        this.fingerprint = lettoToken.getFingerprint();
        this.roles = lettoToken.getRoles();
    }

    public LettoTokenDto(String secret, String token) {
        this(new LettoToken(secret, token));
    }

}
