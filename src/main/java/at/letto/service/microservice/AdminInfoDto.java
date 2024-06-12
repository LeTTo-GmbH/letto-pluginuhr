package at.letto.service.microservice;

import lombok.*;

/**
 * Information über ein Service für den Admin
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminInfoDto {

    /** Name des Services */
    protected String servicename="";
    /** PID des Prozesses */
    protected String pid="";
    /** Home-Verzeichnis des Services am Server */
    protected String homedir="";
    /** Zeit des Service-Starts in ms */
    protected long   startuptime=0;
    /** Zeit die seit dem Service-Start vergangen ist in ms*/
    protected long   updtime=0;
    /** Version des Services */
    protected String version="";
    /** aktuelle Zeit am Service */
    protected String time="";
    /** Betriebssystem auf dem das Service läuft */
    protected String betriebssystem="";
    /** IP des Rechners */
    protected String ip="";
    /** Tastatur-Bildschirm-Encoding */
    protected String encoding="";
    /** File-Encoding */
    protected String fileEncoding="";
    /** File-Trennzeichen Slash-Backslash */
    protected String fileSeparator="";
    /** Java Versionsnummer zB. 1.8 */
    protected String javaSpecificVersion="";
    /** Hersteller der Java-Runtime zB.: Oracle Corporation*/
    protected String javaVendor="";
    /** Genauere Java Versionsbeschreibung zB.: Oracle Corporation 1.8.0_241-b07 */
    protected String javaVersion="";
    /** Genauere Java Versionsnummer zB.: 1.8.0_241-b07 */
    protected String javaVersionNumber="";
    /** Hostname */
    protected String hostname="";
    /** Sprache des Rechner auf dem das Service installiert ist */
    protected String language="";
    /** Beschreibung der Linux Version auf dem das Service läuft */
    protected String linuxDescription="";
    /** Linux Distribution auf der das Service läuft */
    protected String linuxDistribution="";
    /** Linux Release auf der das Service läuft */
    protected String linuxRelease="";
    /** Benutzername unter welchem das Service gestartet wurde */
    protected String serverUsername="";
    /** Version des JEE-Servers */
    protected String serverVersion="";
    /** System Home-Verzeichnis */
    protected String systemHome="";

    /** true wenn das Service unter Linux läuft */
    protected boolean isLinux=false;
    /** true wenn das Service unter Ubuntu läuft */
    protected boolean isUbuntu=false;
    /** true wenn das Service unter Windows läuft */
    protected boolean isWindows=false;

    /** Portnummer des http-Servers */
    protected int httpPort=0;
    /** Portnummer des ajp-Servers */
    protected int ajpPort=0;
    /** Portnummer des https-Servers */
    protected int httpsPort=0;

}
