package at.letto.tools.rest;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Exception wird bei fehlerhaftem Token geworfen!
 */
public class TokenException extends RuntimeException {
    public TokenException(String msg) {
        super(msg);
    }
}
