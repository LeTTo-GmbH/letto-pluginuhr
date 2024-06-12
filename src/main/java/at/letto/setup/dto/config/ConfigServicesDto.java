package at.letto.setup.dto.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Liste aller Services, welche im Setup-Service registriert wurden
 */
@Getter @Setter @NoArgsConstructor
public class ConfigServicesDto {

    private List<ConfigServiceDto> services;

}
