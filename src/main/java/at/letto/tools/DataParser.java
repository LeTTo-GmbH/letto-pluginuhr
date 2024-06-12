package at.letto.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.MatchResult;

/**
 * Konvertierroutinen für Strings
 * @author Werner
 *
 */
public class DataParser {
	/**
	 * Wandelt den String s in eine Long-Zahl, bei einem Fehler wird errorNo 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @param errorNo   Zahl welche im Fehlerfall zurückgegeben wird
	 * @return          den geparsten String als Long Zahl
	 */
	public static long parseLong(String s,long errorNo) {
		long   ret=0;
		try { 
			ret = Long.parseLong(s);
		} catch(NumberFormatException e) {ret=errorNo;}
		return ret;		
	}
	/**
	 * Wandelt den String s in eine Long-Zahl, bei einem Fehler wird -1 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Long Zahl
	 */
	public static long parseLongM1(String s) {
		return parseLong(s,-1);
	}
	/**
	 * Wandelt den String s in eine Long-Zahl, bei einem Fehler wird 0 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Long Zahl
	 */
	public static long parseLong(String s) {
		return parseLong(s,0);
	}
	/**
	 * Wandelt den String s in eine Integer-Zahl, bei einem Fehler wird errorNo 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @param errorNo   Zahl welche im Fehlerfall zurückgegeben wird
	 * @return          den geparsten String als Integer Zahl
	 */
	public static int parseInt(String s,int errorNo) {
		int   ret=0;
		try { 
			ret = Integer.parseInt(s);
		} catch(NumberFormatException e) {ret=errorNo;}
		return ret;		
	}
	/**
	 * Wandelt den String s in eine Integer-Zahl, bei einem Fehler wird -1 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Integer Zahl
	 */
	public static int parseIntM1(String s) {
		return parseInt(s,-1);
	}
	/**
	 * Wandelt den String s in eine Integer-Zahl, bei einem Fehler wird 0 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Integer Zahl
	 */
	public static int parseInt(String s) {
		return parseInt(s,0);
	}
	/**
	 * Wandelt den String s in eine Double-Zahl, bei einem Fehler wird errorNo 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @param errorNo   Zahl welche im Fehlerfall zurückgegeben wird
	 * @return          den geparsten String als Double Zahl
	 */
	public static double parseDouble(String s,double errorNo) {
		double   ret=0;
		try { 
			ret = Double.parseDouble(s);
		} catch(NumberFormatException e) {ret=errorNo;}
		return ret;		
	}
	/**
	 * Wandelt den String s in eine Double-Zahl, bei einem Fehler wird 0 
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Double Zahl
	 */
	public static double parseDouble(String s) {
		return parseDouble(s,0);
	}
	/**
	 * erzeugt ein neues Date-Objekt
	 * @param y  Jahr vierstellig
	 * @param m  Monat 1..12
	 * @param d  Tag   1..xx
	 * @return erzeugt ein neues Date-Objekt
	 */
	public static Date getDate(int y,int m, int d){
		return new GregorianCalendar(y, m-1, d).getTime();
	}
	/**
	 * erzeugt ein neues Date-Objekt
	 * @param y  Jahr vierstellig
	 * @param m  Monat 1..12
	 * @param d  Tag   1..xx
	 * @return erzeugt ein neues Date-Objekt
	 */
	public static Date getDate(String y, String m, String d) {
		int iy = parseInt(y,1900);
		int im = parseInt(m,1);
		int id = parseInt(d,1);
		return getDate(iy,im,id);
	}
	/**
	 * erzeugt ein neues Date-Objekt
	 * @param y  Jahr vierstellig
	 * @param m  Monat 1..12
	 * @param d  Tag   1..xx
	 * @param hour Stunde
	 * @param min  Minute
	 * @param sec  Sekunde
	 * @return erzeugt ein neues Date-Objekt
	 */
	public static Date getDate(int y, int m, int d, int hour, int min, int sec) {
		Calendar c = new GregorianCalendar(y, m-1, d);
		c.set(Calendar.MINUTE,min);
		c.set(Calendar.HOUR  ,hour);
		c.set(Calendar.SECOND,sec);
		return c.getTime();
	}
	/**
	 * erzeugt ein neues Date-Objekt
	 * @param y  Jahr vierstellig
	 * @param m  Monat 1..12
	 * @param d  Tag   1..xx
	 * @param hour Stunde
	 * @param min  Minute
	 * @param sec  Sekunde
	 * @return erzeugt ein neues Date-Objekt
	 */
	public static Date getDate(String y, String m, String d, String hour, String min, String sec) {
		int iy = parseInt(y,1900);
		int im = parseInt(m,1);
		int id = parseInt(d,1);
		int ih = parseInt(hour);
		int imin=parseInt(min);
		int isec=parseInt(sec);
		return getDate(iy,im,id,ih,imin,isec);
	}
	/**
	 * Wandelt den String s in ein Datum, bei einem Fehler wird errorDate
	 * zurückgegeben
	 * @param s         String der konvertiert wird
	 * @param errorDate  Rückgabewert bei Fehler
	 * @return          den geparsten String als Long Zahl
	 */
	public static Date parseDate(String s,Date errorDate) {
		String p;
		p = "(\\d+)[\\-\\.](\\d+)[\\-\\.](\\d\\d\\d\\d) (\\d+):(\\d+):(\\d+)";
		for (MatchResult m:RegExp.findMatches(p, s)) 
			return getDate(m.group(3),m.group(2),m.group(1),m.group(4),m.group(5),m.group(6));
		p = "(\\d\\d\\d\\d)[\\-\\.](\\d+)[\\-\\.](\\d+) (\\d+):(\\d+):(\\d+)";
		for (MatchResult m:RegExp.findMatches(p, s)) 
			return getDate(m.group(1),m.group(2),m.group(3),m.group(4),m.group(5),m.group(6));
		p = "(\\d+)[\\-\\.](\\d+)[\\-\\.](\\d\\d\\d\\d)";
		for (MatchResult m:RegExp.findMatches(p, s)) 
			return getDate(m.group(3),m.group(2),m.group(1));
		p = "(\\d\\d\\d\\d)[\\-\\.](\\d+)[\\-\\.](\\d+)";
		for (MatchResult m:RegExp.findMatches(p, s)) 
			return getDate(m.group(1),m.group(2),m.group(3));
		return errorDate;
	}
	/**
	 * Wandelt den String s in ein Datum, bei einem Fehler wird das 
	 * aktuelle Datum zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Long Zahl
	 */
	public static Date parseDateAkt(String s) {
		return parseDate(s,new Date());
	}
	
	/**
	 * Wandelt den String s in ein Datum, bei einem Fehler wird das 
	 * der 1.1.1900 zurückgegeben
	 * @param s         String der konvertiert wird
	 * @return          den geparsten String als Long Zahl
	 */
	public static Date parseDate(String s) {
		return parseDate(s,new GregorianCalendar(1900, 0, 1).getTime());
	}
	/**
	 * erzeugt aus den vier Stellen einer IP-Adresse die 32-Bit IP
	 * @param ip1  erste Stelle der IP
	 * @param ip2  zweite Stelle der IP
	 * @param ip3  dritte Stelle der IP
	 * @param ip4  vierte Stelle der IP
	 * @return     32 Bit IP-Adresse
	 */
	public static long parseIP(long ip1, long ip2, long ip3, long ip4) {
		return ((ip1&0xFF)<<24)|((ip2&0xFF)<<16)|((ip3&0xFF)<<8)|(ip4&0xFF);
	}
	
	/**
	 * Erzeugt aus einem String, welcher eine IP-Adresse beinhält eine 32-Bit IP-Adresse
	 * @param s   String der Adresse
	 * @return    32-Bit Adresse
	 */
	public static long parseIP(String s) {
		String ip[] = s.split("\\.");
		long   ipn[] = new long[4];
		for (int i=0;i<4;i++) ipn[i] = Long.parseLong(ip[i]);
		return ((ipn[0]&0xFF)<<24)|((ipn[1]&0xFF)<<16)|((ipn[2]&0xFF)<<8)|(ipn[3]&0xFF);
	}
	/**
	 * Wandelt eine 32-Bit IP-Adresse in einen String mit der IP-Adresse
	 * @param ip  32-Bit Adresse
	 * @return    IP-Adress-String
	 */
	public static String IPtoString(long ip) {
		return ((ip>>24)&0xFF)+"."+((ip>>16)&0xFF)+"."+((ip>>8)&0xFF)+"."+(ip&0xFF);
	}

	
	
}
