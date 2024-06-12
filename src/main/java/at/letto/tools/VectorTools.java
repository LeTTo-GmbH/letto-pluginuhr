package at.letto.tools;

import java.util.Vector;

public class VectorTools{

	/**
	 * Bestimmt n Zufallszahlen
	 * @param Anz : Anz an Zufallszahlen im Array
	 * @return int-Array von Anz-Zufallszahlen, wobei alle Zahlen von 0-n im Array vorkommen
	 */
	public static int[] getZufallszahlen(int Anz) {
		// Array erstellen
		int[] reihenfolge = new int[Anz];
		
		// Vektor mit allen Zahlenwerten von 0 bis ANz-1 definieren
		Vector<Integer> nr = new Vector<Integer>();
		for (int i = 0; i<Anz; i++) 
			nr.add(i);
		// Alle Elemente des Vektors werden jetzt zufällig in das Array
		// reihenfolge geschrieben
		for (int i = 0; i<Anz; i++) {
			int zufall = random(0,nr.size());
			reihenfolge[i] = nr.get(zufall);
			nr.remove(zufall);
		}
		return reihenfolge;
	}
	/**
	 * Bestimmt n Zufallszahlen sortiert von 0 bis n-1
	 * @param Anz : Anz an Werten im Array
	 * @return int-Array mit Werten von 0 - n-1
	 */
	public static int[] getSortedZahlen(int Anz) {
		// Array erstellen
		int[] reihenfolge = new int[Anz];
		for (int i = 0; i<Anz; i++) 
			reihenfolge[i] = i;
		return reihenfolge;
	}


	/**
	 * Ain Array mit aufsteigenden Zahlenwerten, beginnend bei 0, erzeugen
	 * @param Anz : Azahl an Werten im Array
	 * @return : [0,1,2,3,4....]
	 */
	public static int[] getZahlenAufsteigend(int Anz) {
		// Array erstellen
		int[] reihenfolge = new int[Anz];
		
		for (int i = 0; i<Anz; i++) 
			reihenfolge[i] = i;

		return reihenfolge;
	}

	
	public static int random(int low, int high) {
		return (int) (Math.random() * (high - low) + low);
	}

}
