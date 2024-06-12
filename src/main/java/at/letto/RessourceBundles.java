package at.letto;

import java.util.Locale;
import java.util.ResourceBundle;

public class RessourceBundles {
    /**
     * Laden eines Ressourcen-Files für eine Lokalisation
     * @param baseName  Name des Ressource-Bundles
     * @param locale    Kurzbezeichnung des Sprachcodesn(de,en,...)
     * @param region    Sprach-Region: zB.: AT
     * @return  Ressource-Bundle zur Sprachdefinition
     */
    public static ResourceBundle loadBundle(String baseName, String locale, String region) {
        ResourceBundle bundle;
        Locale local = new Locale.Builder().setLanguage(locale).setRegion(region).build();
        try {
            bundle = ResourceBundle.getBundle( baseName, local );
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle( "resources/"+ baseName, local );
        }
        return bundle;

    }


}
