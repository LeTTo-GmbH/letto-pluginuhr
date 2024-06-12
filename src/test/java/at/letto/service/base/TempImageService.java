package at.letto.service.base;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Date;

/**
 * Implementierung eines Image-Services welches nur ein Bild speichern kann
 */
@Getter
@Setter
public class TempImageService extends BaseImageService {

    String filename="";

    /**
     * Dieses Image-Service kann nur ein Bild speichern
     * @param localPath  lokaler Pfad im Dateisystem
     * @param urlPath    URL über die das Service erreichbar ist
     * @param filename   Dateiname
     * @throws Exception Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public TempImageService(String localPath, String urlPath, String filename) throws Exception {
        super(localPath, urlPath, false, filename);
        this.filename = filename;
        File f = this.getImageFile("");
        f.delete();
        f.createNewFile();
        if (!f.exists()) new RuntimeException("Temp-Image-Service kann nicht initialisiert werden!");
        String msg="";
        if ((msg=this.checkService()).length()>0)
            new RuntimeException("TempImageService:"+msg);
    }

    @Override
    public File getImageFile(String filename) {
        String path = this.getLocalPath() + this.filename;
        return new File(path);
    }

    @Override
    public String getURL(String filename) {
        String path = this.getUrlPath() + this.filename + "?time=" + (new Date()).getTime();
        return path;
    }

    @Override
    public boolean existImage(String filename) {
        return false;
    }

    @Override
    public String toString() {
        return "Temp-Image-Service:[" + getLocalPath() + "," + getUrlPath() + "," + filename + "]";
    }

    /**
     * Löscht die Datei welche im Service gespeichert ist
     */
    public void delete() {
        File f = this.getImageFile("");
        if (f.exists()) f.delete();
    }

    @Override
    public void finalize() {
        delete();
    }

}
