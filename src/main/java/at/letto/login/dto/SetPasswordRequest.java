package at.letto.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPasswordRequest {

    /** Benutzername */
    private String  username="";
    private String  school="";
    private String  oldPassword="";
    private String  newPassword="";
    private boolean tempPassword=false;

}
