package at.letto.login.endpoints;

import at.letto.restclient.endpoint.EndpointInterface;

public class LoginEndpoint {
    public static final String servicepath = "/login";
    public static final String error       = "/login/error";
    public static final String OPEN        = servicepath+EndpointInterface.open;

    /* ----------------------------------------------------------------------------------------------------
                        Server-to-Server User,Passwort Authentifikation
       ---------------------------------------------------------------------------------------------------- */
    public static final String AUTH        = servicepath+EndpointInterface.auth;
    public static final String AUTH_GAST   = servicepath+EndpointInterface.authgast;
    public static final String AUTH_USER   = servicepath+EndpointInterface.authuser;
    public static final String AUTH_ADMIN  = AUTH+"/admin";
    public static final String AUTH_GLOBAL = AUTH+"/global";
    public static final String AUTH_LETTO  = AUTH+"/letto";

    /* ----------------------------------------------------------------------------------------------------
                        API mit Token-Authentifikation
       ---------------------------------------------------------------------------------------------------- */
    public static final String API         = servicepath+EndpointInterface.api;
    public static final String STUDENT     = API+"/student";
    public static final String TEACHER     = API+"/teacher";
    public static final String ADMIN       = API+"/admin";
    public static final String GLOBAL      = API+"/global";
    public static final String SERVER      = API+"/server";
    public static final String OPENAPI     = API+"/open";
    public static final String jwtrefresh  = API + "/jwtrefresh";   // JWT-Token-Refresh
    public static final String tokeninfo   = API + "/tokeninfo"; // liefert die Info über einen Token
    public static final String getaliastoken = TEACHER + "/getaliastoken"; // liefert einen Alias-Token eines anderen Benutzers

    /* ----------------------------------------------------------------------------------------------------
                        OPEN - STATIC
       ---------------------------------------------------------------------------------------------------- */
    public static final String CSS         = OPEN+"/css";
    public static final String style       = CSS+"/style.css";

    /* ----------------------------------------------------------------------------------------------------
                        LOGIN
       ---------------------------------------------------------------------------------------------------- */
    public static final String login       = OPEN + "/login";        //Login
    public static final String loginletto  = OPEN + "/loginletto";   // Login mit LeTTo-User
    public static final String logout      = OPEN + "/logout";
    public static final String jwtlogin            = OPENAPI  + "/jwtlogin";     // JWT-Token-Login
    public static final String jwttemptoken        = OPENAPI  + "/jwttemptoken"; // JWT-Token aus einem TempToken erzeugen
    public static final String jwtgettemptokenuri  = OPENAPI  + "/jwtgettemptokenuri"; // TempToken aus einem JWT Token erzeugen und eine URI damit liefern
    public static final String jwtgettemptoken     = API  + "/jwtgettemptoken"; // TempToken aus einem JWT Token erzeugen
    public static final String logincheck          = OPEN + "/logincheck";
    public static final String logoutletto         = OPEN + "/logoutletto";
    public static final String templogin           = OPEN+"/templogin";
    public static final String setpassword         = OPEN+"/setpassword";

    public static final String getServerToken        = AUTH_ADMIN+"/getservertoken";
    public static final String serverTokenList       = AUTH_ADMIN+"/servertokenlist";
    public static final String removeServerToken     = AUTH_ADMIN+"/removeservertoken";
    public static final String activateServerToken   = AUTH_ADMIN+"/activateservertoken";
    public static final String deactivateServerToken = AUTH_ADMIN+"/deactivateservertoken";
    public static final String loadServerToken       = AUTH_ADMIN+"/loadservertoken";
    public static final String refreshServerToken    = OPEN+"/getservertokenuri";
    public static final String ServerTokenInfo       = OPEN+"/servertokeninfo";
    public static final String getUserToken          = OPEN+"/getusertoken";
    public static final String getUserTokenDirect    = OPEN+"/getusertokendirect";

    /* ----------------------------------------------------------------------------------------------------
                        Alle notwendigen Ping-Punkte für den Service-Test
       ---------------------------------------------------------------------------------------------------- */
    public static final String ping                = servicepath + "/ping";
    public static final String pingpost            = servicepath + "/pingp";
    public static final String pingget             = servicepath + "/pingg";
    public static final String pingauthgast        = AUTH_GAST   + "/ping";
    public static final String pingauthuser        = AUTH_USER   + "/ping";
    public static final String pingauthadmin       = AUTH_ADMIN  + "/ping";
    public static final String pingauthletto       = AUTH_LETTO  + "/ping";
    public static final String pingstudent         = STUDENT     + "/ping";
    public static final String pingteacher         = TEACHER     + "/ping";
    public static final String pingadmin           = ADMIN       + "/ping";
    public static final String pingglobal          = GLOBAL      + "/ping";
    public static final String pingimageservice    = AUTH_GAST   + "/pingimageservice";

    /* ----------------------------------------------------------------------------------------------------
                        Info-Endpoints
       ---------------------------------------------------------------------------------------------------- */
    public static final String version             = OPEN +"/version";
    public static final String info                = OPEN +"/info";
    public static final String admininfo           = ADMIN+"/admininfo";
    public static final String imageadmininfo      = AUTH_ADMIN +"/imageadmininfo";
    public static final String infoletto           = AUTH_LETTO +"/info";
    public static final String infoadmin           = AUTH_ADMIN +"/info";

     /* ----------------------------------------------------------------------------------------------------
                        Theamleaf - Website - Endpoints
       ---------------------------------------------------------------------------------------------------- */
    public static final String home                = OPEN +"/home";
    public static final String dashboarduser       = OPEN  +"/dashboard";

    /* ----------------------------------------------------------------------------------------------------
                        SESSION LOCAL am HOST
       ---------------------------------------------------------------------------------------------------- */
    public static final String SESSION             = servicepath+EndpointInterface.session;
    public static final String SESSION_ADMIN       = servicepath+EndpointInterface.sessionadmin;
    public static final String SESSION_GLOBAL      = servicepath+EndpointInterface.sessionglobal;
    public static final String SESSION_STUDENT     = servicepath+EndpointInterface.sessionstudent;
    public static final String SESSION_TEACHER     = servicepath+EndpointInterface.sessionteacher;

}
