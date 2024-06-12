package at.letto.tools;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestFileMethods {


    public static File getFile(String filename) {
        filename = "data/"+filename;
        File file = new File(filename);
        if (file.exists() && file.canRead() && !file.isDirectory()) {
            return file;
        }
        return null;
    }

    public static InputStream getInputStream(File f) {
        InputStream reader;
        try {
            return new FileInputStream(f);
        } catch (IOException e) {
            return null;
        }
    }

    @Test
    public void testEncoding() throws IOException {
        File f = getFile("encodingIso.csv");
        assertNotNull(f);
        List<String> encodings = FileMethods.getEncoding(getInputStream(f));
        assertEquals(encodings.get(0), "ISO-8859-1") ;
        f = getFile("encodingUtf.csv");
        encodings = FileMethods.getEncoding(getInputStream(f));
        assertEquals(encodings.get(0), "UTF-8") ;
    }

    @Test
    public void testReadFileInTable() throws IOException {
        try {
            File f = getFile("encodingIso.csv");
            List<List<String>> erg = FileMethods.readFileInTable(f, ",");
            assertEquals(erg.get(1).get(0), "äöüÄÖÜßáà");
            assertEquals(erg.get(0).get(5), "mail");
        }
        catch (RuntimeException r) {
        }
        catch (Exception e) {
        }
    }

    @Test
    public void testReadDataAsTable() throws IOException {
        List<List<String>> data = null;
        try {
            File f = getFile("encodingIso.csv");
            data = FileMethods.readFileInTable(f, ",");
            assertNotNull(data);
        }
        catch (Exception e) {
        }

        List<List<String>> erg = FileMethods.readDataAsTable(data, true, new String[]{"mail", "name"});
        assertEquals(erg.get(1).get(1), "äöüÄÖÜßáà");
        assertEquals(erg.get(0).get(0), "mail");

        erg = FileMethods.readDataAsTable(data, false, new String[]{"xxx:5", "yyy:1"});
        assertEquals(erg.get(2).get(1), "Irgendwas");
        assertEquals(erg.get(0).get(0), "xxx");
        assertEquals(erg.get(1).get(1), "spalte");

        try {
            erg = FileMethods.readDataAsTable(new Vector<>(), false, new String[]{"xxx:5", "yyy:1"});
            assertEquals("FEHLER: ", "Es wurden keine Daten übergeben!");
        }
        catch (RuntimeException r) {
            //assertEquals(r.getMessage(), "Es wurden keine Daten übergeben!");
        }

        try {
            erg = FileMethods.readDataAsTable(data, false, new String[]{"xxx:9", "yyy:1"});
            assertEquals("FEHLER: ", "Spaltenanzahl passt nicht zu den übergebenen Daten!");
        }
        catch (RuntimeException r) {
            //assertEquals(r.getMessage(), "Spaltenanzahl passt nicht zu den übergebenen Daten!");
        }

        try {
            erg = FileMethods.readDataAsTable(data, true, new String[]{"Nicht definiert:0", "yyy:1"});
            assertEquals("FEHLER: ", "Spalte wurde nicht gefunden");
        }
        catch (RuntimeException r) {
            //assert(r.getMessage().startsWith("Spalte wurde nicht gefunden"));
        }
    }
}
