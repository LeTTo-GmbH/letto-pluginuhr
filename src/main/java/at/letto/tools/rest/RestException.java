package at.letto.tools.rest;

import lombok.Getter;

/**
 * Hilfsklasse zur Meldungsverarbeitung in Services, Weiterleitung von Errors an die übergeordnete Schicht
 * Diese Exception wird im Controller gefangen und als Meldung an ObjAndMsg weitergereicht
 */
@Getter
public class RestException extends RuntimeException {
    private Msg errMsg;

    public RestException(Msg msg) {
        super(msg.getMeldung());
        errMsg = msg;
    }
}
