package at.letto.databaseclient.modelMongo.login;


import at.letto.tools.Datum;
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
        int anz = sessions.size();
        int tokenAnz = 0;
        for (LeTToSession session : sessions) {
            tokenAnz += session.getTokenList().size();
        }
        return anz+"/"+tokenAnz;
    }

    public String tokensExpirationString(){
        StringBuilder sb = null;
        for (LeTToSession session : sessions) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(", ");
            }
            sb.append(session.getIpAddress()).append(":");
            for (ActiveLeTToToken lt : session.getTokenList()) {
                sb.append(" ").append(lt.getExpiration()-Datum.nowDateInteger()).append("s");
            }
        }
        return sb==null?"":sb.toString();
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
