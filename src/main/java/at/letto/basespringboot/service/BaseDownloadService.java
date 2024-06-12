package at.letto.basespringboot.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class BaseDownloadService {

    /**
     * Startet einen Download über einen HttpServletResponse
     * @param response       HttpServletResponse für den Download
     * @param file           Datei die heruntergeladen werden soll
     * @throws IOException   Fehlermeldung wenn etwas nicht funktioniert
     */
    public void download(HttpServletResponse response, File file) throws IOException {
        String absolutPath = file.getAbsolutePath().replaceAll("\\\\","/");
        String filename = "";
        String[] pp = absolutPath.split("/");
        if (pp.length==0) filename = "noname.file";
        else filename = pp[pp.length-1];
        download(response, file, filename);
    }

    /**
     * Startet einen Download über einen HttpServletResponse
     * @param response       HttpServletResponse für den Download
     * @param file           Datei die heruntergeladen werden soll
     * @param filename       Zieldateiname mit dem die Datei am Client gespeichert werden soll
     * @throws IOException   Fehlermeldung wenn etwas nicht funktioniert
     */
    public void download(HttpServletResponse response, File file, String filename) throws IOException {
        FileSystemResource resource = new FileSystemResource(file);
        MediaType mediaType = MediaTypeFactory
                .getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        long fileLength = file.length();

        String contentType = mediaType.getType();
        response.setContentType(contentType);
        response.setHeader("Content-Length", Long.toString(fileLength));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename +"\"");

        InputStream ins = new FileInputStream(file.getPath());

        IOUtils.copy(ins, response.getOutputStream());
        IOUtils.closeQuietly(ins);
        IOUtils.closeQuietly(response.getOutputStream());
    }

}
