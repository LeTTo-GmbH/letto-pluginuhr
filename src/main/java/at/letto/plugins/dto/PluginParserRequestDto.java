package at.letto.plugins.dto;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.VarHashDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginParserRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Alle Variablen der Frage */
    private VarHashDto vars;

    /** Berechnungsparameter */
    private CalcParamsDto cp;

    /** Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können */
    private CalcErgebnisDto[] p;

}
