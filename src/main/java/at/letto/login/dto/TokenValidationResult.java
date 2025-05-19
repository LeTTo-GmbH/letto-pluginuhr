package at.letto.login.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TokenValidationResult {

    /** Gibt an ob der Token gültig ist */
    private boolean valid;
    /** Meldung falls der Token ungültig ist */
    private String  message;
    /** Ergebnis des Validierungs-Vorgangs */
    private LOGINSTATUS loginStatus;

}
