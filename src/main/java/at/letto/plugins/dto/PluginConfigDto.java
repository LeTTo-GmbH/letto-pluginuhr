package at.letto.plugins.dto;

import at.letto.plugins.interfaces.PluginService;
import at.letto.tools.JSON;
import at.letto.tools.dto.ImageBase64Dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dto welches vom Plugin über Letto zum Javascript der Konfiguration des Plugins geschickt wird
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PluginConfigDto {

    /** Typ des Plugins */
    private String typ = "";

    /** Name des Plugins im Dialog */
    private String name = "";

    /** Konfigurationsstring */
    private String config = "";

    /** Eindeutiger Bezeichner des PluginTags */
    private String  tagName = "";

    /** Breite des Plugin-Bereiches in Pixel */
    private int width = 500;

    /** Höhe des Plugin-Bereiches in Pixel */
    private int height = 500;

    /** Configuration-ID */
    private String configurationID;

    /** Fehlermeldung wenn das DTO nicht korrekt erzeugt wurde */
    private String errorMsg;

    /** PluginDto für die Initialisierung des Plugins */
    private PluginDto pluginDto;

    /** Uri am Question-Service für das PluginDto */
    private String pluginDtoUri;

    /** Token welcher an der pluginDtoUri benötigt wird */
    private String pluginDtoToken;

    /** Parameter welche vom Plugin an Javascript weitergegeben werden sollen,
     *  wird von LeTTo nicht verwendet, kann aber gelesen und verändert werden */
    private HashMap<String,String> params = new HashMap<>();

    /** JSON-String welcher vom Plugin an Javascript weitergegeben werden soll,
     *  wird von LeTTo nicht verwendet */
    private String jsonData;

    public PluginConfigDto(String params, PluginService pi, PluginQuestionDto q) {
        setSize(params);
        tagName = pi.getName();
        this.width = pi.getWidth();
        this.height = pi.getHeight();
    }

    public String toJson() {
        return JSON.objToJson(this);
    }

    public PluginConfigDto(String params, String name) {
        setSize(params);
        tagName = name;
    }

    @JsonIgnore
    private void setSize(String params) {
        for (String p:params.split(",")) {
            Matcher m;
            if ((m= Pattern.compile("^size=(\\d+)x(\\d+)$").matcher(p)).find()) {
                int w = Integer.parseInt(m.group(1));
                int h = Integer.parseInt(m.group(2));
                width = w;
                height= h;
            } else if ((m=Pattern.compile("^size=(\\d+)$").matcher(p)).find()) {
                int w = Integer.parseInt(m.group(1));
                width = w;
                height= w;
            }
        }
    }

}
