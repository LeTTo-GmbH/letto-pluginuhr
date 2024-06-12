package at.letto.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Vector;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageStringVectorDto extends ImageServiceDto {

    private Vector<String> strings = new Vector<String>();

    public ImageStringVectorDto(SERVICEMODE servicemode, Vector<String> strings) {
        super(servicemode);
        this.strings = strings;
    }

}
