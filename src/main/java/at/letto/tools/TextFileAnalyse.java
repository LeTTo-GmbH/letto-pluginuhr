package at.letto.tools;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

@Getter
public class TextFileAnalyse implements Comparable<TextFileAnalyse> {

    public static final String UMLAUTE ="öäüÖÄÜß";
    public static final String CORRECT_CHARS = "a-zA-ZöäüÖÄÜß0-9_\\s\n\r\\-!\"'§%&/=,;:#°<>\\|\\?\\\\\\^\\$\\+\\*\\s\\.\\(\\)\\[\\]\\{\\}";

    private int ctUmlaute    = 0;
    private int ctChars      = 0;
    private int ctErrorChars = 0;
    private int ctLines      = 0;
    private Charset cs       = null;
    private Vector<String> data =null;

    /** Analysiert eine eingelesene Datei */
    public TextFileAnalyse(List<String> data) {
        if (data == null) { this.data = null; }
        else {
            this.data = new Vector<>();
            ctLines = data.size();
            if (data.size()>0) {
                data.set(0, FileMethods.removeUnknownStartingChars(data.get(0)));
            }
            for (String line : data) {
                this.data.add(line);
                ctUmlaute    += line.replaceAll("[^" + UMLAUTE + "]", "").length();
                ctChars      += line.length();
                ctErrorChars += line.replaceAll("[" + CORRECT_CHARS + "]", "").length();
            }
        }
    }

    public static TextFileAnalyse analyse(String fileName, Charset cs) {
        return analyse(new File(fileName),cs);
    }
    public static TextFileAnalyse analyse(File file, Charset cs) {
        return analyse(file.toPath(),cs);
    }
    public static TextFileAnalyse analyse(Path path, Charset cs) {
        List<String> data = null;
        try {
             data = Files.readAllLines(path, cs);
        } catch (IOException e) { }
        TextFileAnalyse a =  new TextFileAnalyse(data);
        a.cs = cs;
        return a;
    }

    /**
     * Liefert als Qualitätskirterium die Anzahl der Umlaute weniger die Anzahl der fehlerhaften Zeichen
     * @return Qualitätskriterium - größer=besser
     */
    private int quality() {
        return ctUmlaute-ctErrorChars;
    }

    @Override
    public int compareTo(TextFileAnalyse o) {
        if (this.data==null) {
            if (o.data==null)    return 0;
            return 1;
        }
        if (o.data==null)    return -1;
        // Qualitätscheck
        if (this.quality()>o.quality()) return -1;
        if (this.quality()<o.quality()) return 1;
        // Umlaute sollte so viele wie möglich gefunden werden
        if (this.ctUmlaute>o.ctUmlaute) return -1;
        if (this.ctUmlaute<o.ctUmlaute) return  1;
        // fehlerhafte Zeichen sollten so wenig wie möglich gefunden werden
        if (this.ctErrorChars>o.ctErrorChars) return  1;
        if (this.ctErrorChars>o.ctErrorChars) return -1;
        return 0;
    }

}
