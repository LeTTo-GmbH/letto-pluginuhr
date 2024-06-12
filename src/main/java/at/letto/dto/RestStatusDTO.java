package at.letto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestStatusDTO {

    /** Ergebnis der Rest-Anfrage am Server */
    private RestStatus reststatus=RestStatus.UNDEFINED;

    /** Klartext-Meldung als Ergänzung zum restStatus */
    private String message="";

    /** Anzahl der Datensätze, welche gespeichert, geladen, gelöscht wurden. */
    private int count=0;

}
