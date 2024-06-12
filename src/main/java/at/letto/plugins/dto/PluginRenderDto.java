package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRenderDto {

    /** Latex Sourcecode oder HTML-Sourcecode */
    private String source ="";

    /** Liste der Bilder welche im Sourcode eingebunden werden. Sie werden im Verzeichnis img/ relativ zum
     * latex-Sourcode positioniert. Müssen deshalb auch dementsprechend im Latex-Sourcode eingebunden sein.
     * Zulässig sind nur Bilder mit der Extension .png, .jpg, .jpeg, .pdf<br>
     * key   : Dateiname<br>
     * value : Base64 codiertes Bild<br>
     */
    private HashMap<String,String> images = new HashMap<>();

}
