package at.letto.login.restclient;

import at.letto.login.dto.*;
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
    public LettoToken jwtLogin(String username, String password, String school, String jwtsecret, String fingerprint) {
        AuthenticationRequest request = new AuthenticationRequest(username, password, school, fingerprint);
        JWTTokenResponse response = post(LoginEndpoint.jwtlogin,request,JWTTokenResponse.class);
        if (response!=null && response.getToken()!=null)
            return new LettoToken(response.getToken(), jwtsecret);
        return null;
    }

    @Override
    public String jwtLogin(String username, String password, String school, String fingerprint) {
        AuthenticationRequest request = new AuthenticationRequest(username, password, school, fingerprint);
        JWTTokenResponse response = post(LoginEndpoint.jwtlogin,request,JWTTokenResponse.class);
        if (response!=null && response.getToken()!=null)
            return response.getToken();
        return null;
    }

    @Override
    public LettoToken jwtRefresh(LettoToken token, String secret) {
        JWTTokenResponse response = post(LoginEndpoint.jwtrefresh,token.getToken(),JWTTokenResponse.class, token);
        if (response!=null && response.getToken()!=null)
            return new LettoToken(response.getToken(), secret);
        return null;
    }

    @Override
    public String jwtRefresh(String token) {
        JWTTokenResponse response = post(LoginEndpoint.jwtrefresh,token,JWTTokenResponse.class, token);
        if (response!=null && response.getToken()!=null)
            return response.getToken();
        return null;
    }

    public String jwtRefreshGet(String token) {
        JWTTokenResponse response = get(LoginEndpoint.jwtrefresh,JWTTokenResponse.class, token);
        if (response!=null && response.getToken()!=null)
            return response.getToken();
        return null;
    }

    @Override
    public String jwtGetTempTokenUri(LettoToken lettoToken, String secret) {
        String response = post(LoginEndpoint.jwtgettemptokenuri,lettoToken.getToken(),String.class, lettoToken);
        return response;
    }

    @Override
    public String jwtGetTempToken(LettoToken lettoToken, String secret) {
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
    public boolean tempLogin(String username, String tempPassword, String school, String fingerprint) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, tempPassword, school, fingerprint);
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

    public boolean pingStudent(LettoToken token) {
        String pong = post(LoginEndpoint.pingstudent,"ping",String.class, token);
        if (pong!=null && pong.equals("pong")) return true;
        return false;
    }
    public boolean pingTeacher(LettoToken token) {
        String pong = post(LoginEndpoint.pingteacher,"ping",String.class, token);
        if (pong!=null && pong.equals("pong")) return true;
        return false;
    }
    public boolean pingAdmin(LettoToken token)   {
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
    public String getUserToken(String serverTokenString, String userTokenFremd,String language, String backlink, boolean tempToken, String fingerprint) {
        GetUserTokenRequest request = new GetUserTokenRequest(serverTokenString, userTokenFremd, language, backlink, tempToken, fingerprint);
        String response = post(LoginEndpoint.getUserToken,request,String.class);
        return response;
    }

    @Override
    public String getUserTokenDirect(UserTokenRequestDto userTokenRequestDto) {
        String response = post(LoginEndpoint.getUserTokenDirect,userTokenRequestDto,String.class);
        return response;
    }

}
