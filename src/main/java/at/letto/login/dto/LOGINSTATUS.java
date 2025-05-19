package at.letto.login.dto;

/**
 *  SUCCESS: Login oder TOKEN-Refresh erfolgreich<br>
 *  SUCCESS_OLDSESSIONDESTROY: Login erfolgreich, aber die schon bestehende Session wurde gelöscht da kein Mehrfachlogin zulässig ist und der Fingerprint zu bestehenden Session passt<br>
 *  FAILURE: Login fehlgeschlagen da Benutzername oder Passwort falsch sind<br>
 *  MULTILOGIN_FAILURE: Login fehlgeschlagen da Benutzername keinen Mehrfachlogin machen darf und der Fingerprint nicht zum Fingerprint einer bestehenden SESSION passt<br>
 *  EXPIRED: Token ist abgelaufen<br>
 *  INVALID: Token ist ungültig<br>
 *  NOT_FOUND: Token ist nicht vorhanden<br>
 *  NOT_VALID: Token ist nicht mehr gültig<br>
 *  SESSION_EXPIRED: Token ist nicht mehr gültig und die Session ist abgelaufen<br>
 *  SESSION_INVALID: Token ist nicht mehr gültig und die Session ist ungültig<br>
 *  FINGERPRINT_INVALID: Fingerprint passt nicht zum Fingerprint der Session<br>
 */
public enum LOGINSTATUS {

    /** Login oder TOKEN-Refresh erfolgreich */
    SUCCESS,

    /** Login erfolgreich, aber die schon bestehende Session wurde gelöscht da kein Mehrfachlogin zulässig ist und der Fingerprint zu bestehenden Session passt */
    SUCCESS_OLDSESSIONDESTROY,

    /** Login fehlgeschlagen da Benutzername oder Passwort falsch sind */
    FAILURE,

    /** Login fehlgeschlagen da Benutzername keinen Mehrfachlogin machen darf und der Fingerprint nicht zum Fingerprint einer bestehenden SESSION passt */
    MULTILOGIN_FAILURE,

    /** Das eingegebene Passwort entspricht dem Temppasswort und dieses muss geändert werden, es gibt daher noch keinen Token */
    TEMPPASSWORD_OK,

    /** Token ist abgelaufen */
    EXPIRED,

    /** Token ist ungültig */
    INVALID,

    /** Token ist nicht vorhanden */
    NOT_FOUND,

    /** Token ist nicht mehr gültig */
    NOT_VALID,

    /** Token ist nicht mehr gültig und die Session ist abgelaufen */
    SESSION_EXPIRED,

    /** Token ist nicht mehr gültig und die Session ist ungültig */
    SESSION_INVALID,

    /** Fingerprint passt nicht zum Fingerprint der Session */
    FINGERPRINT_INVALID

}
