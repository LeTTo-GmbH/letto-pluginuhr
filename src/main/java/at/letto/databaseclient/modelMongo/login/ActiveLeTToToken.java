package at.letto.databaseclient.modelMongo.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActiveLeTToToken {

    /** Token als String */
    public String token;

    /** Lebensdauer des Tokens in Sekunden */
    public long   expiration;

    public ActiveLeTToToken(String token, long expiration) {
        this.token          = token;
        this.expiration     = expiration;
    }

    @Override
    public String toString() {
        return "ActiveLeTToToken{" + "token=" + token + ", expiration=" + expiration + '}';
    }

}
