package at.letto.setup.dto.ajax;

import lombok.Data;

@Data
public class AjaxEditSchoolSetButtonsDto {

    //@Operation(summary = "Ping für den Verbindungs-check - Nur aus dem Docker-Netzwerk erreichbar!")

    /**
     * Schule neu anlegen (Nur wenn Datenbank noch nicht existiert
     */
    public boolean buttonNeuAnlegen      = false; // Schule neu anlegen (Nur wenn Datenbank noch nicht existiert und nicht importiert wird)
    private boolean buttonNeuDeleteDb     = false; // Schule neu anlegen und dabei die bestehende Datenbank löschen
    private boolean buttonNeuUseDb        = false; // Schule neu anlegen wobei eine bestehende MySQL-Datenbank ohne Import verwendet wird
    private boolean buttonNeuImportDb     = false; // Schule neu anlegen und dafür den angegebenen SQL-Dump importieren
    private boolean buttonImportLastBackup= false; // Importieren des letzten Datenbank-Backups der Schule
    private boolean buttonImportDb        = false; // Importieren des angegebenen Datenbank-Backups
    private boolean msgNeuExistDb         = false; // Warnung wenn die Datenbank der Schule schon am MySQL-Server besteht
    private boolean msgNeuSchoolExist     = false; // Meldung dass die Schule schon existiert
    private boolean msgLoginFail          = false; // Meldung wenn der MySQL-Login nicht erfolgreich ist
    private boolean viewSchoolData        = false; // Zeigt die Eingabefelder der Schule an

}
