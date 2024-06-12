package at.letto.login.dto.servertoken;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class GetServerTokenRequest {

    /** Domain-Name des Servers von dem aus der generierte ServerToken funktionieren soll zB.: letto.htlstp.ac.at <br>
     *  Das Loginservice muss auf https://serverDomainName/endpoint erreichbar sein!! */
    private String serverDomainName;

    /** Endpoint auf dem Server serverDomainName welcher den Login gegenchecken kann<br>
     *  Im Normalfall wird das Loginservice diesen Check vornehmen<br>
     *  Somit erfolgt er Check über https://serverDomainName/endpoint und liefert Benutzername,Schule und Rechte */
    private String checkEndpoint;

    /** Schulkürzel auf dem Server für die der generierte ServerToken funktionieren soll */
    private String school;

    /** Benutzername auf dem Server für den der generierte ServerToken funktionieren soll */
    private String username;

    /** Wenn nocheck auf true ist wird keine Rückfrage an den anfordernden Server https://serverDomainName/checkEndpoint gemacht um einen Token zu erzeugen */
    private boolean nocheck=false;

    /** Gibt an ob ein mit dem ServerToken generierter LeTToToken Lehrerrechte hat */
    private boolean teacher=false;

    /** Gibt an ob ein mit dem ServerToken generierter LeTToToken Schuelerrechte hat */
    private boolean student=false;

    /** Gibt an ob ein mit dem ServerToken generierter LeTToToken Adminrechte hat */
    private boolean admin=false;

    /** Gibt an ob ein mit dem ServerToken generierter LeTToToken globale Adminrechte hat */
    private boolean global=false;

    /** gibt das Service an, bei dem sich der generierte LeTTo-Token anmelden soll (lettohtlstp, exchange,...) */
    private String serviceEndpoint;

    /** Gibt an, ob ein Nutzer des Tokens eine Userkategorie anlegen darf     */
    private boolean create;

    /** Gibt an ob der Servertoken durch eine Aktualisierungsanfrage aktualisiert werden darf */
    private boolean refresh;

    /** Gültigkeitsdauer in Sekunden */
    private long expiration;

}
