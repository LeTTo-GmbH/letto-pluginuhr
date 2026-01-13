package at.letto.login.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckLoginADResponseDto {

    /** Information ob Benutzername und Passwort gepasst haben */
    private boolean success=false;

    /** Schule in der dieser Benutzername vorhanden ist, wenn Benutzer und Passwort korrekt waren */
    private String school="";

    /** Information über die LDAP-Verbindung */
    private String connectionInfo="";

    /** Detailierte HTML-Information über den Loginvorgang */
    private String htmlInfo="";

}
