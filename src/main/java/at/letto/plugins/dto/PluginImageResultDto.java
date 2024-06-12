package at.letto.plugins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PluginImageResultDto {

    /** Nur wenn ok true ist, ist das Bild auch wirklich korrekt gerendert worden */
    private boolean ok = true;
    /** Liste aller Fehlermeldungen, welche beim Rendern des Bildes aufgetreten sind */
    private List<String> messages = new ArrayList<String>();

}
