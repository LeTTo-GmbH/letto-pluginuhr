package at.letto.databaseclient.repository.mongo.letto;

import at.letto.databaseclient.model.mongo.letto.AdminTransactionMongoDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminTransactionRepository extends MongoRepository<AdminTransactionMongoDto, String> {
    Optional<AdminTransactionMongoDto> findByTransactionId(String transactionId);
}
