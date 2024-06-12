package at.letto.tools;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Meldungsfenster welches für den Zeitvertreib während langer Aktionen (Setup) geöffnet wird.
 * @author damboeck
 *
 */
public class MessageFrame extends Frame implements WindowListener {
	
	private static final long serialVersionUID = 1L;

	private Vector<String> msg;
	
	/**
	 * Meldungsfenster erzeugen
	 * @param titel Titel des Fensters
	 */
	public MessageFrame(String titel) {
		msg = new Vector<String>();
		this.setTitle(titel);
		this.setSize(800, 600);
		this.setVisible(true);		
	}
	
	/**
	 * Meldungsfenster erzeugen
	 * @param titel Titel des Fensters
	 * @param sizex Größe x
	 * @param sizey Größe y
	 */
	public MessageFrame(String titel,int sizex, int sizey) {
		msg = new Vector<String>();
		this.setTitle(titel);
		this.setSize(sizex, sizey);
		this.setVisible(true);		
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		int i=0;
		for (String s:msg) {
			g.drawString(s, 30, 50 + i*20);
			i++;
		}
	}
	
	/**
	 * Fügt eine neue Meldung hinzu
	 * @param msg Meldung
	 */
	public void addMsg(String msg) {
		this.msg.add(msg);
		this.repaint();
	}	

	/**
	 * Macht das Fenster schließbar
	 */
	public void setCloseable(){
		this.addWindowListener(this);
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		this.setVisible(false);
		this.dispose();		
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
	
}
