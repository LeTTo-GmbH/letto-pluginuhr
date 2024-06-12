package at.letto.tools.threads;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * Klasse die sich um die Verwaltung von allen laufenden Threads kümmert und ein sinnvolles Logging aller
 * Prozessen ermöglicht
 */
@Getter
@Setter
public class ThreadManagement {

    /** Name und Pfad des Logfiles */
    private File logfile=null;

}
