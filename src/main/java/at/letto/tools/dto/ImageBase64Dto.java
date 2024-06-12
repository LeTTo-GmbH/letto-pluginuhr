package at.letto.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * image with image-information of an image-file which is coded in base64
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBase64Dto {

    /** base64 coded image */
    private String base64Image="";

    /** image information */
    private ImageInfoDto imageInfoDto = new ImageInfoDto();

    /** error message if the image cannot be created */
    private String  error="";

}
