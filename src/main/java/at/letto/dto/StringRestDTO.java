package at.letto.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StringRestDTO extends RestDTO {

    private String data;

    public StringRestDTO(String restkey, String data) {
        super(restkey);
        this.data = data;
    }

}
