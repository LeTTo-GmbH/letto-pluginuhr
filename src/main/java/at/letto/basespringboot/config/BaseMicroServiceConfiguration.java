package at.letto.basespringboot.config;

import at.letto.basespringboot.security.WebSecurityConfig;
import at.letto.tools.config.MicroServiceConfigurationInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hier werden alle Konfigurationen welche aus der application.properties oder aus anderen Konfigurationsdateien oder
 * einer Konfigurationsdatenbank kommen gemeinsam verwaltet.
 */
@Getter @Setter
public abstract class BaseMicroServiceConfiguration implements MicroServiceConfigurationInterface {

    // ----------------------------------------------------------------------------
    // Definition der Logfiles
    // ----------------------------------------------------------------------------
    // Alle Login und Logout-Vorgänge
    @Value("${"+DEFlogfilePath+"}")
    private String logfilePath;
    // Alle Login und Logout-Vorgänge
    @Value("${"+DEFlogfileLogin+"}")
    private String logfileLogin;
    // Alle Fehler die auftreten
    @Value("${"+DEFlogfileError+"}")
    private String logfileError;
    // Start-Stop-Status des Services
    @Value("${"+DEFlogfileStart+"}")
    private String logfileStart;

    // Log-Level des Services
    @Value("${"+DEFlettoLogLevel+"}")
    private String lettoLogLevel;

    // ----------------------------------------------------------------------------
    // Security
    // ----------------------------------------------------------------------------
    @Value("${"+DEFuseHttp+"}")
    private String useHttp;
    @Value("${"+DEFjwtSecret+"}")
    private String jwtSecret;
    @Value("${"+DEFjwtExpiration+"}")
    private long jwtExpiration;
    @Value("${"+DEFjwtAppExpiration+"}")
    private long jwtAppExpiration;
    @Value("${"+DEFjwtRefreshTime+"}")
    private long jwtRefreshTime;
    @Value("${"+DEFserverSecret+"}")
    private String serverSecret;
    
    @Value("${"+DEFshortTempTokenAge+"}")
    private long shortTempTokenAge;
    @Value("${"+DEFmediumTempTokenAge+"}")
    private long mediumTempTokenAge;
    @Value("${"+DEFlongTempTokenAge+"}")
    private long longTempTokenAge;
    
    // ----------------------------------------------------------------------------
    // Schlüssel
    // ----------------------------------------------------------------------------

    @Value("${"+DEFrestkey+"}")
    private String restkey;
    @Value("${"+DEFlocalPrivateKey+"}")
    private String localPrivateKey;
    @Value("${"+DEFlocalPublicKey+"}")
    private String localPublicKey;
    @Value("${"+DEFlicensePublicKey+"}")
    private String licensePublicKey;
    @Value("${"+DEFlicenseServer+"}")
    private String licenseServer;

    // ----------------------------------------------------------------------------
    // Keystore
    // ----------------------------------------------------------------------------
    @Value("${"+DEFkeyStore+"}")
    private String keyStore;
    @Value("${"+DEFkeyStorePassword+"}")
    private String keyStorePassword;
    @Value("${"+DEFkeyStoreType+"}")
    private String keyStoreType;
    @Value("${"+DEFkeyAlias+"}")
    private String keyAlias;

    // ----------------------------------------------------------------------------
    // Benutzerkonfiguration aus der application.properties
    // ----------------------------------------------------------------------------
    @Value("${"+DEFlettoUID+"}")
    private String lettoUID;

    @Value("${"+DEFlettoPath+"}")
    private String lettoPath;

    @Value("${"+DEFsetupComposePath+"}")
    private String setupComposePath;

    @Value("${"+DEFlettoComposePath+"}")
    private String lettoComposePath;

    @Value("${"+DEFhostBetriebssystem+"}")
    private String hostBetriebssystem;

    @Value("${"+DEFpathSeperator+"}")
    private String pathSeperator;

    @Value("${"+DEFserverName+"}")
    private String serverName;

    @Value("${letto_user:${letto.user:}}")
    private String userlist;

    @Value("${"+DEFuserGastPassword+"}")
    private String userGastPassword;
    @Value("${"+DEFuserGastEncryptedPassword+"}")
    private String userGastEncryptedPassword;
    @Value("${"+DEFuserGastRoles+"}")
    private String userGastRoles;

    @Value("${"+DEFuserUserPassword+"}")
    private String userUserPassword;
    @Value("${"+DEFuserUserEncryptedPassword+"}")
    private String userUserEncryptedPassword;
    @Value("${"+DEFuserUserRoles+"}")
    private String userUserRoles;

    @Value("${"+DEFuserAdminPassword+"}")
    private String userAdminPassword;
    @Value("${"+DEFuserAdminEncryptedPassword+"}")
    private String userAdminEncryptedPassword;
    @Value("${"+DEFuserAdminRoles+"}")
    private String userAdminRoles;

    @Value("${"+DEFuserLettoPassword+"}")
    private String userLettoPassword;
    @Value("${"+DEFuserLettoEncryptedPassword+"}")
    private String userLettoEncryptedPassword;
    @Value("${"+DEFuserLettoRoles+"}")
    private String userLettoRoles;

    // ----------------------------------------------------------------------------
    // Konfiguration der Services
    // ----------------------------------------------------------------------------
    //LTI-Service
    @Value("${"+DEFltiHttpPort+"}")
    private int ltiHttpPort;
    @Value("${"+DEFltiAjpPort+"}")
    private int ltiAjpPort;
    @Value("${"+DEFltiHttpsPort+"}")
    private int ltiHttpsPort;
    @Value("${"+DEFltiServiceUri+"}")
    private String ltiServiceUri;
    @Value("${"+DEFltiRestKey+"}")
    private String ltiRestKey;

    //image-Service
    @Value("${"+DEFimageHttpPort+"}")
    private int imageHttpPort;
    @Value("${"+DEFimageAjpPort+"}")
    private int imageAjpPort;
    @Value("${"+DEFimageHttpsPort+"}")
    private int imageHttpsPort;
    @Value("${"+DEFimageServiceUri+"}")
    private String imageServiceUri;
    @Value("${"+DEFimageServiceUser+"}")
    private String imageServiceUser;
    @Value("${"+DEFimageServicePassword+"}")
    private String imageServicePassword;
    @Value("${"+DEFimageServiceMode+"}")
    private String imageServiceMode;
    @Value("${"+DEFimageServiceTempDir+"}")
    private String imageServiceTempDir;
    @Value("${"+DEFimageLocalImagePath+"}")
    private String imageLocalImagePath;
    @Value("${"+DEFimageUri+"}")
    private String imageUri;
    @Value("${"+DEFimagePhotosLocalImagePath+"}")
    private String imagePhotosLocalImagePath;
    @Value("${"+DEFimagePhotosUri+"}")
    private String imagePhotosUri;
    @Value("${"+DEFimagePluginsLocalImagePath+"}")
    private String imagePluginsLocalImagePath;
    @Value("${"+DEFimagePluginsUri+"}")
    private String imagePluginsUri;


    @Value("${"+DEFimageProjektePath+"}")
    private String imageProjektePath;
    @Value("${"+DEFimageProjekteUri+"}")
    private String imageProjekteUri;

    //mathe-Service
    @Value("${"+DEFmatheHttpPort+"}")
    private int matheHttpPort;
    @Value("${"+DEFmatheAjpPort+"}")
    private int matheAjpPort;
    @Value("${"+DEFmatheHttpsPort+"}")
    private int matheHttpsPort;
    @Value("${"+DEFmatheServiceUri+"}")
    private String matheServiceUri;

    //demo-Service
    @Value("${"+DEFdemoHttpPort+"}")
    private int demoHttpPort;
    @Value("${"+DEFdemoAjpPort+"}")
    private int demoAjpPort;
    @Value("${"+DEFdemoHttpsPort+"}")
    private int demoHttpsPort;
    @Value("${"+DEFdemoServiceUri+"}")
    private String demoServiceUri;

    //mail-Service
    @Value("${"+DEFmailHttpPort+"}")
    private int mailHttpPort;
    @Value("${"+DEFmailAjpPort+"}")
    private int mailAjpPort;
    @Value("${"+DEFmailHttpsPort+"}")
    private int mailHttpsPort;
    @Value("${"+DEFmailServiceUri+"}")
    private String mailServiceUri;

    //Login-Service
    @Value("${"+DEFloginHttpPort+"}")
    private int loginHttpPort;
    @Value("${"+DEFloginAjpPort+"}")
    private int loginAjpPort;
    @Value("${"+DEFloginHttpsPort+"}")
    private int loginHttpsPort;
    @Value("${"+DEFloginServiceUri+"}")
    private String loginServiceUri;

    // Setup-Service
    @Value("${"+DEFsetupHttpPort+"}")
    private int setupHttpPort;
    @Value("${"+DEFsetupAjpPort+"}")
    private int setupAjpPort;
    @Value("${"+DEFsetupHttpsPort+"}")
    private int setupHttpsPort;
    @Value("${"+DEFsetupServiceUri+"}")
    private String setupServiceUri;

    // license-Service
    @Value("${"+DEFlicenseHttpPort+"}")
    private int licenseHttpPort;
    @Value("${"+DEFlicenseAjpPort+"}")
    private int licenseAjpPort;
    @Value("${"+DEFlicenseHttpsPort+"}")
    private int licenseHttpsPort;
    @Value("${"+DEFlicenseServiceUri+"}")
    private String licenseServiceUri;

    // print-Service
    @Value("${"+DEFprintHttpPort+"}")
    private int printHttpPort;
    @Value("${"+DEFprintAjpPort+"}")
    private int printAjpPort;
    @Value("${"+DEFprintHttpsPort+"}")
    private int printHttpsPort;
    @Value("${"+DEFprintServiceUri+"}")
    private String printServiceUri;

    // export-Service
    @Value("${"+DEFexportHttpPort+"}")
    private int exportHttpPort;
    @Value("${"+DEFexportAjpPort+"}")
    private int exportAjpPort;
    @Value("${"+DEFexportHttpsPort+"}")
    private int exportHttpsPort;
    @Value("${"+DEFexportServiceUri+"}")
    private String exportServiceUri;

    // Beurteilungs-Service
    @Value("${"+DEFbeurteilungHttpPort+"}")
    private int beurteilungHttpPort;
    @Value("${"+DEFbeurteilungAjpPort+"}")
    private int beurteilungAjpPort;
    @Value("${"+DEFbeurteilungHttpsPort+"}")
    private int beurteilungHttpsPort;
    @Value("${"+DEFbeurteilungServiceUri+"}")
    private String beurteilungServiceUri;

    // Test-Service
    @Value("${"+DEFtestHttpPort+"}")
    private int testHttpPort;
    @Value("${"+DEFtestAjpPort+"}")
    private int testAjpPort;
    @Value("${"+DEFtestHttpsPort+"}")
    private int testHttpsPort;
    @Value("${"+DEFtestServiceUri+"}")
    private String testServiceUri;

    // Question-Service
    @Value("${"+DEFquestionHttpPort+"}")
    private int questionHttpPort;
    @Value("${"+DEFquestionAjpPort+"}")
    private int questionAjpPort;
    @Value("${"+DEFquestionHttpsPort+"}")
    private int questionHttpsPort;
    @Value("${"+DEFquestionServiceUri+"}")
    private String questionServiceUri;

    // plugin-Service
    @Value("${"+DEFpluginHttpPort+"}")
    private int pluginHttpPort;
    @Value("${"+DEFpluginAjpPort+"}")
    private int pluginAjpPort;
    @Value("${"+DEFpluginHttpsPort+"}")
    private int pluginHttpsPort;
    @Value("${"+DEFpluginServiceUri+"}")
    private String pluginServiceUri;

    // pluginsourcecode-Service
    @Value("${"+DEFpluginsourcecodeHttpPort+"}")
    private int pluginsourcecodeHttpPort;
    @Value("${"+DEFpluginsourcecodeAjpPort+"}")
    private int pluginsourcecodeAjpPort;
    @Value("${"+DEFpluginsourcecodeHttpsPort+"}")
    private int pluginsourcecodeHttpsPort;
    @Value("${"+DEFpluginsourcecodeServiceUri+"}")
    private String pluginsourcecodeServiceUri;

    // plugintester-Service
    @Value("${"+DEFplugintesterHttpPort+"}")
    private int plugintesterHttpPort;
    @Value("${"+DEFplugintesterAjpPort+"}")
    private int plugintesterAjpPort;
    @Value("${"+DEFplugintesterHttpsPort+"}")
    private int plugintesterHttpsPort;
    @Value("${"+DEFplugintesterServiceUri+"}")
    private String plugintesterServiceUri;

    //Letto-Edit-Service
    @Value("${"+DEFlettoEditHttpPort+"}")
    private int lettoEditHttpPort;
    @Value("${"+DEFlettoEditAjpPort+"}")
    private int lettoEditAjpPort;
    @Value("${"+DEFlettoEditHttpsPort+"}")
    private int lettoEditHttpsPort;
    @Value("${"+DEFlettoEditServiceUri+"}")
    private String lettoEditServiceUri;

    //Letto-App-Service
    @Value("${"+DEFlettoAppHttpPort+"}")
    private int lettoAppHttpPort;
    @Value("${"+DEFlettoAppAjpPort+"}")
    private int lettoAppAjpPort;
    @Value("${"+DEFlettoAppHttpsPort+"}")
    private int lettoAppHttpsPort;
    @Value("${"+DEFlettoAppServiceUri+"}")
    private String lettoAppServiceUri;


    // Frontend Edit
    @Value("${"+DEFlettoFrontendEditServiceUri+"}")
    private String lettoFrontendEditServiceUri;

    //Lehrplan-Service
    @Value("${"+DEFlehrplanHttpPort+"}")
    private int lehrplanHttpPort;
    @Value("${"+DEFlehrplanAjpPort+"}")
    private int lehrplanAjpPort;
    @Value("${"+DEFlehrplanHttpsPort+"}")
    private int lehrplanHttpsPort;
    @Value("${"+DEFlehrplanServiceUri+"}")
    private String lehrplanServiceUri;

    // Letto-Data-Service
    @Value("${"+DEFlettodataHttpPort+"}")
    private int lettodataHttpPort;
    @Value("${"+DEFlettodataAjpPort+"}")
    private int lettodataAjpPort;
    @Value("${"+DEFlettodataHttpsPort+"}")
    private int lettodataHttpsPort;
    @Value("${"+DEFlettodataServiceUri+"}")
    private String lettodataServiceUri;
    //@Value("${"+DEF+"}")
    @Value("${"+DEFlettodataRedirectTokenUri+"}")
    private String lettodataRedirectTokenUri;

    // erste Schule oder die eine Schule bei nur einer Schule. idSchule vom Lizenzserver
    @Value("${"+DEFschuleStandardIdSchuleLizenz+"}")
    private String schuleStandardIdSchuleLizenz;
    @Value("${"+DEFschuleStandardIdSchuleData+"}")
    private String schuleStandardIdSchuleData;
    @Value("${"+DEFschuleStandardSchulename+"}")
    private String schuleStandardSchulename;
    @Value("${"+DEFschuleStandardLettoDataUri+"}")
    private String schuleStandardLettoDataUri;
    @Value("${"+DEFschuleStandardLettoDataUser+"}")
    private String schuleStandardLettoDataUser;
    @Value("${"+DEFschuleStandardLettoDataPassword+"}")
    private String schuleStandardLettoDataPassword;
    @Value("${"+DEFschuleStandardLettoUri+"}")
    private String schuleStandardLettoUri;
    @Value("${"+DEFschuleStandardLizenz+"}")
    private String schuleStandardLizenz;
    @Value("${"+DEFschuleStandardLettoLoginUriExtern+"}")
    private String schuleStandardLettoLoginUriExtern;
    @Value("${"+DEFschuleStandardLettoUriExtern+"}")
    private String schuleStandardLettoUriExtern;
    @Value("${"+DEFschulen+"}")
    private String schulen;
    @Value("${"+DEFschule+"}")
    private String schule;

    public WebSecurityConfig webSecurityConfig;

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
        webSecurityConfig.setJwtSecret(jwtSecret,jwtExpiration,this);
    }

    public void setJwtExpiration(long jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
        webSecurityConfig.setJwtSecret(jwtSecret,jwtExpiration,this);
    }

    /**
     * Daten von den Konfigurationsdateien laden und Bean Initialisieren<br>
     * Im Fehlerfall ins Logfile, auf die Kommandozeile loggen bzw. ggf mit einer Exception die Bean beenden.
     */
    public BaseMicroServiceConfiguration() {
        //super(PROPERTIES_FILE);
    }




    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        String keystoreFile = keyStore.trim();
        try {
            /*File keystore;
            //keystore = ResourceUtils.getFile(keystoreFile);
            if (keystoreFile.startsWith("classpath:")) {
                int bufferSize = 16384;
                InputStream inputStream = ResourceUtils.getURL(keystoreFile).openStream();
                keystore = new File("lettolocal.p12");
                OutputStream outputStream = new FileOutputStream(keystore);

                byte[] buffer = new byte[bufferSize];

                while (inputStream.read(buffer) != -1) {
                    outputStream.write(buffer);
                }
            } else keystore = new File(keystoreFile);*/

            //protocol.setTruststoreFile(truststore.getAbsolutePath());
            //protocol.setTruststorePass(keyStorePassword);
            //protocol.setSSLCertificateKeyFile();

            return factory -> {
                Ssl ssl = new Ssl();
                ssl.setKeyStoreType(getKeyStoreType()); // Anpassen des Typs des Keystores "PKCS12"
                ssl.setKeyStore(keystoreFile); // Anpassen des Pfads zum Keystore "classpath:keystore.p12"
                ssl.setKeyAlias(getKeyAlias()); // Anpassen des Key-Alias im Keystore "alias"
                ssl.setKeyStorePassword(getKeyStorePassword()); // Anpassen des Keystore-Passworts
                ssl.setKeyPassword(getKeyStorePassword()); // Anpassen des Key-Passworts

                factory.setSsl(ssl);
            };
        }
        catch (Exception ex) {
            throw new IllegalStateException("can't access keystore: [" + keystoreFile, ex);
        }
    }

    /** @return HTTPS-Connector */
    public Connector createSslConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            //String keystoreFile = getPropertyString("key-store").trim();
            String keystoreFile = keyStore.trim();
            File keystore;
            //keystore = ResourceUtils.getFile(keystoreFile);
            if (keystoreFile.startsWith("classpath:")) {
                int bufferSize = 16384;
                InputStream inputStream = ResourceUtils.getURL(keystoreFile).openStream();
                keystore = new File("lettolocal.p12");
                OutputStream outputStream = new FileOutputStream(keystore);

                byte[] buffer = new byte[bufferSize];

                while (inputStream.read(buffer) != -1) {
                    outputStream.write(buffer);
                }
            } else keystore = new File(keystoreFile);
            //File truststore = new ClassPathResource(keyStore).getFile();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(port);
            protocol.setDefaultSSLHostConfigName(getKeyAlias());
            protocol.setSSLEnabled(true);

            /*protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass(getKeyStorePassword());
            protocol.setKeystoreType(getKeyStoreType());
            protocol.setKeyAlias(getKeyAlias());*/

            //protocol.setTruststoreFile(truststore.getAbsolutePath());
            //protocol.setTruststorePass(keyStorePassword);
            //protocol.setSSLCertificateKeyFile();

            return connector;
        }
        catch (IOException ex) {
            throw new IllegalStateException("can't access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }

    /** @return AJP-Connector */
    public Connector createAjpConnector(int port) {
        Connector connector = new Connector("AJP/1.3");
        connector.setScheme("http");
        connector.setPort(port);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        ((AbstractAjpProtocol) connector.getProtocolHandler()).setSecretRequired(false);
        return connector;
    }

    /** @return HTTP-Connector */
    public Connector createStandardConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        return connector;
    }

    public List<String> getSchulen() {
        List<String> ret = new ArrayList<>();
        for (String s:schulen.split("[\\,\\;\\s]+"))
            if (s.trim().length()>0) ret.add(s);
        return ret;
    }

    public void setSchulen(List<String> schulenList) {
        String sl = "";
        for (String s:schulenList)
            if (s.trim().length()>0)
                sl += (sl.length()>0?" ":"")+s;
        schulen = sl;
    }

    public List<String> getUserlist() {
        List<String> ret = new ArrayList<>();
        for (String s:userlist.split("[\\,\\;\\s]+"))
            if (s.trim().length()>0) ret.add(s);
        return ret;
    }

    public void setUserlist(List<String> userList) {
        String sl = "";
        for (String s:userList)
            if (s.trim().length()>0)
                sl += (sl.length()>0?" ":"")+s;
        userlist = sl;
    }

    public String redirect(HttpServletRequest request, String endpoint) {
        if (endpoint==null) endpoint="";
        if (!endpoint.startsWith("/")) endpoint = "/"+endpoint;
        return "redirect:"+baseUrl(request)+endpoint;
    }

    public String baseUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString().trim();
        Matcher m;
        boolean usehttp = useHttp!=null && useHttp.equals("1");
        String protocol = "http"+(!usehttp?"s":"")+"://";
        if ((m=Pattern.compile("^(https?)(:/+[^/]+)/(.*)$").matcher(url)).find()) {
            return (usehttp?m.group(1):"https") + m.group(2);
        } else if ((m=Pattern.compile("^(https?)(:/+[^/]+)$").matcher(url)).find()) {
            return (usehttp?m.group(1):"https") + m.group(2);
        } else if ((m=Pattern.compile("^/?([^/]+)/(.*)$").matcher(url)).find()) {
            return protocol+m.group(1);
        } else if ((m=Pattern.compile("^/?([^/]+)$").matcher(url)).find()) {
            return protocol+m.group(1);
        } else {
            return "";
        }
    }


}
