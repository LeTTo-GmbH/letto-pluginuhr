package at.letto.tools.rest;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Hilfsklasse zur Meldungsverarbeitung in Services:
 * Diese Exception wird im Controller gefangen und als Meldung an ObjAndMsg weitergereicht
 */
public class AuthException extends RuntimeException {
    public AuthException(String msg) {
        super(msg);
    }
}
