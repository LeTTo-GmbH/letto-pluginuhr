package at.letto.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManipulateSetupServiceDto {

    /** Kommando für das Setup Service start,stop,restart,update */
    private String  command;

    /** Setupservice als Docker-Container */
    private boolean docker;

    /** Directory der Setup docker-compose.yml im Filesystem des Host (wird bei Docker nicht benötigt) */
    private String  composeDirectory;

}
