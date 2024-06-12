package at.open.letto.plugin.controller;

import at.letto.basespringboot.controller.BaseInfoController;
import at.letto.dto.ServiceInfoDTO;
import at.letto.restclient.endpoint.InfoControllerInterface;
import at.letto.service.microservice.AdminInfoDto;
import at.open.letto.plugin.PluginApplication;
import at.open.letto.plugin.config.TomcatConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.jar.Manifest;

@Service
public class InfoControllerImplementation implements InfoControllerInterface {

    @Autowired private BaseInfoController infoController;
    @Autowired private TomcatConfiguration tomcatConfiguration;

    @PostConstruct
    private void init() {
        infoController.setInfoControllerInterface(this);
    }

    @Override
    public void setInfo(ServiceInfoDTO serviceInfoDTO, boolean admin) {
        serviceInfoDTO.setServiceName("pluginservice");
        serviceInfoDTO.setAuthor("Werner Damböck");
    }

    @Override
    public void setInfo(AdminInfoDto adminInfoDto) {
        adminInfoDto.setHttpPort(tomcatConfiguration.getHttpPort());
        adminInfoDto.setAjpPort(tomcatConfiguration.getAjpPort());
    }

    public ApplicationContext getContext() {
        return PluginApplication.context;
    }

    public Manifest getManifest() {
        return PluginApplication.manifest;
    }

    @Override
    public Class getMainClass() {
        return PluginApplication.class;
    }


}
