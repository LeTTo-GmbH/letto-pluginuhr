package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Alter Login-Request, der nur Username, Password und Schule enthält und noch keinen Fingerprint oder IP */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestOld {

    private String  username;
    private String  password;
    private String  school;

}
