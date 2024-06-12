package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempTokenRequest {

    /** Secret des Dienstes welches die Anforderung des Tokens stellt */
    private String  secret;

    /** Temporärer Token welcher für die Token-Anforderung verwendet werden kann */
    private String  tempToken;

}
