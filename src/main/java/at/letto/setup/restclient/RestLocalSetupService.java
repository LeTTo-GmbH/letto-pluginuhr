package at.letto.setup.restclient;

import at.letto.service.microservice.AdminInfoDto;
import at.letto.service.rest.RestClient;
import at.letto.setup.dto.CmdResultDto;
import at.letto.setup.dto.CommandDto;
import at.letto.setup.endpoints.SetupEndpoint;

public class RestLocalSetupService  extends RestClient implements LocalSetupService {

    /**
     * Erzeugt ein REST-Client Verbindung zu einem Microservice
     *
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091
     */
    public RestLocalSetupService(String baseURI) {
        super(baseURI);
    }

    public RestLocalSetupService(String baseURI, String user, String password) {
        super(baseURI,user,password);
    }

    @Override
    public boolean         ping(){
        String rev = get(SetupEndpoint.LOCAL_ping,String.class);
        return rev!=null && rev.equals("pong");
    }

    @Override
    public String version() {
        String rev = get(SetupEndpoint.version,String.class);
        return rev;
    }

    @Override
    public String info() {
        String rev = get(SetupEndpoint.info,String.class);
        return rev;
    }

    @Override
    public AdminInfoDto admininfo() {
        AdminInfoDto rev = get(SetupEndpoint.admininfo,AdminInfoDto.class);
        return rev;
    }

    @Override
    public CmdResultDto    callCommand(CommandDto dto) {
        CmdResultDto result = post(SetupEndpoint.LOCAL_cmd,dto,CmdResultDto.class);
        return result;
    }

    @Override
    public String          setupDockerStart(){
        String rev = get(SetupEndpoint.LOCAL_setupDockerStart,String.class);
        return rev;
    }

    @Override
    public String          setupDockerRestart(){
        String rev = get(SetupEndpoint.LOCAL_setupDockerRestart,String.class);
        return rev;
    }

    @Override
    public String          setupDockerStop(){
        String rev = get(SetupEndpoint.LOCAL_setupDockerStop,String.class);
        return rev;
    }

    @Override
    public String          setupDockerUpdate(){
        String rev = get(SetupEndpoint.LOCAL_setupDockerUpdate,String.class);
        return rev;
    }

    @Override
    public String          lettoDockerStart(){
        String rev = get(SetupEndpoint.LOCAL_lettoDockerStart,String.class);
        return rev;
    }

    @Override
    public String          lettoDockerRestart(){
        String rev = get(SetupEndpoint.LOCAL_lettoDockerRestart,String.class);
        return rev;
    }

    @Override
    public String          lettoDockerStop(){
        String rev = get(SetupEndpoint.LOCAL_lettoDockerStop,String.class);
        return rev;
    }

    @Override
    public String          lettoDockerUpdate(){
        String rev = get(SetupEndpoint.LOCAL_lettoDockerUpdate,String.class);
        return rev;
    }

}
