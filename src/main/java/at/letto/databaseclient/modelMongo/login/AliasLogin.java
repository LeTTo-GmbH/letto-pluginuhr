package at.letto.databaseclient.modelMongo.login;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AliasLogin {

    /** Zeitpunkt des Alias-Logins */
    protected long    loginTime=0;

    /** Benutzer der gleichen Schule welcher den Alias-Login getätigt hat */
    protected String  username="";

    /** Zeit wann der Token abläuft */
    protected long    expiration=0;

}
