package at.letto.tools.tikz;

import java.awt.Font;
import java.awt.FontMetrics;

@SuppressWarnings("serial")
public class TikzFontMetrics extends FontMetrics{

	protected TikzFontMetrics(Font font) {
		super(font);
	}

	/**
	 * Berechnet die genaue Breite eines Zeichens
	 * @param data Zeichen 
	 * @return     genaue Breite in Pixel
	 */
	private int getCharWidth(char data) {
		int ret = 0;
		ret     = getFont().getSize();
		return ret;
    }
	
	@Override
	public int charsWidth(char data[], int off, int len) {
		int ret = 0;
		for (int i=off;i<off+len;i++) 
			if (i<data.length) 
				ret += getCharWidth(data[i]);
		return ret;
    }
	
}
