package at.letto.tools.rest;

import at.letto.tools.Cmd;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.PrintWriter;
import java.io.StringWriter;

import static at.letto.tools.rest.ResponseTools.loadToken;

/**
 * Klasse zur Bündelung eines DTO.Objekts und einer Meldung
 * @param <A>       DTO-Objekt aus Service
 */
@NoArgsConstructor @Getter @Setter
public class DtoAndMsg<A> {
    private A data;
    private Msg msg;

    /**
     * Definition des Rückgabe-Elements für ein DTO ohne Fehlermeldung
     * @param object    zu übergebendes DTO, muss JSON-konvertierbar sein
     */
    public DtoAndMsg(A object) {
        this.data = object;
        this.msg = new Msg("", MsgType.OK);
    }

    /**
     * Rückgabe einer Fehlermeldung an das aufrufende Service,
     * ein DTO wird trotz Fehler im Service zurückgegeben
     * @param object    zu übergebendes DTO, muss JSON-konvertierbar sein
     * @param errorMsg  Fehlermelung als String
     */
    public DtoAndMsg(A object, String errorMsg) {
        this.data = object;
        msg = new Msg(errorMsg, Cmd.isEmpty(errorMsg) ? MsgType.OK : MsgType.ERROR);
    }

    public DtoAndMsg(A object, Msg msg) {
        this.data = object;
        this.msg = msg;
    }

    public DtoAndMsg(MsgException msgExc, String lang) {
        this.data = null;
        msg = new Msg(msgExc.getMessage(), lang, msgExc.getDetails());
        msg.setLang(lang);
        this.msg = msg;
    }


    public DtoAndMsg(A object, String errorMsg, Throwable stack) {
        this.data = object;
        // Stack-Trace in String umwandeln
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        stack.printStackTrace(pw);
        sw.toString() ;
        this.msg = new Msg(errorMsg, MsgType.ERROR, sw.toString());
    }

    public DtoAndMsg(Throwable stack) {
        this.data = null;
        stack.printStackTrace();
        // Stack-Trace in String umwandeln
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        stack.printStackTrace(pw);
        sw.toString() ;
        String msg = "";
        if (stack instanceof MsgException) {
            MsgException ex = (MsgException) stack;
            msg = ex.getMessage();
            this.msg = new Msg(msg, MsgType.ERROR, sw.toString(), loadToken().getSprache(), ex.getDetails());
        }
        else
            this.msg = new Msg("", MsgType.ERROR, sw.toString());
    }

    /**
     * Definition des Rückgabe-Elements für ein DTO mit Meldung des Services
     * @param object    zu übergebendes DTO, muss JSON-konvertierbar sein
     * @param msg       Rückmeldung des Service  als String
     * @param typ       Type der Meldung: ERROR, WARNING, INFO, OK
     */
    public DtoAndMsg(A object, String msg, MsgType typ) {
        this.data = object;
        this.msg = new Msg(msg, typ);
    }

    /**
     * Rückgabe einer Fehlermeldung OHNE Dto
     * @param errMsg    zu übertragende Fehlermeldung
     * @param lang      Sprache, in der die Meldung erfolgen soll
     */
    public DtoAndMsg(String errMsg, String lang) {
        this.data = null;
        msg = new Msg(errMsg, Cmd.isEmpty(errMsg) ? MsgType.OK : MsgType.ERROR);
        msg.setLang(lang);
    }

    /**
     * Liefert nach einem Servicezugriff den Status der Aktion
     * @return  true: Aktion war OK
     */
    public boolean checkOk() {
        if (msg==null) return true;
        if (msg.getMsgType().equals(MsgType.OK)) return true;
        return false;
    }

    /**
     * @return Fehlermeldung oder Leerstring
     */
    public String checkMsg() {
        return msg!=null ? msg.getMeldung() : "";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            DtoAndMsg<?> pair = (DtoAndMsg<?>)o;
            if (this.data != null) {
                if (!this.data.equals(pair.data)) {
                    return false;
                }
            } else if (pair.data != null) {
                return false;
            }

            if (this.msg != null) {
                return this.msg.equals(pair.msg);
            } else return pair.msg == null;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.data != null ? this.data.hashCode() : 0;
        return 31 * result + (this.msg != null ? this.msg.hashCode() : 0);
    }


    /**
     * Fehlerhandling von DtoAndMsg-Objekten,
     * FEHLERWEITERLEITUNG an übergeordnetes Service,
     * der komplette ursprüngliche StackTrace wird weitergeleitet
     * @param res   Dto, das auf Fehler untersucht wird
     * @return true, wenn OK, sonst false
     */
    public static boolean check(DtoAndMsg res) {
        if (res==null)
            throw new MsgException("err.restConnection", "");
        if (!res.checkOk()) {
            System.out.println(res.getMsg().getStackTrace());
            throw new RestException(res.getMsg());
        }
        return true;
    }

    /**
     * Fehlerhandling von DtoAndMsg-Objekten, das Ergebnis wird uas DTO bezogen
     * FEHLERWEITERLEITUNG an übergeordnetes Service,
     * der komplette ursprüngliche StackTrace wird weitergeleitet
     * @param res   Dto, das auf Fehler untersucht wird
     * @return Ergebnis des REST-Anfrage
     */
    public static <T> T get(DtoAndMsg<T> res) {
        if (res==null)
            throw new MsgException("err.restConnection", "");
        if (!res.checkOk()) {
            System.out.println(res.getMsg().getStackTrace());
            throw new RestException(res.getMsg());
        }

//        if (res.getData()==null)
//            throw new MsgException("err.restErgNull");
        return res.getData();
    }
}
