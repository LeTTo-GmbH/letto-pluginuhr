package at.letto.login.dto.servertoken;

import at.letto.tools.Datum;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class GeneratedServerToken extends GetServerTokenRequest{

    /** Token-ID des betroffenen Tokens */
    private long     tokenId;

    /** Datum und Uhrzeit der Erzeugung in ms seit 1.1.0000 - Datum.nowDateInteger() */
    private long     creationDate;

    /** Token ist deaktiviert, kann aber wieder aktiviert werden */
    private boolean  deactivated;

    public GeneratedServerToken(GetServerTokenRequest request, long tokenId, long creationDate, boolean deactivated) {
        super(request.getServerDomainName(),
                request.getCheckEndpoint(),
                request.getSchool(),
                request.getUsername(),
                request.isNocheck(),
                request.isTeacher(),
                request.isStudent(),
                request.isAdmin(),
                request.isGlobal(),
                request.getServiceEndpoint(),
                request.isCreate(),
                request.isRefresh(),
                request.getExpiration()
        );
        this.tokenId = tokenId;
        this.creationDate = creationDate;
        this.deactivated = deactivated;
    }

    public String rechte() {
        String ret = "";
        if (isStudent()) ret+="S";
        if (isTeacher()) ret+="T";
        if (isAdmin())   ret+="A";
        if (isGlobal())  ret+="G";
        if (isNocheck()) ret+=",nocheck";
        if (isRefresh()) ret+=",refresh";
        return ret;
    }

    public String create() {
        return Datum.formatDateTime(creationDate);
    }

    public String ablaufdatum() {
        String ret = "";
        try {
            long ablauf = creationDate + getExpiration();
            ret = Datum.formatDateTime(ablauf);
        } catch (Exception ignore) {}
        return ret;
    }

    public String erstelldatum() {
        String ret = "";
        try {
            ret = Datum.formatDateTime(creationDate);
        } catch (Exception ignore) {}
        return ret;
    }

}
