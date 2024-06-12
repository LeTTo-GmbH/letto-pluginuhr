package at.letto.setup.dto;

import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informationen über das Service einer Schule <br>
 * Alles was für Data und Letto für die Verbindung notwendig ist<br>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSchuleDto implements Selectable {

    /** Schul nummer xx der Schule in der Konfiguration LETTO_xx_... */
    private int    id=0;

    /** LETTO_xx_SCHOOL Eindeutiger Bezeichner der Schule für URL und Service-Kennungen */
    private String shortname;

    /** LETTO_xx_MYSQL_HOST Servername und Port des MySQL-Servers der Datenbank */
    private String mySqlHost;

    /** LETTO_xx_MYSQL_USER Benutzername mit dem auf den MySQL-Server zugegriffen wird */
    private String mySqlUser;

    /** LETTO_xx_LICENCE Lizenz der Schule */
    private String licence;

    /** LETTO_xx_ID_SCHULE_LIZENZ id der Schule am Lizenzserver */
    private int idSchuleLizenz;

    /** LETTO_xx_ID_SCHULE_DATA id der Schule am Data-Service */
    private int idSchuleData;

    /** LETTO_xx_SCHULNAME Name der Schule als Langform */
    private String schulname;

    /** LETTO_xx_DATA_URI URI mit Protokoll://Name:Port des Data-Services */
    private String dataUri;

    /** LETTO_xx_DATA_USER Benutzername am Data-Service der schule */
    private String dataUser;

    /** LETTO_xx_DATA_PASSWORD Passwort am Data-Service der schule */
    private String dataPassword;

    /** LETTO_xx_LETTO_URI URI mit Protokoll://Name:Port des letto-server-Services */
    private String lettoUri;

    /** LETTO_xx_LOGIN_URI_EXTERN URI für den externen Zugriff auf das Login Service der Schule */
    private String loginUriExtern;

    /** LETTO_xx_LETTO_URI_EXTERN URI für den externen Zugriff auf das letto-server Service der Schule */
    private String lettoUriExtern;

    @Override @JsonIgnore
    public String getText() {
        return schulname;
    }
}

