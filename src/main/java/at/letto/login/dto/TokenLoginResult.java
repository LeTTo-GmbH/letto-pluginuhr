package at.letto.login.dto;

import at.letto.security.LettoToken;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenLoginResult {

    /** der neue Token als LettoToken */
    private LettoToken lettoToken;

    /** Meldung falls der Login nicht erfolgreich ist */
    private String  message;

    /** Ergebnis des Login-Vorgangs */
    private LOGINSTATUS loginStatus;

}
