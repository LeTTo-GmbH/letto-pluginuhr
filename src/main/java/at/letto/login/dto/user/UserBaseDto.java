package at.letto.login.dto.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserBaseDto {

    // -------------------- Primary Key ----------------------
    private Integer id;

    private Integer IDAbteilung;
    private String  activeDirectoryName;
    private Date    birthdate;

    /** Wenn true, dann darf User die Abos ncht verändern */
    private Boolean changeAbosPossible;
    private Boolean disabled;
    private String  email;
    private Integer htlID;

    /** Administrator für Schule */
    private Boolean admin;
    private Boolean extern;

    /** Globaler Administrator des Systems */
    private Boolean global;
    private Boolean mann;
    private Boolean student;
    private Boolean teacher;

    /** Lizenz für einen zahlenden Studenten */
    private String  licence;
    /** Verwendung der Schul-Lizenz für diesen Benutzer */
    private Boolean useSchoolLicence = true;

    /** Wenn true, dann kann sich der Benutzer mehrfach parallel anmelden */
    private Boolean multipleLogin;
    private String  nachname;
    private String  name;

    /** Passwort im neuen Verschlüsselungsverfahren */
    private String  password;
    private String  passwort;

    /**Wenn payingStudent = true, dann wird dieser Schüler nicht
     * in das Lizenzmodell der Schule einberechnet und muss sich für einen Kurs selber
     * registrieren und bezahlen */
    private Boolean payingStudent;
    private String  persNo;
    private Long    sassID;
    private String  sokratesID;

    /** Passwort am SVN-Server */
    private String  svnPasswort;
    private String  SVNr;
    private String  tel;
    private String  tempPasswort;

    /** Sprache für das GUI */
    private String  sprache;

    /** Kategorieanzeige auf gewünschte Themengebiete einschränken	 */
    private Boolean useAbosCategory;

    /** Kategorieanzeige auf gewünschte Benutzer einschränken */
    private Boolean useAbosUsers;
    private Boolean useCurrentYear;
    private String  vorname;

}
