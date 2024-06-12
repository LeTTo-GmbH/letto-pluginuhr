package at.letto.image.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageServiceDto {

    /** Servicemode definiert welche Art des Services Bildes verwendet werden soll. */
    public enum SERVICEMODE{
        IMAGE, PHOTO, PLUGIN;
        public static SERVICEMODE parse(String s) {
            for (SERVICEMODE sm:SERVICEMODE.values()) if (sm.toString().equalsIgnoreCase(s)) return sm;
            return SERVICEMODE.IMAGE;
        }
    }
    @JsonIgnore
    private SERVICEMODE servicemode = SERVICEMODE.IMAGE;
    public String getServicemodeString() {
        if (servicemode==null) return SERVICEMODE.IMAGE.toString();
        return servicemode.toString();
    }
    public void setServicemodeString(String s) {
        for (SERVICEMODE sm:SERVICEMODE.values()) if (sm.toString().equals(s)) servicemode = sm;
        servicemode=SERVICEMODE.IMAGE;;
    }

    public ImageServiceDto(SERVICEMODE servicemode) {
        this.servicemode = servicemode;
    }

}
