package at.letto.service.rest;

import at.letto.dto.ServiceInfoDTO;
import at.letto.restclient.endpoint.BaseEndpoints;
import at.letto.security.LettoToken;
import at.letto.service.microservice.AdminInfoDto;

public class BaseRestClient extends RestClient {


    /**
     * Erzeugt ein REST-Client Verbindung zu einem Microservice
     *
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091
     */
    public BaseRestClient(String baseURI) {
        super(baseURI);
    }

    public BaseRestClient(String baseURI, String user, String password) {
        super(baseURI,user,password);
    }

    @Override
    public boolean ping() {
        return ping(BaseEndpoints.PING,1000);
    }

    @Override
    public String version() {
        String rev = get(BaseEndpoints.VERSION,String.class);
        return rev;
    }

    @Override
    public String info() {
        String info = get(BaseEndpoints.INFO,String.class);
        return info;
    }

    @Override
    public AdminInfoDto admininfo() {
        ServiceInfoDTO serviceInfoDTO = get(BaseEndpoints.INFO_AUTH_ADMIN,ServiceInfoDTO.class);
        AdminInfoDto adminInfoDto = serviceInfoDTO.getAdminInfoDto();
        return adminInfoDto;
    }

    public ServiceInfoDTO serviceinfo() {
        if (getUser()!=null) {
            try {
                ServiceInfoDTO serviceInfoDTO = get(BaseEndpoints.INFO_AUTH_ADMIN,ServiceInfoDTO.class);
                if (serviceInfoDTO!=null) return serviceInfoDTO;
            } catch (Exception ex) {}
        }
        ServiceInfoDTO serviceInfoDTO = get(BaseEndpoints.INFO,ServiceInfoDTO.class);
        return serviceInfoDTO;
    }

    public ServiceInfoDTO serviceinfoopen() {
        ServiceInfoDTO serviceInfoDTO = get(BaseEndpoints.INFO_OPEN,ServiceInfoDTO.class);
        return serviceInfoDTO;
    }

    public AdminInfoDto admininfoauth() {
        ServiceInfoDTO serviceInfoDTO = get(BaseEndpoints.INFO_AUTH_ADMIN,ServiceInfoDTO.class);
        AdminInfoDto adminInfoDto = serviceInfoDTO.getAdminInfoDto();
        return adminInfoDto;
    }


    public AdminInfoDto admininfo(LettoToken lettoToken) {
        ServiceInfoDTO serviceInfoDTO = get(BaseEndpoints.INFO_API_ADMIN,ServiceInfoDTO.class,lettoToken);
        AdminInfoDto adminInfoDto = serviceInfoDTO.getAdminInfoDto();
        return adminInfoDto;
    }

}
