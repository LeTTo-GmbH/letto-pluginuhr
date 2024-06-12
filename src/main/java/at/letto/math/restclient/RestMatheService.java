package at.letto.math.restclient;

import at.letto.math.endpoints.MatheEndpoint;
import at.letto.service.microservice.AdminInfoDto;
import at.letto.service.rest.RestClient;

/**
 * Verbindung zum Mathematik Service für Berechungen mit Parser und Maxima
 */
public class RestMatheService extends RestClient implements MatheService {

    /**
     * Erzeugt ein REST-Client Verbindung zu einem Microservice
     *
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091
     */
    public RestMatheService(String baseURI) {
        super(baseURI);
    }

    public RestMatheService(String baseURI, String user, String password) {
        super(baseURI,user,password);
    }

    @Override
    public boolean ping() {
        return ping(MatheEndpoint.ping);
    }

    @Override
    public String version() {
        String rev = get(MatheEndpoint.version,String.class);
        return rev;
    }

    @Override
    public String info() {
        String rev = get(MatheEndpoint.info,String.class);
        return rev;
    }

    @Override
    public AdminInfoDto admininfo() {
        AdminInfoDto rev = get(MatheEndpoint.admininfo,AdminInfoDto.class);
        return rev;
    }


}
