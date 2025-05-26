package at.letto.login.restclient;

import at.letto.login.dto.*;
import at.letto.login.dto.message.MessageDto;
import at.letto.login.dto.message.PutMessageDto;
import at.letto.login.dto.servertoken.*;
import at.letto.login.endpoints.LoginEndpoint;
import at.letto.security.LettoToken;
import at.letto.service.microservice.AdminInfoDto;
import at.letto.service.rest.RestClient;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Service für den Login-Vorgang
public class RestLoginService extends RestClient implements LoginService {

    /**
     * Erzeugt ein REST-Client Verbindung zu einem Microservice
     *
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091
     */
    public RestLoginService(String baseURI) {
        super(baseURI);
    }

    public RestLoginService(String baseURI, String user, String password) {
        super(baseURI,user,password);
    }

    @Override
    public boolean ping() {
        return ping(LoginEndpoint.ping,1000);
    }

    @Override
    public String version() {
        String rev = get(LoginEndpoint.version,String.class);
        return rev;
    }

    @Override
    public String info() {
        String rev = get(LoginEndpoint.info,String.class);
        return rev;
    }

    @Override
    public AdminInfoDto admininfo() {
        AdminInfoDto rev = get(LoginEndpoint.admininfo,AdminInfoDto.class);
        return rev;
    }

    @Override
    public String jwtLogin(String username, String password, String school, String fingerprint, String ipaddress) {
        AuthenticationRequest request = new AuthenticationRequest(username, password, school, fingerprint, ipaddress);
        TokenLoginResult response = post(LoginEndpoint.jwtlettologin,request,TokenLoginResult.class);
        if (response!=null) {
            if (response.getLettoToken().getToken()!=null)
                return response.getLettoToken().getToken();
            return null;
        }
        // Fallback auf die alte JWT-Login-Methode ohne Fingerprint und IP-Adresse
        AuthenticationRequestOld requestOld = new AuthenticationRequestOld(username, password, school);
        JWTTokenResponse jwtresponse = post(LoginEndpoint.jwtlogin,request,JWTTokenResponse.class);
        if (jwtresponse!=null && jwtresponse.getToken()!=null)
            return jwtresponse.getToken();
        return null;
    }

    @Override
    public TokenLoginResult jwtLettoLogin(String username, String password, String school, String fingerprint, String ipaddress) {
        AuthenticationRequest request = new AuthenticationRequest(username, password, school, fingerprint, ipaddress);
        TokenLoginResult response = post(LoginEndpoint.jwtlettologin,request,TokenLoginResult.class);
        if (response!=null)
            return response;
        // Fallback auf die alte JWT-Login-Methode ohne Fingerprint und IP-Adresse
        AuthenticationRequestOld requestOld = new AuthenticationRequestOld(username, password, school);
        JWTTokenResponse jwtresponse = post(LoginEndpoint.jwtlogin,request,JWTTokenResponse.class);
        if (jwtresponse!=null && jwtresponse.getToken()!=null) {
            return new TokenLoginResult(new LettoToken(jwtresponse.getToken()), "Login successful", LOGINSTATUS.SUCCESS);
        }
        return new TokenLoginResult(null, "Login failed", LOGINSTATUS.FAILURE);
    }

    @Deprecated
    public TokenLoginResult jwtRefresh(LettoToken lettoToken) {
        TokenLoginResult response = get(LoginEndpoint.lettotokenrefresh,TokenLoginResult.class, lettoToken.getToken());
        return response;
    }

    @Deprecated
    public String jwtRefresh(String token) {
        JWTTokenResponse response = post(LoginEndpoint.jwtrefresh,token,JWTTokenResponse.class, token);
        if (response!=null && response.getToken()!=null)
            return response.getToken();
        return null;
    }

    @Override
    public String jwtRefresh(String token, String fingerprint) {
        TokenLoginResult response = post(LoginEndpoint.lettotokenrefresh, fingerprint, TokenLoginResult.class, token);
        if (response!=null) {
            if (response.getLettoToken()!=null)
                return response.getLettoToken().getToken();
            return null;
        }
        JWTTokenResponse jwtresponse = post(LoginEndpoint.jwtrefresh, token, JWTTokenResponse.class, token);
        if (jwtresponse!=null && jwtresponse.getToken()!=null)
            return jwtresponse.getToken();
        return null;
    }

    @Override
    public TokenLoginResult jwtRefresh(LettoToken lettoToken, String fingerprint) {
        TokenLoginResult response = post(LoginEndpoint.lettotokenrefresh, fingerprint, TokenLoginResult.class, lettoToken.getToken());
        if (response!=null) {
            return response;
        }
        JWTTokenResponse jwtresponse = post(LoginEndpoint.jwtrefresh, lettoToken.getToken(), JWTTokenResponse.class, lettoToken.getToken());
        if (jwtresponse!=null && jwtresponse.getToken()!=null)
            return new TokenLoginResult(new LettoToken(jwtresponse.getToken()),"Login successful",LOGINSTATUS.SUCCESS);
        return new TokenLoginResult(null,"Refresh failed",LOGINSTATUS.FAILURE);
    }

    @Override
    public boolean jwtLogout(String token) {
        String response = post(LoginEndpoint.jwtlogout,token,String.class, token);
        if (response!=null && response.equals("true"))
            return true;
        return false;
    }

    @Override
    public boolean logout(String token, String username, String school) {
        AuthenticationRequest request = new AuthenticationRequest(username, "", school, "","");
        String response = post(LoginEndpoint.userlogout,request,String.class, token);
        if (response!=null && response.equals("true"))
            return true;
        return false;
    }

    @Override
    public TokenValidationResult jwtValidate(String token, String fingerprint) {
        TokenValidationResult result = post(LoginEndpoint.jwtvalidate,fingerprint,TokenValidationResult.class, token);
        return result;
    }

    @Override
    public TokenLoginResult lettoTokenFromTokenString(String token) {
        TokenLoginResult response = get(LoginEndpoint.jwtgetlettotoken,TokenLoginResult.class, token);
        return response;
    }

    @Override
    public String jwtGetTempTokenUri(LettoToken lettoToken) {
        String response = post(LoginEndpoint.jwtgettemptokenuri,lettoToken.getToken(),String.class, lettoToken);
        return response;
    }

    @Override
    public String jwtGetTempToken(LettoToken lettoToken) {
        String response = post(LoginEndpoint.jwtgettemptoken,lettoToken.getToken(),String.class, lettoToken);
        return response;
    }

    @Override
    public String jwtGetTempTokenUri(String token) {
        String response = post(LoginEndpoint.jwtgettemptokenuri,token,String.class, token);
        return response;
    }

    @Override
    public TokenInfoResponseDto tokenInfo(String token) {
        TokenInfoResponseDto response = get(LoginEndpoint.tokeninfo,TokenInfoResponseDto.class, token);
        return response;
    }

    @Override
    public String getAliasToken(String token, String username) {
        String response = post(LoginEndpoint.getaliastoken,username,String.class, token);
        return response;
    }

    @Override
    public boolean tempLogin(String username, String tempPassword, String school, String fingerprint, String ipaddress) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, tempPassword, school, fingerprint, ipaddress);
        String response = post(LoginEndpoint.templogin,authenticationRequest,String.class);
        return response.equalsIgnoreCase("ok");
    }

    @Override
    public boolean setPassword(String username, String school, String oldPassword, String newPassword, boolean tempPassword) {
        SetPasswordRequest setPasswordRequest = new SetPasswordRequest(username, school, oldPassword, newPassword, tempPassword);
        String response = post(LoginEndpoint.setpassword,setPasswordRequest,String.class);
        return response.equalsIgnoreCase("ok");
    }

    @Override
    public String jwtTokenFromTempToken(String tempToken, String serverSecret) {
        TempTokenRequest request = new TempTokenRequest(serverSecret, tempToken);
        JWTTokenResponse response = post(LoginEndpoint.jwttemptoken,request,JWTTokenResponse.class);
        if (response!=null && response.getToken()!=null)
            return response.getToken();
        return null;
    }

    @Override
    public boolean pingStudent(String token) {
        String pong = post(LoginEndpoint.pingstudent,"ping",String.class, token);
        if (pong!=null && pong.equals("pong")) return true;
        return false;
    }

    @Override
    public boolean pingTeacher(String token) {
        String pong = post(LoginEndpoint.pingteacher,"ping",String.class, token);
        if (pong!=null && pong.equals("pong")) return true;
        return false;
    }

    @Override
    public boolean pingAdmin(String token)   {
        String pong = post(LoginEndpoint.pingadmin,"ping",String.class, token);
        if (pong!=null && pong.equals("pong")) return true;
        return false;
    }

    @Override
    public String getServerToken(GetServerTokenRequest request){
        String response = post(LoginEndpoint.getServerToken,request,String.class);
        return response;
    }

    @Override
    public ServerTokenListDto getServerTokenList() {
        ServerTokenListDto response = get(LoginEndpoint.serverTokenList, ServerTokenListDto.class);
        return response;
    }

    @Override
    public boolean removeServerToken(long tokenID) {
        boolean response = post(LoginEndpoint.removeServerToken, tokenID, Boolean.class);
        return response;
    }

    @Override
    public boolean activateServerToken(long tokenID) {
        boolean response = post(LoginEndpoint.activateServerToken, tokenID, Boolean.class);
        return response;
    }

    @Override
    public boolean deactivateServerToken(long tokenID) {
        boolean response = post(LoginEndpoint.deactivateServerToken, tokenID, Boolean.class);
        return response;
    }

    @Override
    public GeneratedServerToken loadServerToken(long tokenID) {
        GeneratedServerToken response = post(LoginEndpoint.loadServerToken, tokenID, GeneratedServerToken.class);
        return response;
    }

    @Override
    public String refreshServerToken(String oldTokenString){
        String response = post(LoginEndpoint.refreshServerToken,oldTokenString,String.class);
        return response;
    }

    @Override
    public HashMap<String,String> serverTokenInfo(String serverTokenString) {
        HashMap<String,String> response = post(LoginEndpoint.ServerTokenInfo,serverTokenString,HashMap.class);
        return response;
    }

    @Override
    public String getUserToken(String serverTokenString, String userTokenFremd,String language, String backlink, boolean tempToken, String fingerprint, String ipaddress) {
        GetUserTokenRequest request = new GetUserTokenRequest(serverTokenString, userTokenFremd, language, backlink, tempToken, fingerprint,ipaddress);
        String response = post(LoginEndpoint.getUserToken,request,String.class);
        return response;
    }

    @Override
    public String getUserTokenDirect(UserTokenRequestDto userTokenRequestDto) {
        String response = post(LoginEndpoint.getUserTokenDirect,userTokenRequestDto,String.class);
        return response;
    }

    /**
     * Generiert eine Nachricht an ein Service welche in der REDIS-Datenbank gespeichert wird<br>
     * @param sender     Kennung des Senders
     * @param receiver   Kennung des Empfängers
     * @param topic      Thema der Nachricht
     * @param message    Nachricht als Objekt welches als JSON gespeichert wird!!
     * @param lifetimeSeconds   Lebensdauer der Nachricht in Sekunden bis sie gelöscht wird
     * @param single     true wenn die Nachricht nur einmal abgeholt werden kann und dann sofort gelöscht wird
     * @param messageSecret Secret damit nur Services eine Nachricht senden können welche das Secret kennen. - Sollte das ServerSecret sein!!
     * @return           Kennung der Nachricht als String welcher auch als get-Parameter verwendet werden kann
     */
    @Override
    public String createMessage(String sender, String receiver, String topic, Object message, long lifetimeSeconds, boolean single, String messageSecret) {
        PutMessageDto putMessageDto = new PutMessageDto(
                sender,
                receiver,
                topic,
                message,
                lifetimeSeconds,
                single,
                messageSecret
        );
        String response = post(LoginEndpoint.createMessage,putMessageDto,String.class);
        return response;
    }

    /** Lädt eine Nachricht aus der REDIS-Datenbank und löscht falls sie single ist sofort <br>
     * @param messageID   Kennung der Nachricht
     * @return            Nachricht als Object oder null wenn die Nachricht nicht existiert
     * */
    public MessageDto getMessage(String messageID) {
        MessageDto response = post(LoginEndpoint.getMessage,messageID,MessageDto.class);
        return response;
    }

}
