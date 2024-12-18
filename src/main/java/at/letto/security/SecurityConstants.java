package at.letto.security;

import at.letto.tools.ENCRYPT;

public final class SecurityConstants {

    /* Signing key for HS512 algorithm
       You can use the page http://www.allkeysgenerator.com/ to generate all kinds of keys
       Dieser Key identifiziert alle JWT-Tokens, dass sie von einem LeTTo-Server kommen
       Alle Services welche zueinander Vertrauensstellung besitzen haben auch das selbe Secret
       Um einen JWT-Token eines anderen Servers akzeptieren zu können muss dessen SECRET bekannt sein!
       */
    public static final String JWT_SECRET = "bzVPRm54bHRxaVhFUUtsaTFnNjVOZGhVYjRKY1MyNHFFUVNzaFhIYlV1eTBNUld0U1MwSFBPemd5Wm1uY1NBdEhtR2tRRDFOR1JqVmVMVHQ=";

    /* Dieser Key wird verwendet um innerhalb eines Servers dem anderen Dienst mitzuteilen, dass die Anfrage vom gleichen Server kommt<br>
     * Ist er in der application.properties nicht gesetzt, dann wird dieser Standard-Schlüssel verwendet */
    public static final String SERVER_SECRET = "cUlRUWpQVFBHQ1FraGhSMExPaWh0ZlREdEEyZlUxS2dVUWRySWVZbVZ5c0tLZmQxRjNaVjZraWFqMlRuRkZYcGVOZzF4VTJyVVpLU3gxaHc=";

    /* Gast Passwort encoded */
    public static final String gastPasswordEncrypted  = "$2a$10$waRjgKHk.YYxD0DWSVCnrOEs/ve/P4wRMbqsD6nmWp.DMa3ugVB.6";

    /* User Passwort encoded */
    public static final String userPasswordEncrypted  = "$2a$10$SPTSQrsWlch2MGo6qA0B6O4yxpqdrkfXRmNRc5gJttIujBj7VMGNi";

    /* Admin Passwort encoded */
    public static final String adminPasswordEncrypted = "$2a$10$F0XbSSonJkSYdRVVLUiOseY5.gzviAhAty4iCmJ4bflT.ZD7oQn6K";

    /* LeTTo Passwort encoded */
    public static final String lettoPasswordEncrypted = "$2a$10$YLRExD6x5b.RyM7EF1Z4v.Et0Vg22HT3wqdtvc4KzXkVuKdgQmHlK";

    /* Bezeichnung des Token-Heades der LeTTo-Tokens */
    public static final String TOKEN_HEADER    = "authorization";

    /* Prefix für die Token-Bezeichnung */
    public static final String TOKEN_PREFIX    = "Bearer ";

    /* Token Type */
    public static final String TOKEN_TYPE      = "JWT";

    /* Ersteller des Tokens */
    public static final String TOKEN_ISSUER    = "LeTTo-Login";

    /* Anwendung für die der Token gilt */
    public static final String TOKEN_AUDIENCE  = "LeTTo";

    /* Standard Dauer in ms für die Gültigkeit eines Tokens */
    public static final long   EXPIRATION_TIME = 1000 * 60 * 60;

    /* Wenn übrige Gültigkeitsdauer eines Tokens unter die Refresh-Time kommt sollte der Token aktualisiert werden */
    public static final long   REFRESH_TIME    = 1000 * 60 * 2;

    private SecurityConstants() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }

}