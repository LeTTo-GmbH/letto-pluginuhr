package at.letto.tools.service;

import at.letto.tools.Cmd;
import at.letto.tools.WebGet;
import at.letto.tools.html.HTMLtool;
import lombok.Getter;
import lombok.Setter;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

public class ConfigService {
    /** Pfad, auf dem alle Scripts für Letto liegen */
    private String scriptPath;

    /** Input für geforderte Revision */
    @Getter @Setter
    private String revNr = "";

    /** Ausgabe der Skript-Meldungen */
    @Getter
    private String msg = "";

    /** Auszuführender Befehl am Server */
    @Getter @Setter
    private String cmd = "";

    /** Watchdog-Zeit */
    @Getter @Setter
    private int watchdogTime = 0;

    /** Watchdog-URL */
    @Getter @Setter
    private String watchdogURL = "https://localhost/letto/test.jsf";

    /**
     * Konstruktor
     * @param scriptPath Pfad, in dem alle Skripts für update ... liegen
     */
    public ConfigService(String scriptPath) {
        this.scriptPath = scriptPath;
        File f = new File(scriptPath);
        if (!f.isDirectory()) {
            Path p = f.toPath();
            try {
                Files.createDirectory(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Startet den Webserver neu
     * @return Meldungen des Scripts
     */
    public String restart() {
        String restart = scriptPath + "/restart.sh";
        String msg = Cmd.systemcall(restart);
        setMsg(msg);
        return msg;
    }

    public String execute() {
        String msg = "";
        if (cmd.isEmpty()) {
            msg = "Das Eingabefeld für den auszuführenden Befehl darf nicht leer sein!";
            setMsg(msg);
            return msg;
        }
        //msg = Cmd.systemcall(HTMLtool.XMLToString(cmd));
        msg = Cmd.systemcallbatch(HTMLtool.XMLToString(cmd),"/opt/letto");
        setMsg(msg);
        return msg;
    }

    /** @return Check, ob alle notwendigen Verzeichnisse definiert sind */
    public String configCheck() {
        String msg = "";
        File f= new File("/opt/letto");
        msg += f.isDirectory() ? "/opt/letto vorhanden\n" : "/opt/letto ist als Verzeichnis nicht vorhanden!\n";
        f= new File("/opt/tomee");
        msg += f.isDirectory() ? "/opt/tomee vorhanden\n" : "/opt/tomee ist als Verzeichnis oder symb. Link nicht vorhanden!\n";
        f= new File("/opt/letto/images");
        msg += f.isDirectory() ? "/opt/letto/images vorhanden\n" : "/opt/letto/images ist als Verzeichnis oder symb. Link nicht vorhanden!\n";
        f= new File("/opt/letto/images/plugins");
        msg += f.isDirectory() ? "/opt/letto/images/plugins vorhanden\n" : "/opt/letto/images/plugins ist als Verzeichnis nicht vorhanden!\n";
        f= new File("/opt/letto/images/photos");
        msg += f.isDirectory() ? "/opt/letto/images/photos vorhanden\n" : "/opt/letto/images/photos ist als Verzeichnis nicht vorhanden!\n";

        this.setMsg(msg);
        return msg;
    }


    /**
     * Update auf die momentan aktuellste Version von Letto
     * oder auf bestimmte Revisionsnummer, die aus der Oberfläche in revNr gesetzt wird
     * @return Meldungen des Scripts
     */
    public String updateLetto() {
        String update = scriptPath + "/update.sh " + revNr;
        String msg = Cmd.systemcall(update);
        setMsg(msg);
        return msg;
    }

    /**
     * Update auf die momentan aktuellste Version von Letto
     * @return Meldungen des Scripts
     */
    public String updateStable() {
        String update = scriptPath + "/update.sh stable";
        String msg = Cmd.systemcall(update);
        setMsg(msg);
        return msg;
    }

    /**
     * Aktuelle Revisionsnummer am Downloadbereich
     * @return  Revisionsnummer
     */
    public String getDailyRevision() {
        Vector<String> w = WebGet.readURL("https://letto.at/download/public/revision/daily-revision.txt");
        if (w.size()>0) return w.get(0);
        return "";
    }

    /**
     * Aktuelle Revisionsnummer der Stable-Version am Downloadbereich
     * @return  Revisionsnummer
     */
    public String getStableRevision() {
        Vector<String> w = WebGet.readURL("https://letto.at/download/public/revision/stable-revision.txt");
        if (w.size()>0) return w.get(0);
        return "";
    }

    public void setMsg(String msg) {
        // this.msg = StringToHTML(msg);
        this.msg = msg;
    }
    /**
     * Initialisierungsskript ausführen, das alle anderen Skripts erzeugt
     * @return Meldungen des Scripts
     */
    public String initScripts() {
        updateScriptDownload();
        String initAllScripts = scriptPath + "/updatescripts.sh ";
        String msg = Cmd.systemcall("chmod a+x " + initAllScripts);
        msg+= Cmd.systemcall(initAllScripts);
        setMsg(msg);
        return msg;
    }
    /**
     * Download der Skripts, mit dem alle anderen update-Scripts heruntergeladen werden können
     * @return  Fehlermeldung oder leerer String, wenn erfolgreich
     */
    public String updateScriptDownload()  {
        String urlString = "http://letto.at/download/letto/updatescripts.sh";
        String username = "letto";
        String password = "h[1EJj_+epQ34Pz";
        String msg = "";
        Authenticator.setDefault(new MyAuthenticator(username, password));
        try {
            URL url = new URL(urlString);
            InputStream content = (InputStream) url.getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            File scriptFile = new File(this.scriptPath + "/updatescripts.sh");
            BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));

            String line;
            while ((line = in.readLine()) != null)
                writer.write(line+"\n");
            writer.close();
        } catch (Exception e) {
            msg = "Download gescheitert! " + e.getStackTrace();
        }
        setMsg(msg);
        return msg;
    }

    static class MyAuthenticator extends Authenticator {
        private String username, password;

        public MyAuthenticator(String user, String pass) {
            username = user;
            password = pass;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            System.out.println("Requesting Host  : " + getRequestingHost());
            System.out.println("Requesting Port  : " + getRequestingPort());
            System.out.println("Requesting Prompt : " + getRequestingPrompt());
            System.out.println("Requesting Protocol: "
                    + getRequestingProtocol());
            System.out.println("Requesting Scheme : " + getRequestingScheme());
            System.out.println("Requesting Site  : " + getRequestingSite());
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

}
