package at.letto.login.dto.servertoken;

import lombok.*;

/** Informationen über den User am Fremdserver und der ServerToken */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenRequestDto {

    /** Servertoken welcher die Serververbindung freigibt */
    private String serverToken;

    /** Benutzername des Benutzers am Fremdserver */
    private String username;

    /** Vorname des Benutzters */
    private String vorname;

    /** Nachname des Benutzers */
    private String nachname;

    /** email des Benutzers */
    private String email;

    /** Schulkürzel des Benutzers am Fremdserver */
    private String school;

    /** Sprache welche im User-Token eingetragen werden soll */
    private String language;

    /** Backlink wenn auf den Fremdserver zurückgesprungen werden soll */
    private String backlink;

    /** bei True wird ein TempToken, ansonsten ein normaler Token erzeugt */
    private boolean tempToken;

    /** Fingerprint des Browsers mit dem zugegriffen wird */
    private String fingerprint="";

}
