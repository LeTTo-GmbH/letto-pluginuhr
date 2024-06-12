package at.letto.basespringboot.controller;

import at.letto.dto.ServiceInfoDTO;
import at.letto.restclient.endpoint.BaseEndpoints;
import at.letto.restclient.endpoint.InfoControllerInterface;
import at.letto.service.microservice.AdminInfoDto;
import at.letto.tools.Datum;
import at.letto.tools.ServerStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.Setter;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping
public class BaseInfoController {

    @Autowired private ApplicationContext applicationContext;

    @Setter private InfoControllerInterface infoControllerInterface;

    public File getJarFile() {
        if (infoControllerInterface!=null) {
            String className = infoControllerInterface.getMainClass().getName().replace('.', '/');
            String classJar = infoControllerInterface.getMainClass().getResource("/" + className + ".class").toString();
            Matcher m;
            if ((m = Pattern.compile("file:([^\\!]+)\\!").matcher(classJar)).find()) {
                File file = new File(m.group(1));
                if (file.exists()) return file;
            }
        }
        return null;
    }

    public String getJarFileName() {
        File file = getJarFile();
        if (file!=null) return file.getName();
        return "";
    }

    public List<String> getLibJars(){
        List<String> libs = new ArrayList<>();
        File jarFile = getJarFile();
        try {
            JarFile jf = new JarFile(jarFile);
            // Holen Sie sich die Enumeration der JAR-Einträge (Dateien und Verzeichnisse)
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("BOOT-INF/lib/")) name = name.substring(13);
                if (name.endsWith(".jar")) {
                    libs.add(name.substring(0,name.length()-4));
                }
            }
        } catch (Exception ex) {}
        Collections.sort(libs);
        return libs;
    }

    public String getTomeeRevision() {
        Matcher m;
        String version = "";
        if (infoControllerInterface!=null) {
            ApplicationContext context = infoControllerInterface.getContext();
            TomcatWebServer tomcatWebServer = (TomcatWebServer) ((AnnotationConfigServletWebServerApplicationContext) context).getWebServer();
            Tomcat tomcat = tomcatWebServer.getTomcat();
            String servername = tomcat.getService().getName();
            String implementationVersion = tomcat.getClass().getPackage().getImplementationVersion();
            if (implementationVersion==null || implementationVersion.trim().length()==0) {
                List<String> libs = getLibJars();
                Pattern p = Pattern.compile("^(tomcat-.*)-([0-9\\.]+)");
                for (String lib:libs) {
                    if ((m=p.matcher(lib)).find()) {
                        if (m.group(1).equals("tomcat-embed-core"))
                            return m.group(2);
                        version=m.group(2);
                    }
                }
            }
        }
        return version;
    }

    public AdminInfoDto calcAdminInfo() {
        String pid = new ApplicationPid().toString();
        String applicationname = applicationContext.getId();
        String applicationhome = new ApplicationHome().toString();
        long uptime = System.currentTimeMillis()-applicationContext.getStartupDate();
        String serverversion = "";
        String springBootCoreVersion = SpringVersion.getVersion();
        String springBootStarterVersion = "";
        try {
            if (infoControllerInterface!=null) {
                springBootStarterVersion = infoControllerInterface.getManifest().getMainAttributes().getValue("Spring-Boot-Version");

                ApplicationContext context = infoControllerInterface.getContext();
                if (springBootStarterVersion==null)
                    springBootStarterVersion = ((AnnotationConfigServletWebServerApplicationContext) context).getWebServer().getClass().getPackage().getImplementationVersion();
                if (springBootStarterVersion==null) springBootStarterVersion="";

                TomcatWebServer tomcatWebServer = (TomcatWebServer)((AnnotationConfigServletWebServerApplicationContext) context).getWebServer();
                Tomcat tomcat = tomcatWebServer.getTomcat();
                String state  = tomcat.getServer().getStateName();
                String servername = tomcat.getService().getName();

                String implementationVersion = getTomeeRevision();
                serverversion = servername+" "+implementationVersion;
            }
        } catch (Exception ex) {}
        serverversion = "Spring-Boot Core:"+ springBootCoreVersion+", Starter: "+springBootStarterVersion+(serverversion!=null && serverversion.length()>0?", "+serverversion:"");
        AdminInfoDto adminInfoDto = new AdminInfoDto(
                applicationname,
                pid,
                applicationhome,
                applicationContext.getStartupDate(),
                uptime,
                ServerStatus.getRevision(),
                Datum.formatDateTime(Datum.nowLocalDateTime()), // (new Date()).toString(),
                ServerStatus.getBetriebssystem(),
                ServerStatus.getIPs(),
                ServerStatus.getEncoding(),
                ServerStatus.getFileEncoding(),
                ServerStatus.getFileSeparator(),
                ServerStatus.getJavaSpecificationVersion(),
                ServerStatus.getJavaVendor(),
                ServerStatus.getJavaVersion(),
                ServerStatus.getJavaVersionNumber(),
                ServerStatus.getHostname(),
                ServerStatus.getLanguage(),
                ServerStatus.getLinuxDescription(),
                ServerStatus.getLinuxDistribution(),
                ServerStatus.getLinuxRelease(),
                ServerStatus.getServerUsername(),
                serverversion,
                ServerStatus.getSystemHome(),
                ServerStatus.isLinux(),
                ServerStatus.isUbuntu(),
                ServerStatus.isWindows(),
                0,0,0
        );
        if (infoControllerInterface!=null)
            infoControllerInterface.setInfo(adminInfoDto);
        return adminInfoDto;
    }

    public ServiceInfoDTO calcInfo(boolean admin) {
        ServiceInfoDTO serviceInfoDTO = new ServiceInfoDTO(
                applicationContext.getId(), // Name des Services
                ServerStatus.getRevision(), // Version des Services
                "LeTTo GmbH",               // Information über den Autor des Services
                "(c) LeTTo GmbH" ,          // Information über die Lizenz des Services
                "",                         // Information über die Endpoints des Services
                getJarFileName(),
                Datum.formatDateTime(new Date(applicationContext.getStartupDate())),
                null, null
        );
        if (admin) {
            AdminInfoDto adminInfoDto = calcAdminInfo();
            serviceInfoDTO.setAdminInfoDto(adminInfoDto);
            serviceInfoDTO.setJarLibs(getLibJars());
        }
        if (infoControllerInterface!=null)
            infoControllerInterface.setInfo(serviceInfoDTO,admin);
        return serviceInfoDTO;
    }

    @Operation(summary = "Information über das Service - Nur aus dem Docker-Netzwerk erreichbar!")
    @GetMapping(BaseEndpoints.INFO)
    public ResponseEntity<ServiceInfoDTO> info()  {
        ServiceInfoDTO serviceInfoDTO = calcInfo(false);
        return ResponseEntity.ok(serviceInfoDTO);
    }

    @Operation(summary = "Information über das Service - Nur aus dem Docker-Netzwerk erreichbar!")
    @GetMapping(BaseEndpoints.INFO_OPEN)
    public ResponseEntity<ServiceInfoDTO> infoOpen()  {
        ServiceInfoDTO serviceInfoDTO = calcInfo(false);
        return ResponseEntity.ok(serviceInfoDTO);
    }

    @Operation(summary = "Information über das Service - Nur aus dem Docker-Netzwerk erreichbar! Authentifiziert als User admin",
            security = { @SecurityRequirement(name = "BasicAuth")})
    @GetMapping(BaseEndpoints.INFO_AUTH_ADMIN)
    public ResponseEntity<ServiceInfoDTO> infoAuthAdmin()  {
        ServiceInfoDTO serviceInfoDTO = calcInfo(true);
        return ResponseEntity.ok(serviceInfoDTO);
    }

    @Operation(summary = "Information über das Service - Nur aus dem Docker-Netzwerk erreichbar! Authentifiziert mit Admin-Token",
            security = { @SecurityRequirement(name = "BasicAuth")})
    @GetMapping(BaseEndpoints.INFO_API_ADMIN)
    public ResponseEntity<ServiceInfoDTO> infoApiAdmin()  {
        ServiceInfoDTO serviceInfoDTO = calcInfo(true);
        return ResponseEntity.ok(serviceInfoDTO);
    }

    @Operation(summary = "Information über die Service-Version - Nur aus dem Docker-Netzwerk erreichbar!")
    @GetMapping(BaseEndpoints.VERSION)
    public ResponseEntity<String> version()  {
        return ResponseEntity.ok(ServerStatus.getRevision());
    }

}
