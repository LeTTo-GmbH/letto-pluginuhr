package at.letto.databaseclient.repository.mongo.letto;

import at.letto.databaseclient.modelMongo.lettoprivate.UserLettoPrivate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLettoPrivateRepository extends MongoRepository<UserLettoPrivate, String> {

    /** Liefert einen Benutzer nach seiner email-Adresse */
    Optional<UserLettoPrivate> getUserLettoPrivateByEmail(String email);

    /** Liefert einen Benutzer nach seinem Nickname */
    Optional<UserLettoPrivate> getUserLettoPrivateByNickname(String nickname);

}
