package at.letto.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * einfache Routinen um mit regular Expressions einfach umgehen zu können
 * @author damboeck
 *
 */
public class RegExp {

	/**
	 * Ergebnis der letzten Regexp-Suche
	 */
	//public static MatchResult result;
		
	/**
	 * sucht alle Treffer vom Regex "pattern" im String s und gibt das 
	 * Suchergebnis zurück
	 * @param pattern Regexp
	 * @param s       String 
	 * @return        gefundene Treffer
	 */
	public static Iterable<MatchResult> findMatches( String pattern, CharSequence s ) { 
	  List<MatchResult> results = new ArrayList<MatchResult>(); 
	  for ( Matcher m = Pattern.compile(pattern,Pattern.DOTALL|Pattern.MULTILINE).matcher(s); m.find(); ) 
	    results.add( m.toMatchResult() ); 
	  
	  return results; 
	}
	
	/**
	 * sucht den ersten Treffer vom Regex "pattern" im String s und gibt das 
	 * Suchergebnis zurück
	 * @param pattern Regexp
	 * @param s       String 
	 * @return        gefundene Treffer
	 */
	public static MatchResult findMatchFirst( String pattern, CharSequence s ) { 
	  Matcher m = Pattern.compile(pattern,Pattern.DOTALL|Pattern.MULTILINE).matcher(s); 
	  if (m.find()) return m.toMatchResult();
	  return null;
	}
		
	/**
	 * ersetzt den ersten Treffer
	 * @param pattern    Regexp welcher eine runde Klammer für den Teil der ersetzt wird haben muss
	 * @param s          String in den gesucht wird
	 * @param replace    String welcher statt der gesuchten Elemente eingesetzt wird als normaler String(nicht Regexp!)
	 * @return           String mit dem ersetzten Teil
	 */
	public static String replaceFirst(String pattern, String s, String replace) {
		String ret = "";
		int p = 0;
		pattern = pattern.replaceFirst("\\(", "\\)\\(");pattern = "("+pattern;
		
		Matcher m = Pattern.compile(pattern,Pattern.DOTALL|Pattern.MULTILINE).matcher(s);
		if (m.find()) {
			MatchResult r = m.toMatchResult();
			ret += s.substring(p,r.start());			
			try {
				ret += r.group(1) + replace + r.group(0).substring(r.group(1).length()+r.group(2).length());				
			} catch (IndexOutOfBoundsException e) {
				ret += r.group(0);
			}
			p = r.end();
		}		
		ret += s.substring(p);
		return ret;
	}
	
	/**
	 * ersetzt rekursiv alle Treffer
	 * @param pattern    Regexp welcher eine runde Klammer für den Teil der ersetzt wird haben muss
	 * @param s          String in den gesucht wird
	 * @param replace    String welcher statt der gesuchten Elemente eingesetzt wird als normaler String(nicht Regexp!)
	 * @return           String mit dem ersetzten Teil
	 */
	public static String replaceAll(String pattern, String s, String replace) {
		String ret = "";
		boolean found;
		int pos = 0;
		pattern = pattern.replaceFirst("\\(", "\\)\\(");pattern = "("+pattern;
		Pattern p = Pattern.compile(pattern,Pattern.DOTALL|Pattern.MULTILINE);
		
		do {
			pos=0;	
			ret = "";
			Matcher m = p.matcher(s);
			found = m.find();
			if (found) {
				MatchResult r = m.toMatchResult();
				ret += s.substring(pos,r.start());			
				try {
					ret += r.group(1) + replace + r.group(0).substring(r.group(1).length()+r.group(2).length());				
				} catch (IndexOutOfBoundsException e) {
					ret += r.group(0);
					ret += s.substring(pos);
					return (ret);
				}
				pos = r.end();
			}		
			s = ret + s.substring(pos);
		} while (found);
		return s;
	}
	
	/**
	 * verblockt alle Sonderzeichen in dem String s um dann mit einem Regexp danach suchen zu können
	 * @param s  String der verblockt wird
	 * @return   verblockter String
	 */
	public static String verblockeString(String s) {
		String vbz[] = {".","+","-","[","(",")","]","{","}","?","/","$","^","*","|",};
		s = s.replaceAll("\\\\","\\\\\\\\");
		for (String z:vbz) s = s.replaceAll("\\"+z,"\\\\\\"+z); 
		return s;		
	}
		
}
