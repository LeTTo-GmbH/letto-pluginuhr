package at.letto.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageLongDto extends ImageServiceDto {

    private long value = 0;

    public ImageLongDto(SERVICEMODE servicemode, long value) {
        super(servicemode);
        this.value = value;
    }

}