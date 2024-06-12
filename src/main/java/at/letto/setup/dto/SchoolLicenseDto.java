package at.letto.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolLicenseDto {

    /** Schulkennung der Schule */
    private String school;
    /** Restkey des Servers */
    private String restkey;
    /** Lizenz der Schule */
    private String license;
    /** Befehl, welcher vom Service ausgeführt werden soll */
    private String cmd;

}
