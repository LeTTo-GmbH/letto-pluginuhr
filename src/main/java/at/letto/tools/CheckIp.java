package at.letto.tools;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Einfache Hilfsklasse zum ermitteln ob ein Host online ist
 * @author achristian
 *
 */
public class CheckIp {
	
	/**
	 * Prüft ob ein Host erreichbar ist. 
	 * Achtung: Wenn der Host eine Firewall-Einstellung 
	 * hat die unzulässige-Portanfragen "dropped" dann 
	 * wird der Online-Status des Host nicht erkannt.
	 * 
	 * Ist der Port am Host offen erhält man einen 
	 * repräsentativen Zeitwert in Millisekunden für den 
	 * Verbindungsaufbau (ähnlich der Ping-Zeit).
	 * 
	 * Ist der Port nicht offen liegt die ermittelte Zeit 
	 * meist um 1000ms.
	 * 
	 * Wird die Portanfrage gedropped oder ist der Host
	 * nicht online erhält man -1
	 *  
	 * @param host der getestet werden soll
	 * @return Zeit in Millisekunden
	 */
	public static int isReachable(String host){
		
		boolean DEBUG = false;
		int CHECKPORT = 80;
		
		long start = System.currentTimeMillis();
	    
		try {
		
			Socket socket = new Socket(host, CHECKPORT);
			socket.close();
			
	    } catch (ConnectException e){
	    	
	    	String ex = e.toString();
	    	
	    	if (ex.contains("Connection refused")){
	    		
	    		long end = System.currentTimeMillis()-start;
	    		if (DEBUG)System.out.println("online, indirekt ermittelt");
	    		return (int)end;
	    		
	    	} else {
	    		
	    		if (DEBUG) System.out.println("offline");
	    		return -1;
	    		
	    	}
	    	
	    } catch (UnknownHostException e) {
			
	    	if (DEBUG) System.out.println("offline");
    		return -1;
			
		} catch (IOException e) {
			
			if (DEBUG) System.out.println("offline");
    		return -1;
    		
		}
		
		long end = System.currentTimeMillis()-start;
		if (DEBUG)System.out.println("online");
		return (int)end;
	}
	
	public static void main(String[] args) {
		System.out.println(CheckIp.isReachable("127.0.0.1"));
		System.out.println(CheckIp.isReachable("www.google.de"));
		System.out.println(CheckIp.isReachable("www.dieadressegibtsgarnicht.de"));
		System.out.println(CheckIp.isReachable("193.170.118.80"));
	}
	
}