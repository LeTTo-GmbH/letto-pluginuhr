package at.letto.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Informationen über das Service aller Schulen des Servers <br>
 * Alles was für Data und Letto für die Verbindung notwendig ist<br>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSchulenListDto {

    private List<ServiceSchuleDto> schulen;

}
