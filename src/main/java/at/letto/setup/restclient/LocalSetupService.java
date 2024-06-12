package at.letto.setup.restclient;

import at.letto.setup.dto.CmdResultDto;
import at.letto.setup.dto.CommandDto;

public interface LocalSetupService {

    boolean         ping();
    CmdResultDto    callCommand(CommandDto dto);
    String          setupDockerStart();
    String          setupDockerRestart();
    String          setupDockerStop();
    String          setupDockerUpdate();
    String          lettoDockerStart();
    String          lettoDockerRestart();
    String          lettoDockerStop();
    String          lettoDockerUpdate();

}
