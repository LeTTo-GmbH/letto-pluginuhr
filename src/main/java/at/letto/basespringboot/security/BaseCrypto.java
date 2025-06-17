package at.letto.basespringboot.security;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;

public class BaseCrypto {

    private static final String ENCRYPTION_ALGO = "AES/CBC/PKCS5Padding";
    private static final String KEY_DERIVATION_FUNCTION = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 65536;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;

    /**
     * Encrypts a plain text string using AES encryption with a password.
     * The method generates a random salt and IV, derives a key from the password,
     * and returns the encrypted data as a Base64-encoded string.
     *
     * @param   plainText the text to encrypt
     * @param   password  the password used for encryption
     * @return  Base64-encoded encrypted string
     * @throws  Exception if encryption fails
     */
    public static String encrypt(String plainText, String password) throws Exception {
        byte[] salt = generateRandomBytes(SALT_LENGTH);
        byte[] iv = generateRandomBytes(IV_LENGTH);

        SecretKey secretKey = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine salt + IV + ciphertext
        byte[] result = new byte[salt.length + iv.length + encryptedBytes.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(iv, 0, result, salt.length, iv.length);
        System.arraycopy(encryptedBytes, 0, result, salt.length + iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * Decrypts a Base64-encoded encrypted string using AES decryption with a password.
     * The method extracts the salt and IV from the encrypted data, derives the key,
     * and returns the decrypted plain text.
     *
     * @param   encryptedBase64 the Base64-encoded encrypted string
     * @param   password        the password used for decryption
     * @return  decrypted plain text string
     * @throws  Exception if decryption fails
     */
    public static String decrypt(String encryptedBase64, String password) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        byte[] encryptedBytes = new byte[decoded.length - SALT_LENGTH - IV_LENGTH];

        System.arraycopy(decoded, 0, salt, 0, SALT_LENGTH);
        System.arraycopy(decoded, SALT_LENGTH, iv, 0, IV_LENGTH);
        System.arraycopy(decoded, SALT_LENGTH + IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        SecretKey secretKey = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Derives a secret key from the given password and salt using PBKDF2.
     *
     * @param   password the password to derive the key from
     * @param   salt     the salt used in key derivation
     * @return  the derived SecretKey
     * @throws  Exception if key derivation fails
     */
    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_FUNCTION);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Generates a random byte array of the specified length.
     *
     * @param   length the length of the byte array to generate
     * @return  a byte array filled with random bytes
     */
    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

}
