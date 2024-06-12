package at.letto.tools;

public class StringDto {

    /**
     * Trimmt den String s. Ist s null, so wird ein Leerstring zurückgegeben
     * @param s String der getrimmt werden soll
     * @return  getrimmter String
     */
    public static String trim(String s) {
        if (s==null) return "";
        return s.trim();
    }

}
