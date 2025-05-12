package at.letto.question.dto.lettoprivate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Abo {

    private String user;

    private String name;

    private String idBook;

    private long gueltigBis;

    private String rechnung;

}
