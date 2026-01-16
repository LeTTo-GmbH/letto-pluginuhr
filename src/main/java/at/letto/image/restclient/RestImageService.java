package at.letto.image.restclient;

import at.letto.globalinterfaces.ImageService;
import at.letto.image.dto.ImageLongDto;
import at.letto.image.dto.ImageServiceDto;
import at.letto.image.dto.ImageStringDto;
import at.letto.image.dto.ImageStringVectorDto;
import at.letto.image.endpoints.ImageEndpoint;
import at.letto.service.microservice.AdminInfoDto;
import at.letto.service.rest.RestClient;
import at.letto.tools.Cmd;
import at.letto.tools.ENCRYPT;
import at.letto.tools.dto.FileDTO;
import at.letto.tools.dto.ImageBase64Dto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Vector;

public class RestImageService extends RestClient implements ImageService {

    ImageServiceDto.SERVICEMODE servicemode;

    String tmpFilePath="";

    String serverpath = "";

    @Override
    public String info() {
        String rev = get(ImageEndpoint.info,String.class);
        rev = "Imageservice: mode="+servicemode.toString()
             +" , server="+serverpath
             +" , tmp="+tmpFilePath
             +" , service="+rev;
        return rev;
    }

    @Override
    public boolean ping() {
        return ping(ImageEndpoint.ping);
    }

    @Override
    public String version() {
        String rev = get(ImageEndpoint.version,String.class);
        return rev;
    }

    @Override
    public AdminInfoDto admininfo() {
        AdminInfoDto rev = get(ImageEndpoint.info,AdminInfoDto.class);
        return rev;
    }

    public RestImageService(String baseURI, String user, String password, ImageServiceDto.SERVICEMODE servicemode, String tmpFilePath) {
        super(baseURI,user,password);
        this.servicemode = servicemode;
        if (tmpFilePath==null) tmpFilePath="";
        tmpFilePath = tmpFilePath.trim();
        while (tmpFilePath.endsWith("/")) tmpFilePath = tmpFilePath.substring(0,tmpFilePath.length()-1);
        this.tmpFilePath = tmpFilePath;
        File pfad = new File(tmpFilePath);
        if (!pfad.exists()) pfad.mkdirs();
        String check = checkFilesystem();
        String checkservice = checkService();
        /*if (check==null) throw new RuntimeException("Check vom Image-Service konnte nicht durchgeführt werden!");
        else if (check.length()>0) throw new RuntimeException("Check vom Image-Service liefert: "+check);
        if (checkservice==null) throw new RuntimeException("CheckService vom Image-Service konnte nicht durchgeführt werden!");
        else if (checkservice.length()>0) throw new RuntimeException("CheckService vom Image-Service liefert: "+checkservice);*/
        if (check==null) System.out.println("Check vom Image-Service konnte nicht durchgeführt werden!");
        else if (check.length()>0) System.out.println("Check vom Image-Service liefert: "+check);
        if (checkservice==null) System.out.println("CheckService vom Image-Service konnte nicht durchgeführt werden!");
        else if (checkservice.length()>0) System.out.println("CheckService vom Image-Service liefert: "+checkservice);
    }

    @Override
    public String checkFilesystem() {
        return post(ImageEndpoint.checkfilesystem,new ImageServiceDto(servicemode),String.class);
    }

    @Override
    public String checkService() {
        return post(ImageEndpoint.checkservice,new ImageServiceDto(servicemode),String.class);
    }

    @Override
    public boolean existImage(String filename) {
        Boolean result = post(ImageEndpoint.existimage,new ImageStringDto(servicemode,filename),Boolean.class);
        return (result!=null && result);
    }

    @Override
    public long getImageAge(String filename) {
        Long result = post(ImageEndpoint.getimageage,new ImageStringDto(servicemode,filename),Long.class);
        if (result!=null) return result;
        return 0;
    }

    @Override
    public long getImageSize(String filename) {
        Long result = post(ImageEndpoint.getimagsize,new ImageStringDto(servicemode,filename),Long.class);
        if (result!=null) return result;
        return 0;
    }

    @Override
    public boolean delImage(String filename) {
        Boolean result = post(ImageEndpoint.delimage,new ImageStringDto(servicemode,filename),Boolean.class);
        return (result!=null && result);
    }

    @Override
    public String getURL(String filename) {
        return post(ImageEndpoint.geturl,new ImageStringDto(servicemode,filename),String.class);
    }

    @Override
    public String getAbsURL(String filename) {
        return post(ImageEndpoint.getabsurl,new ImageStringDto(servicemode,filename),String.class);
    }

    @Override
    public boolean createFile(String filename) {
        Boolean result = post(ImageEndpoint.createfile,new ImageStringDto(servicemode,filename),Boolean.class);
        return (result!=null && result);
    }

    @Override
    public boolean isFilenameOK(String filename) {
        Boolean result = post(ImageEndpoint.isfilenameok,new ImageStringDto(servicemode,filename),Boolean.class);
        return (result!=null && result);
    }

    @Override
    public String getExtension(String filename) {
        return post(ImageEndpoint.getextension,new ImageStringDto(servicemode,filename),String.class);
    }

    @Override
    public String saveImage(ImageBase64Dto imageBase64Dto) {
        //TODO Werner: muss noch angepasst werden!!
        return post(ImageEndpoint.saveimage,imageBase64Dto,String.class);
    }

    @Override
    public ImageBase64Dto loadImageBase64Dto(String filename) {
        //TODO Werner: muss noch angepasst werden!!
        return post(ImageEndpoint.loadimagebase64dto,new ImageStringDto(servicemode,filename),ImageBase64Dto.class);
    }

    @Override
    public String saveImage(String base64File, String filename) {
        return post(ImageEndpoint.saveimage,new ImageStringDto(servicemode,filename,base64File),String.class);
    }

    @Override
    public String saveImage(BufferedImage image, String filename) {
        String base64File = ImageService.imgToBase64String(image);
        return post(ImageEndpoint.saveimage,new ImageStringDto(servicemode,filename,base64File),String.class);
    }

    @Override
    public String saveByteArrayImage(byte[] byteArray, String extension) {
        String base64encodedString = ENCRYPT.base64Encode(byteArray);
        return saveBase64Image(base64encodedString,extension);
    }

    @Override
    public String saveBase64Image(String base64encodedString, String extension) {
        return post(ImageEndpoint.savebase64image,new ImageStringDto(servicemode,"",base64encodedString,extension),String.class);
    }

    @Override
    public String saveURLImage(String webPath) {
        return post(ImageEndpoint.saveurlimage,new ImageStringDto(servicemode,"",webPath),String.class);
    }

    @Override
    public String saveLocalImage(File file) {
        try {
            if (!file.exists()) return "";
            FileInputStream fis;
            fis = new FileInputStream(file);
            byte inhalt[] = new byte[(int)file.length()];
            fis.read(inhalt);
            fis.close();
            String extension = this.getExtension(file.getName());
            if (extension.length()==0 || extension.length()>4 || extension.contains("/")) extension = "";
            return saveByteArrayImage(inhalt,extension);
        } catch (Exception ignored) {}
        return "";
    }

    @Override
    public String loadImageBase64(String filename) {
        return post(ImageEndpoint.loadimagebase64,new ImageStringDto(servicemode,filename),String.class);
    }

    @Override
    public String loadImageBase64(FileDTO fileDTO) {
        return loadImageBase64(fileDTO.getFilename());
    }

    @Override
    public Vector<String> getImages() {
        return post(ImageEndpoint.getimages,new ImageServiceDto(servicemode), ImageStringVectorDto.class).getStrings();
    }

    @Override
    public Vector<String> delImages(Vector<String> images) {
        return post(ImageEndpoint.delimages,new ImageStringVectorDto(servicemode,images), ImageStringVectorDto.class).getStrings();
    }

    @Override
    public Vector<String> getImagesOlderThan(long age) {
        return post(ImageEndpoint.getimagesolderthan,new ImageLongDto(servicemode, age), ImageStringVectorDto.class).getStrings();
    }

    @Override
    public File getLocalFile(String filename) {
        try {
            String base64encodedString = loadImageBase64(filename);
            byte[] inhalt = Base64.getMimeDecoder().decode(base64encodedString);
            if (inhalt.length==0) return null;
            File file = new File(tmpFilePath+"/"+filename);
            file.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            if (inhalt!=null) fos.write(inhalt);
            fos.close();
            return file;
        } catch (Exception ignored) {}
        return null;
    }



    @Override
    public void adaptUrlToRelative(String serverpath) {
        this.serverpath = serverpath;
    }

}
