package at.letto.databaseclient.modelMongo.login;

public class ActiveLeTToToken {

    /** Token als String */
    public final String token;

    /** Lebensdauer des Tokens in Sekunden */
    public final long   expiration;

    public ActiveLeTToToken(String token, long expiration) {
        this.token          = token;
        this.expiration     = expiration;
    }

    @Override
    public String toString() {
        return "ActiveLeTToToken{" + "token=" + token + ", expiration=" + expiration + '}';
    }

}
