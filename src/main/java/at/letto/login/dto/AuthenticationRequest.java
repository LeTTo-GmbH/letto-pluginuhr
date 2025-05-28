package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO für die Authentifizierungsanfrage.
 * Enthält die Anmeldedaten des Benutzers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    private String  username;
    private String  password;
    private String  school;
    private String  fingerprint="";
    private String  ipAddress="";
    private String  service="";     // Service welcher die Authentifizierung anfordert, z.B. "letto-login", "letto-edit", "letto-admin" etc.
    private String  infos="";       // zusätzliche Informationen über den Client, wer, was, wo, warum
    private String  userAgent = ""; // User-Agent des Clients, z.B. "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"

}
