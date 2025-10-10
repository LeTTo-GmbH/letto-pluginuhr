package at.letto.basespringboot.service;

import at.letto.tools.Cmd;
import at.letto.tools.FileMethods;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

/**
 *  Service um Dateien aus den Ressourcen in das Dateisystem zu kopieren oder zu laden
 */

public class BaseResourcenService {

    /** Kopiert eine Textdatei aus den Ressourcen in das Dateisystem
     * @param resourceName   Name der Ressource (Pfad relativ zu src/main/resources)
     * @param targetFileName Zieldatei in die kopiert werden soll
     * @return Fehlermeldung wenn etwas nicht funktioniert hat, sonst leerer String
     */
    public String copyTextFileFromResource(String resourceName, String targetFileName) {
        File targetFile = new File(targetFileName);
        return copyTextFileFromResource(resourceName,targetFile, "UTF-8");
    }

    /** Kopiert eine Textdatei aus den Ressourcen in das Dateisystem
     * @param resourceName   Name der Ressource (Pfad relativ zu src/main/resources)
     * @param targetFile     Zieldatei in die kopiert werden soll
     * @return Fehlermeldung wenn etwas nicht funktioniert hat, sonst leerer String
     */
    public String copyTextFileFromResource(String resourceName, File targetFile) {
        return copyTextFileFromResource(resourceName, targetFile, "UTF-8");
    }

    /** Kopiert eine Textdatei aus den Ressourcen in das Dateisystem
     * @param resourceName   Name der Ressource (Pfad relativ zu src/main/resources)
     * @param targetFile     Zieldatei in die kopiert werden soll
     * @param encoding       Encoding der Textdatei (z.B. UTF-8, ISO-8859-1, ...)
     * @return Fehlermeldung wenn etwas nicht funktioniert hat, sonst leerer String
     */
    public String copyTextFileFromResource(String resourceName, File targetFile, String encoding) {
        String msg = "";
        ClassPathResource res = new ClassPathResource(resourceName);
        try {
            Vector<String> data = FileMethods.readFileInList(res.getInputStream(),0, encoding);
            Cmd.writelnfile(data, targetFile);
        } catch (IOException e2) {
            msg = "cannot load resource file from " + resourceName + " ";
        }
        return msg;
    }

    /** Kopiert eine Datei aus den Ressourcen in das Dateisystem
     * @param resourceName   Name der Ressource (Pfad relativ zu src/main/resources)
     * @param targetFileName Zieldatei in die kopiert werden soll
     * @return Fehlermeldung wenn etwas nicht funktioniert hat, sonst leerer String
     */
    public String copyFileFromResource(String resourceName, String targetFileName) {
        File targetFile = new File(targetFileName);
        return copyFileFromResource(resourceName,targetFile);
    }

    /** Kopiert eine Datei aus den Ressourcen in das Dateisystem
     * @param resourceName   Name der Ressource (Pfad relativ zu src/main/resources)
     * @param targetFile     Zieldatei in die kopiert werden soll
     * @return Fehlermeldung wenn etwas nicht funktioniert hat, sonst leerer String
     */
    public String copyFileFromResource(String resourceName, File targetFile) {
        String msg = "";
        ClassPathResource res = new ClassPathResource(resourceName);
        try {
            Files.copy(res.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e2) {
            msg = "cannot load resource file from " + resourceName + " ";
        }
        return msg;
    }

    /** Lädt eine Textdatei aus den Ressourcen
     * @param resourceName   Name der Ressource (Pfad relativ zu src/main/resources)
     * @return Vektor mit den Zeilen der Datei oder null wenn die Datei nicht geladen werden konnte
     */
    public Vector<String> loadTextFileFromResource(String resourceName) {
        ClassPathResource res = new ClassPathResource(resourceName);
        try {
            Vector<String> data = FileMethods.readFileInList(res.getInputStream(),0);
            return data;
        } catch (IOException e2) {
            System.out.println("cannot load resource file from " + resourceName + " ");
        }
        return null;
    }

}
