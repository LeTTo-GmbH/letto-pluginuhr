package at.letto.databaseclient.repository.mongo.letto;

import at.letto.databaseclient.modelMongo.login.LeTToSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeTToSessionRepository  extends MongoRepository<LeTToSession, String> {

    /** Sucht eine Session nach ihrer sessionID */
    Optional<LeTToSession> findById(String sessionID);

    /** Sucht alle Sessions einer Schule */
    List<LeTToSession> findBySchool(String school);

    /** Sucht alle Sessions eines Benutzers */
    List<LeTToSession> findByUserID(String userID);

    /** Sucht alle aktiven Sessions eines Benutzers */
    List<LeTToSession> findByUserIDAndActiveIsTrue(String userID);

    /** Sucht alle Sessions deren Logout-Datum vor dem angegebenen Datum ist */
    List<LeTToSession> findByDateIntegerLogoutIsLessThan(long logoutDateTime);

    void deleteByActiveIsFalseAndDateIntegerLogoutIsLessThan(long dateIntegerLogoutIsLessThan);

    /** Löscht die Session mit der angegebenen sessionID */
    void deleteById(String sessionID);

    /** Sucht alle Sessions die noch eingeloggt sind */
    List<LeTToSession> findByActiveIsTrue();

}
