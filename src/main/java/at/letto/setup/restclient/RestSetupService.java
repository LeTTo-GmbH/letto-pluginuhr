package at.letto.setup.restclient;

import at.letto.service.microservice.AdminInfoDto;
import at.letto.service.rest.RestClient;
import at.letto.setup.dto.*;
import at.letto.setup.dto.config.*;
import at.letto.setup.endpoints.SetupEndpoint;

import java.util.List;

public class RestSetupService extends RestClient implements SetupService {

    /**
     * Erzeugt ein REST-Client Verbindung zu einem Microservice
     *
     * @param baseURI  Basis-URI des Microservices zb: https://localhost:9091
     */
    public RestSetupService(String baseURI) {
        super(baseURI);
    }

    public RestSetupService(String baseURI, String user, String password) {
        super(baseURI,user,password);
    }

    @Override
    public boolean ping() {
        return ping(SetupEndpoint.DOCKER_ping, 1000);
    }

    public boolean pingDocker() {
        return ping(SetupEndpoint.DOCKER_ping, 1000);
    }

    public boolean pingHost() {
        return ping(SetupEndpoint.LOCAL_ping, 1000);
    }

    public boolean pingUserLocal() {
        String result = get(SetupEndpoint.LOCAL_user_ping,String.class);
        if (result.equals("pong")) return true;
        return false;
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

    public String deactivatemain() {
        String result = get(SetupEndpoint.deactivatemain,String.class);
        return result;
    }

    /** Liefert alle Schulen die an dem Server installiert sind */
    @Override
    public List<ServiceSchuleDto> getSchulen() {
        ServiceSchulenListDto schulen = get(SetupEndpoint.getSchulen, ServiceSchulenListDto.class);
        List<ServiceSchuleDto> ret = schulen.getSchulen();
        return ret;
    }

    /**
     * @param school Schul-Kurzname
     * @return       Liefert die Schul-Konfiguration, wenn sie existiert
     */
    @Override
    public ServiceSchuleDto getSchule(String school) {
        ServiceSchuleDto schule = post(SetupEndpoint.postSchule,school,ServiceSchuleDto.class);
        return schule;
    }

    @Override
    public String getRestKey() {
        String restKey = get(SetupEndpoint.getRestKey,String.class);
        return restKey;
    }

    @Override
    public String setSchoolLicense(String school, String restkey, String license) {
        SchoolLicenseDto dto = new SchoolLicenseDto(school, restkey, license, "save");
        String result = post(SetupEndpoint.setSchoolLicense,dto,String.class);
        return result;
    }

    /**
     * Liefert den Servicestatus der verbundenen Services am Server
     */
    @Override
    public ServiceStatusDto checkServiceStatus() {
        ServiceStatusDto serviceStatusDto = post(SetupEndpoint.checkServiceStatus,ServiceStatusDto.class);
        return serviceStatusDto;
    }

    /**
     * Liefert den Servicestatus der verbundenen Services wenn das Setup-Service am Host läuft
     */
    @Override
    public ServiceStatusDto checkServiceStatusLocal() {
        ServiceStatusDto serviceStatusDto = post(SetupEndpoint.checkServiceStatusLocal,ServiceStatusDto.class);
        return serviceStatusDto;
    }

    /** Liefert die Version des Containers mit der angegebenen URI*/
    public String getContainerVersion(String uri) {
        String version = post(SetupEndpoint.getContainerVersion, uri, String.class);
        return version;
    }

    /** @return Liefert eine Liste aller am Server registrierten Plugins */
    @Override
    public ConfigServicesDto getPlugins(){
        ConfigServicesDto plugins = get(SetupEndpoint.getPlugins, ConfigServicesDto.class);
        return plugins;
    }

    /** @return Liefert eine Liste aller am Server registrierten Services */
    @Override
    public ConfigServicesDto getServices() {
        ConfigServicesDto services = get(SetupEndpoint.getServices, ConfigServicesDto.class);
        return services;
    }

    /** registriert ein Plugin am Setup-Service */
    @Override
    public RegisterServiceResultDto registerPlugin(ConfigServiceDto configPluginDto){
        RegisterServiceResultDto result = post(SetupEndpoint.registerPlugin, configPluginDto, RegisterServiceResultDto.class);
        return result;
    }

    /**
     * registriert ein Plugin am Setup-Service
     *
     * @param configServiceDto
     */
    @Override
    public RegisterServiceResultDto registerService(ConfigServiceDto configServiceDto) {
        RegisterServiceResultDto result = post(SetupEndpoint.registerService, configServiceDto, RegisterServiceResultDto.class);
        return result;
    }

    /**
     * @param service
     * @return Liefert die Daten des angeforderten Services
     */
    @Override
    public ConfigServiceDto getService(String service) {
        ConfigServiceDto result = post(SetupEndpoint.getService, service, ConfigServiceDto.class);
        return result;
    }

    /**
     * @param schule
     * @return Liefert die notwendigen Verbindungsinformationen zu einer Schule
     */
    @Override
    public ConfigServiceDto getSchuleService(String schule) {
        ConfigServiceDto result = post(SetupEndpoint.getSchuleService, schule, ConfigServiceDto.class);
        return result;
    }

    /**
     * Überprüft Benutzername und Passwort direkt am Setup-Service für Benutzer
     * die direkt über das Setup-Service verwaltet werden (admin)
     * @param username Benutzername
     * @param password Passwort
     * @return         Token-String oder Leerstring
     */
    public String checkPassword(String username, String password) {
        AuthenticationRequestDto request = new AuthenticationRequestDto(username, password);
        String response = post(SetupEndpoint.checkPassword,request,String.class);
        return response==null?"":response.trim();
    }

}