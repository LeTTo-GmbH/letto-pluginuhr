package at.letto.login.dto;

import at.letto.security.LettoToken;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenResult {

    /** der neue Token als LettoToken */
    private LettoToken lettoToken;

    /** Ergebnis der Token-Validierung des alten Tokens */
    private TokenValidationResult validationResult;

}
