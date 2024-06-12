package at.letto.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileBase64Dto {

    /** Dateiname der Originaldatei */
    private String name="";

    /** Dateierweiterung */
    private String extension="";

    /** md5-Summe des Dateiinhaltes */
    private String md5="";

    /** Base64 codierter Inhalt */
    private String base64="";

}
