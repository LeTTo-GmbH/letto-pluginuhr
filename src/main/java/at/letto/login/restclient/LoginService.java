package at.letto.login.restclient;

import at.letto.login.dto.TokenInfoResponseDto;
import at.letto.login.dto.TokenLoginResult;
import at.letto.login.dto.TokenValidationResult;
import at.letto.login.dto.message.MessageDto;
import at.letto.login.dto.servertoken.*;
import at.letto.login.endpoints.LoginEndpoint;
import at.letto.security.LettoToken;
import java.util.HashMap;

/**
 * Service für die Authentifikation von Benutzern
 */
public interface LoginService {

    /**
     * Prüft Benutzernamen und Passwort über das Loginservice
     * @param username  Benutzername
     * @param password  Passwort unverschlüsselt
     * @param school    Schulkennung welche auch in der URL verwendet wird (eindeutig am Server)
     * @param fingerprint  Fingerabdruck des Users (z.B. Fingerabdruck des Smartphones)
     * @param ipaddress  IP-Adresse des Users
     * @param service    Service welcher die Authentifizierung anfordert, z.B. "letto-login", "letto-edit", "letto-admin" etc.
     * @param infos      zusätzliche Informationen über den Client, wer, was, wo, warum
     * @param userAgent  User-Agent des Clients, z.B. "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
     * @return          gültiger token als String oder null
     */
    String jwtLogin(String username, String password, String school, String fingerprint, String ipaddress, String service, String infos, String userAgent);

    /**
     * Prüft Benutzernamen und Passwort über das Loginservice und liefert einen LettoToken zurück
     * @param username  Benutzername
     * @param password  Passwort unverschlüsselt
     * @param school    Schulkennung welche auch in der URL verwendet wird (eindeutig am Server)
     * @param fingerprint  Fingerabdruck des Users (z.B. Fingerabdruck des Smartphones)
     * @param ipaddress  IP-Adresse des Users
     * @param service    Service welcher die Authentifizierung anfordert, z.B. "letto-login", "letto-edit", "letto-admin" etc.
     * @param infos      zusätzliche Informationen über den Client, wer, was, wo, warum
     * @param userAgent  User-Agent des Clients, z.B. "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
     * @return          gültiger LettoToken oder null
     */
    TokenLoginResult jwtLettoLogin(String username, String password, String school, String fingerprint, String ipaddress, String service, String infos, String userAgent);

    /**
     * Führt einen Logout des Tokens durch und vernichtet den Token im Token-Store - danach ist kein Token-Refresh dieses Tokens mehr möglich!
     * @param token      Token der ausgeloggt werden soll
     * @return           true wenn der logout erfolgreich war, sonst false
     */
    boolean jwtLogout(String token);

    /**
     * Entfernt alle Tokens des Benutzers sodass kein Token-Refresh für diese Tokens mehr möglich ist!
     * @param token        Token des Benutzers(Lehrer,Admin) der den Benutzer username ausloggen möchte
     * @param username     Benutzername
     * @param school       Schulenkennung welche auch in der URL verwendet wird (eindeutig am Server)
     * @return             true wenn der logout erfolgreich war, sonst false
     */
    boolean logout(String token, String username, String school);

    /**
     * Überprüft die Gültigkeit eines Tokens
     * @param token      Token der geprüft werden muss
     * @return           true wenn der Token gültig ist, sonst false
     */
    TokenValidationResult jwtValidate(String token, String fingerprint);

    /**
     * Liefert einen kompletten LettoToken aus einem Tokenstring wenn der Token gültig ist
     * @param token     Token der geprüft werden muss
     * @return          gültiger LeTTo-Token oder null
     */
    TokenLoginResult lettoTokenFromTokenString(String token);

    /**
     * Aktualisiert einen gültigen Token
     * @param lettoToken Token der aktualisiert werden muss
     * @return           gültiger LeTTo-Token oder null
     */
    @Deprecated
    TokenLoginResult jwtRefresh(LettoToken lettoToken);

    /**
     * Aktualisiert einen gültigen Token
     * @param token      Token der aktualisiert werden muss
     * @return           gültiger JWT-Token oder null
     */
    @Deprecated
    String jwtRefresh(String token);

    /**
     * Aktualisiert einen gültigen Token
     * @param lettoToken Token der aktualisiert werden muss
     * @return           gültiger LeTTo-Token oder null
     */
    TokenLoginResult jwtRefresh(LettoToken lettoToken, String fingerprint);

    /**
     * Aktualisiert einen gültigen Token
     * @param token      Token der aktualisiert werden muss
     * @return           gültiger JWT-Token oder null
     */
    String jwtRefresh(String token, String fingerprint);

    /**
     * Liefert einen gültigen JWT-Token aus einem gültigen TempToken
     * @param tempToken       tempToken der über Get-mitgeschickt wurde
     * @param serverSecret    gemeinsames Secret des Servers ( Nicht das JWT-Secret!!)
     * @return          gültiger JWT-Token
     */
    String jwtTokenFromTempToken(String tempToken, String serverSecret);

    /**
     * @param lettoToken gültiger Token
     * @return Liefert eine URI mit einem temporären Token zur Weiterleitung an LeTTo
     */
    String jwtGetTempTokenUri(LettoToken lettoToken);

    /**
     * Liefert aus einem gültigen Token einen Temptoken für einen neuen refreshten Token
     * @param lettoToken  gültiger Token
     * @return            Temptoken
     */
    String jwtGetTempToken(LettoToken lettoToken);

    /**
     * Liefert aus einem gültigen Token einen Temptoken für einen neuen refreshten Token
     * @param token       gültiger Token
     * @return            Temptoken
     */
    String jwtGetTempToken(String token);

    /**
     * @param token gültiger Token
     * @return Liefert eine URI mit einem temporären Token zur Weiterleitung an LeTTo
     */
    String jwtGetTempTokenUri(String token);

    /** Liefert Information über den Token
     * @param token      aktueller Token
     * @return           Information über den Token */
    TokenInfoResponseDto tokenInfo(String token);

    /** Liefert einen neuen Token um eine Alias-Rolle annehmen zu können
     * @param token      aktueller Token
     * @param username   neuer Benutzername in der gleichen Schule für den Alias-Benutzer
     * @return           gültiger JWT-Token oder null*/
    String getAliasToken(String token, String username);

    /**
     * Schickt Benutzername, Passwort und Schule an den Endpoint und liefert ok wenn das Passwort dem Temp-Passwort entspricht
     * ENDPOINT : LoginEndpoint.templogin
     * @param username       Benutzername
     * @param tempPassword   temporäres Passwort
     * @param school         Schulkürzel
     * @param fingerprint  Fingerabdruck des Users (z.B. Fingerabdruck des Smartphones)
     * @param ipaddress  IP-Adresse des Users
     * @return               true, wenn das Passwort ok ist, ansonsten false
     */
    boolean tempLogin(String username, String tempPassword, String school, String fingerprint, String ipaddress);

    /**
     * Setzt ein neues Benutzerpasswort eines Users an einer Schule und liefert ok wenn das Passwort gesetzt wurde
     * ENDPOINT : LoginEndpoint.setpassword
     * @param username       Benutzername
     * @param school         Schulkürzel
     * @param oldPassword    altes Passwort
     * @param newPassword    neues Passwort
     * @param tempPassword   true wenn das alte Passwort das Temp-Passwort ist, false wenn es das aktuelle normale Passwort ist
     * @return               true wenn das Passwort korrekt gesetzt werden konnte
     */
    boolean setPassword(String username, String school, String oldPassword, String newPassword, boolean tempPassword);

    /**
     * Erzeugt einen neuen Servertoken mit dem Login-Service entsprechend den Angaben im Request<br>
     * Endpoint ist User-Authentificatet als User admin
     * @param request Konfigration des neuen ServerTokens
     * @return        neuen Servertoken als String
     */
    String getServerToken(GetServerTokenRequest request);

    /**
     * Liefert eine Liste aller Token die erzeugt wurden und noch gültig sind
     * @return  Liste der Tokenstrings
     */
    ServerTokenListDto getServerTokenList();

    /**
     * deaktiviert einen bestehenden Token indem er von der Liste gelöscht wird.
     * @param tokenID     Token-ID des Token
     * @return            true wenn es funktioniert hat
     */
    boolean removeServerToken(long tokenID);

    /**
     * deaktiviert einen bestehenden Token durch eine Markierung .
     * @param tokenID     Token-ID des Token
     * @return            true wenn es funktioniert hat
     */
    boolean deactivateServerToken(long tokenID);

    /**
     * aktiviert einen bestehenden Token .
     * @param tokenID     Token-ID des Token
     * @return            true wenn es funktioniert hat
     */
    boolean activateServerToken(long tokenID);


    /**
     * Liefert wichtige Informationen zu einem gültigen ServerToken
     * @param tokenID     Token-ID des Token
     * @return            Informationen über den Token
     */
    GeneratedServerToken loadServerToken(long tokenID);

    /**
     * aktualisiert eine bestehenden Servertoken
     * @param oldTokenString alter Servertoken
     * @return               neuer Servertoken oder Leerstring wenn nicht erfolgreich
     */
    String refreshServerToken(String oldTokenString);

    /**
     * Liefert wichtige Informationen zu einem gültigen ServerToken
     * @param serverTokenString  ServerToken
     * @return                   Informationen über den Token
     */
    HashMap<String,String> serverTokenInfo(String serverTokenString);

    /**
     * Erzeugt einen neuen UserToken auf dem Remote-Server mit dem auf den Remotserver zugegriffen werden kann
     * @param serverTokenString  ServerToken für die Verbindung zum Fremdserver
     * @param userTokenFremd     lokaler User-Token
     * @param language           Sprache welche verwendet werden soll
     * @param backlink           Link welcher zurück zum Fremdserver führt
     * @param tempToken          true wenn ein TempToken statt einem UserToken erzeugt werden soll
     * @param fingerprint        Fingerabdruck des Users (z.B. Fingerabdruck des Smartphones)
     * @param ipaddress          IP-Adresse des Users
     * @return                   UserToken oder Temptoken
     */
    String getUserToken(String serverTokenString, String userTokenFremd, String language, String backlink, boolean tempToken, String fingerprint, String ipaddress);

    /**
     * Erzeugt einen neuen UserToken auf dem Remote-Server mit dem auf den Remotserver zugegriffen werden kann
     * @param userTokenRequestDto  ServerToken für die Verbindung zum Fremdserver und Benutzerdaten
     * @return UserToken auf dem Remote-Server
     */
    String getUserTokenDirect(UserTokenRequestDto userTokenRequestDto);

    public boolean pingStudent(String token);

    public boolean pingTeacher(String token);

    public boolean pingAdmin(String token);

    /**
     * Generiert eine Nachricht an ein Service welche in der REDIS-Datenbank gespeichert wird<br>
     * @param sender     Kennung des Senders
     * @param receiver   Kennung des Empfängers
     * @param topic      Thema der Nachricht
     * @param message    Nachricht als Objekt welches als JSON gespeichert wird!!
     * @param lifetimeSeconds   Lebensdauer der Nachricht in Sekunden bis sie gelöscht wird
     * @param single     true wenn die Nachricht nur einmal abgeholt werden kann und dann sofort gelöscht wird
     * @param messageSecret Secret damit nur Services eine Nachricht senden können welche das Secret kennen.
     * @return           Kennung der Nachricht als String welcher auch als get-Parameter verwendet werden kann
     */
    String createMessage(String sender, String receiver, String topic, Object message, long lifetimeSeconds, boolean single, String messageSecret);

    /** Lädt eine Nachricht aus der REDIS-Datenbank und löscht falls sie single ist sofort <br>
     * @param messageID   Kennung der Nachricht
     * @return            Nachricht als Object oder null wenn die Nachricht nicht existiert
     * */
    public MessageDto getMessage(String messageID);

}