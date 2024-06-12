package at.open.letto.plugin.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IFrameConfigDto {

    /** Button den der Benutzer gedrückt hat */
    private String userAction;

    /** Typ des Plugins */
    private String typ;

    /** Konfigurations ID */
    private String configurationID;

    /** Konfigurations-String */
    private String config="";

    /** Parameter für PIG oder Input */
    private String params="";

    public IFrameConfigDto(String typ, String configurationID) {
        this.configurationID=configurationID;
        this.typ = typ;
    }

}
