package at.letto.login.restclient;

import at.letto.login.dto.AuthenticationRequest;
import at.letto.login.dto.SetPasswordRequest;
import at.letto.login.dto.TokenInfoResponseDto;
import at.letto.login.dto.servertoken.*;
import at.letto.login.endpoints.LoginEndpoint;
import at.letto.security.LettoToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;

/**
 * Service für die Authentifikation von Benutzern
 */
public interface LoginService {

    /**
     * Prüft Benutzernamen und Passwort über das Loginservice und checkt den gelieferten Token mit dem Secret
     * @param username  Benutzername
     * @param password  Passwort unverschlüsselt
     * @param school    Schulkennung welche auch in der URL verwendet wird (eindeutig am Server)
     * @param jwtsecret gemeinsames Secret für den JWT-Token
     * @return          gültiger LeTTo-Token oder null
     */
    LettoToken jwtLogin(String username, String password, String school, String jwtsecret);

    /**
     * Prüft Benutzernamen und Passwort über das Loginservice und checkt den gelieferten Token mit dem Secret
     * @param username  Benutzername
     * @param password  Passwort unverschlüsselt
     * @param school    Schulkennung welche auch in der URL verwendet wird (eindeutig am Server)
     * @return          gültiger token als String oder null
     */
    String jwtLogin(String username, String password, String school);

    /**
     * Aktualisiert einen gültigen Token
     * @param lettoToken      Token der aktualisiert werden muss
     * @param jwtsecret  gemeinsames jwtsecret für den JWT-Token
     * @return           gültiger LeTTo-Token oder null
     */
    LettoToken jwtRefresh(LettoToken lettoToken, String jwtsecret);

    /**
     * Aktualisiert einen gültigen Token
     * @param token      Token der aktualisiert werden muss
     * @return           gültiger JWT-Token oder null
     */
    String jwtRefresh(String token);

    /**
     * Liefert einen gültigen JWT-Token aus einem gültigen TempToken
     * @param tempToken       tempToken der über Get-mitgeschickt wurde
     * @param serverSecret    gemeinsames Secret des Servers ( Nicht das JWT-Secret!!)
     * @return          gültiger JWT-Token
     */
    String jwtTokenFromTempToken(String tempToken, String serverSecret);

    /**
     * @param lettoToken gültiger Token
     * @param jwtsecret  gemeinsames jwtsecret für den JWT-Token
     * @return Liefert eine URI mit einem temporären Token zur Weiterleitung an LeTTo
     */
    String jwtGetTempTokenUri(LettoToken lettoToken, String jwtsecret);

    /**
     * Liefert aus einem gültigen Token einen Temptoken für einen neuen refreshten Token
     * @param lettoToken  gültiger Token
     * @return            Temptoken
     */
    String jwtGetTempToken(LettoToken lettoToken, String jwtsecret);

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
     * @return               true, wenn das Passwort ok ist, ansonsten false
     */
    boolean tempLogin(String username, String tempPassword, String school);

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
     * @return                   UserToken oder Temptoken
     */
    String getUserToken(String serverTokenString, String userTokenFremd, String language, String backlink, boolean tempToken);

    /**
     * Erzeugt einen neuen UserToken auf dem Remote-Server mit dem auf den Remotserver zugegriffen werden kann
     * @param userTokenRequestDto  ServerToken für die Verbindung zum Fremdserver und Benutzerdaten
     * @return
     */
    String getUserTokenDirect(UserTokenRequestDto userTokenRequestDto);

}
