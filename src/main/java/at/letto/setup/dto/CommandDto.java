package at.letto.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandDto {

    /** Nach der Anzahl Millisekunden wird die Ausführung immer abgebrochen, bei 0 gibt es kein timeout */
    public long timeoutMillis;

    /** Verzeichnis in dem die Ausführung gestartet wird */
    public String basedir;

    /** Kommandos welche ausgeführt werden sollen */
    public String[] commands;

}
