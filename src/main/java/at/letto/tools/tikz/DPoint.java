package at.letto.tools.tikz;

import java.awt.geom.Point2D;

public class DPoint extends Point2D{
	
	private double x; 
	private double y;
	
	public DPoint(double x, double y) { 
		this.x = x; 
		this.y = y; 
	}
	
	public IPoint toIPoint(){ 
		return new IPoint((int)(x+0.5), (int)(y+0.5)); 
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x=x; 
		this.y=y;
	}
	
}
