package at.letto.login.dto.servertoken;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserTokenRequest {

    /** Servertoken welcher die Serververbindung freigibt */
    private String serverToken;

    /** Usertoken des Benutzers am Fremdserver für den Gegencheck ob der User auch wirklich ok ist */
    private String userToken;

    /** Sprache welche im User-Token eingetragen werden soll */
    private String language;

    /** Backlink wenn auf den Fremdserver zurückgesprungen werden soll */
    private String backlink;

    /** bei True wird ein TempToken, ansonsten ein normaler Token erzeugt */
    private boolean tempToken;

    /** Fingerprint des Browsers mit dem zugegriffen wird */
    private String fingerprint="";

    /** IP-Adresse des Browsers mit dem zugegriffen wird */
    private String ipaddress="";

}
