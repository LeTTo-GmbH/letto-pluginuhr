package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckLoginADRequestDto {

    /** Benutzername */
    private String  username;

    /** Passwort im Klartext */
    private String  password;

    /** AD-Server */
    private String  serverAD="";

    /** LADP-Server */
    private String  serverLDAP="";

    /** Domain */
    private String  domain="";

    /** LDAP-Parameter */
    private String  params="";

}
