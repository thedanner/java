package capping.jscribe.document.shapes;

import java.awt.Graphics;
import java.awt.Point;

public class OutlinedRoundedRectangle extends Shape
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4720833744281550380L;
	
	public OutlinedRoundedRectangle()
	{
		super();
	}
	
	public OutlinedRoundedRectangle(Point p)
	{
		super(p);
	}
	
	public OutlinedRoundedRectangle(Point p1, Point p2)
	{
		super(p1, p2);
	}
	
	//paint the rectangle
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setColor(getBackground());
		
		g.fillRoundRect(0, 0, getWidth(), getHeight(),
				getWidth() / 6, getHeight() / 6);
		
		g.setColor(getForeground());
		
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
				getWidth() / 6, getHeight() / 6);
	}
}
