package at.letto.tools;

import at.letto.tools.ENCRYPT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LicenseKey {

    private static final String KEYCHECKER="LETTO-LIZENZSERVER";

    /** Lizenz-Nummer mit Bezug zur Rechnungsnummer: Wird vom Lizenzierungsserver verwaltet -- id des Lizenz-Objekts*/
    private int    idLicense;
    /** IP-Adresse 1, für den diese Lizenz gilt */
    private String ip1 = "";
    /** IP-Adresse 2, für den diese Lizenz gilt */
    private String ip2 = "";
    /** IP-Adresse 3, für den diese Lizenz gilt */
    private String ip3 = "";
    /** IP-Adresse 4, für den diese Lizenz gilt */
    private String ip4 = "";
    /** Application, für die diese Lizenz gültig ist: beta wird immer als gültig interpretiert */
    private String application;
    /** Ende des Gültigkeitsdatums dieser Lizenz */
    private Date licenceEnd;
    /** Schule */
    private String schule = "";
    /** Anzahl an Schüler, für die die Lizenz gültig ist */
    private int schuelerAnz;
    /** Ansprechpartner */
    private String contactName;
    /** TelefonNummer */
    private String contactTelephone;
    /** Mail-Adresse */
    private String contactMail;
    /** Erlauben von externen Benutzern zum Bearbeiten von Beispielsammlungen */
    private boolean externalUsers = false;
    /** Studenten können angelegt werden, die selber zahlen müssen und die nicht in die Lizenzberechnung für die Schule eingehen */
    private boolean payingStudents = false;
    /** Kosten für die Anmeldung in einem Kurs pro Schüler */
    private double costsPerCourse = 0.99;
    /** Anzahl an Lehrern in dieser Schule: wenn größer als 0, dann ist die Lehreranzahl auf diese Menge begrenzt, Schüler müssen selber zahlen,
     * Diese Lizenz wird automatisch beim Lehreranlegen generiert +
     * costsPerCourse werden auf x.xx - Euro definiert
     * */
    private int teachers=0;
    /** Automatische Abrechnung für alle importierten Teilnehmer pro Kurs:
     * Die Lizenz wird beim Einspielen neuer Teilnehmer autoatisch erneuert
     * und an die Anzahl der importierten teilnehmer angepasst. */
    private boolean automaticBill=false;
    /** Beispiele können verschlüsselt werden */
    private boolean encodePossible=false;
    /** Lizenzen für Beispielsammlungen für Schulen
     * Aufbau des Eintrages:
     * Key=Passwort:user1,user2;Key2=Passwort2,user3,user4
     * Key: Eindeutiger Bezeichner zur Zuordnung des Passwortes
     * Passwort: Passwort, mit dem die Fragesammlung verschlüsselt ist
     * Doppelpunkt +....: OPTIONAL, damit können User das Recht bekommen,
     * die Fragesammlung zu bearbeiten
     * user1|user2,.... Login-Namen der User mit Bearbeitungsrechten der Fragesammlung,
     * mehrere User getrennt durch jeTUweils |
     *
     * Durch Strichpunkt getrennt können die Keys für unterschiedliche Fragesammlungen angegeben werden
     * BP: Mathe=Passwort:mayt|damb;BpSammlung2=passwort2:asch
     * */
    private String beispielsammlungen = "";
    /** Schlüssel mit dem sich der Letto-Server mit dem Lizenzserver verbindet */
    private String restkey = "";
    //----------------- Lizenzserver Datenbankverbindung ------------------------
    /** id der Schule am Lizenzserver */
    private int idSchule=0;
    /** id des Servers am Lizenzserver */
    private int idServer=0;
    /** String der verwendet wird um zu überprüfen ob die Verschlüsselung korrekt entschlüsselt wurde */
    private String keyChecker="";

    /** Hash zum schnellen Suchen des Verschlüsselungs-Key zumr entschlüsseln einer Frage,
     * wird aus <b>beispielsammlungen</b> erzeugt */
    private HashMap<String,String> beispielsammlungsHash = new HashMap<String,String>();

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Erzeugt aus einem License-Key ein Lizenz Objekt
     * @param key Schlüssel
     */
    public LicenseKey(String key, String license_password) {
        this.load(key, license_password);
    }

    // -------------------------------------------------------------------------------------------------------------

    /** @return Liefert den Key für diesen Lizenzschlüssel */
    @JsonIgnore
    public String getKey(String license_password) {
        return ENCRYPT.enc( this.toString() , license_password);
    }

    /**
     * Liefert den Key, mit dem ein Beispiel entschlüsselt werden kann
     * @param key	Name der Beispielsammlung, ist in der Frage vermerkt
     * @return		Entschlüsselungs-Key
     */
    @JsonIgnore
    public String getKeyForBeispielsammlung(String key) {
        return beispielsammlungsHash.get(key);
    }

    @Override
    public String toString() {
        return  ip1 + "," +
                this.application + "," +
                df.format(licenceEnd) + "," +
                this.schule + "," +
                this.schuelerAnz + "," +
                this.contactName + "," +
                this.contactTelephone + "," +
                this.contactMail + "," +
                this.ip2 + "," +
                this.ip3 + "," +
                this.ip4 + "," +
                this.idLicense + "," +
                this.externalUsers + "," +
                this.payingStudents + "," +
                this.costsPerCourse + "," +
                this.automaticBill + "," +
                this.teachers + "," +
                this.encodePossible + "," +
                this.beispielsammlungen + "," +
                this.restkey+","+
                this.idSchule+","+
                this.idServer+","+
                this.keyChecker+",";
    }

    /** Datums-Formatierer */
    public static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Lädt des LicenceKeyObjekt mit den Daten aus
     * dem übergebenen LicenceKey
     * @param key	Base64 + verschlüsselter LicenceKey
     */
    private void load(String key, String license_password) {
        String info[] = ENCRYPT.dec(key, license_password).split(",");
        try {
            this.ip1 = info[0];
            this.application = info[1];
            this.licenceEnd = df.parse(info[2]);
            this.schule = info[3];
            try {
                this.schuelerAnz = Integer.parseInt(info[4]);
            } catch (Exception e) {}
            this.contactName = info[5];
            this.contactTelephone = info[6];
            this.contactMail = info[7];
            if (info.length>8)
                this.ip2 = info[8];
            if (info.length>9)
                this.ip3 = info[9];
            if (info.length>10)
                this.ip4 = info[10];
            if (info.length>11) {
                try {
                    this.idLicense = Integer.parseInt(info[11]);
                } catch (Exception ex) {
                    this.idLicense = 0;
                }
            }
            if (info.length>12)
                this.externalUsers = info[12].contains("true") ? true : false;
            if (info.length>13)
                this.payingStudents  = info[13].contains("true") ? true : false;
            if (info.length>14)
                this.costsPerCourse = Double.parseDouble(info[14]);
            if (info.length>15)
                this.automaticBill  = info[15].contains("true") ? true : false;
            if (info.length>16)
                this.teachers = Integer.parseInt(info[16]);
            if (info.length>17)
                this.encodePossible = info[17].contains("true") ? true : false;
            if (info.length>18) {
                setBeispielsammlungen(info[18]);
            }
            if (info.length>19)
                this.restkey = info[19];
            if (info.length>20)
                try { this.idSchule = Integer.parseInt(info[20]); } catch (Exception ex){};
            if (info.length>21)
                try { this.idServer = Integer.parseInt(info[21]); } catch (Exception ex){};
            if (info.length>22)
                this.keyChecker = info[22];
        } catch (Exception e) {

        }
    }

    @JsonIgnore
    public void setBeispielsammlungen(String beispielsammlungen) {
        this.beispielsammlungen = beispielsammlungen;
        // Beispielsammlungen ist als String aufgebaut, wobei die
        // unterschiedlichen Beispielsammlungen durch Strichpunkt
        // getrennt sind.
        // Eine Beispielsammlung ist durch Schlüssel=Key definiert.
        for (String s : beispielsammlungen.split(";")) {
            try  {
                // Hash erstellen zum schnellen Ermitteln des Keys zum Entschlüsseln
                String[] x = s.split("=");
                this.beispielsammlungsHash.put(x[0], x[1]);
            } catch (Exception e) {}
        }
    }

    /**
     * setzt den Keychecker auf einen gültigen Wert, damit das Packet vom Lizenzserver als gültig erkannt wird.
     */
    @JsonIgnore
    public void setOk() {
        setKeyChecker(KEYCHECKER);
    }

    /**
     * setzt den Keychecker auf einen ungültigen Wert, damit das Packet vom Lizenzserver als ungültig erkannt wird.
     */
    @JsonIgnore
    public void setNotOk() {
        setKeyChecker("");
    }

    /**
     * prüft ob die Lizenz gültig entschlüsselt wurde
     * @return true wenn die Lizenz gültig entschlüsselt wurde
     */
    @JsonIgnore
    public boolean isOk() {
        return getKeyChecker().equals(KEYCHECKER);
    }

    @JsonIgnore
    public String getBeispielsammlungeninfo(){
        String info  = "";
        Matcher m;
        for (String bs : this.getBeispielsammlungen().split(";")) {
            String name="";
            String user="";
            if ((m = Pattern.compile("^([^=]+)=([^:]+)$").matcher(bs)).find()) {
                name = m.group(1).trim();
            } else if ((m = Pattern.compile("^([^=]+)=([^:]+):(.*)$").matcher(bs)).find()) {
                name = m.group(1).trim();
                user = m.group(3).trim();
            }
            if (name.length()>0) {
                if (info.length()>0) info += "; ";
                info += name;
                if (user.length()>0) info += ":"+user;
            }
        }
        return info;
    }

    @JsonIgnore
    public void setBeispielsammlungeninfo(String s) { }

}
