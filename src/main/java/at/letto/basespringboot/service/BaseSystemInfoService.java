package at.letto.basespringboot.service;

import at.letto.tools.Cmd;
import at.letto.tools.Datum;
import at.letto.tools.ServerStatus;
import at.letto.tools.dto.LeTToServiceInfoDto;
import at.letto.tools.enums.Betriebssystem;
import lombok.Getter;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service liefert alle notwendigen Systeminformation eines laufenden Spring-Boot-Containers
 */
@Service
@Getter
public class BaseSystemInfoService {

    private String          startTime            = Datum.formatDateTime(Datum.now());
    private long            startTimeDateInteger = Datum.toDateInteger(Datum.now());
    private long            startMilliseconds    = System.currentTimeMillis();
    private String          pid                  = new ApplicationPid().toString();
    private Betriebssystem  betriebssystem       = Betriebssystem.UNDEF;
    private String          betriebssystemVersion= "";
    private String          appHome              = new ApplicationHome().toString();;
    private boolean         debugMode         = ServerStatus.isDebug;
    private String          localIP           = ServerStatus.getIP();
    private String          hostname          = ServerStatus.getHostname();
    private String          linuxDistribution = ServerStatus.getLinuxDistribution();
    private String          linuxRelease      = ServerStatus.getLinuxRelease();
    private String          javaVendor        = ServerStatus.getJavaVendor();
    private String          javaVersionNumber = ServerStatus.getJavaVersionNumber();
    private String          javaSpecificationVersion = ServerStatus.getJavaSpecificationVersion();
    private String          springVersion     = SpringVersion.getVersion();
    private String          encoding          = ServerStatus.getEncoding();
    private String          fileEncoding      = ServerStatus.getFileEncoding();
    private String          userDir           = ServerStatus.getUserDir();
    private String          systemHome        = ServerStatus.getSystemHome();
    private String          language          = ServerStatus.getLanguage();
    private String          fileSeparator     = ServerStatus.getFileSeparator();
    private String          systemUsername    = ServerStatus.getServerUsername();
    protected String        cmdCharset        = ServerStatus.getEncoding();

    public BaseSystemInfoService() {
        if (ServerStatus.isUbuntu()) { betriebssystem = Betriebssystem.UBUNTU; betriebssystemVersion = ServerStatus.getBetriebssystem(); }
        else if (ServerStatus.isLinux()) { betriebssystem = Betriebssystem.LINUX; betriebssystemVersion = ServerStatus.getBetriebssystem(); }
        else if (ServerStatus.isWindows()) { betriebssystem = Betriebssystem.WINDOWS; betriebssystemVersion = ServerStatus.getBetriebssystem(); }
        else { betriebssystem = Betriebssystem.OTHER; betriebssystemVersion=ServerStatus.getBetriebssystem(); }
    }

    public String getJavaVersion() {
        return javaVendor + " " + javaVersionNumber;
    }

    public boolean isLinux()   { return betriebssystem == Betriebssystem.UBUNTU || betriebssystem==Betriebssystem.LINUX; }
    public boolean isUbuntu()  { return betriebssystem == Betriebssystem.UBUNTU; }
    public boolean isWindows() { return betriebssystem == Betriebssystem.WINDOWS; }
    public boolean isMac()     { return betriebssystem == Betriebssystem.MAC; }

    public boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket() ) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getPID(String suchstring) {
        if (isWindows()) {
            Pattern p = Pattern.compile("^java.exe\\s+(\\d+)\\s+.*");
            Matcher m;
            for (String s:Cmd.systemcall("tasklist").split("\\r?\\n\\r?")) {
                if ((m = p.matcher(s)).find()) {
                    pid += (pid.length() > 0 ? " " : "") + m.group(1);
                }
            }
            return pid;
        } else if (isLinux()) {
            String res = Cmd.systemcall("ps -ax");
            String pid = "";
            Pattern p = Pattern.compile("^\\s*(\\d+)[^\\d].*"+suchstring+".*");
            Matcher m;
            for (String line : res.split("\\r*\\n\\r*")) {
                if ((m = p.matcher(line)).find()) {
                    pid += (pid.length() > 0 ? " " : "") + m.group(1);
                }
            }
            return pid;
        } else {

        }
        return "";
    }

    /** Liefert den gesamten freien Speicher der Festplatte in der die Anwendung liegt */
    public long getTotalDiskSpace() {
        try {
            File f = new File(ServerStatus.getSystemHome());
            return f.getTotalSpace();
        } catch (Exception ex) {return 0;}
    }
    public long getFreeDiskSpace() {
        try {
            File f = new File(ServerStatus.getSystemHome());
            return f.getFreeSpace();
        } catch (Exception ex) {return 0;}
    }
    public long getUseableDiskSpace() {
        try {
            File f = new File(ServerStatus.getSystemHome());
            return f.getUsableSpace();
        } catch (Exception ex) {return 0;}
    }

    public int getCPUanzahl() {
        try {
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            return operatingSystemMXBean.getAvailableProcessors();
        } catch (Exception ex) {} catch (Error er){}
        return 0;
    }
    public double getUpTime() {
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            return runtimeMXBean.getUptime();
        } catch (Exception ex) {} catch (Error er){}
        return 0;
    }
    public long getMemoryInit() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getInit();
        } catch (Exception ex) {} catch (Error er){}
        return 0;
    }
    public long getMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getUsed();
        } catch (Exception ex) {} catch (Error er){}
        return 0;
    }
    public long getMemoryMax() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getMax();
        } catch (Exception ex) {} catch (Error er){}
        return 0;
    }
    public long getMemoryInitCommited() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getCommitted();
        } catch (Exception ex) {} catch (Error er){}
        return 0;
    }

    /** Liefert die Hauptversion von Java */
    public int getJavaMajorVersion() {
        Matcher m;
        String mv = getJavaMinorVersion();
        if ((m=Pattern.compile("^1\\.([0-9]+)").matcher(javaVersionNumber)).find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ex) {}
        }
        if ((m=Pattern.compile("^[^0-9]*([0-9]+)").matcher(javaVersionNumber)).find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ex) {}
        }
        return 0;
    }

    /** Liefert die UnterVersion von Java als String */
    public String getJavaMinorVersion() {
        Matcher m;
        if ((m=Pattern.compile("^[^0-9]*([0-9\\._]+)").matcher(javaVersionNumber)).find()) {
            return m.group(1);
        }
        return javaVersionNumber;
    }

    public String getServiceName() {
        return "Spring-Boot-Service";
    }

    public int getDebugPort() {
        try {
            List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            for (String arg : arguments) {
                if (arg.startsWith("-agentlib:jdwp")) {
                    String[] parts = arg.split(",");
                    for (String part : parts) {
                        if (part.startsWith("address=")) {
                            String address = part.split("=")[1];
                            if (address.contains(":")) {
                                address = address.split(":")[1];
                            }
                            return Integer.parseInt(address);
                        }
                    }
                }
            }
        } catch (Exception ex) {}
        return 0; // Kein Debugging-Port gefunden
    }

    public LeTToServiceInfoDto getLeTToServiceInfo() {
        LeTToServiceInfoDto dto = new LeTToServiceInfoDto(
                hostname,
                getLocalIP(),
                getHostname(),               // Container-Name
                getServiceName(),            // Korrekter Name des Services
                true,                        // Health-Zustand
                getStartTimeDateInteger(),
                new ArrayList<Long>(),       // Zeitpunkte der letzten Service-Starts als Date-Integer
                Datum.nowDateInteger(),
                (long)(getUpTime()/1000),    // korrekte Zeit wie lange das Service läuft
                0,                           // Dauer (in Sekunden) wie lange das Service beim vorherigen Start bis zum Neustart gelaufen ist
                0,                           // http
                0,                           // ajp
                0,                           // https
                getDebugPort(),              // debug
                ServerStatus.getRevision(),                          // Version
                getBetriebssystem().toString(),
                getBetriebssystemVersion(),
                getLinuxDistribution(),
                getLinuxRelease(),
                getPid(),
                isDebugMode(),
                isWindows(),
                isLinux(),
                isUbuntu(),
                isMac(),
                getJavaVersion(),
                getJavaVendor(),
                getJavaVersionNumber(),
                getJavaSpecificationVersion(),
                getJavaMajorVersion(),
                getJavaMinorVersion(),
                getSpringVersion(),
                getEncoding(),
                getFileEncoding(),
                getUserDir(),
                getSystemHome(),
                getLanguage(),
                getFileSeparator(),
                getSystemUsername(),
                getCmdCharset(),
                getCPUanzahl(),
                getMemoryInit(),
                getMemoryUsed(),
                getMemoryMax(),
                getMemoryInitCommited(),
                new HashMap<>()             // zusätzliche Infos über das Service
        );
        return dto;
    }

}
