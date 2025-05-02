package at.letto.security;

import at.letto.security.LettoToken;
import at.letto.security.SecurityConstants;

import java.util.ArrayList;
import java.util.List;

public class TokenTest {
    public static LettoToken getToken(String username,
                                      String vorname,
                                      String nachname,
                                      String activDirectoryname,
                                      String email,
                                      String lang,
                                      int id,
                                      int idSchule,
                                      String  school,
                                      String  lettoUri,
                                      String  serverRestkey,
                                      String fingerprint,
                                      String ... roles) {
        List<String> roleList = new ArrayList<String>();
        for (String role:roles) roleList.add(role);

        LettoToken token = new LettoToken(
                SecurityConstants.JWT_SECRET,
                SecurityConstants.EXPIRATION_TIME,
                username,
                vorname,
                nachname,
                activDirectoryname,
                email,
                lang,
                id,
                idSchule,
                school,
                lettoUri,
                serverRestkey,
                fingerprint,
                roleList
        );
        return token;
    }

    public static LettoToken getToken(String jwtSecret,
                                      String username,
                                      String vorname,
                                      String nachname,
                                      String activDirectoryname,
                                      String email,
                                      String lang,
                                      int id,
                                      int idSchule,
                                      String  school,
                                      String  lettoUri,
                                      String  serverRestkey,
                                      String fingerprint,
                                      String ... roles) {
        List<String> roleList = new ArrayList<String>();
        for (String role:roles) roleList.add(role);

        LettoToken token = new LettoToken(
                jwtSecret,
                SecurityConstants.EXPIRATION_TIME,
                username,
                vorname,
                nachname,
                activDirectoryname,
                email,
                lang,
                id,
                idSchule,
                school,
                lettoUri,
                serverRestkey,
                fingerprint,
                roleList
        );
        return token;
    }


    public static String db = "test";
    public static String school = "htlstp";
    public static String uri = "http://localhost/lettohtlstp";

    public static LettoToken getLehrerToken(String jwtSecret) {
        int idUser = db.equals("test") ? 106 : 7;
        return getToken(jwtSecret, "l-mayt", "Thomas", "Mayer","l-mayt", "a@b.at","de",
                idUser, 81,school,uri,"12345","", "teacher");
    }

    public static LettoToken getLehrerToken() {
        int idUser = db.equals("test") ? 106 : 7;
        return getToken("l-mayt", "Thomas", "Mayer","l-mayt", "a@b.at","de",
                idUser, 81,school,uri,"12345","", "teacher");
    }
    public static LettoToken getLehrerTokenAdmin() {
        int idUser = db.equals("test") ? 106 : 7;
        return getToken("l-mayt", "Thomas", "Mayer","l-mayt", "a@b.at","de",
                idUser, 81,school,uri,"12345", "","teacher","admin");
    }

    public static LettoToken getLehrerToken2() {
        return getToken("l-damb", "Werner", "Damböck","l-damb", "a@b.at","de",
                134, 81,school,uri,"12345", "","teacher");
    }

    public static LettoToken getStudentToken() {
        return getToken("leojonah.amann", "Leo Jonah", "Amann","", "Leo.Amann@htlstp.at","de",
                691, 81,school,uri,"12345","", "teacher");
    }
}
