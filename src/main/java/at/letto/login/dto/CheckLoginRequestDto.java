package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckLoginRequestDto {

    /** Benutzername */
    private String  username;

    /** Passwort im Klartext */
    private String  password;

    /** Schule oder leer wenn an allen Schulen getestet werden soll */
    private String  school;

}
