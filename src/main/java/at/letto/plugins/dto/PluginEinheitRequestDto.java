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
public class PluginEinheitRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Einheiten der Parameter als Recheneinheiten */
    private String[] p;

}
