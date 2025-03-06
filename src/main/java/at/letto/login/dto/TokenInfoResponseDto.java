package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoResponseDto {

    /* Benutzername */
    String username;

    /** Token erstellt Datum und Uhrzeit */
    Date issuedAt;

    /** Token läuft ab Datum und Uhrzeit */
    Date expiration;

    /** Token ist noch so viele MilliSekunden gültig */
    long valideMillis;

    /** Vorname des Benutzers */
    String  vorname;

    /** Nachname des Benutzers */
    String  nachname;

    /** Active-Directory Name des Benutzers */
    String  activDirectoryname;

    /** Email-Adresse des Benutzers */
    String  email;

    /** Spracheeinstellung des Benutzers */
    String  sprache;

    /** ID des Benutzers in der Schuldatenbank */
    Integer idUser;

    /** ID der Schule in der Schuldatenbank */
    Integer idSchule;

    /** Schulkürzel der Schule */
    String  school;

    /** LeTTo-URI der Schule */
    String  lettoUri;

    /** Restkey des Servers der Schule */
    String  serverRestkey;

    /** Wenn der Token ein Alias-Token ist, der Benutzername welcher sich ursrpünglich eingeloggt hat */
    String originuser;

    /** Zusätzliche Infos über den Token wie zB. Abos als JSON etc. */
    HashMap<String,String> infos;

    boolean valid=false;
    boolean admin=false;
    boolean global=false;
    boolean teacher=false;
    boolean student=false;
    boolean payingstudent=false;
    boolean multiplelogin=false;
    boolean extern=false;

}
