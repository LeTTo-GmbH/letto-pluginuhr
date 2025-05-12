package at.letto.databaseclient.modelMongo.login;

public class ActiveLeTToToken {

    /** Token als String */
    public final String token;

    /** Lebensdauer des Tokens in Sekunden */
    public final long   expiration;

    /** Hardware-Fingerabdruck des Browsers mit dem der Token erzeugt wurde. */
    public final String fingerprint;

    public ActiveLeTToToken(String token, long expiration, String fingerprint) {
        this.token       = token;
        this.expiration  = expiration;
        this.fingerprint = fingerprint;
    }

    @Override
    public String toString() {
        return "ActiveLeTToToken{" + "token=" + token + ", expiration=" + expiration + ", fingerprint=" + fingerprint + '}';
    }

}
