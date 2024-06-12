package at.letto.dto;

import lombok.Getter;
import lombok.Setter;


public class RestDTO {

    @Getter
    /** Ergebnis der Abfrage wenn das DTO als Rückgabeverwendet wird */
    protected RestStatusDTO reststatus = new RestStatusDTO(RestStatus.UNDEFINED,"",0);;

    @Setter @Getter
    /** Restkey für die Authentifizierung einer Rest-Anfrage */
    private String restkey="";

    @Setter @Getter
    /** true wenn der Datensatz erzeugt werden soll, wenn er nicht gefunden wurde */
    private boolean create=false;

    public RestDTO() {
        setReststatus(RestStatus.UNDEFINED,"",0);
    }

    public RestDTO(String restkey) {
        this();
        this.restkey = restkey;
    }

    public void reststatusOK() {
        this.setReststatus(new RestStatusDTO(RestStatus.OK,"",1));
    }

    public void setReststatus(RestStatusDTO reststatus) {
        this.reststatus = reststatus;
    }

    public void setReststatus(RestStatus reststatus, String message, int count) {
        this.reststatus = new RestStatusDTO(reststatus, message, count);
    }

    public void setReststatus(RestStatus reststatus, String message) {
        int count = 0;
        if (reststatus==RestStatus.UPDATED || reststatus==RestStatus.OK) count = 1;
        this.reststatus = new RestStatusDTO(reststatus, message, count);
    }

    /** @return Statu-Enum der Übertragung */
    public RestStatus status() {
        return this.getReststatus().getReststatus();
    }

    /** @return Status-Meldung der Übertragung */
    public String message() {
        return this.getReststatus().getMessage();
    }

}
