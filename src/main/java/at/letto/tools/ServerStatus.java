package at.letto.tools;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URL;
import java.security.CodeSource;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Liefert wichtige System-Information
 * @author Werner Damböck
 *
 */
public class ServerStatus {

	public static void main(String[] args) {
		System.out.println(getBetriebssystem());
		System.out.println(getJavaVersion());
		Vector<String> v = Cmd.readfile("/etc/lsb-release");
		System.out.println(v);
	}
	
	/** alle möglichen Positionen, wo nach einer Datei local.info gesucht wird, welche dem System sagt, dass es sich um eine lokale 
	 * Ausführung und nicht um eine Ausführung am Server handelt. 
	 * */
	private static final String localFiles[] = {"C:/workspace-letto/letto/local.info",
			                                    "/opt/letto/local.info",
			                                    "/local.info",
												"C:/git/lettoserver/local.info",
			                                    "C:/programmieren/java/workspace-letto/letto/local.info"};
	
	/** gibt an ob es sich um den Localhost handelt (true), oder auf dem Letto-Server(false) gearbeitet wird */
	public static boolean isLocal = isLocal();
	private static boolean isLocal() {
		for (String filename:localFiles) {
			try {
				File f = new File(filename);
				if (f.exists() && f.isFile()) return true;
			} catch (Exception ex) {};
		}
		return false;
	}
	
	/** Gibt an ob die Anwendung im Debug-Mode läuft. */
	public static final boolean isDebug = isDebugging();
	private static boolean isDebugging() {
		Pattern debugPattern = Pattern.compile("-Xdebug|jdwp");
	    for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
	        if (debugPattern.matcher(arg).find()) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public static boolean isWindows() {
		String bs = System.getProperty("os.name");
		if (bs!=null && bs.toLowerCase().startsWith("windows")) return true;
		return false;
	}
	
	public static boolean isLinux() {
		String bs = System.getProperty("os.name");
		if (bs!=null && bs.toLowerCase().startsWith("linux")) return true;
		return false;
	}
	
	/** @return Liefert den Distributionsnamen der Linux Distribution wenn es sich um eine Linux-System handelt */
	public static String getLinuxDistribution() {
		if (isLinux()) {
			Vector<String> v = Cmd.readfile("/etc/lsb-release");
			for (String s:v) {
				String t[] = s.trim().split("=");
				if (t[0].trim().equals("DISTRIB_ID")) {
					s = t[1].trim();
					if (s.startsWith("\"")) s=s.substring(1);
					if (s.endsWith("\""))   s=s.substring(0,s.length()-1);
					return s;
				}
			}
		}
		return "";
	}
	
	public static String getLinuxRelease() {
		if (isLinux()) {
			Vector<String> v = Cmd.readfile("/etc/lsb-release");
			for (String s:v) {
				String t[] = s.trim().split("=");
				if (t[0].trim().equals("DISTRIB_RELEASE")) {
					s = t[1].trim();
					if (s.startsWith("\"")) s=s.substring(1);
					if (s.endsWith("\""))   s=s.substring(0,s.length()-1);
					return s;
				}
			}
		}
		return "";
	}
	
	public static String getLinuxDescription() {
		if (isLinux()) {
			Vector<String> v = Cmd.readfile("/etc/lsb-release");
			for (String s:v) {
				String t[] = s.trim().split("=");
				if (t[0].trim().equals("DISTRIB_DESCRIPTION")) {
					s = t[1].trim();
					if (s.startsWith("\"")) s=s.substring(1);
					if (s.endsWith("\""))   s=s.substring(0,s.length()-1);
					return s;
				}
			}
		}
		return "";
	}
	
	public static boolean isUbuntu() {
		if (getLinuxDistribution().toLowerCase().trim().startsWith("ubuntu")) return true;
		return false;
	}
	
	public static String getBetriebssystem() { 
		String ver = System.getProperty("os.version");
		if (isWindows()) return "Windows "+ver;
		if (isLinux())   return "Linux Kernel "+ver+" "+getLinuxDescription();
		return System.getProperty("os.name")+" "+ver;
	}
	
	public static String getJavaVendor() {
		String vendor="";
		try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vendor"); } catch (Exception e) {};
		try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vm.vendor"); } catch (Exception e) {};
		try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vm.specification.vendor"); } catch (Exception e) {};
		return vendor;
	}
	
	public static String getJavaVersionNumber() {
		String nr="";
		try { if (nr==null || nr.length()==0) nr = System.getProperty("java.runtime.version"); } catch (Exception e) {};
		try { if (nr==null || nr.length()==0) nr = System.getProperty("java.version"); } catch (Exception e) {};
		return nr;
	}
	
	public static String getJavaVersion() {
		return getJavaVendor() + " " + getJavaVersionNumber();
	}
	
	private static String getTomEEversion() {
		try {
			String ver = System.getProperty("tomee.version");
			if (ver!=null && ver.length()>0) return ver;
		} catch (Exception e) {};
		return "";
	}
	
	private static String getGlassfishVersion() {
		try {
			String ver = System.getProperty("glassfish.version");
			if (ver!=null && ver.length()>0) return ver;
		} catch (Exception e) {};
		return "";
	}
	
	private static String getTomcatVersion() {
		try {
			String ver = System.getProperty("tomcat.version");
			if (ver!=null && ver.length()>0) return ver;
		} catch (Exception e) {};
		return "";
	}
	
	public static String getServerVersion() {
		if (getTomEEversion().length()>0) return "tomEE "+getTomEEversion();
		if (getGlassfishVersion().length()>0) return getGlassfishVersion();
		if (getTomcatVersion().length()>0) return "Tomcat "+getTomcatVersion();
		return "Kein TomEE oder Glassfish Server!";
	}

	/**
	 * Bestimmt den Ort des Klasse der Main-Routine
	 * @param mainClass Klasse welche die main-Routine enthält
	 * @return Pfad der jar-Datei
	 */
	public static String getRootPath(Class mainClass) {
		try {
			CodeSource codeSource = mainClass.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			String jarDir = jarFile.getParentFile().getPath();
			return jarDir;
		} catch (Exception ex) { }
		try {
			File jarDir = new File(".");
			String d = jarDir.getAbsolutePath();
			while (d.endsWith("/.")) d = d.substring(0,d.length()-2);
			while (d.endsWith("\\.")) d = d.substring(0,d.length()-2);
			return d;
		} catch (Exception ex) {}
		return ".";
	}

	public static String getEncoding() {
		return System.getProperty("sun.jnu.encoding");
	}
	
	public static String getFileEncoding() {
		return System.getProperty("file.encoding");
	}
	
	public static String getUserDir() {
		return System.getProperty("user.dir");
	}
	
	public static String getSystemHome() {
		String ret = System.getProperty("derby.system.home");
		if (ret==null) {
			File f = new File(".");
			if (f.exists()) {
				ret = f.getAbsolutePath();
				while (ret.endsWith("/") || ret.endsWith("\\"))    ret = ret.substring(0,ret.length()-1);
				while (ret.endsWith("/.") || ret.endsWith("\\.") ) ret = ret.substring(0,ret.length()-2);
			}
		}
		if (ret==null) {
			try {
				if (isLinux()) ret = Cmd.systemcall("pwd").split("\\r?\\n")[0];
			} catch (Exception ex) {};
		}
		if (ret==null) ret = getUserDir();
		return ret;
	}
	
	public static String getLanguage() {
		return System.getProperty("user.language");
	}
	
	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}
	
	public static String getServerUsername() {
		return System.getProperty("user.name");
	}
	
	public static String getJavaSpecificationVersion() {
		return System.getProperty("java.specification.version");
	}

	public static String getIP() {
		return IP.getLocalIPString();
	}

	public static String getIPs() {
		return IP.getLocalIPsString();
	}

	public static String getHostname() {
		if (isWindows()) return System.getenv("COMPUTERNAME");
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			if (hostname!=null && hostname.length()>0) return hostname;
		} catch (Exception ex) {}
		return System.getenv("HOSTNAME");
	}

	/**
	 * Gibt einen InputStream auf die gewünschte Resource zurück
	 * @param Resource  Pfad der Resource (innerhalb von src/resources)
	 * @return InputStream auf die Resource
	 */
	public static InputStream getResourceAsStream(String Resource) {
		InputStream res;
		res = ServerStatus.class.getResourceAsStream("/"+Resource);
		return res;
	}

	/**
	 * Gibt eine URL auf die gewünschte Resource zurück
	 * @param Resource  Pfad der Resource (innerhalb von src/resources)
	 * @return URL auf die Resource
	 */
	public static URL getResource(String Resource) {
		URL res;
		res = ServerStatus.class.getResource("/"+Resource);
		return res;
	}

	/**
	 * Liefert die Revisionsnummer des Programmes
	 * @return Revisionsnummer
	 */
	public static String getRevision() {
		String s;
		try {
			InputStream res = getResourceAsStream("revision.txt");
			if (res!=null) {
				BufferedReader br=new BufferedReader(new InputStreamReader(res));
				s = br.readLine();
				br.close();
				return s;
			} else {
				return "F001";
			}
		} catch (IOException e) {
			return "F002";
		}
	}

}
