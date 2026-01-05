package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckLoginResponseDto {

    /** Information ob Benutzername und Passwort gepasst haben */
    private boolean success=false;

    /** Schule auf der der Login gepasst hat */
    private String school="";

}
