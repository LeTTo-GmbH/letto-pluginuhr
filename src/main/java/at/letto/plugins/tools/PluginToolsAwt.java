package at.letto.plugins.tools;

import at.letto.plugins.enums.ORIENTATIONX;
import at.letto.plugins.enums.ORIENTATIONY;

import java.awt.*;

public class PluginToolsAwt {

    /**
     * Zeichnet einen Text auf den Bildschirm
     * @param g       Graphics Zeichenobjekt
     * @param s       AusgabeString
     * @param px      Bezugspunkt x
     * @param py      Bezugspunkt y
     * @param height  Schrifthöhe
     * @param ox      Ausrichtung horizontal RIGHT LEFT CENTER
     * @param oy      Ausrichtung vertikal   UP DOWN CENTER
     * @param arg     Winkel
     */
    public static void drawString(Graphics2D g, String s, int px, int py, int height, ORIENTATIONX ox, ORIENTATIONY oy, double arg) {
        drawString(g,s,px,py,height,ox,oy,arg,false);
    }

    /**
     * Zeichnet einen Text auf den Bildschirm
     * @param g       Graphics Zeichenobjekt
     * @param s       AusgabeString
     * @param px      Bezugspunkt x
     * @param py      Bezugspunkt y
     * @param height  Schrifthöhe
     * @param ox      Ausrichtung horizontal RIGHT LEFT CENTER
     * @param oy      Ausrichtung vertikal   UP DOWN CENTER
     * @param arg     Winkel
     * @param underline Text unterstrichen
     */
    public static void drawString(Graphics2D g,String s, int px, int py, int height, ORIENTATIONX ox, ORIENTATIONY oy, double arg, boolean underline ) {
        if (height>3) {
            Font font = g.getFont();
            g.setFont(new Font(font.getFamily(),font.getStyle(),height));
            FontMetrics fm = g.getFontMetrics();
            int b = fm.stringWidth(s);
            int h = fm.getAscent();
            if (b>0) {
                g.translate(px, py);
                g.rotate(arg);
                int x = 0;
                int y = 0;
                if (ox == ORIENTATIONX.RIGHT)   x=-b-2;
                if (ox == ORIENTATIONX.LEFT)    x=2;
                if (ox == ORIENTATIONX.CENTER)  x=-b/2;
                if (oy == ORIENTATIONY.BOTTOM)  y=-2;
                if (oy == ORIENTATIONY.CENTER)  y=h/2;
                if (oy == ORIENTATIONY.TOP)     y=h+2;
                g.drawString(s,x,y);
                if (underline) g.drawLine(x, y, x+b , y);
                g.rotate(-arg);
                g.translate(-px,-py);
            }
            g.setFont(font);
        }
    }

}
