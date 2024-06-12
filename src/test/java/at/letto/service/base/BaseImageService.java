package at.letto.service.base;

import at.letto.tools.JSON;
import at.letto.tools.WebGet;
import at.letto.globalinterfaces.ImageService;
import at.letto.tools.dto.IMAGEUNIT;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.dto.ImageInfoDto;
import at.letto.tools.threads.LettoTimer;
import lombok.Getter;
import lombok.Setter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basis-Implementierung eines Image-Services
 */
@Getter
@Setter
public class BaseImageService implements ImageService {

    /** Lokaler Pfad in dem die Bilder gespeichert werden */
    private String localPath = "";

    /** URL welche am Webserver auf die Bilder verweist */
    private String urlPath = "";

    /** absolute URL welche am Webserver auf die Bilder verweist */
    private String absUrlPath = "";

    /** Gibt an ob die Dateien in Unterverzeichniss gelegt werden, welche mit den ersten zwei Buchstaben der Datei beginnen */
    private boolean subdirs = true;

    /** Dateiname der Datei welche zum Prüfen verwendet wird */
    protected String filename;

    public BaseImageService(String localPath, String urlPath, boolean subdirs) throws Exception {
        this(localPath,urlPath,subdirs,"checkfs.txt");
    }

    public void adaptUrlToRelative(String serverpath) {
        String server = serverpath.replaceAll("(https?://[^/:]*)(.*)","$1");
        if (urlPath.contains(server+"/"))
            urlPath = urlPath.replaceAll("https?://[^/]*(.*)","$1");
    }

    protected BaseImageService(String localPath, String urlPath, boolean subdirs, String filename) throws Exception {
        System.out.println("Start Image Service");
        this.filename = filename;
        this.setLocalPath(localPath.trim());
        //TODO die nächste Zeile sollte später wieder gelöscht werden
        this.createLocalPath();
        this.setUrlPath(urlPath);
        this.setSubdirs(subdirs);
        String msg;
        if ((msg=this.checkFilesystem()).length()>0)
            throw new Exception("Fehler beim lokalen Pfad des Image-Service: "+msg);
        urlPath = urlPath.trim();
        Pattern p = Pattern.compile("^[a-zA-Z]+:\\/\\/+(.*)$");
        Matcher m = p.matcher(urlPath);
        String url= urlPath;
        if (m.find()) url = m.group(1);
        this.setUrlPath("https://"+url);
        if ((msg=this.checkService()).length()>0) {
            this.setUrlPath("http://"+url);
            if ((msg=this.checkService()).length()>0) {
                this.setUrlPath("https://"+url);
            }
        }
    }

    private void createLocalPath() {
        File file = new File(this.getLocalPath());
        if (file.exists()) {
            if (file.isDirectory()) return;
            throw new RuntimeException("Der Image-Pfad "+this.getLocalPath()+" muss ein Verzeichnis sein, ist aber keines!");
        }
        file.mkdirs();
        if (file.exists()) {
            if (file.isDirectory()) return;
            throw new RuntimeException("Der Image-Pfad "+this.getLocalPath()+" muss ein Verzeichnis sein, ist aber keines!");
        }
        throw new RuntimeException("Der Image-Pfad "+this.getLocalPath()+" konnte nicht angelegt werden!");
    }

    public void setLocalPath(String localPath) {
        localPath = localPath.trim().replaceAll("\\\\", "/");
        localPath = localPath + "/";
        localPath = localPath.replaceAll("//", "/");
        this.localPath = localPath;
    }

    public void setUrlPath(String urlPath) {
        urlPath = urlPath + "/";
        urlPath = urlPath.replaceAll("//$", "/");
        this.urlPath = urlPath;
        this.absUrlPath = urlPath;
    }

    @Override
    public String toString() {
        return "Base-Image-Service:[" + localPath + "," + urlPath + ","+absUrlPath+"]";
    }

    @Override
    public String checkFilesystem(){
        File file = new File(this.getLocalPath() + filename);
        try {
            if (file.exists()) file.delete();
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht gelöscht werden!";
        }
        String s = "" + Math.random();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(s);
            fileWriter.close();
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht geschrieben werden!";
        }
        try {
            file = new File(this.getLocalPath() + filename);
            FileInputStream fis;
            fis = new FileInputStream(file);
            byte fileContent[] = new byte[(int)file.length()];
            fis.read(fileContent);
            String sg = new String(fileContent);
            fis.close();
            if (!sg.equals(s)) return "Datei " + file.getAbsolutePath() + " wurde nicht korrekt geschrieben!";
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht gelesen werden!";
        }
        try {
            if (file.exists()) file.delete();
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht gelöscht werden!";
        }
        return "";
    }

    @Override
    public String checkService() {
        File file = new File(this.getLocalPath() + filename);
        try {
            if (file.exists()) file.delete();
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht gelöscht werden!";
        }
        String s = "" + Math.random();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(s);
            fileWriter.close();
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht geschrieben werden!";
        }
        LettoTimer.delay_ms(2);
        String url = this.getUrlPath() + filename;
        try {
            Vector<String> get = WebGet.readURLTimeout(url,1000);
            String sg = get.get(0);
            if (!sg.equals(s)) return "URL " + url + " kann nicht korrekt gelesen werden!";
        } catch (Exception ex) {
            return "URL " + url + " kann nicht korrekt geladen werden!";
        }
        try {
            if (file.exists()) file.delete();
        } catch (Exception ex) {
            return "Datei " + file.getAbsolutePath() + " kann nicht gelöscht werden!";
        }
        return "";
    }

    @Override
    public String getURL(String filename) {
        if (subdirs) {
            Pattern p = Pattern.compile("^(.+)\\.([^\\.]+)$");
            Matcher m = p.matcher(filename);
            if (m.find() && m.group(1).length()>2) {
                String path = this.urlPath + m.group(1).toLowerCase().substring(0,2) + "/" + filename;
                return path;
            }
        }
        String path = this.urlPath + filename;
        return path;
    }

    @Override
    public String getAbsURL(String filename) {
        if (subdirs) {
            Pattern p = Pattern.compile("^(.+)\\.([^\\.]+)$");
            Matcher m = p.matcher(filename);
            if (m.find() && m.group(1).length()>2) {
                String path = this.absUrlPath + m.group(1).toLowerCase().substring(0,2) + "/" + filename;
                return path;
            }
        }
        String path = this.absUrlPath + filename;
        return path;
    }

    /**
     * Liefert einen File-Handle auf eine Datei
     * @param filename  Dateiname
     * @return          Filehandle auf die Datei
     */
    public File getImageFile(String filename) {
        if (subdirs) {
            Pattern p = Pattern.compile("^(.+)\\.([^\\.]+)$");
            Matcher m = p.matcher(filename);
            if (m.find() && m.group(1).length()>2) {
                String path = this.localPath + m.group(1).toLowerCase().substring(0,2) + "/" + filename;
                return new File(path);
            }
        }
        String path = this.localPath + filename;
        return new File(path);
    }

    /**
     * Liefert einen File-Handle auf eine Info-Datei
     * @param filename  Dateiname
     * @return          Filehandle auf die die Info-Datei zu einer Datei
     */
    public File getImageInfoFile(String filename) {
        if (subdirs) {
            Pattern p = Pattern.compile("^(.+)\\.([^\\.]+)$");
            Matcher m = p.matcher(filename);
            if (m.find() && m.group(1).length()>2) {
                String path = this.localPath + m.group(1).toLowerCase().substring(0,2) + "/" + filename+".info";
                return new File(path);
            }
        }
        String path = this.localPath + filename+".info";
        return new File(path);
    }

    @Override
    public File getLocalFile(String filename) {
        File f =  getImageFile(filename);
        if (f!=null && f.exists() && f.isFile()) return f;
        return null;
    }

    @Override
    public boolean existImage(String filename) {
        File file = getImageFile(filename);
        if (file != null) {
            return file.exists() && file.isFile();
        }
        return false;
    }

    @Override
    public long getImageAge(String filename) {
        File file = getImageFile(filename);
        if (file != null) {
            if (!(file.exists() && file.isFile())) return -1;
            try {
                long age = System.currentTimeMillis() - file.lastModified();
                if (age < 0) age = 0;
                return age;
            } catch (Exception e) {
            }
        }
        return -2;
    }

    @Override
    public long getImageSize(String filename) {
        File file = getImageFile(filename);
        if (file != null) {
            if (!(file.exists() && file.isFile())) return -1;
            try {
                return file.length();
            } catch (Exception e) {
            }
        }
        return -2;
    }

    @Override
    public boolean delImage(String filename) {
        File file = getImageFile(filename);
        if (file != null) {
            try {
                file.delete();
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @Override
    public boolean createFile(String filename) {
        File file = getImageFile(filename);
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

    @Override
    public boolean isFilenameOK(String filename) {
        if (filename.contains("\\")) return false;
        if (filename.contains("/")) return false;
        if (filename.length() < 3) return false;
        Pattern p = Pattern.compile("^(.+)\\.([^\\.]+)$");
        Matcher m = p.matcher(filename);
        if (m.find()) {
            if (m.group(1).length() > 2 && m.group(2).length() > 0) return true;
        }
        return false;
    }

    @Override
    public String getExtension(String filename) {
        Pattern p = Pattern.compile("^(.+)\\.([^\\.\\/\\\\]+)$");
        Matcher m = p.matcher(filename);
        if (m.find()) return m.group(2);
        return "";
    }

    @Override
    public String saveImage(String base64File, String filename) {
        File f = this.getImageFile(filename);
        if (f == null) return "Fehler beim Speichern der Datei " + filename;
        try {
            this.createFile(filename);
            byte[] base64decodedBytes = Base64.getMimeDecoder().decode(base64File);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(f);
                if (base64decodedBytes!=null) fos.write(base64decodedBytes);
                fos.close();
                return "";
            } catch (FileNotFoundException e) {
                return "Datei "+filename+" konnte nicht gefunden werden!";
            } catch (IOException e) {
                return "Datei "+filename+" konnte nicht geschrieben werden!";
            }
        } catch (Exception ignored) {
        }
        return "Fehler beim Speichern der Datei " + filename;
    }

    @Override
    public String loadImageBase64(String filename) {
        File f = this.getImageFile(filename);
        if (f!=null && f.exists()) {
            String ImgString = "";
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                byte fileContent[] = new byte[(int)f.length()];
                fis.read(fileContent);
                byte[] ret = Base64.getEncoder().encode(fileContent);
                ImgString = new String(ret);
                fis.close();
                return ImgString;
            } catch (Exception ignored) {}
        }
        return "";
    }

    /**
     * Speichert eine Base-64-kodierte Datei und sein zugehörigen Bild-Informationen
     * @param imageBase64Dto Bild und Bild-Informationen
     * @return               Leer wenn ok, oder eine Fehlermeldung
     */
    @Override
    public String saveImage(ImageBase64Dto imageBase64Dto){
        String result="";
        try {
            String filename = imageBase64Dto.getImageInfoDto().getFilename();
            result = saveImage(imageBase64Dto.getBase64Image(), filename);
            File f = this.getImageInfoFile(filename);
            FileWriter fileWriter=new FileWriter(f);
            String jsonInfo = JSON.objToJson(imageBase64Dto);
            fileWriter.write(jsonInfo);
            fileWriter.close();
        } catch (Exception ex) { result = "error: "+ex.getMessage(); }
        return result;
    }

    @Override
    public ImageBase64Dto loadImageBase64Dto(String filename) {
        File f = this.getImageFile(filename);
        if (f!=null && f.exists()) {
            String ImgString = "";
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                byte fileContent[] = new byte[(int)f.length()];
                fis.read(fileContent);
                byte[] ret = Base64.getEncoder().encode(fileContent);
                ImgString = new String(ret);
                fis.close();
            } catch (Exception ignored) {}
            // Datei wurde gelesen, jetzt kommt noch die Info aus der Infodatei
            f = this.getImageInfoFile(filename);
            if (f!=null && f.exists()) {
                try {
                    BufferedReader bfr = new BufferedReader(new FileReader(f));
                    StringBuilder stringBuilder = new StringBuilder();
                    String s;
                    while( (s=bfr.readLine())!=null) stringBuilder.append(s);
                    bfr.close();
                    ImageInfoDto imageInfoDto = JSON.jsonToObj(stringBuilder.toString(),ImageInfoDto.class);
                    ImageBase64Dto ret = new ImageBase64Dto(ImgString,imageInfoDto,"");
                    return ret;
                } catch (Exception ignored) { }
            }
            ImageInfoDto imageInfoDto = new ImageInfoDto(
                "","",filename,getURL(filename),0,0, IMAGEUNIT.none,100,
                "","image","",0
            );
            ImageBase64Dto ret = new ImageBase64Dto(ImgString,imageInfoDto,"");

            return ret;
        }
        return new ImageBase64Dto("",new ImageInfoDto(),"cannot read file "+filename);
    }

    private void saveByteArrayInFile(byte[] data, String filename) throws IOException {
        File f = this.getImageFile(filename);
        this.createFile(filename);
        FileOutputStream fos;
        fos = new FileOutputStream(f);
        if (data!=null) fos.write(data);
        fos.close();
    }

    @Override
    public String saveImage(BufferedImage image, String filename) {
        String extension = this.getExtension(filename);
        if (!(extension.equals("jpg") || extension.equals("png") || extension.equals("gif")))
            return filename + " kann nicht gespeichert werden! AWT-Images können nur als png, jpg oder gif gespeichert werden!!";
        File f = this.getImageFile(filename);
        this.createFile(filename);
        try {
            if (f != null) {
                ImageIO.write(image, extension, f);
                return "";
            }
        } catch (IOException e) {
        }
        return "Fehler beim Speichern der Datei " + filename;
    }

    @Override
    public String saveURLImage(String webPath) {
        try {
            // Lade das Bild in ein Byte-Array inhalt
            /*BufferedInputStream in = new BufferedInputStream(new URL(webPath).openStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                buffer.write(dataBuffer, 0, bytesRead);
            }
            buffer.flush();
            byte[] inhalt = buffer.toByteArray();*/
            byte[] inhalt = WebGet.getUrlByteArray(webPath);
            if (inhalt.length==0) return "";
            // Bestimme den Dateinamen über die Prüfsumme
            String extension = this.getExtension(webPath);
            if (extension.length()==0 || extension.length()>4 || extension.contains("/")) extension = "jpg";
            String filename = ImageService.generateFilename(inhalt,extension);
            if (filename.length()==0) throw new RuntimeException("Dateiname kann nicht erzeugt werden!");
            saveByteArrayInFile(inhalt,filename);
            return filename;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public String saveBase64Image(String base64encodedString, String extension){
        try {
            byte[] inhalt = Base64.getMimeDecoder().decode(base64encodedString);
            String filename = ImageService.generateFilename(inhalt,extension);
            if (filename.length()==0) throw new RuntimeException("Dateiname kann nicht erzeugt werden!");
            saveByteArrayInFile(inhalt,filename);
            return filename;
        } catch (Exception ignored) {}
        return "";
    }

    @Override
    public String saveLocalImage(File file){
        try {
            if (!file.exists()) return "";
            FileInputStream fis;
            fis = new FileInputStream(file);
            byte inhalt[] = new byte[(int)file.length()];
            fis.read(inhalt);
            fis.close();
            String extension = this.getExtension(file.getName());
            if (extension.length()==0 || extension.length()>4 || extension.contains("/")) extension = "";
            String filename = ImageService.generateFilename(inhalt,extension);
            saveByteArrayInFile(inhalt,filename);
            return filename;
        } catch (Exception ignored) {}
        return "";
    }

    @Override
    public String saveByteArrayImage(byte[] byteArray, String extension) {
        try {
            String filename = ImageService.generateFilename(byteArray,extension);
            if (filename.length()==0) throw new RuntimeException("Dateiname kann nicht erzeugt werden!");
            saveByteArrayInFile(byteArray,filename);
            return filename;
        } catch (Exception ignored) {}
        return "";
    }

    private Vector<String> getFiles(String path) {
        File dir = new File(path);
        Vector<String> ret = new Vector<String>();
        if (dir.isDirectory()) {
            for (File f:dir.listFiles()) {
                if (f.isFile()) ret.add(f.getName());
            }
        }
        return ret;
    }

    private Vector<String> getDirs(String path) {
        File dir = new File(path);
        Vector<String> ret = new Vector<String>();
        if (dir.isDirectory()) {
            for (File f:dir.listFiles()) {
                if (f.isDirectory()) ret.add(f.getName());
            }
        }
        return ret;
    }

    @Override
    public Vector<String> getImages() {
        Vector<String> ret;
        if (subdirs) {
            ret = new Vector<String>();
            for (String path: getDirs(localPath)) {
                for (String filename: getFiles(localPath+"/"+path)) {
                    if (path.length()==2 && filename.toLowerCase().startsWith(path.toLowerCase()))
                        ret.add(filename);
                }
            }
        } else {
            ret = getFiles(localPath);
        }
        return ret;
    }

    @Override
    public Vector<String> delImages(Vector<String> images){
        Vector<String> ret = new Vector<String>();
        for (String filename:images) {
            if (delImage(filename)) ret.add(filename);
        }
        return ret;
    }

    @Override
    public Vector<String> getImagesOlderThan(long age){
        Vector<String> ret = new Vector<String>();
        for (String filename: getImages()) {
            long fileage = this.getImageAge(filename);
            if (fileage >= age) ret.add(filename);
        }
        return ret;
    }
/*
    @Override
    public String getImageWeb(FileDTO fileDTO, boolean dblClick) {
        if (fileDTO!=null) {
            return String.format(
                    "<img on%sclick='openImg(\"%s\")' src=\"%s\" alt=\"%s\" style=\"width:%s;\">" ,
                    dblClick ? "dbl":"" , fileDTO.getWebPath(),
                    fileDTO.getWebPath(), fileDTO.getAlternate(), fileDTO.htmlImageWidth());
        }
        else return "";
    }*/

}
