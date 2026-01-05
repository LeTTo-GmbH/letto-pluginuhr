package at.letto.plugins.dto;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.tools.enums.Score;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ScoreInfoDto für die score-Methode von Plugins
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginScoreInfoDto {

    /** Eingabe des Schülers geparst als CalcErgebnisDto */
    CalcErgebnisDto schuelerErgebnis;
    /** ZielEinheit der Korrektur als String */
    String zielEinheit;
    /** Punkte IST von der Korrektur */
    double punkteIst;
    /** Punkte SOLL erreichbar */
    double punkteSoll;
    /** Score Ergebnis als Enum-Feld */
    Score status;
    /* HTML Score Information */
    String htmlScoreInfo;
    /** Feedback */
    String feedback;

}
