package at.letto.basespringboot.exceptions;


import lombok.Getter;

/**
 * Benutzer nicht gefunden
 */
@Getter
public class UserNotFoundException extends RuntimeException {

    private String username;

    private String info;

    public UserNotFoundException(String username, String info) {
        super(username+" : "+info);
    }

}
