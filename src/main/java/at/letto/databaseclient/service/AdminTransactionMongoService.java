package at.letto.databaseclient.service;

import at.letto.databaseclient.model.mongo.letto.AdminTransactionMongoDto;
import at.letto.databaseclient.model.mongo.letto.AdminTransactionStatusMongoDto;
import at.letto.databaseclient.repository.mongo.letto.AdminTransactionRepository;
import at.letto.security.LettoToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * Gemeinsamer Mongo-Service für Admin-Audit und Fortschrittsmeldungen.
 * Logging-Fehler werden absichtlich nur geloggt und nie an die Admin-Aktion weitergegeben.
 */
@Service
public class AdminTransactionMongoService extends BaseLettoMongoDBService {
    public static final String HEADER = "X-Letto-Admin-Transaction";
    private static final Logger log = LoggerFactory.getLogger(AdminTransactionMongoService.class);

    private final AdminTransactionRepository repository;
    private final MongoTemplate mongoTemplate;

    public AdminTransactionMongoService(
            AdminTransactionRepository repository,
            @Qualifier("lettoMongoTemplate") MongoTemplate mongoTemplate) {

        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public String newTransactionId() {
        return UUID.randomUUID().toString();
    }

    public void start(String transactionId, LettoToken token, String method, String action, String parameters) {
        try {
            AdminTransactionMongoDto dto = AdminTransactionMongoDto.builder()
                    .transactionId(transactionId)
                    .username(token != null ? token.getUsername() : null)
                    .school(token != null ? token.getSchool() : null)
                    .method(method)
                    .action(action)
                    .parameters(parameters)
                    .state("RUNNING")
                    .startedAt(Instant.now())
                    .build();
            repository.save(dto);
            status(transactionId, "edit-service", "INFO", "Aktion gestartet", 0);
        } catch (Exception e) {
            log.warn("Admin-Transaktion {} konnte nicht gestartet/protokolliert werden", transactionId, e);
        }
    }

    public void status(String transactionId, String service, String level, String message, Integer progress) {
        if (transactionId == null || transactionId.isBlank()) return;
        try {
            Query q = Query.query(Criteria.where("transactionId").is(transactionId));
            AdminTransactionStatusMongoDto item = AdminTransactionStatusMongoDto.builder()
                    .timestamp(Instant.now()).service(service).level(level).message(message).progress(progress).build();
            mongoTemplate.updateFirst(q,
                    new org.springframework.data.mongodb.core.query.Update().push("status", item),
                    AdminTransactionMongoDto.class);
        } catch (Exception e) {
            log.warn("Status für Admin-Transaktion {} konnte nicht geschrieben werden", transactionId, e);
        }
    }

    public void success(String transactionId) {
        finish(transactionId, null);
    }

    public void error(String transactionId, Throwable error) {
        finish(transactionId, error);
    }

    private void finish(String transactionId, Throwable error) {
        if (transactionId == null || transactionId.isBlank()) return;
        try {
            Instant stop = Instant.now();
            AdminTransactionMongoDto old = repository.findByTransactionId(transactionId).orElse(null);
            org.springframework.data.mongodb.core.query.Update u = new org.springframework.data.mongodb.core.query.Update()
                    .set("stoppedAt", stop)
                    .set("state", error == null ? "OK" : "ERROR");
            if (old != null && old.getStartedAt() != null) {
                u.set("durationMs", Duration.between(old.getStartedAt(), stop).toMillis());
            }
            if (error != null) {
                u.set("errorClass", error.getClass().getName())
                 .set("errorMessage", error.getMessage())
                 .set("stackTrace", stackTrace(error));
            }
            mongoTemplate.updateFirst(Query.query(Criteria.where("transactionId").is(transactionId)), u,
                    AdminTransactionMongoDto.class);
            status(transactionId, "edit-service", error == null ? "INFO" : "ERROR",
                    error == null ? "Aktion abgeschlossen" : "Aktion mit Fehler beendet", 100);
        } catch (Exception e) {
            log.warn("Admin-Transaktion {} konnte nicht abgeschlossen/protokolliert werden", transactionId, e);
        }
    }

    public List<AdminTransactionMongoDto> find(String text, String state, int limit) {
        Query q = new Query();
        if (text != null && !text.isBlank()) {
            String regex = ".*" + java.util.regex.Pattern.quote(text.trim()) + ".*";
            q.addCriteria(new Criteria().orOperator(
                    Criteria.where("username").regex(regex, "i"),
                    Criteria.where("school").regex(regex, "i"),
                    Criteria.where("method").regex(regex, "i"),
                    Criteria.where("action").regex(regex, "i"),
                    Criteria.where("transactionId").regex(regex, "i")
            ));
        }
        if (state != null && !state.isBlank()) q.addCriteria(Criteria.where("state").is(state));
        q.with(Sort.by(Sort.Direction.DESC, "startedAt"));
        q.limit(Math.max(1, Math.min(limit, 1000)));
        return mongoTemplate.find(q, AdminTransactionMongoDto.class);
    }

    public AdminTransactionMongoDto get(String transactionId) {
        return repository.findByTransactionId(transactionId).orElse(null);
    }

    private String stackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String s = sw.toString();
        return s.length() > 20000 ? s.substring(0, 20000) : s;
    }
}
