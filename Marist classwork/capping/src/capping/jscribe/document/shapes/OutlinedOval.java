package capping.jscribe.document.shapes;

import java.awt.Graphics;
import java.awt.Point;

public class OutlinedOval extends Shape
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4720833744281550380L;
	
	public OutlinedOval()
	{
		super();
	}
	
	public OutlinedOval(Point p)
	{
		super(p);
	}
	
	public OutlinedOval(Point p1, Point p2)
	{
		super(p1, p2);
	}
	
	//paint the oval
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setColor(getBackground());
		
		g.fillOval(0, 0, getWidth(), getHeight());
		
		g.setColor(getForeground());
		
		g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
	}
}
