package at.letto.security;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestJWT {

    private static final String secret     = "Mkl6N2FaaDdNNTI0a3F0bXczQU5vbEEzVVpYZEZNaFRtUmxRMmdJTQ==";
    private static final String secretfail = "XXX-Mkl6N2FaaDdNNTI0a3F0bXczQU5vbEEzVVpYZEZNaFRtUmxRMmdJTQ==";

    @Test
    public void jwttest() {
        LettoToken token = new LettoToken(secret,60000L,"username",
                "franz","user","adlogin","franz.user@test.at","de",
                32,34,"htlstp","http://localhost/lettohtlstp","12345",Arrays.asList("a","b"));
        String tokenstring = token.getToken();
        LettoToken token1 = new LettoToken(tokenstring, secret);
        LettoToken token2 = new LettoToken(tokenstring, secretfail);
        assertTrue(token.isValid());
        assertTrue(token1.isValid());
        assertTrue(token1.isTokenNotExpired());
        assertEquals("username",token1.getUsername());
        assertFalse(token2.isValid());
        List<String> roles = token1.getRoles();
        assertEquals("a",roles.get(0));
        assertEquals("b",roles.get(1));
        assertEquals((int)token1.getIdSchule(),34);
    }

}
