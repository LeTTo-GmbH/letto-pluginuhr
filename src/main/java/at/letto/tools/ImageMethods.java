package at.letto.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class ImageMethods {

    /**
     * Speichert ein AWT Image in einen Bas64-codierten String
     * @param image Bild
     * @param type  Dateityp
     * @return      Base64 codiertes Bild
     */
    public static String ImageToBas64(Image image, String type) {
        BufferedImage bufferedImage = imageToBufferedImage(image);
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, type, bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = Base64.getEncoder().encodeToString(imageBytes);
            bos.close();
        } catch (IOException e) { }
        return imageString;
    }

    public static BufferedImage imageToBufferedImage(Image image) {
        BufferedImage bi = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(image,0,0,null);
        g.dispose();
        return bi;
    }

    /**
     * Speichert eine Base64 codierte Datei in ein File
     * @param base64File Base64 codierte Datei
     * @param file       Zieldatei
     * @return           True wenn alles funktioniert hat!
     */
    public static boolean saveBase64Image(String base64File, File file) {
        try {
            byte[] base64decodedBytes = Base64.getMimeDecoder().decode(base64File);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                if (base64decodedBytes!=null) fos.write(base64decodedBytes);
                fos.close();
                return true;
            } catch (Exception e) { }
        } catch (Exception ignored) { }
        return false;
    }

    public static String loadFileAsBase64(File file) {
        String imgString = "";
        if (file!=null && file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                byte fileContent[] = new byte[(int) file.length()];
                fis.read(fileContent);
                byte[] ret = Base64.getEncoder().encode(fileContent);
                imgString = new String(ret);
                fis.close();
            } catch (Exception ignored) {
            }
        }
        return imgString;
    }

}
