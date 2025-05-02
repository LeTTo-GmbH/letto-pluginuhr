package at.letto.setup.endpoints;

public class SetupEndpoint {
    public static final String LeTToIcon = "lettoTT";   // .svg .png .ico

    /* Endpoints für die Verwendung als lokale Setup-Instanz direkt am Host */
    public static final String SETUP_LOCAL           = "/setup";
    public static final String SETUP_DOCKER          = "/config";
    public static final String LOCAL_ERROR           = SETUP_LOCAL+"/error";
    public static final String DOCKER_ERROR          = SETUP_DOCKER+"/error";
    public static final String LOCAL_OPEN            = SETUP_LOCAL+"/open";
    public static final String DOCKER_OPEN           = SETUP_DOCKER+"/open";
    public static final String LOCAL_CSS             = LOCAL_OPEN+"/css";
    public static final String DOCKER_CSS            = DOCKER_OPEN+"/css";
    public static final String LOCAL_IMAGES          = LOCAL_OPEN+"/images";
    public static final String DOCKER_IMAGES         = DOCKER_OPEN+"/images";
    public static final String LOCAL_DOC             = LOCAL_OPEN+"/doc";
    public static final String DOCKER_DOC            = DOCKER_OPEN+"/doc";
    public static final String LOCAL_style           = LOCAL_CSS +"/style.css";
    public static final String style                 = DOCKER_CSS+"/style.css";
    public static final String LOCAL_icon            = LOCAL_IMAGES+"/"+LeTToIcon;
    public static final String icon                  = DOCKER_IMAGES+"/"+LeTToIcon;
    public static final String LOCAL_fonts           = LOCAL_OPEN+"/fonts";
    public static final String DOCKER_fonts          = DOCKER_OPEN+"/fonts";
    public static final String LOCAL_fontawesome     = LOCAL_OPEN+"/fontawesome";
    public static final String DOCKER_fontawesome    = DOCKER_OPEN+"/fontawesome";
    public static final String LOCAL_js              = LOCAL_OPEN+"/js";
    public static final String DOCKER_js             = DOCKER_OPEN+"/js";
    public static final String jsLettoSetupTools     = "/lettosetuptools.js";
    public static final String jsJQuery              = "/jquery-3.6.0.min.js";
    public static final String jsBootstrap           = "/bootstrap.min.js"; //4.3.1
    public static final String cssBootstrap          = "/bootstrap.min.css"; //4.3.1
    public static final String jsChart               = "/chart.min.js"; //3.9.1
    public static final String LOCAL_head_include    = LOCAL_OPEN+"/head_include.html";
    public static final String DOCKER_head_include   = DOCKER_OPEN+"/head_include.html";
    public static final String LOCAL_logo10          = LOCAL_IMAGES+"/letto2-10.png";//"/LeTTo_Logo_animated_10.gif";
    public static final String logo10                = DOCKER_IMAGES+"/letto2-10.png";//"/LeTTo_Logo_animated_10.gif";
    public static final String LOCAL_logo20          = LOCAL_IMAGES+"/letto2-20.png";//"/LeTTo_Logo_animated_20.gif";
    public static final String logo20                = DOCKER_IMAGES+"/letto2-20.png";//"/LeTTo_Logo_animated_20.gif";
    public static final String LOCAL_logo50          = LOCAL_IMAGES+"/letto2-50.png";//"/LeTTo_Logo_animated_50.gif";
    public static final String logo50                = DOCKER_IMAGES+"/letto2-50.png";//"/LeTTo_Logo_animated_50.gif";
    public static final String LOCAL_logo100         = LOCAL_IMAGES+"/letto2.png";//"/LeTTo_Logo_animated_100.gif";
    public static final String logo100               = DOCKER_IMAGES+"/letto2.png";//"/LeTTo_Logo_animated_100.gif";
    public static final String LOCAL_AUTH            = SETUP_LOCAL+"/auth";
    public static final String DOCKER_AUTH           = SETUP_DOCKER+"/auth";
    public static final String LOCAL_GAST            = LOCAL_AUTH+"/gast";
    public static final String DOCKER_GAST           = DOCKER_AUTH+"/gast";
    public static final String LOCAL_USER            = LOCAL_AUTH+"/user";
    public static final String DOCKER_USER           = DOCKER_AUTH+"/user";
    public static final String LOCAL_ADMIN           = LOCAL_AUTH+"/admin";
    public static final String DOCKER_ADMIN          = DOCKER_AUTH+"/admin";
    public static final String LOCAL_GLOBAL          = LOCAL_AUTH+"/global";
    public static final String DOCKER_GLOBAL         = DOCKER_AUTH+"/global";
    public static final String LOCAL_LETTO           = LOCAL_AUTH+"/letto";
    public static final String DOCKER_LETTO          = DOCKER_AUTH+"/letto";

    // Login
    public static final String LOCAL_login           = LOCAL_OPEN + "/login";
    public static final String DOCKER_login          = DOCKER_OPEN + "/login"; // Login mit User Gast,User,Admin,Letto
    public static final String LOCAL_loginletto      = LOCAL_OPEN  + "/loginletto";  // Login mit LeTTo-User
    public static final String DOCKER_loginletto     = DOCKER_OPEN + "/loginletto";  // Login mit LeTTo-User
    public static final String LOCAL_logout          = LOCAL_OPEN  + "/logout";
    public static final String DOCKER_logout         = DOCKER_OPEN + "/logout";
    public static final String LOCAL_tokenlogin      = LOCAL_OPEN  + "/tokenlogin";
    public static final String DOCKER_tokenlogin     = DOCKER_OPEN + "/tokenlogin";

    /* ----------------------------------------------------------------------------------------------------
                        API mit Token-Authentifikation
       ---------------------------------------------------------------------------------------------------- */
    public static final String API         = SETUP_DOCKER+"/api";   // User-Token Authentifikation Stateless
    public static final String API_OPEN    = API+"/open";
    public static final String API_STUDENT = API+"/student";
    public static final String API_TEACHER = API+"/teacher";
    public static final String API_ADMIN   = API+"/admin";
    public static final String API_GLOBAL  = API+"/global";

    public static final String LOCALAPI    = SETUP_LOCAL+"/api";
    public static final String LOCALAPI_OPEN    = LOCALAPI+"/open";
    public static final String LOCALAPI_STUDENT = LOCALAPI+"/student";
    public static final String LOCALAPI_TEACHER = LOCALAPI+"/teacher";
    public static final String LOCALAPI_ADMIN   = LOCALAPI+"/admin";
    public static final String LOCALAPI_GLOBAL  = LOCALAPI+"/global";

    public static final String lehrer      = API_TEACHER + "/lehrer";
    public static final String schueler    = API_STUDENT + "/schueler";
    public static final String DOCKER_pingadmin  = API_ADMIN+"/ping";
    public static final String LOCAL_pingadmin   = LOCALAPI_ADMIN+"/ping";

    /* ----------------------------------------------------------------------------------------------------
                        SESSION LOCAL am HOST
       ---------------------------------------------------------------------------------------------------- */
    // Setup Local
    public static final String SESSION_LOCAL       = SETUP_LOCAL  +"/session";
    public static final String SL_welcome          = SESSION_LOCAL+"/welcome";
    public static final String SL_ADMIN            = SESSION_LOCAL + "/admin";
    public static final String SL_TEACHER          = SESSION_LOCAL + "/teacher";
    public static final String SL_STUDENT          = SESSION_LOCAL + "/student";
    public static final String SL_GLOBAL           = SESSION_LOCAL + "/global";
    public static final String welcomeanalyze      = SESSION_LOCAL + "/welcomeanalyze";
    public static final String install             = SESSION_LOCAL + "/install";
    public static final String analyze             = SESSION_LOCAL + "/analyze";
    public static final String SL_fileedit         = SESSION_LOCAL + "/fileedit";
    public static final String SL_update           = SL_ADMIN + "/update";
    public static final String SL_serverToken      = SL_ADMIN + "/servertoken";
    public static final String SL_plugins          = SL_ADMIN + "/plugins";
    public static final String SL_users            = SL_ADMIN + "/users";
    public static final String SL_doc              = SL_ADMIN + "/doc";
    public static final String SL_fileview         = SESSION_LOCAL + "/fileview";
    public static final String SL_reload           = SESSION_LOCAL + "/reload";
    public static final String SL_cmdoutput        = SL_ADMIN + "/cmdoutput";
    public static final String SL_cmd              = SL_ADMIN + "/cmd";
    public static final String SL_mysqlcmd         = SL_ADMIN + "/mysqlcmd";
    public static final String SL_emailconfig      = SL_ADMIN + "/emailconfig";
    public static final String SL_backup           = SL_ADMIN + "/backup";
    public static final String SL_backup_upload    = SL_ADMIN + "/backupupload";
    public static final String SL_explorer         = SL_ADMIN + "/explorer";
    public static final String SL_explorer_upload  = SL_ADMIN + "/explorerupload";
    public static final String SL_globalDownload   = SL_GLOBAL + "/download";
    public static final String SL_sicherungLocal   = SESSION_LOCAL+"/sicherunglocal";
    public static final String SL_uploadlettosql   = SESSION_LOCAL+"/uploadlettosql";
    public static final String SL_uploadltisql     = SESSION_LOCAL+"/uploadltisql";
    public static final String SL_dockerInitEnv    = SL_ADMIN + "/dockerinitenv";
    public static final String SL_dockerLettoStatus= SL_ADMIN + "/dockerlettostatus";
    public static final String SL_systemStatus     = SL_ADMIN + "/systemstatus";
    public static final String SL_dockerLettoProxy = SL_ADMIN + "/dockerlettoproxy";
    public static final String SL_dockerEditSchool = SL_ADMIN + "/dockereditschool";
    public static final String SL_schoolConfig     = SL_ADMIN + "/schoolconfig";

     /* ----------------------------------------------------------------------------------------------------
                        SESSION DOCKER
       ---------------------------------------------------------------------------------------------------- */
    // Setup Docker
    public static final String SESSION             = SETUP_DOCKER+"/session";
    public static final String SESSION_ADMIN       = SESSION + "/admin";
    public static final String SESSION_TEACHER     = SESSION + "/teacher";
    public static final String SESSION_STUDENT     = SESSION + "/student";
    public static final String SESSION_GLOBAL      = SESSION + "/global";
    public static final String DOCKER_welcome      = SESSION + "/welcome";
    public static final String reload              = SESSION + "/reload";
    public static final String sicherungLocal      = SESSION+"/sicherunglocal";
    public static final String cmd                 = SESSION_ADMIN + "/cmd";
    public static final String mysqlcmd            = SESSION_ADMIN + "/mysqlcmd";
    public static final String emailconfig         = SESSION_ADMIN + "/emailconfig";
    public static final String DOCKER_backup       = SESSION_ADMIN + "/backup";
    public static final String DOCKER_backup_upload= SESSION_ADMIN + "/backupupload";
    public static final String DOCKER_explorer     = SESSION_ADMIN + "/explorer";
    public static final String DOCKER_explorer_upload = SESSION_ADMIN + "/explorerupload";
    public static final String DOCKER_analyze      = SESSION + "/analyze";
    public static final String DOCKER_welcomeanalyze = SESSION + "/welcomeanalyze";
    public static final String cmdoutput           = SESSION_ADMIN + "/cmdoutput";
    public static final String dockerInitEnv       = SESSION_ADMIN + "/dockerinitenv";
    public static final String dockerEditSchool    = SESSION_ADMIN + "/dockereditschool";
    public static final String schoolConfig  = SESSION_ADMIN + "/schoolconfig";
    public static final String dockerLettoStatus   = SESSION_ADMIN + "/dockerlettostatus";
    public static final String systemStatus        = SESSION_ADMIN + "/systemStatus";
    public static final String dockerLettoProxy    = SESSION_ADMIN + "/dockerlettoproxy";
    public static final String fileedit            = SESSION + "/fileedit";
    public static final String DOCKER_update       = SESSION_ADMIN + "/update";
    public static final String DOCKER_serverToken  = SESSION_ADMIN + "/servertoken";
    public static final String DOCKER_plugins      = SESSION_ADMIN + "/plugins";
    public static final String DOCKER_users        = SESSION_ADMIN + "/users";
    public static final String DOCKER_doc          = SESSION_ADMIN + "/doc";
    public static final String fileview            = SESSION + "/fileview";
    public static final String authDownload        = SESSION + "/download";
    public static final String globalDownload      = SESSION_GLOBAL + "/download";
    public static final String adminDownload       = SESSION_ADMIN  + "/download";
    public static final String teacherDownload     = SESSION_TEACHER+ "/download";
    public static final String studentDownload     = SESSION_STUDENT+ "/download";

    // Alle notwendigen Ping-Punkte für den Service-Test
    public static final String DOCKER_ping                = SETUP_DOCKER+"/ping";
    public static final String DOCKER_pingpost            = SETUP_DOCKER+"/pingp";
    public static final String DOCKER_pingget             = SETUP_DOCKER+"/pingg";
    public static final String LOCAL_ping                 = SETUP_LOCAL+"/ping";
    public static final String LOCAL_pingpost             = SETUP_LOCAL+"/pingp";
    public static final String LOCAL_pingget              = SETUP_LOCAL+"/pingg";

    /* ----------------------------------------------------------------------------------------------------
                       Open - API-Endpoints für AJAX - Javascript GUI
      ---------------------------------------------------------------------------------------------------- */
    public static final String AJAX_OPEN_LOCAL           = LOCAL_OPEN+"/ajax";
    public static final String AJAX_OPEN_DOCKER          = DOCKER_OPEN+"/ajax";
    private static final String AJAX_ANALYZEDOCKER        = "/analyzedocker";
    public static final String AJAX_LOCAL_ANALYZEDOCKER  = AJAX_OPEN_LOCAL  + AJAX_ANALYZEDOCKER;
    public static final String AJAX_DOCKER_ANALYZEDOCKER = AJAX_OPEN_DOCKER + AJAX_ANALYZEDOCKER;

    /* ----------------------------------------------------------------------------------------------------
                       Session-authenticated - API-Endpoints für AJAX - Javascript GUI
      ---------------------------------------------------------------------------------------------------- */
    public static final String AJAX_TOKEN_ADMIN_LOCAL    = SL_ADMIN+"/ajax"; // LOCAL_ADMIN+"/ajax";
    public static final String AJAX_TOKEN_ADMIN_DOCKER   = SESSION_ADMIN+"/ajax"; //DOCKER_ADMIN+"/ajax";
    private static final String AJAX_EDITSCHOOL_SETBUTTONS       = "/editschoolsetbuttons";
    public static final String AJAX_LOCAL_EDITSCHOOL_SETBUTTONS  = AJAX_TOKEN_ADMIN_LOCAL  + AJAX_EDITSCHOOL_SETBUTTONS;
    public static final String AJAX_DOCKER_EDITSCHOOL_SETBUTTONS = AJAX_TOKEN_ADMIN_DOCKER + AJAX_EDITSCHOOL_SETBUTTONS;
    private static final String AJAX_EDITSCHOOL_CHECKDATABASE       = "/editschoolcheckdatabase";
    public static final String AJAX_LOCAL_EDITSCHOOL_CHECKDATABASE  = AJAX_TOKEN_ADMIN_LOCAL  + AJAX_EDITSCHOOL_CHECKDATABASE;
    public static final String AJAX_DOCKER_EDITSCHOOL_CHECKDATABASE = AJAX_TOKEN_ADMIN_DOCKER + AJAX_EDITSCHOOL_CHECKDATABASE;
    private static final String AJAX_WELCOMEINFO        = "/welcomeinfo";
    // public static final String AJAX_LOCAL_WELCOMEINFO  = AJAX_OPEN_LOCAL  + AJAX_WELCOMEINFO;
    // public static final String AJAX_DOCKER_WELCOMEINFO = AJAX_OPEN_DOCKER + AJAX_WELCOMEINFO;
    public static final String AJAX_LOCAL_WELCOMEINFO  = AJAX_TOKEN_ADMIN_LOCAL   + AJAX_WELCOMEINFO;
    public static final String AJAX_DOCKER_WELCOMEINFO = AJAX_TOKEN_ADMIN_DOCKER + AJAX_WELCOMEINFO;
    private static final String AJAX_CMDOUTPUT       = "/cmdoutput";
    public static final String AJAX_LOCAL_CMDOUTPUT  = AJAX_TOKEN_ADMIN_LOCAL  + AJAX_CMDOUTPUT;
    public static final String AJAX_DOCKER_CMDOUTPUT = AJAX_TOKEN_ADMIN_DOCKER + AJAX_CMDOUTPUT;
    /* ----------------------------------------------------------------------------------------------------
                       Open-Endpoints im DOCKER-Container
      ---------------------------------------------------------------------------------------------------- */
    // Download
    public static final String openDownload        = DOCKER_OPEN + "/download";

    // Open Endpoints
    public static final String version             = DOCKER_OPEN +"/version";
    public static final String info                = DOCKER_OPEN +"/info";
    public static final String home                = DOCKER_OPEN +"/home";

    /* ----------------------------------------------------------------------------------------------------
                       Endpoints im DOCKER-Container mit User-Authentifikation
      ---------------------------------------------------------------------------------------------------- */
    public static final String infoletto           = DOCKER_LETTO+"/infoletto";

    // API-Endpoints für user admin
    public static final String infoadmin           = DOCKER_ADMIN+"/info";
    public static final String admininfo           = DOCKER_ADMIN+"/admininfo";
    public static final String imageadmininfo      = DOCKER_ADMIN+"/imageadmininfo";
    public static final String checkServiceStatus  = DOCKER_ADMIN+"/checkservicestatus";
    public static final String deactivatemain      = DOCKER_ADMIN+"/deactivatemain";
    public static final String getContainerVersion = DOCKER_ADMIN+"/getContainerVersion";

    // REST-Endpoints für Infos zu anderen Services
    public static final String getSchulen          = DOCKER_USER+"/getschulen";
    public static final String postSchule          = DOCKER_USER+"/postschule";
    public static final String getRestKey          = DOCKER_USER+"/getrestkey";
    public static final String setSchoolLicense    = DOCKER_USER+"/setschoollicense";

    // Config-REST-Endpoints
    public static final String getPlugins          = DOCKER_USER+"/getplugins";
    public static final String getServices         = DOCKER_USER+"/getservices";
    public static final String getService          = DOCKER_USER+"/getservice";
    public static final String getSchuleService    = DOCKER_USER+"/getschuleservice";
    public static final String registerPlugin      = DOCKER_USER+"/registerplugin";
    public static final String registerService     = DOCKER_USER+"/registerservice";
    public static final String checkPassword       = DOCKER_USER+"/checkpassword";

    /* ----------------------------------------------------------------------------------------------------
                       API-Endpoints am lokalen HOST mit User-Authentifikation
      ---------------------------------------------------------------------------------------------------- */
    public static final String LOCAL_user_ping          = LOCAL_USER +"/ping";
    public static final String LOCAL_admin_ping         = LOCAL_ADMIN+"/ping";
    public static final String LOCAL_cmd                = LOCAL_USER+"/cmd";
    public static final String LOCAL_setupDockerStart   = LOCAL_USER+"/setupdockerstart";
    public static final String LOCAL_setupDockerRestart = LOCAL_USER+"/setupdockerrestart";
    public static final String LOCAL_setupDockerStop    = LOCAL_USER+"/setupdockerstop";
    public static final String LOCAL_setupDockerUpdate  = LOCAL_USER+"/setupdockerupdate";
    public static final String LOCAL_lettoDockerStart   = LOCAL_USER+"/lettodockerstart";
    public static final String LOCAL_lettoDockerRestart = LOCAL_USER+"/lettodockerrestart";
    public static final String LOCAL_lettoDockerStop    = LOCAL_USER+"/lettodockerstop";
    public static final String LOCAL_lettoDockerUpdate  = LOCAL_USER+"/lettodockerupdate";
    public static final String checkServiceStatusLocal  = LOCAL_ADMIN+"/checkservicestatus";
    public static final String postSchuleLocal          = LOCAL_USER+"/postschule";

    /* ----------------------------------------------------------------------------------------------------
                       Docker interne Endpoints ohne Authentifizierung
      ---------------------------------------------------------------------------------------------------- */


}
