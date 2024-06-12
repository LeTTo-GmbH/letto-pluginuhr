package at.letto.math.endpoints;

public class MatheEndpoint {

    public static final String servicepath = "/mathe";
    public static final String error       = "/error";
    public static final String OPEN        = servicepath+"/open";
    public static final String AUTH        = servicepath+"/auth";
    public static final String API         = servicepath+"/api";
    public static final String AUTH_GAST   = AUTH+"/gast";
    public static final String AUTH_USER   = AUTH+"/user";
    public static final String AUTH_ADMIN  = AUTH+"/admin";
    public static final String AUTH_LETTO  = AUTH+"/letto";
    public static final String STUDENT     = API+"/student";
    public static final String TEACHER     = API+"/teacher";
    public static final String ADMIN       = API+"/admin";
    public static final String GLOBAL      = API+"/global";

    // static
    public static final String CSS         = OPEN+"/css";
    public static final String style       = CSS+"/style.css";

    // Login
    public static final String login       = AUTH + "/login";       // Login mit User Gast,User,Admin,Letto
    public static final String loginletto  = OPEN + "/loginletto";  // Login mit LeTTo-User
    public static final String logout      = OPEN + "/logout";

    // Alle notwendigen Ping-Punkte für den Service-Test
    public static final String ping                = servicepath + "/ping";
    public static final String pingpost            = OPEN + "/pingp";
    public static final String pingget             = OPEN + "/pingg";
    public static final String pingauthgast        = AUTH_GAST   + "/ping";
    public static final String pingauthuser        = AUTH_USER   + "/ping";
    public static final String pingauthadmin       = AUTH_ADMIN  + "/ping";
    public static final String pingauthletto       = AUTH_LETTO  + "/ping";
    public static final String pingstudent         = STUDENT     + "/ping";
    public static final String pingteacher         = TEACHER     + "/ping";
    public static final String pingadmin           = ADMIN       + "/ping";
    public static final String pingglobal          = GLOBAL      + "/ping";

    public static final String version             = OPEN+"/version";
    public static final String info                = OPEN+"/info";
    public static final String admininfo           = ADMIN+"/admininfo";

    public static final String pingimageservice    = AUTH_GAST +"/pingimageservice";
    public static final String imageadmininfo      = ADMIN +"/imageadmininfo";

    public static final String home                = OPEN +"/home";
    public static final String hello               = OPEN +"/hello";
    public static final String infoletto           = ADMIN+"/infoletto";
    public static final String infoadmin           = ADMIN+"/info";
    public static final String dashboard           = ADMIN +"/dashboard";

}
