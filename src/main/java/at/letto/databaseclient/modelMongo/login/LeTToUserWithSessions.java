package at.letto.databaseclient.modelMongo.login;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * LeTToUserWithSessions ist eine Erweiterung von LeTToUser, die eine Liste von aktiven Sessions enthält.
 */
@Getter
@Setter
public class LeTToUserWithSessions {

    private LeTToUser user;
    private List<LeTToSession> sessions = new ArrayList<>();

    public LeTToUserWithSessions(LeTToUser user) {
        this.user = user;
    }

    public String getId(){
        return user.getId();
    }

    public String actualLogins() {
        return "AL";
    }

    public String tokensExpirationString(){
        return "TE";
    }

    public long loggedInSortString(){
        return user.loggedInSortString();
    }

    public String loggedOutSort(){
        String result = user.isStudent()?"S":"T";
        result += user.getUsername();
        return result;
    }


}
