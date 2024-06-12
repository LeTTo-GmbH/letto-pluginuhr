package at.letto.tools.config;

import at.letto.tools.Cmd;
import at.letto.tools.FileMethods;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class DockerComposeEnvFile {

    public static HashMap<String, String> readFile(File file, HashMap<String, String> values) throws IOException {
        Pattern p = Pattern.compile("^([^=]+)=(.*)\\n?$");
        Matcher m;
        try {
            Vector<String> inhalt = FileMethods.readFileInList(file);
            for (String s : inhalt) {
                if ((m=p.matcher(s)).find()) {
                    String key = m.group(1).trim();
                    String value = m.group(2).trim();
                    if (key.length()>0) {
                        values.put(key,value);
                    }
                }
            }
            return values;
        } catch (Exception e) {
            throw new IOException("Datei "+file.getAbsolutePath()+" kann nicht gelesen werden!");
        }
    }

    public HashMap<String, String> readFile(File file) throws IOException  {
        return readFile(file,new HashMap<>());
    }

    public static void writeFile(String filename, HashMap<String, String> values) throws IOException {
        writeFile(new File(filename), values);
    }
    public static void writeFile(File file, HashMap<String, String> values) throws IOException {
        Vector<String> inhalt = new Vector<>();
        for (String key:values.keySet()) {
            inhalt.add(key+" = "+values.get(key));
        }
        if (!Cmd.writelnfile(inhalt,file)) throw new IOException("Datei "+file.getAbsolutePath()+" kann nicht geschrieben werden!");
    }

}
