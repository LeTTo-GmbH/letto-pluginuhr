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

}
