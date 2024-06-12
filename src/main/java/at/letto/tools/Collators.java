package at.letto.tools;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

public class Collators {
    /**
     * Laden eines Collators zum Vergleich von Texten in einer definierten Sprache
     * @param lang  Kurzbezeichnung der Locale (de, en, ...)
     * @return  RuleBasedCollatorm der auch Spaces berücksichtigt
     */
    public static Collator loadCollator(String lang) {
        RuleBasedCollator collatorDe = null;
        try {
            Locale locale = new Locale.Builder().setLanguage(lang.toLowerCase()).setRegion(lang.toUpperCase()).build();
            RuleBasedCollator defaultCollator = (RuleBasedCollator) Collator.getInstance(locale);
            final String rules = defaultCollator.getRules();
            RuleBasedCollator collator1 = new RuleBasedCollator(rules + "& ' ' < x,z");
            return new RuleBasedCollator(rules.replaceAll(
                    "<'\u005f'", "<' '<'\u005f'"));
        } catch (ParseException e) {
            return null;
        }

    }
}
