package at.letto.image.endpoints;

public class ImageEndpoint {

    public static final String servicepath = "/image";
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
    public static final String admininfo           = AUTH_ADMIN+"/admininfo";
    public static final String lettoinfo           = AUTH_LETTO+"/admininfo";

    public static final String checkfilesystem      = OPEN+"/checkfilesystem";
    public static final String checkservice         = OPEN+"/checkservice";
    public static final String existimage           = OPEN+"/existimage";
    public static final String getimageage          = OPEN+"/getimageage";
    public static final String getimagsize          = OPEN+"/getimagsize";
    public static final String geturl               = OPEN+"/geturl";
    public static final String getabsurl            = OPEN+"/getabsurl";
    public static final String isfilenameok         = OPEN+"/isfilenameok";
    public static final String getextension         = OPEN+"/getextension";
    public static final String loadimagebase64      = OPEN+"/loadimagebase64";
    public static final String loadimagebase64dto   = OPEN+"/loadimagebase64dto";
    public static final String getimages            = OPEN+"/getimages";
    public static final String getimagesolderthan   = OPEN+"/getimagesolderthan";

    public static final String delimage             = AUTH_USER+"/delimage";
    public static final String createfile           = AUTH_USER+"/createfile";
    public static final String saveimage            = AUTH_USER+"/saveimage";
    public static final String savebase64image      = AUTH_USER+"/savebase64image";
    public static final String saveurlimage         = AUTH_USER+"/saveurlimage";
    public static final String delimages            = AUTH_USER+"/delimages";

}
