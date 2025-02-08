package at.letto.tools.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class LeTToServiceInfoDto {

    /** eindeutige Adresse des Services - Wird als KEY mit service:info:KEY verwendet!! */
    private String serviceAddress;

    /** IP-Adresse des Containers */
    private String ipAddress;

    /** Containername des Docker-Containers (eindeutig auf einem Serverknoten */
    private String containerName;

    /** Name des Services zB. login,export,etc. */
    private String serviceName;

    /** Zustand des Containers */
    private boolean healthy=true;

    /** DateInteger des Service-Starts */
    private long startTime;

    /** Zeitpunkte der letzten Service-Starts als Date-Integer */
    private List<Long> lastServiceStarts = new ArrayList<Long>();

    /** DateInteger des letzten Info-Updates */
    private long infoUpdateTime;

    /** Dauer wie lange das Service vom Start bis zum letzten Check gelaufen ist (in Sekunden) */
    private long runningTime;

    /** Dauer (in Sekunden) wie lange das Service beim vorherigen Start bis zum Neustart gelaufen ist */
    private long lastRunningTime;

    /** TCP-Port auf dem das Service über http erreichbar ist */
    private int httpPort;

    /** TCP-Port auf dem das Service über ajp(http) erreichbar ist */
    private int ajpPort;

    /** TCP-Port auf dem das Service über https erreichbar ist */
    private int httpsPort;

    /** TCP-Port auf dem das Service über Remote-Debugging erreichbar ist */
    private int debugPort;

    /** Version des Services */
    private String version;

    /** aktuelles  Betriebssystems */
    private String betriebssystem;

    /** Version des Betriebssystems */
    private String betriebssystemVersion= "";

    /** Linux Distribution */
    private String linuxDistribution;

    /** Linux Release */
    private String linuxRelease;

    /** aktuelle PID */
    private String          pid;

    /** Debugging eingeschaltet? */
    private boolean         debugMode;

    private boolean isWindows;
    private boolean isLinux;
    private boolean isUbuntu;
    private boolean isMacOS;

    /** Java-Version */
    private String javaVersion;

    private String javaVendor ;
    private String javaVersionNumber;
    private String javaSpecificationVersion;
    private int    javaMajorVersion;
    private String javaMinorVersion;
    private String springVersion;
    private String encoding;
    private String fileEncoding;
    private String userDir;
    private String systemHome;
    private String language;
    private String fileSeparator;
    private String systemUsername;
    private String cmdCharset;
    private int    cpuAnzahl;
    private long   memoryInit;
    private long   memoryUsed;
    private long   memoryMax;
    private long   memoryInitCommited;

    /** Allgemeine Infos über das Service können, aber müssen nicht verwendet werden */
    private HashMap<String,String> infos = new HashMap<>();


}
