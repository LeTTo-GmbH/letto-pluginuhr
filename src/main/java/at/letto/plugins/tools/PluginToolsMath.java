package at.letto.plugins.tools;

import at.letto.math.dto.ToleranzDto;
import at.letto.math.enums.TOLERANZMODE;

/**
 * Tools für Plugins ohne Verwendung des Parser-Moduls letto-math
 */
public class PluginToolsMath {

    /**
     * Prüft ob der zweite Wert gleich ist wie der erste Wert<br>
     * Zahlenwerte mit Berücksichtigung einer Toleranz <br>
     * @param w1         erster Wert
     * @param w2         zweiter Werte
     * @param tol        Toleranz
     * @return           true wenn die zwei Werte gleich sind
     */
    public static boolean equals(double w1, double w2, ToleranzDto tol) {
        if (!Double.isFinite(w1) || !Double.isFinite(w2))
            throw new RuntimeException("Werte unendlich oder undefiniert");
        if (w1==w2) return true;
        if (tol.getMode() == TOLERANZMODE.ABSOLUT) {
            if (Math.abs(w2-w1)<=Math.abs(tol.getToleranz())) return true;
        } else {
            if (w1==0d) {
                if (Math.abs(w2)<1e-20) return true;
                else return false;
            }
            double t = w2/w1;
            if (t>=0d && t<=1d) t=1d/t;
            t = t-1d;
            if (t>=0 && t<=tol.getToleranz()) return true;
        }
        return false;
    }

    /**
     * parst einen String in ein Feld von Double-Werten
     * @param s String
     * @return  Feld von double Werten
     */
    public static double[] parsedoubleArray(String s) {
        s = s.trim();
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length()-1);
            String sa[] = s.split(",");
            double ret[] = new double[sa.length];
            for (int i=0;i<sa.length;i++)
                ret[i] = Double.parseDouble(sa[i].trim());
            return ret;
        }
        throw new RuntimeException("String kann nicht in ein Feld von double-Werten geparste werden.");
    }

    /**
     * parst einen String in ein Feld von int-Werten
     * @param s String
     * @return  Feld von int Werten
     */
    public static int[] parseintArray(String s) {
        s = s.trim();
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length()-1);
            String sa[] = s.split(",");
            int ret[] = new int[sa.length];
            for (int i=0;i<sa.length;i++)
                ret[i] = Integer.parseInt(sa[i].trim());
            return ret;
        }
        throw new RuntimeException("String kann nicht in ein Feld von int-Werten geparste werden.");
    }

}
