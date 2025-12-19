package at.letto.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * image with image-information of an image-file at an open Url
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUrlDto {

    /** image Url */
    private String imageUrl="";

    /** image information */
    private ImageInfoDto imageInfoDto = new ImageInfoDto();

    /** error message if the image cannot be created */
    private String  error="";

}
