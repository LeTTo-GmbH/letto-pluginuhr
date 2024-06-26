package at.letto.tools;

import java.security.SecureRandom;

/**
 * statische Routine für das Erzeugen einer zufälligen Zeichenfolge für Passwort,Key,etc.
 */
public class RandomString {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();

    public static void main(String[] args) {

        System.out.println("String : " + DATA_FOR_RANDOM_STRING);
        System.out.println("result : " + generateRandomString(20));

    }

    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            // System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);
        }

        return sb.toString();

    }

}
