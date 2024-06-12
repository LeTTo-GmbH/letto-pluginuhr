package at.letto.tools;

import at.letto.tools.threads.CallAdapter;
import at.letto.tools.threads.TimerCall;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import java.net.URL;
import java.util.Vector;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class WebGet {

	/**
	 * Liest eine Seite von einer URL ein und bricht nach einem vorgegebenen Timeout automatisch ab
	 * @param url       URL die Eingelesen werden soll
	 * @param timeoutms Timeout nachdem der Ladevorgang abgebrochen werden soll
	 * @return          Inhalt der URL zeilenweise als Vektor oder ein Vector mit Länge 0 wenn die Seite nicht erreichbar ist.
	 */
	public static Vector<String> readURLTimeout(String url, int timeoutms) {
		TimerCall.CallResult ret = TimerCall.callMethode(new CallAdapter() {
			String url="";
			 @Override
			 public Object callMethode(Object ... objects) {
			 	 url = (String)objects[0];
				 Vector<String> ret = readURL(url);
				 return ret;
			 }
			 @Override public String getMethodeName() { return "readURLTimeout"; }
			 @Override public String getMethodeInfo() { return  url; }
		}, timeoutms, url );
		if (ret.status== TimerCall.RESULT.OK) {
			try {
				return (Vector<String>) (ret.getResult());
			} catch (Exception ex) {};
		} else {
			System.out.println(url+" kann nicht gelesen werden! Result:"+ret.status+" "+ret.toString());
		}
		return new Vector<String>();
	}

	public static Vector<String> readURL(String url) {
		URL server;
		Vector<String> ret = new Vector<String>();
		try {
			server = new URL(url);
			BufferedReader in = new BufferedReader(
	        new InputStreamReader(server.openStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
				ret.add(inputLine);
				if (Thread.interrupted()) {
					in.close();
					return ret;
				}
			}
	        in.close();
	        return ret;
		} catch (Exception e) { }
		return ret;
	}

	/** Lädt eine Datei von einer Url und speichert sie in der angegebenen Datei
	 * @param url    URL der Datei
	 * @param file   Zieldatei
	 * @return       true wenn alles funktioniert hat, sonst false
	 */
	/*public static boolean saveUrlToFile(String url, File file) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection)new URL(url).openConnection();
			try {
				connection = (HttpsURLConnection)new URL(connection.getHeaderField("location")).openConnection();
			} catch (Exception ex) {}
			try {
				connection = (HttpsURLConnection)new URL(connection.getHeaderField("location")).openConnection();
			} catch (Exception ex) {}
			try {
				if (connection.getURL().toString().startsWith("https:")) {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
					connection.setSSLSocketFactory(sc.getSocketFactory());
					connection.setHostnameVerifier(new TrustAllHostnameVerifier());
				}
			} catch (Exception ex) {}
			Files.copy(connection.getInputStream(),file.toPath(),REPLACE_EXISTING);
			return true;
		} catch (IOException e) { }
		return false;
	}*/

	/** Lädt eine Datei von einer Url und speichert sie in einem String
	 * @param url    URL der Datei
	 * @return       Inhalt der Datei
	 */
	/*public static String saveUrlToString(String url) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection)new URL(url).openConnection();
			try {
				connection = (HttpsURLConnection)new URL(connection.getHeaderField("location")).openConnection();
			} catch (Exception ex) {}
			try {
				connection = (HttpsURLConnection)new URL(connection.getHeaderField("location")).openConnection();
			} catch (Exception ex) {}
			try {
				if (connection.getURL().toString().startsWith("https:")) {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
					connection.setSSLSocketFactory(sc.getSocketFactory());
					connection.setHostnameVerifier(new TrustAllHostnameVerifier());
				}
			} catch (Exception ex) {}
			// Lade die URL in ein Byte-Array
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			// new BufferedInputStream(new URL(webPath).openStream());
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				buffer.write(dataBuffer, 0, bytesRead);
			}
			buffer.flush();
			byte[] b = buffer.toByteArray();
			String s = new String(b);
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}*/

	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAllHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * Liefert einen Buffered InputStream, welcher von einer URL-liest
	 * @param webPath          URL von der gelesen werden soll
	 * @return                 BufferedInputStream
	 * @throws IOException     Fehlermeldung wenn nicht verbunden werden kann
	 * @throws NoSuchAlgorithmException Fehlermeldung wenn nicht verbunden werden kann
	 * @throws KeyManagementException   Fehlermeldung wenn nicht verbunden werden kann
	 */
	public static BufferedInputStream getBufferedInputStreamFromURL(String webPath) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		URL url = new URL(webPath);
		if (webPath.startsWith("https:")) {
			HttpsURLConnection conn = null;
			conn = (HttpsURLConnection) url.openConnection();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAllHostnameVerifier());
			return new BufferedInputStream(conn.getInputStream());
		} else {
			return new BufferedInputStream(url.openStream());
		}
	}

	/**
	 * Liest eine URL direkt in eine Datei ein ohne dabei unnötig viel Speicher zu verschwenden
	 * @param webPath URL von der gelesen werden soll
	 * @param file    ZielDatei
	 * @return        true wenn alles Ok war
	 */
	public static boolean readFileFromURL(String webPath, File file) {
		FileOutputStream outs = null;
		try {
			BufferedInputStream ins = getBufferedInputStreamFromURL(webPath);
			outs = new FileOutputStream(file);
			IOUtils.copy(ins, outs);
			IOUtils.closeQuietly(ins);
			IOUtils.closeQuietly(outs);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}

	public static byte[] getUrlByteArray(String webPath) {
		try {
			// Lade die URL in ein Byte-Array
			BufferedInputStream in = getBufferedInputStreamFromURL(webPath);
					// new BufferedInputStream(new URL(webPath).openStream());
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				buffer.write(dataBuffer, 0, bytesRead);
			}
			buffer.flush();
			return buffer.toByteArray();
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return new byte[0];
	}

	public static String getUrlString(String webPath) {
		try {
			byte[] buffer = getUrlByteArray(webPath);
			String inhalt = new String(buffer);
			return inhalt;
		} catch (Exception ignored) {}
		return "";
	}

	public static Vector<String> getUrlStringVector(String webPath) {
		Vector<String> ret = new Vector<String>();
		try {
			String[] strings = getUrlString(webPath).split("\\n");
			for (String s:strings) ret.add(s.replaceAll("\\r",""));
		} catch (Exception ignored) {}
		return ret;
	}

	public static void main(String[] args) {
		System.out.print("Start");
		Vector<String> inhalt = WebGet.readURLTimeout("http://localhost/images/plugins/checkfs.txt",1500);
		System.out.println("\nInhalt:"+inhalt);
	}

}
