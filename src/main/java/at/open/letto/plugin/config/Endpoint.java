package at.open.letto.plugin.config;

import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.restclient.endpoint.EndpointInterface;

public class Endpoint implements EndpointInterface {

    @Override
    public String servicepath() {
        return servicepath;
    }

    public static final String servicepath = "/pluginuhr";
    public static final String error= servicepath+EndpointInterface.error;
    public static final String ping= servicepath+EndpointInterface.ping;
    public static final String open= servicepath+EndpointInterface.open;
    public static final String api= servicepath+EndpointInterface.api;
    public static final String apiopen= servicepath+EndpointInterface.apiopen;
    public static final String apistudent= servicepath+EndpointInterface.apistudent;
    public static final String apiteacher= servicepath+EndpointInterface.apiteacher;
    public static final String apiadmin= servicepath+EndpointInterface.apiadmin;
    public static final String apiglobal= servicepath+EndpointInterface.apiglobal;
    public static final String auth= servicepath+EndpointInterface.auth;
    public static final String authgast= servicepath+EndpointInterface.authgast;
    public static final String authuser= servicepath+EndpointInterface.authuser;
    public static final String authletto= servicepath+EndpointInterface.authletto;
    public static final String authadmin= servicepath+EndpointInterface.authadmin;
    public static final String session= servicepath+EndpointInterface.session;
    public static final String sessionadmin= servicepath+EndpointInterface.sessionadmin;
    public static final String sessionteacher= servicepath+EndpointInterface.sessionteacher;
    public static final String sessionstudent= servicepath+EndpointInterface.sessionstudent;
    public static final String sessionglobal= servicepath+EndpointInterface.sessionglobal;

    public static final String pingpost     = servicepath+EndpointInterface.pingpost;
    public static final String pingget      = servicepath+EndpointInterface.pingget;
    public static final String version      = servicepath+EndpointInterface.version;
    public static final String info         = servicepath+EndpointInterface.info;
    public static final String infoletto    = servicepath+EndpointInterface.infoletto;
    public static final String infoadmin    = servicepath+EndpointInterface.infoadmin;

    public static final String LOCAL_API    = "/open";
    public static final String EXTERN_API   = authuser;
    public static final String EXTERN_OPEN  = apiopen;

    public static final String iframeConfig = open + PluginConnectionEndpoint.configurationHttp;

    public String getIframeConfig() {
        return iframeConfig;
    }

    public String getLogoEP10() {
        return OPEN()+"/images/letto2-10.png";
    }

}
