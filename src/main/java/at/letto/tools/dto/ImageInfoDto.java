package at.letto.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Information of an Image which is stored in the Image-Service or returned from other service.<br>
 * It consists any Data about the source of then image and the properties to view in LeTTo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoDto {

    /** version of the plugin if image was built in a plugin */
    private String version="";

    /** pugin-type of the plugin if image was built in a plugin */
    private String pluginTyp="";

    /** filename of then image file */
    private String filename = "";

    /** open URL of the image or stringlength=0 if it is not an open image */
    private String url="";

    /** origin width of then image in pixel */
    private int width;

    /** orogin height of then image in pixel */
    private int height;

    /** the unit of imageWidth to display image in HTML */
    private IMAGEUNIT unit=IMAGEUNIT.none;

    /** image-width to display in percent of the screen or with the defined unit */
    private int imageWidth=100;

    /** CSS style definition to display on a browser with HTML */
    private String style;

    /** alternative text used for a handicapped accessible html-site */
    private String alternate="plugin image";

    /** title of the image */
    private String title="";

    /** lifetime of the image file in milliseconds since 1.1.1970<br>
     *  file can be deleted if lifetime is reached <br>
     *  default lifetime is one year from now <br>
     *  if lifetime is equal or less then zero there is no lifetime defined*/
    private long lifetime = (new Date()).getTime() + 365L*24L*3600L*1000L;

    /** @return true if actual time is greater then lifetime */
    public boolean lifetimeOutdated() {
        if (lifetime<=0) return false;
        long time = (new Date()).getTime();
        if (lifetime<time) return true;
        return false;
    }

}