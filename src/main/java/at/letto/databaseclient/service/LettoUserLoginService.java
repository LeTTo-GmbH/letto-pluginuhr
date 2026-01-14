package at.letto.databaseclient.service;

import at.letto.databaseclient.modelMongo.login.*;
import at.letto.databaseclient.repository.mongo.letto.LeTToSessionRepository;
import at.letto.databaseclient.repository.mongo.letto.LeTToUserRepository;
import at.letto.security.LettoToken;
import at.letto.tools.Datum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Zugriff auf die Mongo-DB */
@Service
public class LettoUserLoginService {

    /** Zugriff auf die Benutzer in der Default-Datenbank */
    @Autowired LeTToUserRepository lettoUserRepository;
    /** Zugriff auf die Sessions in der Default-Datenbank */
    @Autowired LeTToSessionRepository lettoSessionRepository;
    @Autowired BaseLettoRedisDBService lettoRedisDBService;

    private Logger logger = LoggerFactory.getLogger(LettoUserLoginService.class);

    public LeTToUserWithSessions addActiveSessionsToUser(LeTToUser user) {
        LeTToUserWithSessions userWithSessions = new LeTToUserWithSessions(user);
        // aktive Sessions des Benutzers
        try {
            List<LeTToSession> sessions = lettoSessionRepository.findByUserIDAndActiveIsTrue(user.getId());
            userWithSessions.setSessions(sessions);
        } catch (Exception e) {
            logger.error("Error getting active sessions for user "+user.getUsername()+"@"+user.getSchool()+" : "+e.getMessage());
        }
        return userWithSessions;
    }

    public LeTToUserWithSessions addAllSessionsToUser(LeTToUser user) {
        LeTToUserWithSessions userWithSessions = new LeTToUserWithSessions(user);
        // aktive Sessions des Benutzers
        try {
            List<LeTToSession> sessions = lettoSessionRepository.findByUserID(user.getId());
            userWithSessions.setSessions(sessions);
        } catch (Exception e) {
            logger.error("Error getting active sessions for user "+user.getUsername()+"@"+user.getSchool()+" : "+e.getMessage());
        }
        return userWithSessions;
    }

    public List<LeTToSession> getSessionsForUser(String userID) {
        try {
            List<LeTToSession> sessions = lettoSessionRepository.findByUserID(userID);
            return sessions;
        } catch (Exception e) {
            logger.error("Error getting sessions for user "+userID+" : "+e.getMessage());
        }
        return new ArrayList<>();
    }

    /** Liefert alle eingeloggten Benutzer an einem Serverknoten */
    public List<LeTToUserWithSessions> getLoggedInUsers() {
        try {
            List<LeTToUserWithSessions> result = new ArrayList<>();
            for (LeTToUser u : lettoUserRepository.findByCurrentlyLoggedInIsTrue())
                result.add(addActiveSessionsToUser(u));
            return result;
        } catch (Exception e) { }
        return new ArrayList<>();
    }

    /** Liefert alle eingeloggten Benutzer einer Schule */
    public List<LeTToUserWithSessions> getLoggedInUsers(String school) {
        try {
            List<LeTToUserWithSessions> result = new ArrayList<>();
            for (LeTToUser u : lettoUserRepository.findByCurrentlyLoggedInIsTrueAndSchool(school))
                result.add(addActiveSessionsToUser(u));
            return result;
        } catch (Exception e) { }
        return new ArrayList<>();
    }

    /**
     * Liefert alle Benutzer welche innerhalb der letzten "seconds" Sekunden ausgeloggt wurden
     * @param seconds    Anzahl der Sekunden
     * @return           Liste aller Benutzer die ausgeloggt wurden
     */
    public List<LeTToUserWithSessions> lastLoggedOutUsersSessionsLastSeconds(long seconds) {
        try {
            List<LeTToUserWithSessions> result = new ArrayList<>();
            for (LeTToUser u : lettoUserRepository.findByLastUserActionTimeGreaterThanAndCurrentlyLoggedInIsFalse(Datum.nowDateInteger()-seconds))
                result.add(addActiveSessionsToUser(u));
            return result;
        } catch (Exception e) { }
        return new ArrayList<>();
    }

    /** löscht alle abgelaufenen Tokens, schließt Sessions welche keine Tokens mehr haben und speichert die Daten beim Benutzer */
    public void logoutOutdatedUsers() {
        long now = Datum.nowDateInteger();
        try{
            List<LeTToSession> sessions = lettoSessionRepository.findByActiveIsTrue();
            NEXTSESSION:
            for (LeTToSession session : sessions) {
                boolean changed = false;
                List<ActiveLeTToToken> tokenList = session.getTokenList();
                if (tokenList == null || tokenList.size() == 0) {
                    // Wenn keine Tokens mehr vorhanden sind, dann wird die Session geschlossen
                    session.setActive(false);
                    session.setDateIntegerLogout(now);
                    session.setStatus(LeTToSession.STATUS_LOGGED_TIMEOUT);
                    lettoSessionRepository.save(session);
                    continue NEXTSESSION;
                }
                // Prüfe alle Tokens auf Ablauf
                for (int i=0; i<tokenList.size(); i++) {
                    ActiveLeTToToken a = tokenList.get(i);
                    if (a.getExpiration()<now) {
                        //TODO Werner: Hier muss noch der Token aus dem Redis-Cache gelöscht werden - oder auch nicht, da er ja von selbst abläuft
                        tokenList.remove(i);
                        changed = true;
                        i--;
                    }
                }
                if (changed) {
                    //session.setActiveTokens(tokenList);
                    if (tokenList.size() == 0) {
                        // Wenn keine Tokens mehr vorhanden sind, dann wird die Session geschlossen
                        session.setActive(false);
                        session.setDateIntegerLogout(now);
                        session.setStatus(LeTToSession.STATUS_LOGGED_TIMEOUT);
                        lettoSessionRepository.save(session);
                        // Nun wird der Benutzer aktualisiert
                        LeTToUser leTToUser = lettoUserRepository.findById(session.getUserID()).orElse(null);
                        if (leTToUser != null) {
                            List<LeTToSession> userSessions = lettoSessionRepository.findByUserIDAndActiveIsTrue(session.getUserID());
                            if (userSessions.size() >0) {
                                // Es gibt noch andere Sessions
                                leTToUser.setCurrentlyLoggedIn(true);
                                leTToUser.setLastUserActionTime(now);
                                leTToUser.setLastTimeoutLogout(now);
                                leTToUser.setTimeoutLogouts(leTToUser.getTimeoutLogouts()+1);
                                leTToUser.setLastUserAction(LeTToUser.USER_ACTION_TOKEN_TIMEOUT);
                            } else {
                                // Es gibt keine anderen Sessions mehr
                                leTToUser.setCurrentlyLoggedIn(false);
                                leTToUser.setLastUserActionTime(now);
                                leTToUser.setLastTimeoutLogout(now);
                                leTToUser.setTimeoutLogouts(leTToUser.getTimeoutLogouts()+1);
                                leTToUser.setLastUserAction(LeTToUser.USER_ACTION_TOKEN_TIMEOUT);
                            }
                            lettoUserRepository.save(leTToUser);
                        }
                    } else {
                        // Wenn noch Tokens vorhanden sind, dann wird die Session aktualisiert
                        lettoSessionRepository.save(session);
                    }
                }
            }
        } catch (Exception e) { }
        try {
            List<LeTToUser> users = lettoUserRepository.findByCurrentlyLoggedInIsTrue();
            for (LeTToUser u:users) {
                // Prüfe ob der Benutzer noch aktuell eingeloggt ist
                List<LeTToSession> userSessions = lettoSessionRepository.findByUserIDAndActiveIsTrue(u.getId());
                if (userSessions.size() == 0) {
                    // Wenn keine aktiven Sessions mehr vorhanden sind, dann wird der Benutzer aktualisiert
                    u.setCurrentlyLoggedIn(false);
                    u.setLastUserActionTime(now);
                    u.setLastTimeoutLogout(now);
                    u.setTimeoutLogouts(u.getTimeoutLogouts()+1);
                    u.setLastUserAction(LeTToUser.USER_ACTION_TOKEN_TIMEOUT);
                    lettoUserRepository.save(u);
                }
            }
        } catch (Exception e) { }
    }

    public LeTToUser getUser(LettoToken lettoToken) {
        try {
            return getUser(lettoToken.getSchool(), lettoToken.getUsername());
        } catch (Exception e) { }
        return null;
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

    /** Sucht nach einem Benutzereintrag eines Users mit der userID einer Schule */
    public LeTToUser getUser(String school, Long userId) {
        try {
            List<LeTToUser> users = lettoUserRepository.findBySchoolAndUserId(school, userId);
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

    /** erzeugt eine neue Session-ID */
    public String newSessionID() {
        String sessionID;
        int i=0;
        do {
            sessionID = UUID.randomUUID().toString();
            if (i++>10000) {
                // Wenn die Schleife zu lange dauert dann wird eine Exception geworfen
                throw new RuntimeException("Cannot create new SessionID");
            }
        } while (sessionExists(sessionID));
        return sessionID;
    }

    /** Erzeugt eine neue LeTToSession für einen Login-Vorgang */
    public LeTToSession createLeTToSession(String sessionID, LeTToUser leTToUser, String fingerprint, String ipAddress, LettoToken lettoToken, String service, String infos, String userAgent) {
        LeTToSession leTToSession = LeTToSession.createFromToken(sessionID, leTToUser, fingerprint, ipAddress, lettoToken, service, infos, userAgent);
        loginOk(leTToUser, lettoToken);
        leTToSession.setUserID(leTToUser.getId());
        save(leTToSession);
        return leTToSession;
    }

    /** Liefert eine Session mit der angegebenen sessionID */
    public LeTToSession getSession(String sessionID) {
        try {
            return lettoSessionRepository.findById(sessionID).orElse(null);
        } catch (Exception e) { }
        return null;
    }

    public LeTToSession getSession(LettoToken lettoToken) {
        try {
            return getSession(lettoToken.getServersession());
        } catch (Exception e) {
            return null;
        }
    }

    /** Prüft ob eine Session mit der angegebenen sessionID existiert */
    public boolean sessionExists(String sessionID) {
        try {
            return lettoSessionRepository.findById(sessionID).isPresent();
        } catch (Exception e) { }
        return false;
    }

    /** Speichert eine Session true wenn erfolgreich false wenn nicht erfolgreich */
    public boolean save(LeTToSession leTToSession) {
        try {
            lettoSessionRepository.save(leTToSession);
            return true;
        } catch (Exception e) {
            logger.error("Error saving session "+leTToSession.getUsername()+"@"+leTToSession.getSchool()+" : "+e.getMessage());
            return false;
        }
    }

    public List<LeTToSession> getActiveSessions(LeTToUser user) {
        return getActiveSessions(user.getId());
    }

    public List<LeTToSession> getActiveSessions(String userID) {
        List<LeTToSession> userSessions = lettoSessionRepository.findByUserIDAndActiveIsTrue(userID);
        return userSessions;
    }

    public boolean currentlyLoggedIn(LeTToUser user) {
        return getActiveSessions(user).size()>0;
    }

    // --------------------------------------------------------------- USER -------------------------------------------------
    public LeTToUser failedLogin(LeTToUser u) {
        long now = Datum.nowDateInteger();
        if (u.getLastFailedLogin()>0 && Datum.year(u.getLastFailedLogin())==Datum.year(now) &&
                Datum.month(u.getLastFailedLogin())==Datum.month(now) &&
                Datum.day(u.getLastFailedLogin())==Datum.day(now)) {
            u.setFailedLoginsAktualDay(0);
        }
        u.setLastLoginAttempt(now);
        u.setLastFailedLogin(now);
        u.incFailedLogins();
        u.incFailedLoginsAfterCorrectLogin();
        u.incFailedLoginsAktualDay();
        u.setCorrectLoginsAfterFailedLogin(0);
        u.setLastUserAction(LeTToUser.USER_ACTION_FAILED_LOGIN);
        u.setLastUserActionTime(now);
        u.setCurrentlyLoggedIn(currentlyLoggedIn(u));
        save(u);
        return u;
    }

    private LeTToUser loginOk(LeTToUser u, LettoToken lettoToken) {
        long now = Datum.nowDateInteger();
        u.setLastLoginAttempt(now);
        u.setLastCorrectLogin(now);
        if (u.getFirstLogin()==0) u.setFirstLogin(now);
        u.incCorrectLoginsAfterFailedLogin();
        u.incCorrectLogins();
        u.setFailedLoginsAfterCorrectLogin(0);
        u.setLastUserAction(LeTToUser.USER_ACTION_LOGIN);
        u.setLastUserActionTime(now);
        u.userCredentials(lettoToken);
        long expiration = Datum.toDateInteger(lettoToken.getExpirationDate());
        u.setCurrentlyLoggedIn(true);
        save(u);
        return u;
    }

    /** Alias Login von einem berechtigten Benutzer aus */
    public LeTToUser aliasLogin(LeTToUser u, LettoToken lettoToken, String originUserName) {
        long now = Datum.nowDateInteger();
        long expiration = Datum.toDateInteger(lettoToken.getExpirationDate());
        AliasLogin aliasLogin = new AliasLogin(now,originUserName,expiration);
        u.getAliasLoginList().add(aliasLogin);
        save(u);
        LeTToSession leTToSession = getSession(lettoToken.getServersession());
        addToken(u, leTToSession, lettoToken);
        return u;
    }

    public boolean logout(LettoToken lettoToken) {
        LeTToUser leTToUser       = getUser(lettoToken.getSchool(),lettoToken.getUsername());
        LeTToSession leTToSession = getSession(lettoToken.getServersession());
        long now           = Datum.nowDateInteger();
        boolean ok=true;
        if (leTToSession!=null) {
            leTToSession.setDateIntegerLogout(now);
            leTToSession.setStatus(LeTToSession.STATUS_LOGGED_OUT);
            leTToSession.setActive(false);
            removeAllTokensFromSession(leTToSession);
            save(leTToSession);
        } else ok=false;
        if (leTToUser!=null) {
            leTToUser.setCurrentlyLoggedIn(currentlyLoggedIn(leTToUser));
            leTToUser.setLastUserAction(LeTToUser.USER_ACTION_LOGOUT);
            leTToUser.setLastUserActionTime(now);
            leTToUser.setLastLogout(now);
            leTToUser.incCorrectLogouts();
            save(leTToUser);
        } else ok=false;
        return ok;
    }

    public boolean logout(LeTToSession leTToSession) {
        LeTToUser leTToUser       = lettoUserRepository.findById(leTToSession.getUserID()).orElse(null);
        long now           = Datum.nowDateInteger();
        boolean ok=true;
        if (leTToSession!=null) {
            leTToSession.setDateIntegerLogout(now);
            leTToSession.setStatus(LeTToSession.STATUS_LOGGED_OUT);
            leTToSession.setActive(false);
            removeAllTokensFromSession(leTToSession);
            save(leTToSession);
        } else ok=false;
        if (leTToUser!=null) {
            leTToUser.setCurrentlyLoggedIn(currentlyLoggedIn(leTToUser));
            leTToUser.setLastUserAction(LeTToUser.USER_ACTION_LOGOUT);
            leTToUser.setLastUserActionTime(now);
            leTToUser.setLastLogout(now);
            leTToUser.incCorrectLogouts();
            save(leTToUser);
        } else ok=false;
        return ok;
    }

    public boolean delete(LeTToSession leTToSession) {
        LeTToUser leTToUser       = lettoUserRepository.findById(leTToSession.getUserID()).orElse(null);
        long now           = Datum.nowDateInteger();
        boolean ok=true;
        if (leTToSession!=null) {
            lettoSessionRepository.delete(leTToSession);
        } else ok=false;
        return ok;
    }

    public boolean logout(LeTToUser u) {
        long now           = Datum.nowDateInteger();
        // setze den User auf ausgeloggt
        u.setLastLogout(now);
        u.incCorrectLogouts();
        u.setLastUserAction(LeTToUser.USER_ACTION_LOGOUT);
        u.setLastUserActionTime(now);
        u.setCurrentlyLoggedIn(false);
        save(u);
        // Zerstöre alle Sessions des Benutzers
        for (LeTToSession session : getActiveSessions(u)) {
            session.setActive(false);
            session.setDateIntegerLogout(now);
            session.setStatus(LeTToSession.STATUS_LOGGED_OUT);
            //session.setTokenList(new ArrayList<>());
            removeAllTokensFromSession(session);
            save(session);
        }
        return true;
    }

    public boolean removeAllTokensFromSession(LeTToSession leTToSession) {
        if (leTToSession==null) return false;
        //Lösche alle Tokens aus dem REDIS-Cache
        for (ActiveLeTToToken token : leTToSession.getTokenList()) {
            lettoRedisDBService.removeToken(token.getToken());
        }
        leTToSession.setTokenList(new ArrayList<>());
        //save(leTToSession);
        return true;
    }

    public boolean addToken(LeTToUser u, LeTToSession leTToSession, LettoToken lettoToken) {
        if (u==null || leTToSession==null || lettoToken==null) return false;
        long now = Datum.nowDateInteger();
        long expiration = Datum.toDateInteger(lettoToken.getExpirationDate());
        ActiveLeTToToken na = new ActiveLeTToToken(lettoToken.getToken(),expiration);
        if (leTToSession.isActive() && leTToSession.getStatus()==LeTToSession.STATUS_LOGGED_IN) {
            u.setLastUpdate(now);
            u.userCredentials(lettoToken);
            save(u);
            leTToSession.incTokenCount();
            leTToSession.getTokenList().add(na);
            save(leTToSession);
            return true;
        } else {
            logger.error("LeTToSession not found for token "+lettoToken.getToken());
            return false;
        }
    }

}
