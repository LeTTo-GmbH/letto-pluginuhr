package at.letto.setup.dto.ajax;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AjaxWelcomeInfo {

    @Data @AllArgsConstructor public static class AjaxServiceInfo{
        private String servicename="";
        private String htmlInfo="";
        private String rev="";
    }

    @Data @AllArgsConstructor public static class AjaxWelcomeSchuleInfo{
        private String school="";
        private String htmlInfoSchule="";
        private String htmlLicenseInfo="";
        private String htmlLettoRevision="";
        private String htmlDataRevision="";
        private String htmlLettoCpu="";
        private String htmlLettoMem="";
        private String htmlDataCpu="";
        private String htmlDataMem="";
    }

    private String htmlRevision = "";
    private String htmlAnalyzeInfo = "";
    private String htmlLizenzserver = "";
    private String htmlDiskUsage = "";
    private String htmlJavaMemory = "";
    private String htmlCpuUsage = "";
    private String htmlMemory = "";
    private String htmlSwap = "";
    private String htmlServices = "";
    private String htmlRunningTasks="";
    private String htmlInfoDockerPfade="";
    private String htmlInfoMysqlDocker="";
    private String revLettoMysql="";
    private String htmlInfoSetupDocker="";
    private String revSetupDocker="";
    private String htmlInfoLettoDocker="";
    private String revLettoDocker="";
    private List<AjaxServiceInfo> services = new ArrayList<>();
    private String htmlInfoHttpsCertificate="";
    private String htmlInfoMysqlDatabase="";
    private String htmlInfoMysqlLti="";
    private String htmlInfoLettoProxy="";
    private String htmlInfoRedirection="";
    private List<AjaxWelcomeSchuleInfo> schulen = new ArrayList<>();
    private String servertime="";

}
