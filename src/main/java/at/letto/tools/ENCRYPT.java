package at.letto.tools;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.IntStream;


public class ENCRYPT {

	/**
	 * erzeugt eine MD5-Prüfsumme
	 *
	 * @param s String
	 * @return MD5 Prüfsumme
	 */
	public static String md5falsch(String s) {
		String hash = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(s.getBytes());
			for (int i = 0; i < digest.length; i++) {
				hash += (Integer.toHexString(digest[i] & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * erzeugt eine MD5-Prüfsumme
	 *
	 * @param s String
	 * @return MD5 Prüfsumme
	 */
	public static String md5(String s) {
		String hash = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(s.getBytes());
			for (int i = 0; i < digest.length; i++) {
				String p = (Integer.toHexString(digest[i] & 0xff));
				if (p.length()<2) p = "0"+p;
				hash += p;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * erzeugt eine MD5-Prüfsumme
	 *
	 * @param file Datei als Byte-Array
	 * @return MD5 Prüfsumme
	 */
	public static String md5falsch(byte[] file) {
		String hash = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(file);
			for (int i = 0; i < digest.length; i++) {
				hash += (Integer.toHexString(digest[i] & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * erzeugt eine MD5-Prüfsumme
	 *
	 * @param file Datei als Byte-Array
	 * @return MD5 Prüfsumme
	 */
	public static String md5(byte[] file) {
		String hash = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(file);
			for (int i = 0; i < digest.length; i++) {
				String p = (Integer.toHexString(digest[i] & 0xff));
				if (p.length()<2) p = "0"+p;
				hash += p;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * erzeugt eine MD5-Prüfsumme einer Datei
	 *
	 * @param file  Datei
	 * @return MD5 Prüfsumme
	 */
	public static String md5(File file) {
		try (InputStream is = Files.newInputStream(file.toPath())) {
			String checksum = DigestUtils.md5Hex(is);
			is.close();
			return checksum;
		} catch (Exception ex) {}
		return "";
	}

	public static String sha512(File file) {
		String hash = "";
		try {
			byte[] digest = MessageDigest
					.getInstance("SHA-512")
					.digest(Files.readAllBytes(file.toPath()));
			for (int i = 0; i < digest.length; i++) {
				String p = (Integer.toHexString(digest[i] & 0xff));
				if (p.length()<2) p = "0"+p;
				hash += p;
			}
		} catch (Exception ex) {}
		return hash;
	}

	public static String sha512(String s) {
		String hash = "";
		try {
			byte[] digest = MessageDigest
					.getInstance("SHA-512")
					.digest(s.getBytes());
			for (int i = 0; i < digest.length; i++) {
				String p = (Integer.toHexString(digest[i] & 0xff));
				if (p.length()<2) p = "0"+p;
				hash += p;
			}
		} catch (Exception ex) {}
		return hash;
	}

	private static final byte[] SALT = {
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	};

	/* public static String enc(String text) {
		String ret = "";
		try {
			ret = encrypt(text, PASSWORD);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return ret;
	} */

	/* einfache symmetrische Verschlüsselung von text mit key */
	public static String enc(String text, String key) {
		String ret = "";
		try {
			ret = encrypt(text, key.toCharArray());
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/*public static String dec(String text) {
		String ret = "";
		try {
			ret = decrypt(text);
		} catch (GeneralSecurityException | IOException e) {
			System.out.println(e);
		}
		return ret;
	} */

	public static String dec(String text, String key) {
		String ret = "";
		try {
			ret = decrypt(text, key.toCharArray());
		} catch (GeneralSecurityException | IOException e) {
			System.out.println(e);
		}
		return ret;
	}

	public static String encrypt(String property, String schluessel) throws GeneralSecurityException, UnsupportedEncodingException {
		return encrypt(property,schluessel.toCharArray());
	}

	/*public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
		return encrypt(property, PASSWORD);
	}*/

	public static String encrypt(String property, char[] schluessel) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(schluessel));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		byte b[] = property.getBytes("UTF-8");
		byte c[] = pbeCipher.doFinal(b);
		return base64Encode(c);
	}


	/*public static String decrypt(String property) throws GeneralSecurityException, IOException {
		return decrypt(property, PASSWORD);
	}*/

	public static String decrypt(String property, String schluessel) throws GeneralSecurityException, IOException {
		return decrypt(property,schluessel.toCharArray());
	}

	/**
	 * Entschlüsseln eines Strings über einen bekannten Key
	 *
	 * @param property   Text, der zu entschlüsseln ist
	 * @param schluessel Key, mit dem der Text entschlüsselt werden kann
	 * @throws GeneralSecurityException  Fehlermeldung
	 * @throws IOException Fehlermeldung
	 * @return entschlüsselter Text
	 */
	public static String decrypt(String property, char[] schluessel) throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(schluessel));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		byte b64[] = base64Decode(property);
		byte c[] = pbeCipher.doFinal(b64);
		return new String(c, "UTF-8");
	}

	/**
	 * Erzeugt aus einem binären Dateiinhalt eines Byte-Arrays einen Base64-codierten String
	 * @param bytes Dateiinhalt
	 * @return      Base64-codierter String
	 */
	public static String base64Encode(byte[] bytes) {
		String encodedString = Base64.getMimeEncoder().encodeToString(bytes);
		encodedString = encodedString.replaceAll("\n","").replaceAll("\r","");
		return encodedString;
	}

	/**
	 * Erzeugt aus einem Base64-codierten String den ursprünglichen binären Dateiinhalt
	 * @param base64encodedString Base64-codierter String
	 * @return                    Dateiinhalt
	 * @throws IOException        Fehler wenn etwas nicht funktioniert hat
	 */
	public static byte[] base64Decode(String base64encodedString) throws IOException {
		byte[] base64decodedBytes = Base64.getMimeDecoder().decode(base64encodedString);
		return base64decodedBytes;
	}

	/**
	 * Erzeugt aus einem beliebigen String einen Base64-codierten String
	 * @param s     String
	 * @return      Base64-codierter String
	 */
	public static String base64Encode(String s) {
		String encodedString = Base64.getMimeEncoder().encodeToString(s.getBytes());
		encodedString = encodedString.replaceAll("\n","").replaceAll("\r","");
		return encodedString;
	}

	/**
	 * Erzeugt aus einem Base64-codierten String den ursprünglichen String
	 * @param base64encodedString Base64-codierter String
	 * @return                    String
	 * @throws IOException        Fehler wenn etwas nicht funktioniert hat
	 */
	public static String base64DecodeString(String base64encodedString) throws IOException {
		return new String(Base64.getMimeDecoder().decode(base64encodedString));
	}

	/**
	 * Erzeugt eine RSA Schlüsselpaar mit einer Schlüssellänge von 1024 bit
	 * @return Schlüsselpaar
	 * @throws NoSuchAlgorithmException Fehler wenn RSA nicht funktioniert
	 */
	public static KeyPair generateRSAkeypair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair pair = keyGen.generateKeyPair();
		return pair;
	}

	public static String privateKeyBase64FromRSAkeypair(KeyPair keyPair) {
		if (keyPair==null) return null;
		return base64Encode(keyPair.getPrivate().getEncoded());
	}

	public static PrivateKey privateKeyFromBase64(String privateKeyBase64) {
		try {
			byte[] keyBytes = base64Decode(privateKeyBase64);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(spec);
		} catch (Exception e) { }
		return null;
	}

	public static String publicKeyBase64FromRSAkeypair(KeyPair keyPair) {
		if (keyPair==null) return null;
		return base64Encode(keyPair.getPublic().getEncoded());
	}

	public static PublicKey publicKeyFromBase64(String publicKeyBase64) {
		try {
			byte[] keyBytes = base64Decode(publicKeyBase64);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(spec);
		} catch (Exception e) { }
		return null;
	}

	public static String encryptTextRSAprivate(String text, String privateKeyBase64){
		if (text==null || privateKeyBase64==null) return null;
		PrivateKey privateKey = privateKeyFromBase64(privateKeyBase64);
		if (privateKey==null) return null;
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			String encrypted = base64Encode(cipher.doFinal(text.getBytes("UTF-8")));
			return encrypted;
		} catch (Exception e) {	}
		return null;
	}

	public static String decryptTextRSApublic(String encryptedText, String publicKeyBase64){
		if (encryptedText==null || publicKeyBase64==null) return null;
		PublicKey publicKey = publicKeyFromBase64(publicKeyBase64);
		if (publicKey==null) return null;
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			String text = new String(cipher.doFinal(base64Decode(encryptedText)),"UTF-8");
			return text;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encryptTextRSApublic(String text, String publicKeyBase64){
		if (text==null || publicKeyBase64==null) return null;
		PublicKey publicKey = publicKeyFromBase64(publicKeyBase64);
		if (publicKey==null) return null;
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			String encrypted = base64Encode(cipher.doFinal(text.getBytes("UTF-8")));
			return encrypted;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptTextRSAprivate(String encryptedText, String privateKeyBase64){
		if (encryptedText==null || privateKeyBase64==null) return null;
		PrivateKey privateKey = privateKeyFromBase64(privateKeyBase64);
		if (privateKey==null) return null;
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			String text = new String(cipher.doFinal(base64Decode(encryptedText)),"UTF-8");
			return text;
		} catch (Exception e) {	}
		return null;
	}

	/**
	 * Erzeugung eines zufälligen Strings aus lauter Großbuchstaben
	 * @param length	Anzahl an Zeichen
	 * @return	Key aus length zufälligen Großbuchstaben
	 */
	public static String generateKey(int length) {
		Random random = new SecureRandom();
		IntStream specialChars = random.ints(length, 0x41, 0x5B);
		return specialChars.mapToObj(data -> (char) data).collect(Collector.of(
				StringBuilder::new,
				StringBuilder::append,
				StringBuilder::append,
				StringBuilder::toString));
	}

	/**
	 * Erzeugung eines zufälligen Strings aus lauter Klein-, Großbuchstaben und Zahlen
	 * @param length	Anzahl an Zeichen
	 * @return	Key aus length zufälligen Großbuchstaben
	 */
	public static String generateKeyAz09(int length) {
		Random random = new SecureRandom();
		String set = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		char[] chars = set.toCharArray();
		IntStream specialChars = random.ints(length, 0,chars.length-1);
		return specialChars.mapToObj(data -> chars[data]).collect(Collector.of(
				StringBuilder::new,
				StringBuilder::append,
				StringBuilder::append,
				StringBuilder::toString));
	}

	public static String base64toBase64URL(String base64string) {
		return base64string.replaceAll("\\+","\\-").replaceAll("\\/","_");
	}

	public static String base64URLtoBase64(String base64URLstring) {
		return base64URLstring.replaceAll("\\-","\\+").replaceAll("_","/");
	}

}