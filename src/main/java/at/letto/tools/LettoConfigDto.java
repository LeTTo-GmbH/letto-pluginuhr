package at.letto.tools;

import at.letto.tools.LicenseKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LettoConfigDto {

    /** Lizenz welche aktuell am System läuft als Lizenzobjekt */
    private LicenseKey license;

    /** Maxima-Pfad aus der LeTTo-Konfiguration */
    private String maximaPath;

    /** Befehle welche beim Start von Maxima immer ausgeführt werden sollen */
    private String moodlemac;

}
