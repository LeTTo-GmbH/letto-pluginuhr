package at.letto.basespringboot.controller;

import at.letto.restclient.endpoint.BaseEndpoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Ping Controller",
     description = "ping über REST an das Service - Muss von jedem Service realisiert werden" +
                   " [JavaDoc](https://build.letto.at/pluginuhr/open/javadoc/at/letto/basespringboot/controller/PingController.html)"
)
public class PingController {

    @Operation(summary = "Ping für den Verbindungs-check - Nur aus dem Docker-Netzwerk erreichbar!")
    @GetMapping(BaseEndpoints.PING)
    public ResponseEntity<String> ping()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check - Open",hidden=true)
    @GetMapping(BaseEndpoints.PING_OPEN)
    public ResponseEntity<String> pingOpen()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check. User-authentifiziert als Gast.",
            security = { @SecurityRequirement(name = "BasicAuth")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_AUTH_GAST)
    public ResponseEntity<String> pingAuthGast()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check. User-authentifiziert als User.",
            security = { @SecurityRequirement(name = "BasicAuth")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_AUTH_USER)
    public ResponseEntity<String> pingAuthUser()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check. User-authentifiziert als Admin.",
            security = { @SecurityRequirement(name = "BasicAuth")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_AUTH_ADMIN)
    public ResponseEntity<String> pingAuthAdmin()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check. User-authentifiziert als Global.",
            security = { @SecurityRequirement(name = "BasicAuth")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_AUTH_GLOBAL)
    public ResponseEntity<String> pingAuthGlobal()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(hidden = true)
    @GetMapping(BaseEndpoints.PING_AUTH_LETTO)
    public ResponseEntity<String> pingAuthLetto()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check einer API-Verbindung",hidden=true)
    @GetMapping(BaseEndpoints.PING_API_OPEN)
    public ResponseEntity<String> pingApiOpen()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check einer API-Verbindung mit einem Student Token",
            security = { @SecurityRequirement(name = "None")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_API_STUDENT)
    public ResponseEntity<String> pingApiStudent()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check einer API-Verbindung mit einem Teacher Token",
            security = { @SecurityRequirement(name = "None")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_API_TEACHER)
    public ResponseEntity<String> pingApiTeacher()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check einer API-Verbindung mit einem Admin Token",
            security = { @SecurityRequirement(name = "None")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_API_ADMIN)
    public ResponseEntity<String> pingApiAdmin()  {
        return ResponseEntity.ok("pong");
    }

    @Operation(summary = "Ping für den Verbindungs-check einer API-Verbindung mit einem Global-Admin Token",
            security = { @SecurityRequirement(name = "None")},hidden=true
    )
    @GetMapping(BaseEndpoints.PING_API_GLOBAL)
    public ResponseEntity<String> pingApiGlobal()  {
        return ResponseEntity.ok("pong");
    }

}
