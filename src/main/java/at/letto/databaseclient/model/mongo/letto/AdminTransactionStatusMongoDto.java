package at.letto.databaseclient.model.mongo.letto;

import lombok.*;
import java.time.Instant;

/** Status-/Fortschrittsmeldung innerhalb einer Admin-Transaktion. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTransactionStatusMongoDto {
    private Instant timestamp;
    private String service;
    private String level;       // INFO, WARN, ERROR
    private String message;
    private Integer progress;   // 0..100, optional
}
