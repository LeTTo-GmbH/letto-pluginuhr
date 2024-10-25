package at.letto.tools;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileMethods {
    /**
     * Ermittlung der wahrscheinlichsten Codierung der übergebenen Bytes
     * @param bytes    zu analysierende Bytes
     * @return     Encodierung in Text-Angabe
     */
    public static String guessEncoding(byte[] bytes) {
        String DEFAULT_ENCODING = "UTF-8";
        org.mozilla.universalchardet.UniversalDetector detector =
                new org.mozilla.universalchardet.UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }

    /**
     * Liefert eine liste von Encodierungen, wobei die zuletzt gefundene an erster Position steht
     * @param inputStream   Eingangs-Daten
     * @return              Liste mit Strings, die mögliche Codierungen beschreiben
     * @throws IOException   Fehlermeldung
     */
    public static List<String> getEncoding(InputStream inputStream) throws IOException {
        Vector<String> ret = new Vector<>();
        ret.add("UTF-8");
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            String enc = guessEncoding(data);
            if (enc.startsWith("WINDOWS"))
                enc = "ISO-8859-1";

            if (!ret.contains(enc))
                ret.add(0, enc);
        }
        return ret;
    }

    /**
     * Einlesen eines Datei mit Encoding-Abschätzung
     * @param file         File-Objekt mit Pfad zur Datei
     * @return             Vektor mit Strings als Zeileninhalt
     */
    public static Vector<String> readFileInList(File file) {
        if (file.exists() && file.canRead() && !file.isDirectory()) {
            InputStream reader;
            try {
                reader = new FileInputStream(file);
                long size = Files.size(file.toPath());
                Vector<String> ret =  readFileInList(reader, size);
                reader.close();
                return ret;
            } catch (IOException e) {
                throw new RuntimeException("IO-Exception beim Lesen der Datei");
            }
        }
        throw new RuntimeException("Datei wurde nicht gefunden: " + file.getPath());
    }

    /**
     * Einlesen eines Datei mit Encoding-Abschätzung
     * @param filename     Vollständiger Dateiname
     * @return             Vektor mit Strings als Zeileninhalt
     */
    public static Vector<String> readFileInList(String filename) {
        File file = new File(filename);
        return readFileInList(file);
    }

    /**
     * Einlesen eines Input-Streams mit Encoding-Abschätzung
     * @param inputStream   InputStream
     * @param size          Größe  nicht benutzt
     * @return              Vektor mit Strings als Zeileninhalt
     */
    public static Vector<String> readFileInList(InputStream inputStream, long size)  {
        byte[] inp = null;
        Vector<String> file = new Vector<>();

        try {
            inp = ByteStreams.toByteArray(inputStream);
            InputStream stream =  ByteSource.wrap(inp).openStream();
            List<String> enc = getEncoding(stream);
            String encoding = "UTF-8";
            encoding = enc.get(0);
            if (encoding.startsWith("WINDOWS"))
                encoding = "ISO-8859-1";
            stream.close();

            stream =  ByteSource.wrap(inp).openStream();
            Charset charset = Charset.forName(encoding);
            // Bufferer - Reader mit entsprechendem Zeichensatz erzeugen
            BufferedReader datei = new BufferedReader(new InputStreamReader(stream, charset));
            String s;
            int lineNr = 0;
            while ((s = datei.readLine()) != null) {
                if (lineNr++ ==0)
                    s = removeUnknownStartingChars(s);
                // Entfernen von Leerzeilen
                //if (s!=null && !s.isEmpty())
                    file.add(s);
            }
            datei.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("IO-Fehler beim lesen der Daten");
        }
    }

    /**
     * Einlesen eines Input-Streams mit definiertem Encoding
     * @param inputStream   InputStream
     * @param size          Größe  nicht benutzt
     * @param encoding      Encoding des Input-Streams
     * @return              Vektor mit Strings als Zeileninhalt
     */
    public static Vector<String> readFileInList(InputStream inputStream, long size, String encoding)  {
        byte[] inp = null;
        Vector<String> file = new Vector<>();
        try {
            // Bufferer - Reader mit entsprechendem Zeichensatz erzeugen
            BufferedReader datei = new BufferedReader(new InputStreamReader(inputStream, encoding));
            String s;
            int lineNr = 0;
            while ((s = datei.readLine()) != null) {
                if (lineNr++ ==0)
                    s = removeUnknownStartingChars(s);
                // Entfernen von Leerzeilen
                //if (s!=null && !s.isEmpty())
                file.add(s);
            }
            datei.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("IO-Fehler beim lesen der Daten");
        }
    }


    /**
     * Einlesen eines Datei mit Encoding-Abschätzung
     * @param file         File-Objekt mit Pfad zur Datei
     * @param trennzeichen  Trennzeichen als String
     * @return  Doppelt geschachtelte Liste mit allen Werten in Tabellenform
     */
    public static List<List<String>> readFileInTable(File file, String trennzeichen) {
        if (file.exists() && file.canRead() && !file.isDirectory()) {
            InputStream reader;
            try {
                reader = new FileInputStream(file);
                long size = Files.size(file.toPath());
                return readFileInTable(reader, size, trennzeichen);
            } catch (IOException e) {
                throw new RuntimeException("IO-Exception beim Lesen der Datei");
            }
        }
        throw new RuntimeException("Datei wurde nicht gefunden: " + file.getPath());
    }

    /**
     * Einlesen einer Tabelle aus CSV-Datei mit eindeutigem Zelltrennzeichen
     * @param filename     Vollständiger Dateiname
     * @param trennzeichen  Trennzeichen als String
     * @return  Doppelt geschachtelte Liste mit allen Werten in Tabellenform
     */
    public static List<List<String>> readFileInTable(String filename, String trennzeichen) {
        File file = new File(filename);
        return readFileInTable(file, trennzeichen);
    }

    /**
     * Einlesen einer Tabelle aus CSV-Datei mit eindeutigem Zelltrennzeichen
     * @param inputStream   Input-Strem des CSV-Files
     * @param size          Größe
     * @param trennzeichen  Trennzeichen als String
     * @return  Doppelt geschachtelte Liste mit allen Werten in Tabellenform
     */
    public static List<List<String>> readFileInTable(InputStream inputStream, long size, String trennzeichen)  {
        List<List<String>> ret = new Vector<>();
        List<String> content = readFileInList(inputStream, size);
        // Leerzeilen entfernen
        content = content.stream()
                .filter(l->l!=null && !l.isEmpty())
                .collect(Collectors.toList());

        // Suche ob Trennzeichen in allen Zeilen vorkommt
        String finalTrennzeichen = trennzeichen;
        if (!content.stream().allMatch(l->l.contains(finalTrennzeichen)||l.trim().isEmpty())) {
            switch (trennzeichen) {
                case ";": trennzeichen=",";break;
                case ",": trennzeichen=";";break;
                default:  trennzeichen=";";break;
            }
        }
        String finalTrennzeichen1 = trennzeichen;
        content.forEach(l->{
            ret.add( Arrays.stream(l.split(finalTrennzeichen1)).map(x ->
                    x.trim()
                            .replaceAll("^\"", "")
                            .replaceAll("\"$", ""))
                    .collect(Collectors.toList()));
        });
        return ret;
    }

    /**
     * Entfernt aus einem String alle nicht Standardzeichen vom Zeilenbeginn
     * @param s String
     * @return  String ohne die entfernten Zeichen
     */
    public static String removeUnknownStartingChars(String s) {
    	Matcher m = Pattern.compile("[a-zA-Z0-9öäüÖÄÜß<>\\;\\,\\.\\:\\-\\_\\+\\*\\#\\~\\\"\\'\\!\\§\\$\\%\\&\\/\\(\\)\\[\\]\\{\\}\\?\\\\\\s\\n].*$").matcher(s);
    	if (m.find()) return m.group(0);
    	return s;
    }

    /**
     * Sucht nach den geforderten Spaltenüberschriften eines CSV-Files
     * @param data          Daten-Tabelle mit oder ohne Überschriften
     * @param headerLine    Angabe, ob die Spaltennamen in der ersten Zeile übergeben wurden
     * @param colNames      Namen der vorkommenden Spalten.
     *                      Enthält die erste Spalte keine Überschriften, dann kann die Spaltenpostion
     *                      definiert werden: Spaltenname:SpaltenNummer.
     *                      Bsp: name:4,mail:7 bedeutet, dass die 4. Spalte den Namen enthält, die 7.Spalte die mailadresse....
     * @return Hashmap mit den Spaltenüberschriften und den entsprechenden Spalten-Angaben
     */
    public static Map<String, Integer> checkColumns(List<List<String>> data, boolean headerLine, String[] colNames) {
        if (data.size()==0)
            throw new RuntimeException("Es wurden keine Daten übergeben!");

        long colAnz = Arrays.stream(colNames).filter(n->!n.contains("?")).count();
        if (data.get(0).size()<colAnz)
            throw new RuntimeException("Spaltenanzahl passt nicht zu den angeforderten Daten!");

        // Vorbereitung der zurückgebenen Hashmap mit den notwendigen Spalten
        Map<String, Integer> columns = new LinkedHashMap<String, Integer>();
        for (String c: colNames) {
            String[] x = c.split(":");
            String colName = x[0].replaceAll("^\\?", "");
            int colNr = -1;
            if (x.length>1) {
                try {
                    colNr = Integer.parseInt(x[1]);
                } catch (Exception e) {}
            }
            if (headerLine) {
                // Spalte muss in der ersten Zeile vorkommen
                int nr = 0;
                boolean found = false;
                for (String cell : data.get(0)) {
                    if (cell.equals(colName)) {
                        colNr = nr;
                        found = true;
                        break;
                    }
                    nr++;
                }
                if (!found && !c.startsWith("?"))
                    throw new RuntimeException("Spalte wurde nicht gefunden: " + c);
            }
            if (colNr < 0 && !c.startsWith("?"))
                throw new RuntimeException("Spaltenposition ist nicht korrekt definiert: " + c);
            if (data.get(0).size() < colNr)
                throw new RuntimeException("Spaltenanzahl passt nicht zu den übergebenen Daten!");

            if (colNr >= 0)
                columns.put(colName, colNr);
        }
        //Rückgabe der zugehörigen Spalten-Indizes
        return columns;
    }

    /**
     * Einlesen von Daten in Tabellenform
     * @param inp           Tabellarischer Input als geschachtelte Liste von Strings
     * @param headerLine    Angabe, ob die Spaltennamen in der ersten Zeile übergeben wurden
     * @param colNames      Namen der vorkommenden Spalten.
     *                      Enthält die erste Spalte keine Überschriften, dann kann die Spaltenpostion
     *                      definiert werden: Spaltenname:SpaltenNummer.
     *                      Bsp: name:4,mail:7 bedeutet, dass die 4. Spalte den Namen enthält, die 7.Spalte die mailadresse....
     * @param alternateNames Alternativnamen
     * @return  Tabelle mit Überschriften in der ersten Zeile, nur mehr die gewünschten
     *          Tabellenspalten (colNames) werden zurückgegeben
     * @throws RuntimeException Wenn Tabellenspalten nicht zu den geforderten Angaben passen
     */
    public static List<List<String>> readDataAsTable(List<List<String>> inp, boolean headerLine, String [] colNames, String [] ... alternateNames)  {
        Map<String, Integer> cols = null;
        try {
            cols = checkColumns(inp, headerLine, colNames);
        } catch (RuntimeException e) {
            if (alternateNames!=null && alternateNames.length>0) {
                for (String [] alternative: alternateNames) {
                    try {
                        cols = checkColumns(inp, headerLine, alternative);
                    } catch (RuntimeException e1) { }
                    if (cols!=null) break;
                }
            }
        }
        List<List<String>> erg = new Vector<>();
        int anzCols = cols.keySet().size();
        // Werden keine Spaltennamen übergeben, dann wird eine Default-Belegung vorausgesetzt!
        if (!headerLine)
            erg.add(cols.keySet().stream().collect(Collectors.toList()));

        Map<String, Integer> finalCols = cols;
        inp.stream().forEach(line->{
            List<String> l = new Vector<>(anzCols);
            int i = 0;
            for (String colName : finalCols.keySet()) {
                try {
                    l.add(i++, line.get(finalCols.get(colName)));
                } catch (Exception e) {
                    l.add("");
                }
            }

            erg.add(l);
        });
        return erg;
    }

    /**
     * Erstellt eine Datei, wenn diese noch nicht existiert,
     * Alle Sonderzeichen werden aus dem Dateinamen entfernt, um kompatibel mit allen Filesystemen zu sein
     * @param file  File-Objekt, das angelegt werden soll
     * @return  true, wenn erfolgreich oder Datei bereits existiert
     */
    public static boolean createFile(File file) {
        String path = file.getAbsolutePath();
        Pattern p = Pattern.compile("^(.+)[\\/\\\\]([^\\/\\\\]+)$");
        Matcher m = p.matcher(path);
        if (m.find()) path = m.group(1);
        File pfad = new File(path);
        if (!pfad.exists()) pfad.mkdirs();
        if (pfad.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex) {
            }
        }
        if (file.exists()) return true;
        return false;
    }

    /**
     * Liest eine Konfigurationsdatei
     * @param file Datei die gelesen werden soll
     * @return     Hashmap der Werte
     */
    public static HashMap<String,String> loadConfigFile(File file) {
        try {
            Vector<String> lines = readFileInList(file);
            HashMap<String, String> data = new HashMap<>();
            Matcher m;
            for (String line : lines) {
                if (line.trim().startsWith("#")) continue;
                if ((m = Pattern.compile("^(?<key>[^=]*)=(?<value>.*)$").matcher(line)).find()) {
                    String key = m.group("key").trim();
                    String value = m.group("value");
                    data.put(key, value);
                }
            }
            return data;
        } catch (Exception igonre) {}
        return null;
    }

    /**
     * Schreibt eine Hashmap von Werten in eine Konfigurationsdatei
     * @param data Hashmap von Wertem
     * @param file Datei in die geschrieben werden soll
     * @return     true wenn alles funktioniert hat.
     */
    public static boolean writeConfigFile(HashMap<String,String> data, File file) {
        try {
            Vector<String> lines = new Vector<>();
            for (String key : data.keySet()) {
                lines.add(key + "=" + data.get(key));
            }
            return Cmd.writelnfile(lines, file);
        } catch (Exception igonre) {}
        return false;
    }

    /** löscht das angegebene Verzeichnis inklusive aller Unterverzeichnisse */
    public static boolean rmDir(String filename) {
        return rmDir(new File(filename.replaceAll("\\\\","/")));
    }

    /** löscht das angegebene Verzeichnis inklusive aller Unterverzeichnisse */
    public static boolean rmDir(File f) {
        return rmDir(f.toPath());
    }

    /** löscht das angegebene Verzeichnis inklusive aller Unterverzeichnisse */
    public static boolean rmDir(Path dir) {
        try {
            // Durchläuft das Verzeichnis und löscht alle Dateien und Unterverzeichnisse
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // Löscht jede Datei
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // Löscht das Verzeichnis nach dem Löschen seiner Inhalte
                    return FileVisitResult.CONTINUE;
                }
            });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
