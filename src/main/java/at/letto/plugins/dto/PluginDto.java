package at.letto.plugins.dto;

import at.letto.plugins.interfaces.PluginService;
import at.letto.tools.JSON;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.dto.ImageUrlDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dto welches vom Plugin über Letto zum Javascript des Plugins geschickt wird
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PluginDto {

    /** Url eines eingebetteten Bildes - meist base64 codiert */
    private String  imageUrl = "";

    /** True wenn das Plugin über ein PIG-Tag direkt in der Frage eingebunden ist */
    private boolean pig;

    /** True wenn Plugin in einer Subquestion definiert ist */
    private boolean result;

    /** Eindeutiger Bezeichner des PluginTags */
    private String  tagName = "";

    /** Breite des Plugin-Bereiches in Pixel */
    private int width = 500;

    /** Höhe des Plugin-Bereiches in Pixel */
    private int height = 500;

    /** Parameter welche vom Plugin an Javascript weitergegeben werden sollen,
     *  wird von LeTTo nicht verwendet */
    private HashMap<String,Object> params = new HashMap<>();

    /** Base64-codierter JSON-String welcher vom Plugin an Javascript weitergegeben werden soll,
     *  wird von LeTTo nicht verwendet */
    private String jsonData;

    public PluginDto(String params, PluginService pi, PluginQuestionDto q, int nr) {
        setSize(params);
        tagName = q.getId() + "_" + pi.getName() + "_" + nr;
        String src="";
        try {
            ImageUrlDto imageUrlDto = null;
            try {
                imageUrlDto = pi.getImageUrl(params, q);
            } catch (Exception ignore) {}
            if (imageUrlDto != null && imageUrlDto.getImageUrl() != null && imageUrlDto.getImageUrl().trim().length() > 0) {
                src = imageUrlDto.getImageUrl();
            } else {
                ImageBase64Dto imageBase64Dto = pi.getImageDto(params, q);
                src = "data:image/png;base64," + imageBase64Dto.getBase64Image();
            }
            setImageUrl(src);
        } catch (Exception ex) {}
        this.width = pi.getWidth();
        this.height = pi.getHeight();
    }

    public String toJson() {
        return JSON.objToJson(this);
    }

    public PluginDto(String params, String name, int nr, long idQuestion) {
        setSize(params);
        tagName = idQuestion + "_" + name + "_" + nr;
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
