package at.letto.databaseclient.modelMongo.login;


import at.letto.security.LettoToken;
import at.letto.tools.Datum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "sessions") // Name der Collection in MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeTToSession {

    /** Status der Session */
    public static final int STATUS_LOGGED_IN      = 1;
    public static final int STATUS_LOGGED_UNDEF   = 0;
    public static final int STATUS_LOGGED_OUT     = 2;
    public static final int STATUS_LOGGED_TIMEOUT = 3;
/*
    @PersistenceConstructor
    public LeTToSession(String id, String userID, int status, String username, String school, long dateIntegerLogin,
                        long dateIntegerLogout, int tokenCount, String fingerprint, String ipAddress, boolean active, List<ActiveLeTToToken> tokenList) {
        this.id       = id;
        this.userID   = userID;
        this.status   = status;
        this.username = username;
        this.school  = school;
        this.dateIntegerLogin  = dateIntegerLogin;
        this.dateIntegerLogout = dateIntegerLogout;
        this.tokenCount        = tokenCount;
        this.fingerprint       = fingerprint;
        this.ipAddress         = ipAddress;
        this.active            = active;
        this.tokenList         = tokenList != null ? tokenList : new ArrayList<>();
    }*/

    /** Erzeugt eine neue LeTToSession für einen Login-Vorgang */
    public static LeTToSession createFromToken(String id, LeTToUser leTToUser, String fingerprint, String ipAddress, LettoToken lettoToken, String service, String infos, String userAgent) {
        List<ActiveLeTToToken> tokenList = new ArrayList<>();
        long expiration        = Datum.toDateInteger(lettoToken.getExpirationDate());
        tokenList.add(new ActiveLeTToToken(lettoToken.getToken(),expiration));
        LeTToSession leTToSession = new LeTToSession(
                id, leTToUser.getId(), STATUS_LOGGED_IN, lettoToken.getUsername(), lettoToken.getSchool(),
                Datum.nowDateInteger(), 0,1,fingerprint,ipAddress,service,infos,userAgent,true,tokenList);
        return leTToSession;
    }

    /** ID der Session als eindeutiger String */
    @Id
    private String id;

    /** ID des Benutzers als String kombiniert Schulkuerzel und User-ID aus der MySQL-DB,
     * bzw. email-Adresse bei LeTTo-Private */
    @Indexed(unique = false)
    private String userID;

    /** Status der Session */
    @Indexed(unique = false)
    private int  status = STATUS_LOGGED_UNDEF;

    /** Benutzername des Benutzers */
    private String username;

    /** Schulkuerzel des Benutzers */
    @Indexed(unique = false)
    private String school;

    /** Datum und Uhrzeit des Logins als DateInteger */
    private long dateIntegerLogin=0;

    /** Datum und Uhrzeit des Logouts/Timouts als DateInteger */
    private long dateIntegerLogout=0;

    /** Anzahl der Tokens welche zu dieser Session ausgestellt wurden */
    private int  tokenCount = 0;

    /** Fingerprint des Clients */
    private String fingerprint = null;

    /** IP-Adresse des Clients */
    private String ipAddress = null;

    /** Service welcher die Authentifizierung anfordert, z.B. "letto-login", "letto-edit", "letto-admin" etc. */
    String service="";

    /** zusätzliche Informationen über den Client, wer, was, wo, warum */
    String infos="";

    /** User-Agent des Clients, z.B. "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3" */
    String userAgent="";

    /** Gibt an ob die Session noch aktiv ist */
    private boolean active = false;

    /** Liste der Tokens */
    private List<ActiveLeTToToken> tokenList = new ArrayList<>();

    public void incTokenCount(){ tokenCount++; }

    /**
     * prüft ob der Token in der Liste als korrekter Token eingetragen ist
     * @param token Token der geprüft werden soll
     * @return      true wenn der Token in der Liste eingetragen ist, sonst false
     */
    public boolean validateToken(String token) {
        List<ActiveLeTToToken> tokenList = getTokenList();
        for (ActiveLeTToToken a : tokenList) {
            if (a.getToken().equals(token)) return true;
        }
        return false;
    }

    public String loginString() {
        return Datum.formatDateTime(dateIntegerLogin);
    }

    public String logoutString() {
        return Datum.formatDateTime(dateIntegerLogout);
    }

    public long sortTime() {
        return -dateIntegerLogin;
    }


}
