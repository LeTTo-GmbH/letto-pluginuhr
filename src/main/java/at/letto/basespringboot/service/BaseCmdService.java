package at.letto.basespringboot.service;

import at.letto.basespringboot.cmd.CmdThread;
import at.letto.tools.JSON;
import at.letto.tools.threads.ThreadStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.*;
import java.util.Vector;

/**
 * Service für die Ausführung von Threads am Server
 */
@Service
@Getter @Setter
public class BaseCmdService {

    private boolean windows = false;
    private String  rootPath   = "/";
    private String  cmdCharset = "UTF-8";

    public void init(boolean windows, String  rootPath, String cmdCharset) {
        this.windows    = windows;
        this.rootPath   = rootPath;
        this.cmdCharset = cmdCharset;
    }

    private Vector<CmdThread> threads = new Vector<CmdThread>();

    private String charset = null;

    /** speichert alle Threads der Threadliste als JSON in einer Datei */
    public String saveThreads(File file) {
        if (file==null) return "cannot save threadlist to null-file ";
        String data = JSON.objToJson(threads);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
            return "";
        } catch (IOException e) {
            return "cannot save threadlist to file "+file.getAbsolutePath();
        }
    }

    /** Lädt alle Threads aus einer Datei in die Threadliste */
    public String loadThreads(File file) {
        if (file==null) return "cannot read threadlist from null-file ";
        String data;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            while ((data=bufferedReader.readLine())!=null)
                stringBuilder.append(data+"\n");
            bufferedReader.close();
            Vector<CmdThread> threadlist = JSON.jsonToObj(stringBuilder.toString(),Vector.class);
            if (threadlist!=null)
                threads = threadlist;
            return "";
        } catch (IOException e) {
            return "cannot save threadlist to file "+file.getAbsolutePath();
        }
    }

    /**
     * Führt ein Kommando auf der Kommandozeile oder Linux-Shell immer über ein Script aus<br>
     * Die Ausführung erfolgt asynchron in einem eigenen Thread
     * @param backlink Backlink für den Link am Ende der Ausführung
     * @param cmd      Kommando welches ausgeführt werden soll
     * @param homedir  Basisverzeichnis der Ausführung
     * @return         Thread
     */
    public CmdThread cmdScriptBackground(String backlink, String homedir, String ... cmd) {
        CmdThread cmdThread;
        CmdThread.CmdMode mode = windows ? CmdThread.CmdMode.BATCH : CmdThread.CmdMode.BASH;
        cmdThread = createThread(homedir,mode, cmd).backlink(backlink);
        return cmdThread;
    }

    /**
     * Führt ein Kommando auf der Kommandozeile oder Linux-Shell immer über ein Script aus<br>
     * Die Ausführung erfolgt asynchron in einem eigenen Thread
     * @param backlink Backlink für den Link am Ende der Ausführung
     * @param template HTML-Template Name als Backlink am Ende der Ausführung
     * @param cmd      Kommando welches ausgeführt werden soll
     * @param homedir  Basisverzeichnis der Ausführung
     * @return         Thread
     */
    public CmdThread cmdScriptBackgroundTL(String backlink, String template, String homedir, String ... cmd) {
        CmdThread cmdThread;
        CmdThread.CmdMode mode = windows ? CmdThread.CmdMode.BATCH : CmdThread.CmdMode.BASH;
        cmdThread = createThread(homedir,mode, cmd).backlink(backlink).template(template);
        return cmdThread;
    }

    /**
     * Führt ein Kommando auf der Kommandozeile oder Linux-Shell aus immer als Einzelprozess, auch bei mehreren Kommandos<br>
     * Die Ausführung erfolgt asynchron in einem eigenen Thread
     * @param backlink Backlink für den Link am Ende der Ausführung
     * @param cmd      Kommando welches ausgeführt werden soll
     * @param homedir  Basisverzeichnis der Ausführung
     * @return         Thread
     */
    public CmdThread cmdBackground(String backlink, String homedir, String ... cmd) {
        CmdThread cmdThread;
        if (windows) cmdThread = createThread(homedir,CmdThread.CmdMode.CMD,cmd).backlink(backlink);
        else cmdThread = createThread(homedir,CmdThread.CmdMode.NORMAL, cmd).backlink(backlink);
        return cmdThread;
    }

    /**
     * Startet einen oder mehrere Kommandozeilenbefehle im Vordergrund immer als Einzelprozesse!
     * @param timeoutms Maximale Dauer aller Commandos bis es automatisch abgebrochen wird!
     * @param cmd       Kommandos welches ausgeführt werden sollen
     * @return          Thread
     */
    public CmdThread cmdForeground(long timeoutms, String ... cmd) {
        if (windows) return cmdForeground(rootPath, timeoutms, CmdThread.CmdMode.CMD, true, cmd);
        else return cmdForeground(rootPath, timeoutms, CmdThread.CmdMode.NORMAL, true, cmd);
    }

    /**
     * Startet einen Kommandozeilenbefehl im Vordergrund aber immer im Bash-Modus
     * @param timeoutms Maximale Dauer des Commandos bis es automatisch abgebrochen wird!
     * @param cmd       Kommando welches ausgeführt werden soll
     * @return          Thread
     */
    public CmdThread cmdScriptForeground(long timeoutms, String ... cmd) {
        return cmdScriptForeground(timeoutms, true, cmd);
    }

    /**
     * Startet einen Kommandozeilenbefehl im Vordergrund aber immer im Bash-Modus
     * @param timeoutms Maximale Dauer des Commandos bis es automatisch abgebrochen wird!
     * @param save      Gibt an ob der Thread in der Threadliste threads gespeichert werden soll
     * @param cmd       Kommando welches ausgeführt werden soll
     * @return          Thread
     */
    public CmdThread cmdScriptForeground(long timeoutms,boolean save, String ... cmd) {
        if (windows) return cmdForeground(rootPath, timeoutms, CmdThread.CmdMode.BATCH, save, cmd);
        else return cmdForeground(rootPath,timeoutms, CmdThread.CmdMode.BASH, save, cmd);
    }

    /**
     * Startet einen Kommandozeilenbefehl im Vordergrund aber immer im Bash-Modus
     * @param homedir   Basisverzeichnis der Ausführung
     * @param timeoutms Maximale Dauer des Commandos bis es automatisch abgebrochen wird!
     * @param save      Gibt an ob der Thread in der Threadliste threads gespeichert werden soll
     * @param cmd       Kommando welches ausgeführt werden soll
     * @return          Thread
     */
    public CmdThread cmdForeground(String homedir, long timeoutms, CmdThread.CmdMode cmdMode, boolean save, String ... cmd) {
        CmdThread cmdThread;
        cmdThread = CmdThread.createThread(homedir, cmdCharset, cmdMode, cmd);
        threads.add(cmdThread);
        long t = System.currentTimeMillis();
        do {
            try { Thread.sleep(10); } catch (InterruptedException e) { }
        } while (System.currentTimeMillis()-t<timeoutms && !cmdThread.isFinished());
        if (!cmdThread.isFinished())
            cmdThread.stop();
        if (!save && cmdThread.isFinished())
            threads.remove(cmdThread);
        return cmdThread;
    }

    public String opencmd(Model model, String backlink, CmdThread cmdThread) {
        cmdThread.backlink(backlink);
        model.addAttribute("cmdThread",cmdThread);
        model.addAttribute("threads",threads);
        return "cmdoutput";
    }

    private CmdThread createThread(String homedir, CmdThread.CmdMode cmdMode, String ... cmd) {
        CmdThread cmdThread;
        cmdThread = CmdThread.createThread(homedir,cmdCharset, cmdMode, cmd);
        addThread(cmdThread);
        return cmdThread;
    }

    /**
     * Fügt einen neuen Thread zur Liste der Threads hinzu, und wartet bis zu 1000ms ob der Thread gleich fertig ist
     * @param cmdThread neuer Thread
     */
    public void addThread(CmdThread cmdThread) {
        addThread(cmdThread,1000);
    }

    /**
     * Fügt einen neuen Thread zur Liste der Threads hinzu, und wartet bis zu waitms ob der Thread gleich fertig ist
     * @param cmdThread neuer Thread
     */
    public void addThread(CmdThread cmdThread, long waitms) {
        threads.add(cmdThread);
        // Warte maximal 1000ms damit
        long t = System.currentTimeMillis();
        do {
            try { Thread.sleep(10); } catch (InterruptedException e) { }
        } while (System.currentTimeMillis()-t<waitms &&
                (cmdThread.getThreadStatus()==ThreadStatus.RUNNING || cmdThread.getThreadStatus()==ThreadStatus.NEW));
    }

    public CmdThread getThread(long id) {
        if (id<1) return null;
        try {
            for (int i = 0; i < threads.size(); i++) {
                CmdThread t = threads.get(i);
                if (t.getId() == id) return t;
            }
        } catch (Exception ex) {}
        return null;
    }

    public CmdThread getThread(String id) {
        if (id==null || id.length()<1) return null;
        try {
            for (int i = 0; i < threads.size(); i++) {
                CmdThread t = threads.get(i);
                if ((t.getId()+"").equals(id)) return t;
            }
        } catch (Exception ex) {}
        return null;
    }

    public void removeThread(CmdThread cmdThread) {
        try {
            cmdThread.stop();
            threads.remove(cmdThread);
        } catch (Exception ex) {}
    }

    public String htmlStatusInfo() {
        int running  = 0;
        int finished = 0;
        for (int i = 0; i < threads.size(); i++) {
            CmdThread t = threads.get(i);
            if (t.isFinished()) finished ++;
            else running++;
        }
        if (running+finished==0) return "";
        String ret = "";
        if (running>0) ret = "<span style=\"color:red;\">"+running+"</span> tasks running - ";
        ret = ret+finished+" tasks finished";
        return ret;
    }

}
