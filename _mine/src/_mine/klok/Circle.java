package _mine.klok;

import java.awt.Point;

public class Circle {
	private Point corner;
	private int radius;
	
	public Circle() {
		this(0, 0, 0);
	}
	
	public Circle(int x, int y, int radius) {
		this(new Point(x, y), radius);
	}
	
	public Circle(Point center, int radius) {
		this.corner = new Point(center);
		this.radius = radius;
	}
	
	public int getX() {
		return corner.x;
	}
	
	public void setX(int x) {
		this.corner.x = x;
	}
	
	public int getY() {
		return corner.y;
	}
	
	public void setY(int y) {
		this.corner.y = y;
	}
	
	public Point getCorner() {
		return corner;
	}

	public void setCorner(Point center) {
		this.corner = center;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public Point getCenter() {
		int x = corner.x + radius;
		int y = corner.y + radius;
		
		return new Point(x, y);
	}
}
