package at.letto.service.rest;

import at.letto.security.LettoToken;
import at.letto.service.interfaces.MicroService;
import at.letto.tools.JSON;
import at.letto.tools.rest.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

//import javax.ws.rs.client.*;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;


import lombok.Getter;
import lombok.Setter;
import org.apache.cxf.logging.NoOpFaultListener;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.net.ssl.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Rest-Client für Username-Password Authentification an einem Microservice
 */
public abstract class RestClient implements MicroService {

    /** Basis URI des REST-Services zB.: https://localhost:9091/ */
    @Getter  private String baseURI = "";

    /** Jersey-Client welcher mit dem Benutzernamen und dem Passwort welches im Constructor angegeben ist beim Systemstart verbunden wurde */
    private Client client=null;

    /** Benutzername mit dem der Dienst verbunden wurde */
    @Getter private String user="";

    /** Passwort des Benutzers, welcher mit dem Dienst verbunden ist */
    private String password="";

    /** Erzwingt vorherhehendes exteres JSON-Umwandeln mit JSON.objToJson */
    @Getter @Setter
    private boolean forceJsonExternal = false;
    /**
     * Erzeugt eine REST-Client Verbindung zu einem Microservice mit Authentifikation
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091/
     * @param user     Benutzernamen mit dem die Verbindung standardmäßig aufgebaut wird
     * @param password Passwort des Benutzers mit dem die Verbindung standardmäßig aufgebaut wird
     */
    public RestClient(String baseURI, String user, String password) {
        this.baseURI  = baseURI!=null?baseURI.trim():baseURI;
        this.user     = user!=null?user.trim():user;
        this.password = (this.user!=null && this.user.length()>0)?(password!=null?password.trim():password):"";

        while (this.baseURI.endsWith("/"))
            this.baseURI = this.baseURI.substring(0,this.baseURI.length()-1);
        setClient();
    }

    /**
     * Erzeugt eine REST-Client Verbindung zu einem Microservice ohne Authentifikation zB. für die JWT-Authentifikation
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091/
     */
    public RestClient(String baseURI) {
        this.baseURI  = baseURI;
        this.user     = null;
        this.password = null;
        while (this.baseURI.endsWith("/"))
            this.baseURI = this.baseURI.substring(0,this.baseURI.length()-1);
        setClient();
    }

    private void setClient() {
        if (baseURI.startsWith("https:"))
            client = RestClient.getHttpsRestClient(this.user, this.password);
        else if (baseURI.startsWith("http:"))
            client = RestClient.getHttpRestClient(this.user, this.password);
        else {
            // Protokoll ist nicht definiert, wir probliern zuerst mal https mit ping
            try {
                client = RestClient.getHttpsRestClient(this.user, this.password);
                String ret = this.get("",String.class);
                if (ret!=null) return;
            } catch (Exception ex) {}
            // nun mal http mit ping
            try {
                client = RestClient.getHttpRestClient(this.user, this.password);
                String ret = this.get("",String.class);
                if (ret!=null) return;
            } catch (Exception ex) {}
            // Wir bleiben bei https:
            client = RestClient.getHttpsRestClient(this.user, this.password);
        }
    }

    public static Client getRestClient(String uri, String user, String password) {
        if (uri.startsWith("https:"))
            return RestClient.getHttpsRestClient(user, password);
        else if (uri.startsWith("http:"))
            return  RestClient.getHttpRestClient(user, password);
        else {
            Client newClient=null;
            // Protokoll ist nicht definiert, wir probliern zuerst mal https mit ping
            try {
                newClient = RestClient.getHttpsRestClient(user, password);
                WebTarget webTarget = newClient.target(uri);
                Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
                Response response          = builder.get();
                if (response.getStatusInfo()==Response.Status.OK) return newClient;
            } catch (Exception ex) {}
            // nun mal http mit ping
            try {
                newClient = RestClient.getHttpRestClient(user, password);
                WebTarget webTarget = newClient.target(uri);
                Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
                Response response          = builder.get();
                if (response.getStatusInfo()==Response.Status.OK) return newClient;
            } catch (Exception ex) {}
            // Wir bleiben bei https:
            return RestClient.getHttpsRestClient(user, password);
        }
    }

    /**
     * Schickt einen POST-Ping an das Service mit dem Endpoint info/ping
     * @return true wenn das Service erreichbar ist
     */
    protected boolean ping(String endpoint, int timeoutMilliseconds) {
        Client client=null;
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts(), new java.security.SecureRandom());
            ClientBuilder clientBuilder = ClientBuilder.newBuilder()
                    .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    .sslContext(sslContext)
                    .hostnameVerifier(new HostnameVerifier(){
                        @Override
                        public boolean verify(String paramString, SSLSession paramSSLSession) { return true; }
                    });
             client = clientBuilder
                     .connectTimeout(timeoutMilliseconds,TimeUnit.MILLISECONDS)
                     .readTimeout(timeoutMilliseconds,TimeUnit.MILLISECONDS)
                    .build()
                     .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    ;
        } catch (Exception e) {
            client = ClientBuilder
                    .newBuilder()
                    .connectTimeout(timeoutMilliseconds,TimeUnit.MILLISECONDS)
                    .readTimeout(timeoutMilliseconds,TimeUnit.MILLISECONDS)
                    .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    .build();
        }
        String uri = this.baseURI;
        if (endpoint!=null && endpoint.length()>0)
            uri += (endpoint.startsWith("/")?"":"/")+endpoint;
        while (uri.endsWith("/"))
            uri=uri.substring(0,uri.length()-1);
        try {
            WebTarget webTarget = client.target(uri);
            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
            Response response;
            // POST
            response = builder.post(null);
            if (response.getStatusInfo()==Response.Status.OK || response.getStatus()==200) {
                String pong  = response.readEntity(String.class);
                client.close();
                if (pong!=null && pong.equals("pong")) return true;
            }
        } catch (Exception e) { }
        return false;
    }

    /**
     * Schickt einen POST-Ping an das Service mit dem Endpoint info/ping
     * @return true wenn das Service erreichbar ist
     */
    protected boolean ping(String endpoint) {
        String pong = post(endpoint,String.class);
        if (pong!=null && pong.equals("pong")) return true;
        return false;
    }

    public <T> T getDto(String endpoint, Object dto, TypeReference<T> Class, String token) {
        return rest(endpoint, dto, Class, false, token, false);
    }
    public <T> T postDto(String endpoint, Object dto, TypeReference<T> Class, String token) {
        return rest(endpoint, dto, Class, true, token, false);
    }

    public <T> T postDtoJson(String endpoint, Object dto, TypeReference<T> Class, String token) {
        try {
            return rest(endpoint, dto, Class, true, token, true);
        }
        catch (MsgException e) {
            System.out.println(e.getMessage()+ " - " + e.getDetails());
            return null;
        }
    }


    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T get(String endpoint, Class<T> Class) {
        return rest(endpoint,null,Class,false,null, false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T post(String endpoint, Class<T> Class) {
        return rest(endpoint, null, Class, true,null, false);
    }

    public <T> T postJson(String endpoint, Object dto, String token, Class<T> Class) {
        return rest(endpoint, dto, Class, true, token, true);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden als String welcher nach der uri und ? angehängt werden
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T get(String endpoint, String dto, Class<T> Class) {
        return rest(endpoint, dto, Class, false,null, false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T post(String endpoint, Object dto, Class<T> Class) {
        return rest(endpoint, dto, Class, true,null, false);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param lettoToken Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T get(String endpoint, Class<T> Class, LettoToken lettoToken) {
        return rest(endpoint,null,Class,false, lettoToken.getToken(), false);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param token  Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T get(String endpoint, Class<T> Class, String token) {
        return rest(endpoint,null,Class,false, token, false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param lettoToken Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T post(String endpoint, Class<T> Class, LettoToken lettoToken) {
        return rest(endpoint, null, Class, true, lettoToken.getToken(), false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param token  Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T post(String endpoint, Class<T> Class, String token) {
        return rest(endpoint, null, Class, true, token, false);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden als String welcher nach der uri und ? angehängt werden
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param lettoToken Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T get(String endpoint, String dto, Class<T> Class, LettoToken lettoToken) {
        return rest(endpoint, dto, Class, false, lettoToken.getToken(), false);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden als String welcher nach der uri und ? angehängt werden
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param token  Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T get(String endpoint, String dto, Class<T> Class, String token) {
        return rest(endpoint, dto, Class, false, token, false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param lettoToken Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T post(String endpoint, Object dto, Class<T> Class, LettoToken lettoToken) {
        return rest(endpoint, dto, Class, true, lettoToken.getToken(), false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param token  Token für die Authentifikation
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public <T> T post(String endpoint, Object dto, Class<T> Class, String token) {
        return rest(endpoint, dto, Class, true, token, false);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param post   true wenn POST, false bei GET
     * @param token  JWT-Token für die Authentifikation
     * @param jsonExternal  DTO wird mit externem JSONer zu Text umgewandelt (JSON.objToJson)
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public  <T> T rest(String endpoint, Object dto, Class<T> Class,boolean post, String token, boolean jsonExternal) {
        String uri = this.baseURI;
        if (endpoint!=null && endpoint.length()>0)
            uri += (endpoint.startsWith("/")?"":"/")+endpoint;
        while (uri.endsWith("/"))
            uri=uri.substring(0,uri.length()-1);
        try {
            Entity entity=null;
            if (dto!=null) {
                if (dto instanceof String)
                    entity = jsonExternal || forceJsonExternal ? Entity.json(dto) :
                            Entity.entity(dto, MediaType.APPLICATION_JSON);
                else
                    entity = jsonExternal || forceJsonExternal ? Entity.json(JSON.objToJson(dto)) :
                            Entity.entity(dto, MediaType.APPLICATION_JSON);
                if (!post)
                    uri=loadGetUri(uri, dto);
            }

            //uri = URLEncoder.encode(uri, "UTF-8");
            WebTarget webTarget = client.target(uri);

            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
            if (token!=null && token.length()>0)
                builder = builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            Response response;
            if (post)
                response = builder.post(entity);
            else
                response = builder.get();
            if (response.getStatusInfo()==Response.Status.OK || response.getStatus()==200) {
                T ret;
                if (Class==String.class) ret = response.readEntity(Class);
                else {
                    String resp = response.readEntity(String.class);
                    ret = JSON.jsonToObj(resp, Class);
                }
                return ret;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    private static String loadGetUri(String uri, Object dto) {
        if (dto instanceof String)
            uri += "?"+dto;
        else {
            String params = JSON.objToJson((dto))
                    .replaceAll("\\{", "")
                    .replaceAll("\\}", "")
                    .replaceAll("\"", "")
                    .replaceAll(":", "=")
                    .replaceAll(" ", "+")
                    .replaceAll(",\\s*", "&");

            uri += "?" + params;
        }
        return uri;
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param endpoint    Endoint am RestServer
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param type   Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param post   true wenn POST, false bei GET
     * @param token  JWT-Token für die Authentifikation
     * @param jsonExternal  DTO wird mit externem JSONer zu Text umgewandelt (JSON.objToJson)
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    private <T> T rest(String endpoint, Object dto,
                       TypeReference<T> type, boolean post, String token, boolean jsonExternal) {
        String uri = this.baseURI;
        if (endpoint!=null && endpoint.length()>0)
            uri += (endpoint.startsWith("/")?"":"/")+endpoint;
        while (uri.endsWith("/"))
            uri=uri.substring(0,uri.length()-1);
        Response response=null;
        try {
            Entity entity = null;
            if (dto != null) {
                if (dto instanceof String)
                    entity = jsonExternal || forceJsonExternal ? Entity.json(dto) :
                            Entity.entity(dto, MediaType.APPLICATION_JSON);
                else
                    entity = jsonExternal || forceJsonExternal ? Entity.json(JSON.objToJson(dto)) :
                            Entity.entity(dto, MediaType.APPLICATION_JSON);
                if (!post)
                    uri = loadGetUri(uri, dto);
            }

            WebTarget webTarget = client.target(uri);

            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
            if (token != null && token.length() > 0)
                builder = builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            if (post)
                response = builder.post(entity);
            else
                response = builder.get();
            if (response.getStatusInfo() == Response.Status.OK || response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(SerializationFeature.INDENT_OUTPUT);
                return mapper.readValue(json, type);

            }
            else if (response.getStatus() == 403) {
                DtoAndMsg<Object> ret = new DtoAndMsg<>();
                ret.setData(null);
                ret.setMsg(new Msg("auth.error",MsgType.ERROR,""));
                String json = JSON.objToJson(ret);
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(SerializationFeature.INDENT_OUTPUT);
                return mapper.readValue(json, type);
            }
            else if (jsonExternal) {
                List<String> err = new Vector<>();
                err.add(response.getStatusInfo().getStatusCode() + "");
                throw new MsgException("err.rest.connection", err);
            }
        } catch (MsgException msg) {
            throw msg;
        } catch (Exception e) {
            if (e instanceof MsgException) {
                MsgException ex = (MsgException)e;
                throw ex;
            }
            e.printStackTrace();
        }
        if (response!=null && response.getStatusInfo().getStatusCode()==401) {
            throw new TokenException("Token fehlerhaft");
        }
        return null;
    }

    /* --------------------------------------------------------------------------------------------
     *   statische Methoden
     * -------------------------------------------------------------------------------------------- */
    private static TrustManager[] trustAllCerts() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
            }
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
            }
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        return trustAllCerts;
    }

    /**
     * Liefert einen Jersey REST-Client mit ssl-Funktionialität
     * @return REST-Client
     */
    public static Client getHttpsRestClient(String user, String password) {
        if (user==null) user="";
        if (password==null) password="";
        HttpAuthenticationFeature basicAuth =
                (user.length()>0) ? HttpAuthenticationFeature.basic(user,password) : null;
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts(), new java.security.SecureRandom());
            ClientBuilder clientBuilder = ClientBuilder.newBuilder()
                    .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    .sslContext(sslContext)
                    .hostnameVerifier(new HostnameVerifier(){
                        @Override
                        public boolean verify(String paramString, SSLSession paramSSLSession) { return true; }
                    });
            if (basicAuth!=null)
                clientBuilder = clientBuilder.register(basicAuth);
            Client client = clientBuilder
                    .build()
                    //.property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    ;
            return client;
        } catch (Exception e) {	}
        if (basicAuth!=null)
            return ClientBuilder
                    .newBuilder()
                    .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    .register(basicAuth)
                    .build();
        return ClientBuilder
                .newBuilder()
                .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                .build();
    }

    /**
     * Liefert einen Jersey REST-Client ohne ssl-Funktionialität
     * @return REST-Client
     */
    public static Client getHttpRestClient(String user, String password) {
        if (user==null) user="";
        if (password==null) password="";
        HttpAuthenticationFeature basicAuth =
                (user.length()>0) ? HttpAuthenticationFeature.basic(user,password) : null;
        if (basicAuth!=null)
            return ClientBuilder
                    .newBuilder()
                    .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                    .register(basicAuth)
                    .build();
        return ClientBuilder
                .newBuilder()
                .property("org.apache.cxf.logging.FaultListener", new NoOpFaultListener())
                .build();
    }

    /**
     * Liefert einen Jersey REST-Client mit ssl-Funktionialität ohne Authentifikation
     * @return REST-Client
     */
    public static Client getHttpsRestClient() {
        return getHttpsRestClient(null,null);
    }

    /**
     * Liefert einen Jersey REST-Client ohne ssl-Funktionialität ohne Authentifikation
     * @return REST-Client
     */
    public static Client getHttpRestClient() {
        return getHttpRestClient(null,null);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param uri    Server URI
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public static <T> T restGet(String uri, Class<T> Class) {
        return restGetPost(uri,null,Class,false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server ohne Anfrageparameter
     * @param uri    Server URI
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public static <T> T restPost(String uri, Class<T> Class) {
        return restGetPost(uri, null, Class, true);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server
     * @param uri    Server URI
     * @param dto    Daten die als Parameter übergeben werden als String welcher nach der uri und ? angehängt werden
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public static <T> T restGet(String uri, String dto, Class<T> Class) {
        return restGetPost(uri, dto, Class, false);
    }

    /**
     * Stellt eine REST-POST-Anfrage an einen Server
     * @param uri    Server URI
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    public static <T> T restPost(String uri, Object dto, Class<T> Class) {
        return restGetPost(uri, dto, Class, true);
    }

    /**
     * Stellt eine REST-GET-Anfrage an einen Server ohne Anfrageparameter
     * @param uri    Server URI
     * @param dto    Daten die als Parameter übergeben werden, null wenn keine Daten
     * @param Class  Klasse des Ergebnisses
     * @param <T>    Type der Klasse des Ergebnisses
     * @param post   true wenn POST, false bei GET
     * @return       Ergebnis der REST-Anfrage als Objekt. Im Fehlerfall null.
     */
    private static <T> T restGetPost(String uri, Object dto, Class<T> Class,boolean post) {
        Client client = null;
        try {
            client = RestClient.getRestClient(uri,"","");
            Entity entity=null;
            if (dto!=null) {
                entity = Entity.entity(dto, MediaType.APPLICATION_JSON);
                if (!post)
                    uri=loadGetUri(uri, dto);
            }

            WebTarget webTarget = client.target(uri);

            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
            Response response;
            if (post)
                response = builder.post(entity);
            else
                response = builder.get();
            if (response.getStatusInfo()==Response.Status.OK) {
                try {
                    T ret = response.readEntity(Class);
                    client.close();
                    return ret;
                } catch (Exception e) {
                    client.close();
                }
            } else if (response.getStatus()==200) {
                T ret = response.readEntity(Class);
                client.close();
                return ret;
            }
        } catch (Exception e) {
            if (client!=null) client.close();
        }
        return null;
    }

    public static <T> T jsonTest(String json, TypeReference<T> Class) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Class);
    }

}
