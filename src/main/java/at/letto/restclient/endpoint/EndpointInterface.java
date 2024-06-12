package at.letto.restclient.endpoint;

public interface EndpointInterface {

    String open  = "/open";
    String ping  = "/ping";
    String error = "/error";
    String pingpost = open + "/pingp";
    String pingget  = open + "/pingg";
    String login    = open+"/login";
    String logout   = open+"/logout";
    String api      = "/api";
    String apiopen    = api+"/open";
    String apistudent = api+"/student";
    String apiteacher = api+"/teacher";
    String apiadmin   = api+"/admin";
    String apiglobal  = api+"/global";
    String auth       = "/auth";
    String authgast   = auth+"/gast";
    String authuser   = auth+"/user";
    String authletto  = auth+"/letto";
    String authadmin  = auth+"/admin";
    String session    = "/session";
    String sessionadmin   = session+"/admin";
    String sessionteacher = session+"/teacher";
    String sessionstudent = session+"/student";
    String sessionglobal  = session+"/global";

    String version   = open + "/version";
    String info      = open + "/info";
    String infoletto = apiadmin+"/infoletto";
    String infoadmin = apiadmin+"/info";

    String servicepath();

    default String OPEN() {
        return servicepath()    + open;
    }
    default String PING() {
        return servicepath()    + ping;
    }
    default String ERROR() {
        return servicepath()   + error;
    }
    default String LOGIN() {
        return servicepath()   + login;
    }
    default String LOGOUT() {
        return servicepath()  + logout;
    }

    default String API() {
        return servicepath()     + api;
    }
    default String OPENAPI() {
        return servicepath() + apiopen;
    }
    default String STUDENT() {
        return servicepath() + apistudent;
    }
    default String TEACHER() {
        return servicepath() + apiteacher;
    }
    default String ADMIN() {
        return servicepath()   + apiadmin;
    }
    default String GLOBAL() {
        return servicepath()  + apiglobal;
    }

    default String AUTH() {
        return servicepath()      + auth;
    }
    default String AUTH_GAST() {
        return servicepath() + authgast;
    }
    default String AUTH_USER() {
        return servicepath() + authuser;
    }
    default String AUTH_LETTO() {
        return servicepath()+ authletto;
    }
    default String AUTH_ADMIN() {
        return servicepath()+ authadmin;
    }

    default String SESSION() {
        return servicepath()        + session;
    }
    default String SESSION_ADMIN() {
        return servicepath()  + sessionadmin;
    }
    default String SESSION_TEACHER() {
        return servicepath()+ sessionteacher;
    }
    default String SESSION_STUDENT() {
        return servicepath()+ sessionstudent;
    }
    default String SESSION_GLOBAL() {
        return servicepath() + sessionglobal;
    }

}
