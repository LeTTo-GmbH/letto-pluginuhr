package at.letto.databaseclient.modelMongo.login;

import at.letto.security.LettoToken;
import at.letto.tools.Datum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "users") // Name der Collection in MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeTToUser {

    public static final int USER_ACTION_LOGIN               = 1;
    public static final int USER_ACTION_LOGOUT              = 2;
    public static final int USER_ACTION_LOGIN_TEMP_PASSWORD = 3;
    public static final int USER_ACTION_TOKEN_TIMEOUT       = 4;
    public static final int USER_ACTION_FAILED_LOGIN        = 5;

    /** ID des Benutzers als String kombiniert Schulkuerzel und User-ID aus der MySQL-DB,
     * bzw. email-Adresse bei LeTTo-Private */
    @Id private String id;

    /** Schulkürzel */
    @Indexed(unique = false)
    private String school;

    @Indexed(unique = false)
    /** User-ID aus der MySQL-Datenbank */
    private long   userId=0;

    @Indexed(unique = false)
    /** School-ID aus der MySQL-Datenbank */
    private long   schoolId=0;

    @Indexed(unique = false)
    /** Benutzername */
    private String username;

    @Indexed(unique = false)
    /** LDAP-Benutzername */
    private String ldapName;

    /** Vorname */
    private String vorname;

    /** Familienname */
    private String nachname;

    @Indexed(unique = false)
    /** Email */
    private String email;

    private boolean multipleLogin=false;
    private boolean admin=false;
    private boolean teacher=false;
    private boolean student=false;
    private boolean global=false;

    /** Datum und Uhrzeit des letzten Logins als DateInteger */
    private long lastLoginAttempt=0;

    /** Datum und Uhrzeit des letzten korrekten Logins als DateInteger */
    private long lastCorrectLogin=0;

    /** Datum und Uhrzeit des letzten fehlerhaften Logins als DateInteger */
    private long lastFailedLogin=0;

    /** Datum und Uhrzeit des ersten Logins als DateInteger */
    private long firstLogin=0;

    /** Datum und Uhrzeit des letzten Token-Updates als DateInteger */
    private long lastUpdate=0;

    /** Datum und Uhrzeit des letzten korrekten Logouts als DateInteger */
    private long lastLogout=0;

    /** Datum und Uhrzeit des letzten Logouts durch einen Token-Timeout als DateInteger */
    private long lastTimeoutLogout=0;

    /** Anzahl der fehlerhaften Logins insgesamt */
    private int  failedLogins=0;

    /** Anzahl der fehlerhaften Logins nach dem letzten erfolgreichen Login */
    private int  failedLoginsAfterCorrectLogin=0;

    /** Anzahl der fehlerhaften Logins des aktuellen Tages */
    private int  failedLoginsAktualDay=0;

    /** Anzahl der korrekten Logins nach dem letzten fehlerhaften Login */
    private int  correctLoginsAfterFailedLogin=0;

    /** Anzahl der korrekten Logins insgesamt */
    private int  correctLogins=0;

    /** Anzahl der korrekten Logouts insgesamt */
    private int  correctLogouts=0;

    /** Anzahl der Logouts durch Ablauf der Token-Zeit */
    private int  timeoutLogouts=0;

    @Indexed(unique = false)
    /** Gibt an ob der Benutzer gerade eingeloggt ist */
    private boolean currentlyLoggedIn=false;

    /** Anzahl der gerade aktiven Login-Tokens key..Token value..Ablaufzeit des Tokens */
    //private HashMap<String,Long> activeTokens=new HashMap<>();
    private String activeTokens = "";

    /** letzte durchgeführte Aktion mit dem Benutzer */
    private int lastUserAction = 0;

    /** Datum und Uhrzeit der letzten Benutzer-Action als DateInteger */
    private long lastUserActionTime = 0;

    /** Alias-Logins mit mit diesem Benutzer */
    private List<AliasLogin> aliasLoginList = new ArrayList<>();

    @Transient @JsonIgnore
    private List<ActiveLeTToToken> tokenList = null;

    public LeTToUser(LettoToken lettoToken) {
        this();
        userCredentials(lettoToken);
    }

    /** liefert die aktiven Tokens als Map */
    /*public HashMap<String,Long> activeTokensMap(){
        HashMap<String,Long> activeTokensMap = new HashMap<>();
        for (String token : activeTokens.split(",")){
            if (token.trim().length()>0) {
                String[] parts = token.split(":");
                if (parts.length == 2) {
                    activeTokensMap.put(parts[0], Long.parseLong(parts[1]));
                }
            }
        }
        return activeTokensMap;
    }*/

    /** @return Liefert eine Liste aller Token mit ihre restlichen Lebensdauer in Sekunden */
    public List<ActiveLeTToToken> getTokenList(){
        if (tokenList==null){
            tokenList = new ArrayList<>();
            for (String token : activeTokens.split(",")){
                try { if (token.trim().length()>0) {
                    String[] parts = token.split(":");
                    if (parts.length == 2) {
                        tokenList.add(new ActiveLeTToToken(parts[0], Long.parseLong(parts[1]),""));
                    } else  if (parts.length == 3) {
                        tokenList.add(new ActiveLeTToToken(parts[0], Long.parseLong(parts[1]),parts[2]));
                    }
                }} catch (Exception e){ }
            }
        }
        List<ActiveLeTToToken> result = new ArrayList<>();
        for (ActiveLeTToToken token : tokenList)
            result.add(token);
        return result;
    }

    /** Wandelt eine Map aktiver Tokens wieder zurück in einen String */
    private void setActiveTokens(HashMap<String,Long> activeTokensMap){
        String result = "";
        for (String token : activeTokensMap.keySet()){
            if (result.length()>0) result += ",";
            result += token+":"+activeTokensMap.get(token);
        }
        activeTokens = result;
    }

    /** Wandelt eine Map aktiver Tokens wieder zurück in einen String */
    private void setActiveTokens(List<ActiveLeTToToken> activeLeTToTokenList){
        String result = "";
        tokenList = new ArrayList<>();
        for (ActiveLeTToToken a : activeLeTToTokenList) {
            if (result.length()>0) result += ",";
            result += a.token+":"+a.expiration+":"+a.fingerprint;
            tokenList.add(a);
        }
        activeTokens = result;
    }

    private void setActiveTokens(String activeTokens){
        this.activeTokens = activeTokens;
        this.tokenList = null;
    }

    private void activeTokensPut(String token, Long expiration, String fingerprint) {
        boolean found = false;
        List<ActiveLeTToToken> tokenList = getTokenList();
        for (int i=0; i<tokenList.size() && !found; i++) {
            if (tokenList.get(i).token.equals(token)) {
                found=true;
                tokenList.set(i,new ActiveLeTToToken(token,expiration,fingerprint));
            }
        }
        if (!found) tokenList.add(new ActiveLeTToToken(token,expiration,fingerprint));
        setActiveTokens(tokenList);
    }

    private int activeTokensSize() {
        return getTokenList().size();
    }

    /** setzt alle Benutzerdaten aus dem Token ohne den Token in die aktiven Tokens einzutragen */
    public LeTToUser userCredentials(LettoToken lettoToken) {
        school    = (lettoToken.getSchool()!=null && lettoToken.getSchool().length()>0)?
                lettoToken.getSchool():school;
        userId    = lettoToken.getIdUser()!=null?lettoToken.getIdUser():userId;
        id        = school+"_"+userId;
        schoolId  = lettoToken.getIdSchule()!=null?lettoToken.getIdSchule():schoolId;
        username  = (lettoToken.getUsername()!=null && lettoToken.getUsername().length()>0)?
                lettoToken.getUsername():username;
        vorname   = (lettoToken.getVorname()!=null && lettoToken.getVorname().length()>0)?
                lettoToken.getVorname():vorname;
        nachname  = (lettoToken.getNachname()!=null && lettoToken.getNachname().length()>0)?
                lettoToken.getNachname():nachname;
        email     = (lettoToken.getEmail()!=null && lettoToken.getEmail().length()>0)?
                lettoToken.getEmail():email;
        ldapName  = (lettoToken.getActiveDirectoryName()!=null && lettoToken.getActiveDirectoryName().length()>0)?
                lettoToken.getActiveDirectoryName():ldapName;
        multipleLogin = lettoToken.isMultipleLogin();
        admin = lettoToken.isAdmin();
        teacher = lettoToken.isTeacher();
        student = lettoToken.isStudent();
        global = lettoToken.isGlobal();
        return this;
    }

    public LeTToUser failedLogin() {
        long now = Datum.nowDateInteger();
        if (lastFailedLogin>0 && Datum.year(lastFailedLogin)==Datum.year(now) &&
            Datum.month(lastFailedLogin)==Datum.month(now) &&
            Datum.day(lastFailedLogin)==Datum.day(now)) {
            failedLoginsAktualDay=0;
        }
        lastLoginAttempt = now;
        lastFailedLogin  = now;
        failedLogins    += 1;
        failedLoginsAfterCorrectLogin += 1;
        failedLoginsAktualDay         += 1;
        correctLoginsAfterFailedLogin  = 0;
        lastUserAction     = USER_ACTION_FAILED_LOGIN;
        lastUserActionTime = now;
        currentlyLoggedIn = activeTokensSize()>0;
        return this;
    }

    public LeTToUser loginOk(LettoToken lettoToken) {
        long now = Datum.nowDateInteger();
        lastLoginAttempt               = now;
        lastCorrectLogin               = now;
        if (firstLogin==0) firstLogin  = now;
        correctLoginsAfterFailedLogin += 1;
        correctLogins                 += 1;
        failedLoginsAfterCorrectLogin  = 0;
        lastUserAction                 = USER_ACTION_LOGIN;
        lastUserActionTime             = now;
        userCredentials(lettoToken);
        long expiration = Datum.toDateInteger(lettoToken.getExpirationDate());
        activeTokensPut(lettoToken.getToken(),expiration,lettoToken.getFingerprint());
        currentlyLoggedIn = activeTokensSize()>0;
        return this;
    }

    /** Alias Login von einem berechtigten Benutzer aus */
    public LeTToUser aliasLogin(LettoToken lettoToken, String originUserName) {
        long now = Datum.nowDateInteger();
        long expiration = Datum.toDateInteger(lettoToken.getExpirationDate());
        AliasLogin aliasLogin = new AliasLogin(now,originUserName,expiration);
        aliasLoginList.add(aliasLogin);
        return this;
    }

    public boolean logout(String oldLettoToken) {
        long now           = Datum.nowDateInteger();
        lastLogout         = now;
        correctLogouts    += 1;
        lastUserAction     = USER_ACTION_LOGOUT;
        lastUserActionTime = now;
        List<ActiveLeTToToken> tokenList = getTokenList();
        boolean found = false;
        for (int i=0; i<tokenList.size(); i++) {
            if (tokenList.get(i).token.equals(oldLettoToken)) {
                tokenList.remove(i);
                found=true;
                break;
            }
        }
        setActiveTokens(tokenList);
        currentlyLoggedIn  = activeTokensSize()>0;
        return found;
    }

    public boolean logout() {
        long now           = Datum.nowDateInteger();
        lastLogout         = now;
        correctLogouts    += 1;
        lastUserAction     = USER_ACTION_LOGOUT;
        lastUserActionTime = now;
        List<ActiveLeTToToken> tokenList = new ArrayList<>();
        setActiveTokens(tokenList);
        currentlyLoggedIn  = activeTokensSize()>0;
        return true;
    }

    public LeTToUser tokenUpdate(String oldLettoToken, LettoToken newLettoToken) {
        long now           = Datum.nowDateInteger();
        lastUpdate         = now;
        List<ActiveLeTToToken> tokenList = getTokenList();
        userCredentials(newLettoToken);
        long expiration = Datum.toDateInteger(newLettoToken.getExpirationDate());
        ActiveLeTToToken na = new ActiveLeTToToken(newLettoToken.getToken(),expiration,newLettoToken.getFingerprint());
        boolean found = false;
        for (int i=0; i<tokenList.size(); i++) {
            if (tokenList.get(i).token.equals(oldLettoToken)) {
                tokenList.set(i,na);
                found = true;
                break;
            }
        }
        setActiveTokens(tokenList);
        currentlyLoggedIn  = activeTokensSize()>0;
        return this;
    }

    /** Prüft ob token abgelaufen sind und löscht sie gegebenenfalls
     * @param now            aktuelle Zeit als DateInteger
     * @return               true bei Änderungen
     */
    public boolean tokenTimeout(long now) {
        boolean changed = false;
        List<ActiveLeTToToken> tokenList = getTokenList();
        for (int i=0; i<tokenList.size(); i++) {
            ActiveLeTToToken a = tokenList.get(i);
            if (a.expiration<now) {
                tokenList.remove(i);
                lastTimeoutLogout  = now;
                timeoutLogouts    += 1;
                lastUserAction     = USER_ACTION_TOKEN_TIMEOUT;
                lastUserActionTime = now;
                changed = true;
                i--;
            }
        }
        setActiveTokens(tokenList);
        currentlyLoggedIn  = activeTokensSize()>0;
        return changed;
    }

    /** gibt die aktuelle Anzahl von Logins dieses Benutzers an */
    public int actualLogins(){
        return activeTokensSize();
    }

    public String loginTimeString(){          return lastCorrectLogin>0?Datum.formatDateTime(lastCorrectLogin):""; }
    public String firstLoginString(){         return firstLogin>0?Datum.formatDateTime(firstLogin):""; }
    public String lastLoginString(){          return lastLoginAttempt>0?Datum.formatDateTime(lastLoginAttempt):""; }
    public String lastCorrectLoginString(){   return lastCorrectLogin>0?Datum.formatDateTime(lastCorrectLogin):""; }
    public String lastFailedLoginString(){    return lastFailedLogin>0?Datum.formatDateTime(lastFailedLogin):""; }
    public String lastLogoutString(){         return lastLogout>0?Datum.formatDateTime(lastLogout):""; }
    public String lastTimeoutLogoutString(){  return lastTimeoutLogout>0?Datum.formatDateTime(lastTimeoutLogout):""; }
    public String lastUserActionTimeString(){ return lastUserActionTime>0?Datum.formatDateTime(lastUserActionTime):""; }

    public String tokensExpirationString() {
        long now           = Datum.nowDateInteger();
        StringBuilder sb = new StringBuilder();
        List<ActiveLeTToToken> tokenList = getTokenList();
        for (int i=0; i<tokenList.size(); i++) {
            ActiveLeTToToken a = tokenList.get(i);
            sb.append(a.expiration-now).append("s ");
        }
        return sb.toString().trim();
    }

    public String roleString(){
        StringBuilder sb = new StringBuilder();
        if (admin) sb.append("Admin ");
        if (teacher) sb.append("Teacher ");
        if (student) sb.append("Student ");
        if (global) sb.append("Global ");
        String result=sb.toString().trim();
        return result.length()>0?result:"other User";
    }

    public long loggedInSortString(){
        return -lastUserActionTime;
    }

    public String loggedOutSort(){
        String result = student?"S":"T";
        result += getUsername();
        return result;
    }

    /**
     * prüft ob der Token in der Liste als korrekter Token eingetragen ist
     * @param token Token der geprüft werden soll
     * @return      true wenn der Token in der Liste eingetragen ist, sonst false
     */
    public boolean validateToken(String token) {
        List<ActiveLeTToToken> tokenList = getTokenList();
        for (ActiveLeTToToken a : tokenList) {
            if (a.token.equals(token)) return true;
        }
        return false;
    }

}
