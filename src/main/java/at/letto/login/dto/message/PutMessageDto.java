package at.letto.login.dto.message;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class PutMessageDto {

    private String sender;          // Sender der Nachricht
    private String receiver;        // Empfänger der Nachricht
    private String topic;           // Thema der Nachricht
    private Object message;         // Nachricht als Objekt welches als JSON gespeichert wird
    private long   lifeTimeSeconds; // Lebensdauer der Nachricht in Sekunden bis sie gelöscht wird
    private boolean single;         // true wenn die Nachricht nur einmal abgeholt werden kann und dann sofort gelöscht wird
    private String messageSecret;   // Secret damit nur Services eine Nachricht senden können welche das Secret kennen.

}
