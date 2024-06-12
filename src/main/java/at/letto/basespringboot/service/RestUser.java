package at.letto.basespringboot.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Benutzer welche zwischen den Microservices für die Authentifikation verwendet werden
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestUser {

    /** Benutzername */
    @Setter
    private String name="";

    /** verschlüsseltes Passwort */
    private String encodedpassword="";

    /** Liste aller Authentifizierungs-Rollen des Benutzers */
    private String[] roles = new String[0];

    /**
     * Setzt das Passwort des Benutzers
     * @param password Klartextpasswort
     */
    public void setPassword(String password) {
        if (password.length()>0) {
            this.encodedpassword = (new BCryptPasswordEncoder()).encode(password);
        }
    }

    /**
     * Setzt das Passwort des Benutzers
     * @param password encrypted Passwort
     */
    public void setEncodedpassword(String password) {
        if (password.length()>0) {
            this.encodedpassword = password;
        }
    }

    /**
     * Fügt eine Benutzerrolle zu dem Benutzer hinzu
     * @param role Rollennamen
     */
    public void addRole(String role) {
        for (String s:roles)
            if (s.equals(role)) return;
        String[] ret = new String[roles.length+1];
        for (int i=0;i< roles.length;i++)
            ret[i] = roles[i];
        ret[roles.length] = role;
        roles = ret;
    }

}
