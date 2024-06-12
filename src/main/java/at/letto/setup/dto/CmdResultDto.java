package at.letto.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ergebnis der Kommandoausführung
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmdResultDto {

    /** ID des Commandos als fortlaufende Nummer innerhalb des Services */
    public long    id;

    /** Gibt an ob das Kommando schon abgeschlossen wurde */
    public boolean finished;

    /** Ausgabe das Kommandos als HTML-formatierter Code */
    public String  htmlOutput;

}
