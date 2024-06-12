package at.letto.setup.dto.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ergebnis der Registratur eines Services */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterServiceResultDto {

    /** Registration erfolgreich */
    private boolean registrationOK;

    /** Gibt an ob das Service neu registriert wurde oder schon vorhanden war */
    private boolean newRegistered;

    /** Gibt die Anzahl der insgesamt registrierten Services dieses Plugin-Services an */
    private int registrationCounter;

    /** Rückmeldung wenn ein Fehler aufgetreten ist */
    private String msg;

}
