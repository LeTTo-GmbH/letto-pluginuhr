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

    /** Erzeugt eine neue LeTToSession für einen Login-Vorgang */
    public LeTToSession(String sessionID, LeTToUser leTToUser, String fingerprint, String ipAddress, LettoToken lettoToken) {
        this.sessionID         = sessionID;
        this.userID            = leTToUser.getId();
        this.status            = STATUS_LOGGED_IN;
        this.username          = leTToUser.getUsername();
        this.school            = leTToUser.getSchool();
        this.dateIntegerLogin  = Datum.nowDateInteger();
        this.dateIntegerLogout = 0;
        this.tokenCount        = 1;
        this.fingerprint       = fingerprint;
        this.ipAddress         = ipAddress;
        this.active            = true;
        long expiration        = Datum.toDateInteger(lettoToken.getExpirationDate());
        this.tokenList.add(new ActiveLeTToToken(lettoToken.getToken(),expiration));
        //activeTokensPut(lettoToken.getToken(),expiration);
    }

    /** ID der Serssion als eindeutiger String */
    @Id
    private String sessionID;

    /** ID des Benutzers als String kombiniert Schulkuerzel und User-ID aus der MySQL-DB,
     * bzw. email-Adresse bei LeTTo-Private */
    @Indexed(unique = false) private String userID;

    /** Status der Session */
    @Indexed(unique = false) private int  status = STATUS_LOGGED_UNDEF;

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

    /** aktive Tokens der Session als String token:expiration,token:.... */
    //private String activeTokens = "";

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
            if (a.token.equals(token)) return true;
        }
        return false;
    }

    /*@Transient
    @JsonIgnore
    private List<ActiveLeTToToken> tokenList = null;

    ///@return Liefert eine Liste aller Token mit ihrer Token-Ablaufzeit in Sekunden
    @JsonIgnore
    public List<ActiveLeTToToken> getTokenList(){
        if (tokenList==null){
            tokenList = new ArrayList<>();
            for (String token : activeTokens.split(",")){
                try { if (token.trim().length()>0) {
                    String[] parts = token.split(":");
                    if (parts.length == 2) {
                        tokenList.add(new ActiveLeTToToken(parts[0], Long.parseLong(parts[1]), sessionID));
                    }
                }} catch (Exception e){ }
            }
        }
        List<ActiveLeTToToken> result = new ArrayList<>();
        for (ActiveLeTToToken token : tokenList)
            result.add(token);
        return result;
    }

    public void setActiveTokens(List<ActiveLeTToToken> activeLeTToTokenList){
        String result = "";
        tokenList = new ArrayList<>();
        for (ActiveLeTToToken a : activeLeTToTokenList) {
            if (result.length()>0) result += ",";
            result += a.token+":"+a.expiration;
            tokenList.add(a);
        }
        activeTokens = result;
    }

    private void setActiveTokens(String activeTokens){
        this.activeTokens = activeTokens;
        this.tokenList = null;
    }

    private void activeTokensPut(String token, Long expiration) {
        boolean found = false;
        List<ActiveLeTToToken> tokenList = getTokenList();
        for (int i=0; i<tokenList.size() && !found; i++) {
            if (tokenList.get(i).token.equals(token)) {
                found=true;
                tokenList.set(i,new ActiveLeTToToken(token,expiration,sessionID));
            }
        }
        if (!found) tokenList.add(new ActiveLeTToToken(token,expiration,sessionID));
        setActiveTokens(tokenList);
    }

    private int activeTokensSize() {
        return getTokenList().size();
    }*/

}
