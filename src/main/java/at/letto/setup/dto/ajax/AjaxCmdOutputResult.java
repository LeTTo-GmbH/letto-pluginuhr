package at.letto.setup.dto.ajax;

import lombok.Data;

@Data
public class AjaxCmdOutputResult {

    private boolean error    = false;
    private boolean finished = false;
    private String htmlThreadStatus = "";
    private String htmlTimeInfo  = "";
    private String htmlOutput = "";

}
