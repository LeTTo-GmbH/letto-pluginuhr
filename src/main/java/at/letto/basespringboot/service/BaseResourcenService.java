package at.letto.basespringboot.service;

import at.letto.tools.Cmd;
import at.letto.tools.FileMethods;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

public class BaseResourcenService {

    public String copyTextFileFromResource(String resourceName, String targetFileName) {
        File targetFile = new File(targetFileName);
        return copyTextFileFromResource(resourceName,targetFile, "UTF-8");
    }
    public String copyTextFileFromResource(String resourceName, File targetFile) {
        return copyTextFileFromResource(resourceName, targetFile, "UTF-8");
    }

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

    public String copyFileFromResource(String resourceName, String targetFileName) {
        File targetFile = new File(targetFileName);
        return copyFileFromResource(resourceName,targetFile);
    }

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
