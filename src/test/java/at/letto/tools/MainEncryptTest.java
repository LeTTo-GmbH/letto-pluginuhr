package at.letto.tools;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MainEncryptTest {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPair keyPair = ENCRYPT.generateRSAkeypair();
        String  privkey = ENCRYPT.privateKeyBase64FromRSAkeypair(keyPair);
        String  pubkey  = ENCRYPT.publicKeyBase64FromRSAkeypair(keyPair);
        System.out.println("private Key:"+privkey);
        System.out.println("public Key:"+pubkey);
        PrivateKey privatekey = ENCRYPT.privateKeyFromBase64(privkey);
        PublicKey  publickey  = ENCRYPT.publicKeyFromBase64(pubkey);
        System.out.println(privkey+"\n"+pubkey);
        // Verschlüsselung mit dem private Key
        String ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        String encrypted = ENCRYPT.encryptTextRSAprivate(ts1,privkey);
        String ts2 = ENCRYPT.decryptTextRSApublic(encrypted,pubkey);
        System.out.println("Encrypted Text:"+encrypted);
        System.out.println(ts1+"\n"+ts2);

        // Verschlüsselung mit dem public Key
        ts1 = "Dies ist ein TestString \\!\"§$%&/(;)=?";
        encrypted = ENCRYPT.encryptTextRSApublic(ts1,pubkey);
        ts2 = ENCRYPT.decryptTextRSAprivate(encrypted,privkey);
        System.out.println("Encrypted Text:"+encrypted);
        System.out.println(ts1+"\n"+ts2);

    }
}
