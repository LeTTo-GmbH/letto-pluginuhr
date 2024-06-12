package at.letto.math.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * VarHash für den Transport in Rest-Services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarHashDto implements Cloneable {

    private LinkedHashMap<String,VarDto> vars;

    /** @return liefert einen String des Element mit dem Key key oder den Leerstring wenn nicht vorhanden */
    public String getString(String key) {
        try {
            VarDto varDto = vars.get(key);
            return varDto.getCalcErgebnisDto().getString();
        } catch (Exception ex) {}
        return "";
    }

    /** @return liefert ein CalcErgebnisDto des Element mit dem Key key oder Null wenn es nicht vorhanden ist*/
    public CalcErgebnisDto getCalcErgebnisDto(String key) {
        try {
            VarDto varDto = vars.get(key);
            return varDto.getCalcErgebnisDto();
        } catch (Exception ex) {}
        return null;
    }

    @Override
    public String toString() {
        String ret = "";
        for (String name : vars.keySet()) {
            VarDto varDto = vars.get(name);
            ret += (ret.length()==0?"":",")+name+"="+varDto.toString();
        }
        return ret;
    }

}
