package at.letto.tools.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Msg {
    private String meldung;
    private MsgType msgType;
    private String stackTrace;
    private String lang;
    private List<String> details;

    public Msg(String meldung, MsgType msgType, String stackTrace) {
        this.meldung = meldung;
        this.msgType = msgType;
        this.stackTrace = stackTrace;
    }
    /**
     * Check, ob Aktion erfolgreich war
     * @return  true, wenn Aktion OK
     */
    public boolean ckeckOk() {
        if (msgType==null) return true;
        if (msgType.equals(MsgType.OK)) return true;
        return false;
    }

    /** Konstruktor der Fehlermeldung */
    public Msg(String err, String lang, List<String> details) {
        meldung = err;
        msgType = MsgType.ERROR;
        this.details = details;
        this.lang=lang;
    }

    /** Konstruktor der Fehlermeldung */
    public Msg(String err, String lang) {
        meldung = err;
        msgType = err==null || err.isEmpty() ? MsgType.OK : MsgType.ERROR;
        this.lang=lang;
    }


    /** Konstruktor der Fehlermeldung */
    public Msg(String err, MsgType type) {
        meldung = err;
        msgType = type;
    }

    /** Konstruktor der Fehlermeldung */
    public Msg(MsgType type) {
        meldung = "";
        msgType = type;
    }

    public Msg(Throwable stack) {
        // Stack-Trace in String umwandeln
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        stack.printStackTrace(pw);
        sw.toString() ;
        this.msgType = MsgType.ERROR;
        this.stackTrace = sw.toString();
        this.meldung = "";
    }

}
