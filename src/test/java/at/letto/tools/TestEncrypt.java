package at.letto.tools;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestEncrypt {

    @Test
    public void testBase64() throws IOException {
        String ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        String encoded = ENCRYPT.base64Encode(ts1.getBytes());
        String ts2 =new String(ENCRYPT.base64Decode(encoded));
        assertEquals(ts1,ts2);
    }

    @Test
    public void testBase64String() throws IOException {
        String ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        String encoded = ENCRYPT.base64Encode(ts1 );
        String ts2 = ENCRYPT.base64DecodeString(encoded);
        assertEquals(ts1,ts2);
    }

    /*@Test
    public void testEncrypt() throws GeneralSecurityException, IOException {
        String ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        String password = "Das ist mein Passwort";
        String encoded = ENCRYPT.encrypt(ts1, LettoLicenseSecurityConstants.LICENSE_PASSWORD);
        String ts2 = ENCRYPT.decrypt(encoded);
        assertEquals(ts1,ts2);
        encoded = ENCRYPT.encrypt(ts1,password);
        ts2 = ENCRYPT.decrypt(encoded,password);
        assertEquals(ts1,ts2);
    }*/

    @Test
    public void testRSA() throws NoSuchAlgorithmException {
        KeyPair keyPair = ENCRYPT.generateRSAkeypair();
        String  privkey = ENCRYPT.privateKeyBase64FromRSAkeypair(keyPair);
        String  pubkey  = ENCRYPT.publicKeyBase64FromRSAkeypair(keyPair);
        PrivateKey privatekey = ENCRYPT.privateKeyFromBase64(privkey);
        PublicKey  publickey  = ENCRYPT.publicKeyFromBase64(pubkey);
        // Verschlüsselung mit dem private Key
        String ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        String encrypted = ENCRYPT.encryptTextRSAprivate(ts1,privkey);
        String ts2 = ENCRYPT.decryptTextRSApublic(encrypted,pubkey);
        assertEquals(ts1,ts2);
        // Verschlüsselung mit dem public Key
        ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        encrypted = ENCRYPT.encryptTextRSApublic(ts1,pubkey);
        ts2 = ENCRYPT.decryptTextRSAprivate(encrypted,privkey);
        assertEquals(ts1,ts2);
    }

    @Test
    public void testEncDec() {
        String ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        String pw = "dbfsoinbrtserlkbndfboisertnbdf";
        String encrypted = ENCRYPT.enc(ts1,pw);
        String ts2 = ENCRYPT.dec(encrypted,pw);
        assertEquals(ts1,ts2);
    }

    @Test
    public void base64URL(){
        String ts1 = "a+b/c=d++//==";
        String url = ENCRYPT.base64toBase64URL(ts1);
        String ts2 = ENCRYPT.base64URLtoBase64(url);
        assertEquals(ts1,ts2);
    }

    @Test
    public void testSha512() throws IOException {
        String sha = ENCRYPT.sha512("abc");
        String sha2= ENCRYPT.sha512("def");
        assertTrue(sha.length()>10);
        assertTrue(sha2.length()>10);
        assertNotEquals(sha,sha2);
    }

}
