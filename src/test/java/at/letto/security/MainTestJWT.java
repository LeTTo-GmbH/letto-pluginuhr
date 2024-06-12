package at.letto.security;

import java.util.Arrays;
import java.util.HashMap;

public class MainTestJWT {

    private static final String secret     = "Mkl6N2FaaDdNNTI0a3F0bXczQU5vbEEzVVpYZEZNaFRtUmxRMmdJTQ==";

    public static void main(String[] args) {
        HashMap<String,String> payload = new HashMap<String, String>();
        payload.put("abc","BlaBla");
        payload.put("def","BlaBwefwefwefwla");
        LettoToken token = new LettoToken(secret,SecurityConstants.TOKEN_ISSUER, SecurityConstants.TOKEN_AUDIENCE,60000L,
                "username","franz","user","adlogin","franz.user@test.at","de",
                32,34,"htlstp","http://localhost/lettohtlstp","12345", Arrays.asList("a","b"),
                payload);
        String tokenstring = token.getToken();
        LettoToken token1 = new LettoToken(secret,tokenstring);
        System.out.println("payload:"+payload);
    }

}
