package at.letto.databaseclient.repository.mongo.letto;

import at.letto.databaseclient.modelMongo.login.LeTToUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeTToUserRepository  extends MongoRepository<LeTToUser, String> {

    /** Sucht einen Benutzer nach seiner ID */
    Optional<LeTToUser> findById(String id);

    /** Sucht alle Benutzer einer Schule */
    List<LeTToUser> findBySchool(String school);

    /** Sucht alle Benutzer mit dem angegebenen Benutzernamen */
    List<LeTToUser> findByUsername(String username);

    /** Sucht die Benutzer welche noch eingeloggt sind */
    List<LeTToUser> findByCurrentlyLoggedInIsTrue();

    /** Sucht die Benutzer einer Schule welche noch eingeloggt sind */
    List<LeTToUser> findByCurrentlyLoggedInIsTrueAndSchool(String school);

    /** Sucht einen Benutzer einer Schule */
    List<LeTToUser> findBySchoolAndUsername(String school, String username);

    /** Sucht alle Benutzer nach der Email-Adresse */
    List<LeTToUser> findByEmail(String email);

    /** Sucht alle Benutzer welche eine Änderung ihres Loginzustandes seit dem angegebenen Datum hatten */
    List<LeTToUser> findByLastUserActionTimeGreaterThan(long lastUserActionTime);

    /** Sucht alle Benutzer welche eine Änderung ihres Loginzustandes seit dem angegebenen Datum hatten und nicht eingeloggt sind. */
    List<LeTToUser> findByLastUserActionTimeGreaterThanAndCurrentlyLoggedInIsFalse(long lastUserActionTime);

}