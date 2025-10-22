package at.letto.login.dto.servertoken;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HtmlRenderTokenRequest {


    /** Schulkürzel auf dem Server für die der generierte ServerToken funktionieren soll */
    private String school;

    /** Sprache */
    private String lang;

    /** Infos über die Tokenanfrage */
    private String infos;

    /** Infos über den User-Agent */
    private String userAgent;

    /** Fingerprint des Zugriffes */
    private String fingerprint;

    /** Cient-IP-Adresse */
    private String clientIpAddress;

}
