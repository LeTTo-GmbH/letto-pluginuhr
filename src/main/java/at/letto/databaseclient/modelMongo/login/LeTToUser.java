package at.letto.databaseclient.modelMongo.login;

import at.letto.security.LettoToken;
import at.letto.tools.Datum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "users") // Name der Collection in MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeTToUser {

    public static final int USER_ACTION_LOGIN               = 1;
    public static final int USER_ACTION_LOGOUT              = 2;
    public static final int USER_ACTION_LOGIN_TEMP_PASSWORD = 3;
    public static final int USER_ACTION_TOKEN_TIMEOUT       = 4;
    public static final int USER_ACTION_FAILED_LOGIN        = 5;

    /** ID des Benutzers als String kombiniert Schulkuerzel und User-ID aus der MySQL-DB,
     * bzw. email-Adresse bei LeTTo-Private */
    @Id private String id;

    /** Schulkürzel */
    @Indexed(unique = false)
    private String school;

    @Indexed(unique = false)
    /** User-ID aus der MySQL-Datenbank */
    private long   userId=0;

    @Indexed(unique = false)
    /** School-ID aus der MySQL-Datenbank */
    private long   schoolId=0;

    @Indexed(unique = false)
    /** Benutzername */
    private String username;

    @Indexed(unique = false)
    /** LDAP-Benutzername */
    private String ldapName;

    /** Vorname */
    private String vorname;

    /** Familienname */
    private String nachname;

    @Indexed(unique = false)
    /** Email */
    private String email;

    private boolean multipleLogin=false;
    private boolean admin=false;
    private boolean teacher=false;
    private boolean student=false;
    private boolean global=false;

    /** Datum und Uhrzeit des letzten Logins als DateInteger */
    private long lastLoginAttempt=0;

    /** Datum und Uhrzeit des letzten korrekten Logins als DateInteger */
    private long lastCorrectLogin=0;

    /** Datum und Uhrzeit des letzten fehlerhaften Logins als DateInteger */
    private long lastFailedLogin=0;

    /** Datum und Uhrzeit des ersten Logins als DateInteger */
    private long firstLogin=0;

    /** Datum und Uhrzeit des letzten Token-Updates als DateInteger */
    private long lastUpdate=0;

    /** Datum und Uhrzeit des letzten korrekten Logouts als DateInteger */
    private long lastLogout=0;

    /** Datum und Uhrzeit des letzten Logouts durch einen Token-Timeout als DateInteger */
    private long lastTimeoutLogout=0;

    /** Anzahl der fehlerhaften Logins insgesamt */
    private int  failedLogins=0;

    /** Anzahl der fehlerhaften Logins nach dem letzten erfolgreichen Login */
    private int  failedLoginsAfterCorrectLogin=0;

    /** Anzahl der fehlerhaften Logins des aktuellen Tages */
    private int  failedLoginsAktualDay=0;

    /** Anzahl der korrekten Logins nach dem letzten fehlerhaften Login */
    private int  correctLoginsAfterFailedLogin=0;

    /** Anzahl der korrekten Logins insgesamt */
    private int  correctLogins=0;

    /** Anzahl der korrekten Logouts insgesamt */
    private int  correctLogouts=0;

    /** Anzahl der Logouts durch Ablauf der Token-Zeit */
    private int  timeoutLogouts=0;

    @Indexed(unique = false)
    /** Gibt an ob der Benutzer gerade eingeloggt ist */
    private boolean currentlyLoggedIn=false;

    /** letzte durchgeführte Aktion mit dem Benutzer */
    private int lastUserAction = 0;

    /** Datum und Uhrzeit der letzten Benutzer-Action als DateInteger */
    private long lastUserActionTime = 0;

    /** Alias-Logins mit mit diesem Benutzer */
    private List<AliasLogin> aliasLoginList = new ArrayList<>();

    public LeTToUser(LettoToken lettoToken) {
        this();
        userCredentials(lettoToken);
    }

    /** setzt alle Benutzerdaten aus dem Token ohne den Token in die aktiven Tokens einzutragen */
    public LeTToUser userCredentials(LettoToken lettoToken) {
        school    = (lettoToken.getSchool()!=null && lettoToken.getSchool().length()>0)?
                lettoToken.getSchool():school;
        userId    = lettoToken.getIdUser()!=null?lettoToken.getIdUser():userId;
        id        = school+"_"+userId;
        schoolId  = lettoToken.getIdSchule()!=null?lettoToken.getIdSchule():schoolId;
        username  = (lettoToken.getUsername()!=null && lettoToken.getUsername().length()>0)?
                lettoToken.getUsername():username;
        vorname   = (lettoToken.getVorname()!=null && lettoToken.getVorname().length()>0)?
                lettoToken.getVorname():vorname;
        nachname  = (lettoToken.getNachname()!=null && lettoToken.getNachname().length()>0)?
                lettoToken.getNachname():nachname;
        email     = (lettoToken.getEmail()!=null && lettoToken.getEmail().length()>0)?
                lettoToken.getEmail():email;
        ldapName  = (lettoToken.getActiveDirectoryName()!=null && lettoToken.getActiveDirectoryName().length()>0)?
                lettoToken.getActiveDirectoryName():ldapName;
        multipleLogin = lettoToken.isMultipleLogin();
        admin = lettoToken.isAdmin();
        teacher = lettoToken.isTeacher();
        student = lettoToken.isStudent();
        global = lettoToken.isGlobal();
        return this;
    }

    public String loginTimeString(){          return lastCorrectLogin>0?Datum.formatDateTime(lastCorrectLogin):""; }
    public String firstLoginString(){         return firstLogin>0?Datum.formatDateTime(firstLogin):""; }
    public String lastLoginString(){          return lastLoginAttempt>0?Datum.formatDateTime(lastLoginAttempt):""; }
    public String lastCorrectLoginString(){   return lastCorrectLogin>0?Datum.formatDateTime(lastCorrectLogin):""; }
    public String lastFailedLoginString(){    return lastFailedLogin>0?Datum.formatDateTime(lastFailedLogin):""; }
    public String lastLogoutString(){         return lastLogout>0?Datum.formatDateTime(lastLogout):""; }
    public String lastTimeoutLogoutString(){  return lastTimeoutLogout>0?Datum.formatDateTime(lastTimeoutLogout):""; }
    public String lastUserActionTimeString(){ return lastUserActionTime>0?Datum.formatDateTime(lastUserActionTime):""; }
    public void   incCorrectLogins()       { correctLogins++; }
    public void   incCorrectLogouts()      { correctLogouts++; }
    public void   incTimeoutLogouts()      { timeoutLogouts++; }
    public void   incFailedLogins()        { failedLogins++; }
    public void   incFailedLoginsAktualDay() { failedLoginsAktualDay++; }
    public void   incFailedLoginsAfterCorrectLogin() { failedLoginsAfterCorrectLogin++; }
    public void   incCorrectLoginsAfterFailedLogin() { correctLoginsAfterFailedLogin++; }

    public String roleString(){
        StringBuilder sb = new StringBuilder();
        if (admin) sb.append("Admin ");
        if (teacher) sb.append("Teacher ");
        if (student) sb.append("Student ");
        if (global) sb.append("Global ");
        String result=sb.toString().trim();
        return result.length()>0?result:"other User";
    }

    public long loggedInSortString(){
        return -lastUserActionTime;
    }

    public String loggedOutSort(){
        String result = student?"S":"T";
        result += getUsername();
        return result;
    }

}
