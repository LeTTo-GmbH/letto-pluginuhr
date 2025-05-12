package at.letto.databaseclient.service;

import at.letto.databaseclient.modelMongo.login.LeTToUser;
import at.letto.databaseclient.repository.mongo.letto.LeTToUserRepository;
import at.letto.security.LettoToken;
import at.letto.tools.Datum;
import at.letto.tools.config.MicroServiceConfigurationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Zugriff auf die Mongo-DB */
@Service
public class LettoUserLoginService {

    /** Zugriff auf die Default-Datenbank */
    @Autowired LeTToUserRepository lettoUserRepository;

    private Logger logger = LoggerFactory.getLogger(LettoUserLoginService.class);

    /** Liefert alle eingeloggten Benutzer an einem Serverknoten */
    public List<LeTToUser> getLoggedInUsers() {
        try {
            return lettoUserRepository.findByCurrentlyLoggedInIsTrue();
        } catch (Exception e) { }
        return new ArrayList<>();
    }

    /** Liefert alle eingeloggten Benutzer einer Schule */
    public List<LeTToUser> getLoggedInUsers(String school) {
        try {
            return lettoUserRepository.findByCurrentlyLoggedInIsTrueAndSchool(school);
        } catch (Exception e) { }
        return new ArrayList<>();
    }

    /** markiert alle eingeloggten Benutzer deren Token abgelaufen ist als ausgeloggt */
    public void logoutOutdatedUsers() {
        long now = Datum.nowDateInteger();
        try{
            List<LeTToUser> users = lettoUserRepository.findByCurrentlyLoggedInIsTrue();
            for (LeTToUser user : users)
                if (user.tokenTimeout(now))
                    lettoUserRepository.save(user);
        } catch (Exception e) { }
    }

    /** Sucht nach einem Benutzereintrag eines username einer Schule */
    public LeTToUser getUser(String school, String username) {
        try {
            List<LeTToUser> users = lettoUserRepository.findBySchoolAndUsername(school, username);
            if (users.size() > 0) {
                LeTToUser user = users.get(0);
                return user;
            }
        } catch (Exception e) { }
        return null;
    }

    /** Speichert einen Benutzereintrag true wenn erfolgreich false wenn nicht erfolgreich */
    public boolean save(LeTToUser user) {
        try {
            lettoUserRepository.save(user);
            return true;
        } catch (Exception e) {
            logger.error("Error saving user "+user.getUsername()+"@"+user.getSchool()+" : "+e.getMessage());
            return false;
        }
    }

    /**
     * Liefert alle Benutzer welche seit dem letzten timestamp(DateInteger) ausgeloggt wurden
     * @param timestamp  DateInteger für die Abfrage
     * @return           Liste aller Benutzer die seit dem letzten timestamp ausgeloggt wurden
     */
    public List<LeTToUser> lastLoggedOutUsers(long timestamp) {
        try {
            return lettoUserRepository.findByLastUserActionTimeGreaterThanAndCurrentlyLoggedInIsFalse(timestamp);
        } catch (Exception e) { }
        return new ArrayList<>();
    }

    /**
     * Liefert alle Benutzer welche innerhalb der letzten "seconds" Sekunden ausgeloggt wurden
     * @param seconds    Anzahl der Sekunden
     * @return           Liste aller Benutzer die ausgeloggt wurden
     */
    public List<LeTToUser> lastLoggedOutUsersLastSeconds(long seconds) {
        try {
            return lettoUserRepository.findByLastUserActionTimeGreaterThanAndCurrentlyLoggedInIsFalse(Datum.nowDateInteger()-seconds);
        } catch (Exception e) { }
        return new ArrayList<>();
    }

}
