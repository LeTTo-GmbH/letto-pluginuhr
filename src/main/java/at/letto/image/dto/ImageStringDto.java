package at.letto.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ImageStringDto extends ImageServiceDto {

    private String filename;
    private List<String> strings = new ArrayList<String>();

    public ImageStringDto(SERVICEMODE servicemode, String filename, String ... strings) {
        super(servicemode);
        this.filename = filename;
        for (String s:strings)
            this.strings.add(s);
    }

}
