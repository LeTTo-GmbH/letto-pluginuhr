package at.letto.math.enums;

/**
 * Gibt an, ob eine Potenz als Potenz oder als Wurzel dargestellt werden soll<br>
 * AUTO : Die Darstellung wird automatisch festgelegt<br>
 * POW  : Potenzen werden als Potenz dargestellt<br>
 * SQRT : Wenn es sinnvoll ist, werden Potenten als Wurzel dargestellt<br>
 */
public enum SHOWPOTENZ{
    AUTO,POW,SQRT;
    public static SHOWPOTENZ parse(String s) {
        for (SHOWPOTENZ o:SHOWPOTENZ.values()) {
            if (o.toString().length()>0 && o.toString().equalsIgnoreCase(s)) return o;
        }
        return null;
    }
}
