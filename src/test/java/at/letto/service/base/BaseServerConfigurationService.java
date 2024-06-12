package at.letto.service.base;

import at.letto.globalinterfaces.ServerConfigurationService;
import at.letto.tools.Datum;
import at.letto.globalinterfaces.ImageService;
import at.letto.tools.ServerStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class BaseServerConfigurationService implements ServerConfigurationService {

    BaseImageService imageService = null;
    BaseImageService pluginImageService = null;
    BaseImageService fotoImageService = null;

    @Override
    public String Res(String Key) {
        return "";
    }

    @Override public String getOS() {
                return System.getProperties().getProperty("os.name");
        }

        @Override public boolean isLinux() {
            if ( getOS().toLowerCase().matches("linux.*")) return true;
            return false;
        }

        @Override public boolean isWindows()  {
            if ( getOS().toLowerCase().matches("windows.*")) return true;
            return false;
        }

        @Override public boolean isServer() { return false; }

        @Override public String Get(String Key) { return ""; }

        @Override public Vector<String> loadConfigFile(String filename) { return null; }

        @Override public void Msg1(String message) { System.out.println(message); }

        @Override public String getCurrentTime() { return Datum.toString(Datum.now()); }

        @Override public void err(String txt1, String txt2) { System.out.println(txt1+"\n"+txt2); }

        @Override public void warn(String txt1, String txt2) { System.out.println(txt1+"\n"+txt2); }

        @Override public void info(String txt1, String txt2) { System.out.println(txt1+"\n"+txt2); }

        @Override public Vector<String> readResourceFile(String Resource){
            Vector<String> ret = new Vector<String>();
            String s;
            try {
                InputStream res = getResourceAsStream(Resource);
                if (res!=null) {
                    BufferedReader br=new BufferedReader(new InputStreamReader(res));
                    while ((s = br.readLine()) != null) {
                        ret.add(s);
                    }
                    br.close();
                } else {
                    System.out.println("Resource "+Resource+" not found!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        public ImageService getPluginImageService() {
            if (pluginImageService!=null) return pluginImageService;
            try {
                return pluginImageService = new BaseImageService("c:/opt/letto/images/plugins","http://localhost/images/plugins",true);
            } catch (Exception exception) {
                return null;
            }
        }

        @Override
        public ImageService getImageService() {
            if (imageService!=null) return imageService;
            try {
                return imageService = new BaseImageService("c:/opt/letto/images/plugins","http://localhost/images/plugins",true);
            } catch (Exception exception) {
                return null;
            }
        }

        @Override
        public ImageService getFotoImageService() {
            if (fotoImageService!=null) return fotoImageService;
            try {
                return fotoImageService = new BaseImageService("c:/opt/letto/images/photos","http://localhost/images/photos",true);
            } catch (Exception exception) {
                return null;
            }
        }

    @Override
    public String getServerInfo() {
        return  "IP:"+ ServerStatus.getIP()+
                " host:"+ServerStatus.getHostname()+
                " bs:"+ServerStatus.getBetriebssystem()+
                " schule: Unit-Test";
    }

    /**
         * Gibt einen InputStream auf die gewünschte Resource zurück
         * @param Resource  Pfad der Resource (innerhalb von src/resources)
         * @return InputStream auf die Resource
         */
        private InputStream getResourceAsStream(String Resource) {
            InputStream res;
            if (this.isServer()) res = BaseServerConfigurationService.class.getResourceAsStream("/"+Resource);
            else 		  res = BaseServerConfigurationService.class.getResourceAsStream("/"+Resource);
            return res;
        }



    }
