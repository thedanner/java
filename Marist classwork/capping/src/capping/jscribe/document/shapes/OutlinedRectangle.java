package capping.jscribe.document.shapes;

import java.awt.Graphics;
import java.awt.Point;

public class OutlinedRectangle extends Shape
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4720833744281550380L;
	
	public OutlinedRectangle()
	{
		super();
	}
	
	public OutlinedRectangle(Point p)
	{
		super(p);
	}
	
	public OutlinedRectangle(Point p1, Point p2)
	{
		super(p1, p2);
	}
	
	//paint the rectangle
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setColor(getBackground());
		
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(getForeground());
		
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
}
