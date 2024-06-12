package at.letto.basespringboot.service;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Service
public class BaseUploadService {

    /**
     * Lädt ein Multipart-File auf den Server hoch
     * @param multipartFile    Multipart-Teil des Formulares können Zieldateiname "uploadFilename"
     *                         und Zielverzeichnis "uploadDirectory" als Input-Element angegeben werden.
     * @param uploadDirectory  Verzeichnis in das hochgeladen werden soll wenn im Formular nicht anders angegeben
     * @return                 File Element auf die heruntergeladene Datei
     * @throws Exception       Fehlermeldung wenn etas nicht funktioniert hat
     */
    public File handleUpload(MultipartFile multipartFile, String uploadDirectory, String uploadFilename) throws Exception {
        if (uploadDirectory==null || uploadDirectory.length()==0)
            uploadDirectory = System.getProperty("java.io.tmpdir");
        if (uploadDirectory==null || uploadDirectory.length()==0)
            uploadDirectory = "/tmp";
        if (uploadFilename==null || uploadFilename.trim().length()==0)
            uploadFilename = multipartFile.getOriginalFilename();
        File file = new File(uploadDirectory+"/"+uploadFilename);
        file = new File(file.getAbsolutePath());
        multipartFile.transferTo(file);
        return file;
    }

}
