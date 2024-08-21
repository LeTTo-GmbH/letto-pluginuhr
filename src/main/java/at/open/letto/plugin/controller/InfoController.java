package at.open.letto.plugin.controller;

import at.letto.service.microservice.AdminInfoDto;
import at.letto.tools.ServerStatus;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.config.TomcatConfiguration;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * allgemeine Information und externer Ping (von extern erreichbar)
 */
@RestController
@RequestMapping
public class InfoController {

    private final ApplicationContext applicationContext;

    private final TomcatConfiguration tomcatConfiguration;

    public InfoController(ApplicationContext applicationContext, TomcatConfiguration tomcatConfiguration) {
        this.applicationContext = applicationContext;
        this.tomcatConfiguration = tomcatConfiguration;
    }

    @PostMapping(Endpoint.ping)
    public ResponseEntity<String> pingPost()  {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(Endpoint.ping)
    public ResponseEntity<String> pingGet()   {
        return ResponseEntity.ok("pong");
    }

    @PostMapping(Endpoint.pingpost)
    public ResponseEntity<String> pingP(@RequestBody String dto)  {
        if (dto.equals("ping")) return ResponseEntity.ok("pong");
        return ResponseEntity.ok("fail");
    }

    @GetMapping(Endpoint.pingget)
    public ResponseEntity<String> pingG(@RequestParam final String dto)  {
        if (dto.equals("ping")) return ResponseEntity.ok("pong");
        return ResponseEntity.ok("fail");
    }

    /** Liefert die Version des Services */
    @GetMapping(Endpoint.version)
    public ResponseEntity<String> version()  {
        return ResponseEntity.ok(ServerStatus.getRevision());
    }

    /** Liefert Information über das Service */
    @GetMapping(Endpoint.info)
    public ResponseEntity<String> info()  {
        String applicationname = applicationContext.getId();
        String msg = "Application: "+applicationname;
        return ResponseEntity.ok(msg);
    }

    /** Liefert Information über das Service für den Admin*/
    @GetMapping(Endpoint.infoletto)
    public ResponseEntity<AdminInfoDto> lettoinfo()  {
        return admininfo();
    }

    @GetMapping(Endpoint.infoadmin)
    public ResponseEntity<AdminInfoDto> admininfo()  {
        String pid = new ApplicationPid().toString();
        String applicationname = applicationContext.getId();
        String applicationhome = new ApplicationHome().toString();
        long uptime = System.currentTimeMillis()-applicationContext.getStartupDate();
        String serverversion = ServerStatus.getServerVersion();
        serverversion = "Spring Boot "+SpringVersion.getVersion()+(serverversion!=null && serverversion.length()>0?" , "+serverversion:"");
        AdminInfoDto ret = new AdminInfoDto(
                applicationname,
                pid,
                applicationhome,
                applicationContext.getStartupDate(),
                uptime,
                ServerStatus.getRevision(),
                (new Date()).toString(),
                ServerStatus.getBetriebssystem(),
                ServerStatus.getIP(),
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
                tomcatConfiguration.getHttpPort(),
                tomcatConfiguration.getAjpPort(),
                0
        );
        return ResponseEntity.ok(ret);
    }

}
