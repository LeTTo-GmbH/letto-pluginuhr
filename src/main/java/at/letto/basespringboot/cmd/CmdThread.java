package at.letto.basespringboot.cmd;

import at.letto.tools.Cmd;
import at.letto.tools.Datum;
import at.letto.tools.threads.ThreadStatus;
import lombok.Getter;
import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.interrupted;

/**
 * Objekt welche für jedes asynchron gestartete Kommando auf der Commandline erzeugt wird
 */
//@AllArgsConstructor
@Getter
public class CmdThread implements Runnable {

    public enum CmdMode{ NORMAL, CMD, BATCH, BASH, SH }

    protected final CmdMode cmdMode;
    /** aktuell höchster Wert der Thread id */
    protected static long id_counter = 0;
    /** Thread id für die eindeutige Identifikation des Threads */
    protected final long id;
    /** Homeverzeichnis des Commandos */
    protected final String homedir;
    /** Liste aller Commandos welche ausgeführt werden */
    protected final String[] cmd;
    /** Alle Befehle durch Beistrich getrennt */
    protected final String command;
    /** Zeichensatz mit dem die Kommandos ausgeführt werden */
    protected String charset;
    /** Thread der läuft */
    protected final Thread thread;
    /** Startzeit des Threads in ms */
    protected final long starttime;
    /** Startzeit als Systemzeit */
    protected final Date startdate;
    /** Endzeit des Threads in ms */
    protected long stoptime=0;
    /** aktueller Status des Threads */
    protected ThreadStatus threadStatus = ThreadStatus.NEW;
    /** Fehlermeldung bei einer fehlerhaften Beendigung */
    protected Throwable error=null;
    /** Standard-Ausgabe des Befehls */
    private Vector<Vector<String>> out = new Vector<Vector<String>>();
    /** Error-Ausgabe des Befehls */
    private Vector<Vector<String>> err = new Vector<Vector<String>>();
    /** Hier werden alle Ausgaben in HTML-formatierter Form angehängt */
    protected Vector<String> htmlOutput = new Vector<String>();
    /** Backlink für die Rückverlinkung */
    protected String backlink="";
    /** HTML-Template für die Rückverlinkung */
    protected String template="";
    /** Datei für Batchverarbeitung */
    public File batchfile;
    /** Prozess für das Commando, welches abgesetzt wurde */
    protected Process p=null;

    public CmdThread(String homedir, String charset, CmdMode cmdMode, String ... cmd) {
        this.id      = ++id_counter;
        this.homedir = homedir;
        Vector<String> cv = new Vector<String>();
        for (String c:cmd) {
            c = c.replaceAll("\\r","").trim();
            for (String cs:c.split("\\n")) {
                cs = cs.trim();
                if (cs.startsWith("#") || cs.startsWith("/*") || cs.startsWith("//")) {
                    // Bemerkungen werden ignoriert
                } else cv.add(cs);
            }
        }
        String[] nc = new String[cv.size()];
        for (int i=0;i<cv.size();i++) nc[i] = cv.get(i);
        this.cmd     = nc;

        this.charset = charset;
        this.cmdMode = cmdMode;

        String command1 = "";
        if (this.cmd.length==1) command1 = this.cmd[0];
        else if (this.cmd.length<1)  command1 = "";
        else {
            command1 = this.cmd[0];
            for (int i = 1; i < this.cmd.length; i++)
                command1 += ", " + this.cmd[i];
        }
        command = command1;

        this.thread  = new Thread(this);
        this.starttime = System.currentTimeMillis();
        this.startdate = new Date();
    }

    public static CmdThread createThread(String homedir, String charset, String ... cmd) {
        CmdThread t = new CmdThread(homedir,charset,CmdMode.NORMAL, cmd);
        t.start();
        return t;
    }

    public static CmdThread createThread(String homedir, String charset, CmdMode cmdMode, String ... cmd) {
        CmdThread t = new CmdThread(homedir,charset,cmdMode, cmd);
        t.start();
        return t;
    }

    public static CmdThread createThreadMessage(String message) {
        CmdThread t =  new CmdThread("","UTF-8",CmdMode.NORMAL,"message");
        t.start();
        t.htmlOutput.add("<div style=\"color:blue;\">"+ HtmlEscape.escapeHtml5(message)+"</div>");
        t.threadStatus=ThreadStatus.FINISHED;
        t.stoptime = System.currentTimeMillis();
        return t;
    }

    public CmdThread backlink(String backlink) { this.backlink = backlink; return this; }

    public CmdThread template(String template) { this.template = template; return this; }

    public void start() {
        thread.start();
    }

    /** @param cmd cmd wird als blauer String ausgegeben alle Sonderzeichen werden duch Entities ersetzt! */
    public void htmlCmd(String cmd) {
        htmlOutput.add("<div style=\"color:blue;\">"+ HtmlEscape.escapeHtml5(cmd)+"</div>");
    }
    /** @param cmd cmd wird als blauer String ausgegeben. Es können im String cmd HTML-Tags verwendet werden. */
    protected void htmlCmdPlain(String cmd) {
        htmlOutput.add("<div style=\"color:blue;\">"+ HtmlEscape.escapeHtml5(cmd)+"</div>");
    }
    /** @param cmd cmd wird als String ausgegeben alle Sonderzeichen werden duch Entities ersetzt! */
    public void htmlOut(String cmd) {
        htmlOutput.add("<div style=\"color:black;\">"+ HtmlEscape.escapeHtml5(cmd)+"</div>");
    }
    /** @param cmd cmd wird als String ausgegeben. Es können im String cmd HTML-Tags verwendet werden. */
    protected void htmlOutPlain(String cmd) {
        htmlOutput.add("<div style=\"color:black;\">"+ HtmlEscape.escapeHtml5(cmd)+"</div>");
    }
    /** @param cmd cmd wird als roter String ausgegeben alle Sonderzeichen werden duch Entities ersetzt! */
    public void htmlErr(String cmd) {
        htmlOutput.add("<div style=\"color:red;\">"+ HtmlEscape.escapeHtml5(cmd)+"</div>");
    }
    /** @param cmd cmd wird als roter String ausgegeben. Es können im String cmd HTML-Tags verwendet werden. */
    protected void htmlErrPlain(String cmd) {
        htmlOutput.add("<div style=\"color:red;\">"+ HtmlEscape.escapeHtml5(cmd)+"</div>");
    }

    /**
     * Führt mehrer Kommandos auf der Betriebssystem-Commandline aus
     * @param cmd     Kommand
     */
    public final void runCmd(String ... cmd) {
        Vector<String> vout;
        Vector<String> verr;
        if (cmdMode==CmdMode.BATCH || cmdMode==CmdMode.BASH || cmdMode==CmdMode.SH) {
            // Batchverarbeitung mit Batchdatei
            int i=0;
            do {
                String filename = ("b"+(Math.random() * 1e12)).replaceAll("\\.","").replaceAll("E","");
                if (cmdMode==CmdMode.BATCH) filename += ".bat"; else filename += ".sh";
                batchfile = new File(filename);
                if (batchfile.exists()) batchfile=null;
                else {
                    try {
                        batchfile.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                i++;
            } while (i<1000 && batchfile==null);
            if (!batchfile.exists()) batchfile=null;
            if (batchfile==null) throw new RuntimeException("Fehler, Batchdatei kann nicht erstellt werden!!");
            Vector<String> data = new Vector<>();
            if (cmdMode==CmdMode.BASH) data.add("#!/bin/bash -e");
            if (cmdMode==CmdMode.SH) data.add("#!/bin/sh -e");
            if (cmdMode==CmdMode.BATCH) data.add("@echo off");
            for (i=0; i<cmd.length; i++) {
                if (cmdMode==CmdMode.BATCH) data.add("echo XXX"+i+":"+cmd[i]);
                else                        data.add("echo \"XXX"+i+":"+cmd[i].replaceAll("\"","\\\\\"")+"\"");
                data.add(cmd[i]);
            }
            Cmd.writelnfile(data,batchfile);
            batchfile.setExecutable(true);
            String c = batchfile.getAbsolutePath();
            htmlCmd(c);
            vout = new Vector<String>();
            verr = new Vector<String>();
            out.add(vout);
            err.add(verr);
            systemcall(c,charset,vout);
            try { if (batchfile!=null && batchfile.exists()) batchfile.delete(); batchfile=null;} catch (Exception ex) {}
        } else {
            // Alle Befehle getrennt voneineander einzeln starten
            for (int i=0; i<cmd.length; i++) {
                String c = cmd[i];
                htmlCmd(c);
                vout = new Vector<String>();
                verr = new Vector<String>();
                out.add(vout);
                err.add(verr);
                systemcall(c,charset,vout,verr);
            }
        }
    }

    public void task(){
        runCmd(cmd);
    }

    @Override
    public void run() {
        threadStatus = ThreadStatus.RUNNING;
        File batchfile=null;
        try {
            task();
            // Verarbeitung des Threads ist fertig
            if (interrupted())
                threadStatus = ThreadStatus.STOPPED;
            else
                threadStatus = ThreadStatus.FINISHED;
        } catch (Exception ex) {
            threadStatus = ThreadStatus.ERROR;
            error = ex;
        } catch (Error err) {
            threadStatus = ThreadStatus.ERROR;
            error = err;
        }
        try { if (batchfile!=null && batchfile.exists()) batchfile.delete(); batchfile=null;} catch (Exception ex) {}
        this.stoptime = System.currentTimeMillis();
    }

    /** @return liefert die Ausgabe des Befehls */
    public String getHtmlOutput() {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i=0;i<htmlOutput.size();i++)
                sb.append(htmlOutput.get(i));
        } catch (Exception ex) {}
        String s = sb.toString();
        return s;
    }

    public void systemcall(String cmd, String charset,Vector<String> out) {
        systemcall(cmd,charset,out,null);
    }

    /**
     * Führt das Kommando cmd im Betriebssystem aus, und wartet bis
     * es wieder beendet wird!
     * @param cmd     Kommando
     * @param charset Character-Set
     * @param out     Output des Programmes als Vektor von Strings
     * @param err     Fehlerausgabe des Programmes, wenn null - dann Fehler in out!
     */
    public void systemcall(String cmd, String charset,Vector<String> out, Vector<String> err) {
        p=null;
        try {
            String acmd = "";
            String h    = cmd;
            int    mode=0;      // 0 normal 1..innerhalb von Hochkomma
            int    f;

            /*
             * Zerlegen des Kommandos in die Parameterliste und Entfernen der Doppelhochkomma
             */
            Vector<String> cv = new Vector<String>();
            if (cmdMode==CmdMode.CMD || cmdMode==CmdMode.BATCH) { cv.add("cmd"); cv.add("/c"); }

            do {
                // Durchuche den String nach dem nächsten Vorkommen von " oder blank
                int pos      = h.length();
                char c       = 'a';
                f = h.indexOf("\""); if ((f>-1) && (f<pos)) { pos=f;c='"'; }
                f = h.indexOf(" "); if ((f>-1) && (f<pos)) { pos=f;c=' '; }
                if (c!='a') {
                    acmd += h.substring(0,pos);
                    h = h.substring(pos);
                }
                if (c=='"') {
                    if (mode==1) mode=0;
                    else         mode=1;
                    h = h.substring(1);
                } else if (c==' ') {
                    if (mode==1) {
                        acmd += " ";
                    } else {
                        if (acmd.length()>0) cv.add(acmd);
                        acmd="";
                    }
                    h = h.substring(1);
                }  else {
                    acmd += h;
                    h="";
                }
            } while (h.length()>0);

            if (acmd.length()>0) cv.add(acmd);
            String cmdlst[] = new String[cv.size()];
            for (int j=0;j<cv.size();j++) cmdlst[j] = cv.get(j);
            /**
             * Starten den Prozesses
             */
            ProcessBuilder pb = new ProcessBuilder(cmdlst);
            if (err==null) pb.redirectErrorStream(true);  // Leite des Error auf Output um!
            File hd = new File(homedir);
            if (hd.exists()) pb = pb.directory(hd);

            p = pb.start();
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream(),charset));
            BufferedReader error=null;
            error =new BufferedReader(new InputStreamReader(p.getErrorStream(),charset));

            String line, line1;
            Pattern pattern = Pattern.compile("^XXX(\\d+):(.+)$");
            Matcher m;
            while ((line=reader.readLine())!=null) {
                if ((m=pattern.matcher(line)).find()) {
                    htmlCmd(m.group(2));
                } else {
                    out.add(line);
                    htmlOut(line);
                }
                if (interrupted()) {
                    p.destroyForcibly();
                    p=null;
                    return;
                }
            }
            if (err!=null && error!=null)
                while ((line1=error.readLine())!=null) {
                    err.add(line1);
                    htmlErr(line1);
                }

            p.destroyForcibly();
        } catch(IOException e1) {
            String e = cmd+" kann nicht gestartet werden!";
            if (err==null) { out.add(e); htmlOut(e); }
            else           { err.add(e); htmlErr(e); }
        }
        p=null;
    }

    public void stop() {
        if (threadStatus==ThreadStatus.RUNNING) {
            try {
                thread.interrupt();
                int ms=0;
                while (ms<1000 && thread.isAlive()) {
                    ms += 10;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    if (ms==500)
                        p.destroyForcibly();
                }
                //this.thread.stop();
                //if (thread.isAlive())
                //    this.thread.destroy();
            } catch (Exception ex) {
                System.out.println("Prozess konnte nicht gestoppt werden!"+this.command.replaceAll("\n"," "));
                threadStatus=ThreadStatus.ZOMBIE;
            } catch (Error error) {
                System.out.println("Error: Prozess konnte nicht gestoppt werden!");
                threadStatus=ThreadStatus.ZOMBIE;
            }
            if (thread.isAlive())
                threadStatus=ThreadStatus.ZOMBIE;
            else
                threadStatus=ThreadStatus.STOPPED;
            // Lösche das Batchfile
            try { if (batchfile!=null && batchfile.exists()) batchfile.delete(); batchfile=null;} catch (Exception ex) {}
        }
    }

    public String getTimeInfoHTML() {
        String ret = "<span style=\"color:orange\">"+ Datum.formatDateTime(startdate)+"</span> - <span style=\"color:blue\">";
        if (isFinished()) ret += (stoptime-starttime)/1000.0;
        else ret += (System.currentTimeMillis()-starttime)/1000.0;
        ret += " s </span>";
        return ret;
    }

    public boolean isFinished() {
        switch (threadStatus) {
            default:
            case NEW:
            case RUNNING:
                return false;
            case ZOMBIE:
            case ERROR:
            case STOPPED:
            case FINISHED:
                return true;
        }
    }

    public CmdDto getCmdDto() {
        CmdDto cmdDto = new CmdDto();
        String cmd = ""; for (String s:getCmd()) cmd+=(cmd.length()>0?"\n":"")+s;
        cmdDto.setCmd(cmd);
        cmdDto.setHomedir(cmdDto.getHomedir());
        cmdDto.setBacklink(getBacklink());
        cmdDto.setId(getId());
        cmdDto.setUserAction("");
        return cmdDto;
    }

    public String lastOutputLine(int lines) {
        String ret = "";
        if (out!=null && out.size()>0) {
            Vector<String> lastcmd = out.lastElement();
            if (lastcmd!=null && lastcmd.size()>0) {
                int start = lastcmd.size()-lines;
                if (start<0) start=0;
                for (int i=start;i<lastcmd.size();i++)
                    ret += (ret.length()>0 ? " " : "") + lastcmd.get(i);
            }
        }
        return ret;
    }

}
