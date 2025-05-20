package at.letto.login.dto.message;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class MessageDto {

    private String messageID;   // Kennung der Nachricht
    private long   timestamp;   // Zeitstempel der Nachricht als DateInteger in Sekunden
    private String sender;      // Sender der Nachricht
    private String receiver;    // Empfänger der Nachricht
    private String topic;       // Thema der Nachricht
    private String classname;   // Klasse des Objektes
    private Object message;     // Nachricht als Objekt welches als JSON gespeichert wird
    private boolean single;     // true wenn die Nachricht nur einmal abgeholt werden kann und dann sofort gelöscht wird

}
