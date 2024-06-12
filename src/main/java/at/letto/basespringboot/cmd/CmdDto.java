package at.letto.basespringboot.cmd;

import lombok.Data;

@Data
public class CmdDto {

    private String userAction;
    private String cmd;
    private String cmdline;
    private String homedir;
    private String backlink;
    private long   id;

}
