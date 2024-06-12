package at.letto.tools.tikz;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Vector;

public class Graphics2DTikz extends Graphics2D {

	private Stroke          stroke = null;
	private Font            f      = null;
	private AffineTransform T;
	private Color bgc              = Color.white;
	private Color c                = Color.black;
	private int massstab           = 1;
	private int x,y,width,height;
	private Vector<String> tikz;

	/**
	 * Erzeugt ein Tikz-Objekt mit einem definierten Maßstab für die Ausgabe
	 * @param massstab Anzahl der Pixel pro mm 
	 * @param width    Breite der Zeichenfläche
	 * @param height   Höhe der Zeichenfläche
	 */
	public Graphics2DTikz(int massstab,int width, int height) {
		this.massstab = massstab;
		tikz = new Vector<String>();
		T = new AffineTransform();
		T.setToScale(massstab, massstab);
		x=0;
		y=0;
		this.width = width;
		this.height= height;
		f      = new Font("SansSerif",Font.PLAIN,36);
		stroke = new BasicStroke(1);		
	}
	
	//private Point2D transform(Point p)     { return transform(p.x,p.y); }
	/*private Point2D transform(int x, int y){
		Point2D src = new DPoint(x,y);
		Point2D ret=null;
		T.transform(src, ret);
		return ret;
	}*/
	
	/**
	 * Liefert den Tikz-Code 
	 * @return Tikz Code
	 */
	public String getTikz() {
		String ret = "";
		for (String s:tikz) ret += s;
		return ret;
	}
	
	@Override
	public void draw(Shape s) {
		throw new RuntimeException("draw(Shape s) wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");		
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		throw new RuntimeException("drawRenderedImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		throw new RuntimeException("drawRenderableImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (float)x, (float)y);		
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		drawString(iterator.toString(),x,y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		drawString(iterator.toString(),x,y);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		throw new RuntimeException("drawGlyphVector wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void fill(Shape s) {
		throw new RuntimeException("fill(shape) wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		throw new RuntimeException("hit wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		throw new RuntimeException("getDeviceConfiguration wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void setComposite(Composite comp) {
		throw new RuntimeException("setComposite wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void setPaint(Paint paint) {
		throw new RuntimeException("setPaint wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void setStroke(Stroke s) {
		stroke = s;
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		throw new RuntimeException("setRenderingHint wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		throw new RuntimeException("getRenderingHint wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		throw new RuntimeException("setRenderingHints wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		throw new RuntimeException("addRenderingHints wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public RenderingHints getRenderingHints() {
		throw new RuntimeException("getRenderingHints wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void translate(int x, int y) {
		translate((double)x,(double)y);
	}

	@Override
	public void translate(double tx, double ty) {
		AffineTransform Tx = new AffineTransform();
		T.setToTranslation(tx, ty);
		T.concatenate(Tx);
	}

	@Override
	public void rotate(double theta) {
		AffineTransform Tx = new AffineTransform();
		T.setToRotation(theta);
		T.concatenate(Tx);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		AffineTransform Tx = new AffineTransform();
		T.setToRotation(theta,x,y);
		T.concatenate(Tx);
	}

	@Override
	public void scale(double sx, double sy) {
		AffineTransform Tx = new AffineTransform();
		T.setToScale(sx,sy);
		T.concatenate(Tx);
	}

	@Override
	public void shear(double shx, double shy) {
		AffineTransform Tx = new AffineTransform();
		T.setToShear(shx,shy);
		T.concatenate(Tx);
	}

	@Override
	public void transform(AffineTransform Tx) {
		T.concatenate(Tx);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		T = Tx;
	}

	@Override
	public AffineTransform getTransform() {
		return T;
	}

	@Override
	public Paint getPaint() {
		throw new RuntimeException("getPaint wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public Composite getComposite() {
		throw new RuntimeException("getComposite wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void setBackground(Color color) {
		bgc = color;
	}

	@Override
	public Color getBackground() {
		return bgc;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void clip(Shape s) {
		throw new RuntimeException("clip wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		throw new RuntimeException("getFontRenderContext wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public Graphics create() {
		Graphics2DTikz ret = new Graphics2DTikz(massstab,width,height);
		ret.bgc = bgc;
		ret.c   = c;
		ret.f   = f;
		ret.x      = x;
		ret.y      = y;
		ret.stroke = stroke;
		ret.T      = T;
		ret.tikz   = new Vector<String>();
		// for (String s:tikz) ret.tikz.add(s);
		return null;
	}

	@Override
	public Color getColor() {
		return c;
	}

	@Override
	public void setColor(Color c) {
		this.c = c;
	}

	@Override
	public void setPaintMode() {
		
	}

	@Override
	public void setXORMode(Color c1) {
		
	}

	@Override
	public Font getFont() {
		return f;
	}

	@Override
	public void setFont(Font font) {
		f = font;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return new TikzFontMetrics(f);
	}

	@Override
	public Rectangle getClipBounds() {
		return new Rectangle(x,y,width,height);
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		this.width = (x+width>this.width)  ? this.width-x : width ;
		this.height= (y+height>this.height)? this.height-y: height;
		this.x = this.x+x;
		this.y = this.y+y;
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height= height;
	}

	@Override
	public Shape getClip() {
		throw new RuntimeException("getClip wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void setClip(Shape clip) {
		throw new RuntimeException("setClip wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new RuntimeException("copyArea wird von Graphics2DTikz nicht unterstützt!");
	}


	@Override
	public void drawString(String str, float x, float y) {
		
	}
	
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		tikz.add(x1+","+y1+"-"+x2+","+y2);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {
		throw new RuntimeException("drawImage wird von Graphics2DTikz nicht unterstützt!");
	}

	@Override
	public void dispose() {
		tikz = null;		
	}

}
