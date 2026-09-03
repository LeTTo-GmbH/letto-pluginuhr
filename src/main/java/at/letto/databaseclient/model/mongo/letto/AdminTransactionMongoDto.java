package at.letto.databaseclient.model.mongo.letto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Mongo-Protokoll einer administrativen Aktion. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "adminTransactions")
public class AdminTransactionMongoDto {
    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionId;

    @Indexed
    private String username;

    @Indexed
    private String school;

    @Indexed
    private String method;

    @Indexed
    private String action;

    @Indexed
    private String state; // RUNNING, OK, ERROR

    private String parameters;
    private Instant startedAt;
    private Instant stoppedAt;
    private Long durationMs;
    private String errorClass;
    private String errorMessage;
    private String stackTrace;

    @Builder.Default
    private List<AdminTransactionStatusMongoDto> status = new ArrayList<>();
}
