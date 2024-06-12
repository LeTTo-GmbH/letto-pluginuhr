package at.letto.restclient.endpoint;

/**
 * Standard Endpoints welche jedes Service realisieren muss
 */
public class BaseEndpoints {

    public static final String ERROR = "/error";
    public static final String OPEN  = "/open";

    // ----------------------------- User-Authentifikation ------------------
    public static final String AUTH = "/auth";
    public static final String AUTH_GAST   = AUTH+"/gast";
    public static final String AUTH_USER   = AUTH+"/user";
    public static final String AUTH_ADMIN  = AUTH+"/admin";
    public static final String AUTH_GLOBAL = AUTH+"/global";
    public static final String AUTH_LETTO  = AUTH+"/letto";

    // ----------------------------- Token-Authentifikation ------------------
    public static final String API  = "/api";
    public static final String API_STUDENT     = API+"/student";
    public static final String API_TEACHER     = API+"/teacher";
    public static final String API_ADMIN       = API+"/admin";
    public static final String API_GLOBAL      = API+"/global";
    public static final String API_OPEN        = API+"/open";

    // ----------------------------- Test Endpoints ---------------------------
    public static final String PING             = "/ping";
    public static final String PING_OPEN        = OPEN+PING;
    public static final String PING_AUTH_GAST   = AUTH_GAST+PING;
    public static final String PING_AUTH_USER   = AUTH_USER+PING;
    public static final String PING_AUTH_ADMIN  = AUTH_ADMIN+PING;
    public static final String PING_AUTH_GLOBAL = AUTH_GLOBAL+PING;
    public static final String PING_AUTH_LETTO  = AUTH_LETTO+PING;
    public static final String PING_API_STUDENT = API_STUDENT+PING;
    public static final String PING_API_TEACHER = API_TEACHER+PING;
    public static final String PING_API_ADMIN   = API_ADMIN+PING;
    public static final String PING_API_GLOBAL  = API_GLOBAL+PING;
    public static final String PING_API_OPEN    = API_OPEN+PING;

    // ----------------------------- Service Info Endpoints ---------------------
    public static final String INFO             = "/info";
    public static final String INFO_OPEN        = OPEN+INFO;
    public static final String INFO_AUTH_ADMIN  = AUTH_ADMIN+INFO;
    public static final String INFO_API_ADMIN   = API_ADMIN+INFO;
    public static final String VERSION          = "/version";

}
