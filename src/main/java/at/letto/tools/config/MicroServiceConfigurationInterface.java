package at.letto.tools.config;

import at.letto.security.SecurityConstants;
import java.util.List;

/** Interface für die Konfigurationsdaten aller Microservices */
public interface MicroServiceConfigurationInterface {

    // ----------------------------------------------------------------------------
    // Definition der Logfiles
    // ----------------------------------------------------------------------------
    String DEFlogfilePath = "letto_log_path:/opt/letto/log";
    /** @return Verzeichnispfad für alle Logfiles des Services */
    String getLogfilePath();

    String DEFlogfileLogin = "letto_log_login:login.log";
    /** @return Dateiname des Logfile für alle Login und Logout-Vorgänge */
    String getLogfileLogin();

    String DEFlogfileError = "letto_log_error:error.log";
    /** @return Dateiname des Logfile für alle Fehler die auftreten */
    String getLogfileError();

    String DEFlogfileStart = "letto_log_error:start.log";
     /** @return Dateiname des Logfile für Start-Stop-Status des Services */
    String getLogfileStart();

    String DEFlettoLogLevel = "letto_log_level:NORMAL";
    /** @return Standard-Log-Level des Services */
    String getLettoLogLevel();

    // ----------------------------------------------------------------------------
    // Pfade
    // ----------------------------------------------------------------------------
    String DEFlettoPath = "LETTO_PATH:/opt/letto";
    /** @return Verzeichnispfad für Letto allgemein */
    String getLettoPath();

    String DEFsetupComposePath = "SETUP_COMPOSE:/opt/letto/docker/compose/setup";
    /** @return Verzeichnispfad für alle docker-configs von setup */
    String getSetupComposePath();

    String DEFlettoComposePath = "LETTO_COMPOSE:/opt/letto/docker/compose/letto";
    /** @return Verzeichnispfad für alle docker-configs von setup */
    String getLettoComposePath();

    String DEFhostBetriebssystem = "HOST_BS:undefined";
    /** @return Betriebssystem des Host bei Docker */
    String getHostBetriebssystem();

    String DEFpathSeperator = "PATH_SEPERATOR:/";
    /** @return Pfad-Trennzeichen des Host bei Docker */
    String getPathSeperator();

    String DEFserverName = "servername:";
    /** @return Servername des Host bei Docker */
    String getServerName();

    // ----------------------------------------------------------------------------
    // Security
    // ----------------------------------------------------------------------------
    String DEFuseHttp  = "use_http:0";
    /** @return Gibt an ob beim Redirect http(1) oder https(0,Standard) genutzt werden soll */
    String getUseHttp();

    String DEFlettoUID = "LETTO_UID:1000";
    /** @return Verzeichnispfad für alle Logfiles des Services */
    String getLettoUID();

    String DEFjwtSecret = "jwt_secret:"+SecurityConstants.JWT_SECRET;
    /** @return Secret für die JWT-Tokens des Services */
    String getJwtSecret();

    String DEFjwtExpiration = "jwt_expiration:"+SecurityConstants.EXPIRATION_TIME;
    /* @return Standard Lebensdauer eines JWT-Tokens */
    long getJwtExpiration();

    String DEFjwtAppExpiration = "jwt_app_expiration:"+SecurityConstants.EXPIRATION_TIME_APP;
    /* @return Standard Lebensdauer eines JWT-Tokens */
    long getJwtAppExpiration();

    String DEFjwtRefreshTime = "jwt_refresh_time:"+SecurityConstants.REFRESH_TIME;
    /* @return Ist die restliche Lebensdauer eines JWT-Tokens kleiner als RefreshTime sollte der Token aktualisiert werden */
    long getJwtRefreshTime();

    String DEFserverSecret = "server_secret:"+SecurityConstants.SERVER_SECRET;
    /* @return Standard - Server-Secret für interne Server-Kommmunikation  */
    String getServerSecret();

    String DEFshortTempTokenAge = "jwt_temptoken_age_short:10";
    /** @return kurze Lebensdauer für temporäre Tokens in Sekunden */
    long getShortTempTokenAge();

    String DEFmediumTempTokenAge = "jwt_temptoken_age_medium:600";
    /** @return mittlere Lebensdauer für temporäre Tokens in Sekunden */
    long getMediumTempTokenAge();

    String DEFlongTempTokenAge = "jwt_temptoken_age_long:7300";
    /** @return lange Lebensdauer für temporäre Tokens in Sekunden */
    long getLongTempTokenAge();

    // ----------------------------------------------------------------------------
    // Schlüssel
    // ----------------------------------------------------------------------------

    String DEFrestkey = "letto_local_restkey:";
    /** @return Schlüssel welcher den Server am Lizenzserver identifiziert */
    String getRestkey();

    String DEFlocalPrivateKey = "letto_local_privatkey:";
    /** @return privateKey des Servers für asynchrone Kommunikation */
    String getLocalPrivateKey();

    String DEFlocalPublicKey = "letto_local_publickey:";
    /** @return public Key des Servers für asynchrone Kommunikation */
    String getLocalPublicKey();

    String DEFlicensePublicKey = "letto_licenseserver_publickey:";
    /** @return Public Key vom Lizenzserver für asynchrone Kommunikation mit dem Lizenzserver */
    String getLicensePublicKey();

    String DEFlicenseServer = "letto_license_server:https://letto.at";
    /** @return liefert die Uri von Lizenzserver von LeTTo */
    String getLicenseServer();

    // ----------------------------------------------------------------------------
    // Keystore
    // ----------------------------------------------------------------------------
    String DEFkeyStore = "key-store:classpath:keystore/lettolocal.p12";
    /** @return Keystore-Datei für ssl-Verbindungen*/
    String getKeyStore();
    
    String DEFkeyStorePassword = "key-store-password:lettoserver";
    /** @return Keystore Passwort für die Keystore Datei*/
    String getKeyStorePassword();
    
    String DEFkeyStoreType = "key-store-type:PKCS12";
    /** @return Keystore Typ der Keystore Datei*/
    String getKeyStoreType();
    
    String DEFkeyAlias = "key-alias:lettolocal";
    /** @return Alias für den Keystore*/
    String getKeyAlias();

    // ----------------------------------------------------------------------------
    // Benutzerkonfiguration aus der application.properties
    // ----------------------------------------------------------------------------
    String DEFuserlist = "letto_user:";
    /** @return Liste aller Benutzer welche auf den Dienst direkt zugreifen können */
    List<String> getUserlist();

    String DEFuserGastPassword = "letto_user_gast_password:";
    /** @return Passwort der Benutzers "gast" im Klartext  */
    String getUserGastPassword();
    
    String DEFuserGastEncryptedPassword = "letto_user_gast_encryptedpassword:";
    /** @return Passwort des Benutzers "gast" encrypted */
    String getUserGastEncryptedPassword();
    
    String DEFuserGastRoles = "letto_user_gast_roles:";
    /** @return Alle Rollen des Benutzers gast */
    String getUserGastRoles();

    String DEFuserUserPassword = "letto_user_user_password:xyz";
    /** @return Passwort der Benutzers "user" im Klartext */
    String getUserUserPassword();
    
    String DEFuserUserEncryptedPassword = "letto_user_user_encryptedpassword:";
    /** @return Passwort des Benutzers "user" encrypted */
    String getUserUserEncryptedPassword();
    
    String DEFuserUserRoles = "letto_user_user_roles:";
    /** @return Alle Rollen des Benutzers user */
    String getUserUserRoles();

    String DEFuserAdminPassword = "letto_user_admin_password:";
    /** @return Passwort der Benutzers "admin" im Klartext */
    String getUserAdminPassword();
    
    String DEFuserAdminEncryptedPassword = "letto_user_admin_encryptedpassword:";
    /** @return Passwort des Benutzers "admin" encrypted */
    String getUserAdminEncryptedPassword();
    
    String DEFuserAdminRoles = "letto_user_admin_roles:";
    /** @return Alle Rollen des Benutzers admin */
    String getUserAdminRoles();

    String DEFuserLettoPassword = "letto_user_letto_password:";
    /** @return Passwort der Benutzers "letto" im Klartext */
    String getUserLettoPassword();
    
    String DEFuserLettoEncryptedPassword = "letto_user_letto_encryptedpassword:";
    /** @return Passwort des Benutzers "letto" encrypted */
    String getUserLettoEncryptedPassword();
    
    String DEFuserLettoRoles = "letto_user_letto_roles:";
    /** @return Alle Rollen des Benutzers letto */
    String getUserLettoRoles();

    // ----------------------------------------------------------------------------
    // Konfiguration der Services
    // ----------------------------------------------------------------------------
    //LTI-Service
    String DEFltiHttpPort = "service_lti_http_port:8090";
    /** @return HTTP-Port des LTI-Services */
    int getLtiHttpPort();
    String DEFltiAjpPort = "service_lti_ajp_port:7090";
    /** @return AJP-Port des LTI-Services */
    int getLtiAjpPort();
    String DEFltiHttpsPort = "service_lti_https_port:9090";
    /** @return HTTPS-Port des LTI-Services */
    int getLtiHttpsPort();
    String DEFltiServiceUri = "letto_lti_uri:http://localhost:8090";
    /** @return LTI-Service URI Server-Intern */
    String getLtiServiceUri();
    String DEFltiRestKey = "letto_lti_restkey:oive9rvweve9rvb98vb23v898vbw";
    /** @return LTI-Service RestKey für die Kommunikation mit LeTTo */
    String getLtiRestKey();

    //image-Service
    String DEFimageHttpPort = "service_image_http_port:8091";
    /** @return HTTP-Port des Image-Service */
    int getImageHttpPort();    
    String DEFimageAjpPort = "service_image_ajp_port:7091";
    /** @return AJP-Port des Image-Services */
    int getImageAjpPort();
    String DEFimageHttpsPort = "service_image_https_port:9091";
    /** @return HTTPS-Port des Images-Services */
    int getImageHttpsPort();
    String DEFimageServiceUri = "letto_image_uri:http://localhost:8091";
    /** @return Image-Service URI Server-Intern*/
    String getImageServiceUri();
    String DEFimageServiceUser = "letto_image_user:user";
    /** @return Benutzer mit sich der Client am Image-Service anmeldet */
    String getImageServiceUser();
    String DEFimageServicePassword = "letto_image_password:wqEycXhK65pPL3";
    /** @return Passwort mit dem sich der Client am Image-Service anmeldet */
    String getImageServicePassword();
    String DEFimageServiceMode = "letto_image_mode:IMAGE";
    /** @return Mode mit de */
    String getImageServiceMode();
    String DEFimageServiceTempDir = "letto_image_tempdir:/tmp/imageservice";
    /** @return temporäres Verzeichnis für den download von Bildern am Client*/
    String getImageServiceTempDir();
    String DEFimageLocalImagePath = "service_image_local_image_path:/opt/letto/images";
    /** @return Lokaler Pfad der Bilder des Image-Services */
    String getImageLocalImagePath();
    String DEFimageUri = "service_image_uri:https://localhost/images";
    /** @return URL-Pfad der Bilder für externen Zugriff*/
    String getImageUri();
    String DEFimagePhotosLocalImagePath = "service_image_photos_local_image_path:/opt/letto/images/photos";
    String getImagePhotosLocalImagePath();
    String DEFimagePhotosUri = "service_image_photos_uri:https://localhost/photos";
    String getImagePhotosUri();
    String DEFimagePluginsLocalImagePath = "service_image_plugins_local_image_path:/opt/letto/images/plugins";
    String getImagePluginsLocalImagePath();
    String DEFimagePluginsUri = "service_image_plugins_uri:https://localhost/plugins";
    String getImagePluginsUri();

    String DEFimageProjektePath = "service_image_projekte_path:/opt/letto/projekte";
    String DEFimageProjekteUri =  "service_image_projekte_uri:https://localhost/projekte";

    //mathe-Service
    String DEFmatheHttpPort = "service_mathe_http_port:8092";
    int getMatheHttpPort();
    String DEFmatheAjpPort = "service_mathe_ajp_port:7092";
    int getMatheAjpPort();
    String DEFmatheHttpsPort = "service_mathe_https_port:9092";
    int getMatheHttpsPort();
    String DEFmatheServiceUri = "letto_mathe_uri:http://localhost:8092";
    String getMatheServiceUri();

    //demo-Service
    String DEFdemoHttpPort = "service_demo_http_port:8093";
    int getDemoHttpPort();
    String DEFdemoAjpPort = "service_demo_ajp_port:7093";
    int getDemoAjpPort();
    String DEFdemoHttpsPort = "service_demo_https_port:9093";
    int getDemoHttpsPort();
    String DEFdemoServiceUri = "letto_demo_uri:http://localhost:8093";
    String getDemoServiceUri();

    //mail-Service
    String DEFmailHttpPort = "service_mail_http_port:8094";
    int getMailHttpPort();
    String DEFmailAjpPort = "service_mail_ajp_port:7094";
    int getMailAjpPort();
    String DEFmailHttpsPort = "service_mail_https_port:9094";
    int getMailHttpsPort();
    String DEFmailServiceUri = "letto_mail_uri:http://localhost:8094";
    String getMailServiceUri();

    //Login-Service
    String DEFloginHttpPort = "service_login_http_port:8095";
    int getLoginHttpPort();
    String DEFloginAjpPort = "service_login_ajp_port:7095";
    int getLoginAjpPort();
    String DEFloginHttpsPort = "service_login_https_port:9095";
    int getLoginHttpsPort();
    String DEFloginServiceUri = "letto_login_uri:http://localhost:8095";
    String getLoginServiceUri();

    // Setup-Service
    String DEFsetupHttpPort = "service_setup_http_port:8096";
    int getSetupHttpPort();
    String DEFsetupAjpPort = "service_setup_ajp_port:7096";
    int getSetupAjpPort();
    String DEFsetupHttpsPort = "service_setup_https_port:9096";
    int getSetupHttpsPort();
    String DEFsetupServiceUri = "letto_setup_uri:https://localhost:3096";
    String getSetupServiceUri();

    // license-Service
    String DEFlicenseHttpPort = "service_license_http_port:8097";
    int getLicenseHttpPort();
    String DEFlicenseAjpPort = "service_license_ajp_port:7097";
    int getLicenseAjpPort();
    String DEFlicenseHttpsPort = "service_license_https_port:9097";
    int getLicenseHttpsPort();
    String DEFlicenseServiceUri = "letto_license_uri:http://localhost:8097";
    String getLicenseServiceUri();

    // print-Service
    String DEFprintHttpPort = "service_print_http_port:8098";
    int getPrintHttpPort();
    String DEFprintAjpPort = "service_print_ajp_port:7098";
    int getPrintAjpPort();
    String DEFprintHttpsPort = "service_print_https_port:9098";
    int getPrintHttpsPort();
    String DEFprintServiceUri = "letto_print_uri:http://localhost:8098";
    String getPrintServiceUri();

    // export-Service
    String DEFexportHttpPort = "service_export_http_port:8099";
    int getExportHttpPort();
    String DEFexportAjpPort = "service_export_ajp_port:7099";
    int getExportAjpPort();
    String DEFexportHttpsPort = "service_export_https_port:9099";
    int getExportHttpsPort();
    String DEFexportServiceUri = "letto_export_uri:http://localhost:8099";
    String getExportServiceUri();

    // Beurteilungs-Service
    String DEFbeurteilungHttpPort = "service_beurteilung_http_port:8100";
    int getBeurteilungHttpPort();
    String DEFbeurteilungAjpPort = "service_beurteilung_ajp_port:7100";
    int getBeurteilungAjpPort();
    String DEFbeurteilungHttpsPort = "service_beurteilung_https_port:9100";
    int getBeurteilungHttpsPort();
    String DEFbeurteilungServiceUri = "letto_beurteilung_uri:http://localhost:8100";
    String getBeurteilungServiceUri();

    // Test-Service
    String DEFtestHttpPort = "service_test_http_port:8101";
    int getTestHttpPort();
    String DEFtestAjpPort = "service_beurteilung_ajp_port:7101";
    int getTestAjpPort();
    String DEFtestHttpsPort = "service_beurteilung_https_port:9101";
    int getTestHttpsPort();
    String DEFtestServiceUri = "letto_beurteilung_uri:http://localhost:8101";
    String getTestServiceUri();

    // Question-Service
    String DEFquestionHttpPort = "service_question_http_port:8102";
    int getQuestionHttpPort();
    String DEFquestionAjpPort = "service_question_ajp_port:7102";
    int getQuestionAjpPort();
    String DEFquestionHttpsPort = "service_question_https_port:9102";
    int getQuestionHttpsPort();
    String DEFquestionServiceUri = "letto_question_uri:http://localhost:8102";
    String getQuestionServiceUri();


    // plugin-Service
    String DEFpluginHttpPort = "service_plugin_http_port:8200";
    int getPluginHttpPort();
    String DEFpluginAjpPort = "service_plugin_ajp_port:7200";
    int getPluginAjpPort();
    String DEFpluginHttpsPort = "service_plugin_https_port:9200";
    int getPluginHttpsPort();
    String DEFpluginServiceUri = "letto_plugin_uri:http://localhost:8200";
    String getPluginServiceUri();

    // pluginsourcecode-Service
    String DEFpluginsourcecodeHttpPort = "service_pluginsourcecode_http_port:8204";
    int getPluginsourcecodeHttpPort();
    String DEFpluginsourcecodeAjpPort = "service_pluginsourcecode_ajp_port:7204";
    int getPluginsourcecodeAjpPort();
    String DEFpluginsourcecodeHttpsPort = "service_pluginsourcecode_https_port:9204";
    int getPluginsourcecodeHttpsPort();
    String DEFpluginsourcecodeServiceUri = "letto_pluginsourcecode_uri:http://localhost:8204";
    String getPluginsourcecodeServiceUri();

    // plugintester-Service
    String DEFplugintesterHttpPort = "service_plugintester_http_port:8290";
    int getPlugintesterHttpPort();
    String DEFplugintesterAjpPort = "service_plugintester_ajp_port:7290";
    int getPlugintesterAjpPort();
    String DEFplugintesterHttpsPort = "service_plugintes_https_port:9290";
    int getPlugintesterHttpsPort();
    String DEFplugintesterServiceUri = "letto_plugintester_uri:http://localhost:8290";
    String getPlugintesterServiceUri();

    //Letto-Edit-Service
    String DEFlettoEditHttpPort = "service_edit_http_port:8103";
    int getLettoEditHttpPort();
    String DEFlettoEditAjpPort = "service_edit_ajp_port:7103";
    int getLettoEditAjpPort();
    String DEFlettoEditHttpsPort = "service_edit_https_port:9103";
    int getLettoEditHttpsPort();
    String DEFlettoEditServiceUri = "letto_edit_uri:http://localhost:8103";
    String getLettoEditServiceUri();

    //Letto-App-Service
    String DEFlettoAppHttpPort = "service_app_http_port:8199";
    int getLettoAppHttpPort();
    String DEFlettoAppAjpPort = "service_app_ajp_port:7199";
    int getLettoAppAjpPort();
    String DEFlettoAppHttpsPort = "service_app_https_port:9199";
    int getLettoAppHttpsPort();
    String DEFlettoAppServiceUri = "letto_app_uri:http://localhost:8199";
    String getLettoAppServiceUri();

    String DEFlettoFrontendEditServiceUri = "frontend_edit_uri:http://localhost:8080";
    String getLettoFrontendEditServiceUri();

    //Lehrplan-Service
    String DEFlehrplanHttpPort = "service_lehrplan_http_port:8104";
    int getLehrplanHttpPort();
    String DEFlehrplanAjpPort = "service_lehrplan_ajp_port:7104";
    int getLehrplanAjpPort();
    String DEFlehrplanHttpsPort = "service_lehrplan_https_port:9104";
    int getLehrplanHttpsPort();
    String DEFlehrplanServiceUri = "letto_lehrplan_uri:https://lehrplan.letto.at";
    String getLehrplanServiceUri();

    // Letto-Data-Service
    String DEFlettodataHttpPort = "service_lettodata_http_port:8300";
    int getLettodataHttpPort();
    String DEFlettodataAjpPort = "service_lettodata_ajp_port:7300";
    int getLettodataAjpPort();
    String DEFlettodataHttpsPort = "service_lettodata_https_port:9300";
    int getLettodataHttpsPort();
    String DEFlettodataServiceUri = "letto_lettodata_uri:http://localhost:8300";
    String getLettodataServiceUri();
    //String DEF = "letto_lettodata_redirecttokenuri:http://localhost:8096/setup/open/tokenlogin";
    String DEFlettodataRedirectTokenUri = "letto_lettodata_redirecttokenuri:http://localhost:8088/letto_war_exploded/loginTempToken.jsf";
    String getLettodataRedirectTokenUri();

    String DEFschule="letto_schule:";
    /** @return Name der Schule welche mit dem LeTTo-Service gehostet wird */
    String getSchule();
    String DEFschuleStandardLizenz = "letto_schule_standard_lizenz:";
    /** @return Lizenz der Schule*/
    String getSchuleStandardLizenz();
    String DEFschuleStandardIdSchuleLizenz = "letto_schule_standard_idschule_lizenz:0";
    /** @return id der Schule am Lizenzserver */
    String getSchuleStandardIdSchuleLizenz();
    String DEFschuleStandardIdSchuleData = "letto_schule_standard_idschule_data:0";
    /** @return ID der Schule an der MySQL-Datenbank */
    String getSchuleStandardIdSchuleData();
    String DEFschuleStandardSchulename = "letto_schule_standard_schulname:HTL-Testschule";
    /** @return Name der Schule ausgeschrieben */
    String getSchuleStandardSchulename();
    String DEFschuleStandardLettoDataUri = "letto_schule_standard_lettodata_uri:http://localhost:8300";
    /** @return URI das Data-Services Server-Intern */
    String getSchuleStandardLettoDataUri();
    String DEFschuleStandardLettoDataUser = "letto_schule_standard_lettodata_user:user";
    /** @return Benutzer mit sich ein Service auf das Data-Service verbinden soll */
    String getSchuleStandardLettoDataUser();
    String DEFschuleStandardLettoDataPassword = "letto_schule_standard_lettodata_password:wqEycXhK65pPL3";
    /** @return Passwort des Benutzers mit sich ein Service auf das Data-Service verbinden soll */
    String getSchuleStandardLettoDataPassword();
    String DEFschuleStandardLettoUri = "letto_schule_standard_letto_uri:https://localhost/letto";
    /** @return URI das LeTTo-Services Server-Intern */
    String getSchuleStandardLettoUri();
    String DEFschuleStandardLettoLoginUriExtern = "letto_schule_standard_login_uriextern:https://localhost/login";
    /** @return URI das Login-Services des Servers wo die Schule liegt von extern erreichbar */
    String getSchuleStandardLettoLoginUriExtern();
    String DEFschuleStandardLettoUriExtern = "letto_schule_standard_letto_uriextern:https://localhost/letto";
    /** @return URI das Letto-Services der Schule von extern erreichbar */
    String getSchuleStandardLettoUriExtern();

    /** Liste aller Schulen welche von dem Server gehostet werden als Namen der Schulen durch Leerzeichen getrennt */
    String DEFschulen = "letto_schulen:";
    /** @return Liste aller Schulen welche von dem Server gehostet werden als Namen der Schulen */
    List<String> getSchulen();

}
