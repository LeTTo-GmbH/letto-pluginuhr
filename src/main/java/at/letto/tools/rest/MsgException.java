package at.letto.tools.rest;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Hilfsklasse zur Meldungsverarbeitung in Services:
 * Diese Exception wird im Controller gefangen und als Meldung an ObjAndMsg weitergereicht
 */
public class MsgException extends RuntimeException {

    @Getter
    private List<String> details;

    public MsgException(String msg) {
        super(msg);
        this.details = new Vector<>();
    }

    public MsgException(String msg, List<String> details) {
        super(msg);
        this.details = details;
    }

    public MsgException(String msg, String ... details) {
        super(msg);
        this.details = Arrays.asList(details);
    }

}
