package capping.jscribe.document.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author Craig
 *
 */
public class Line extends Shape
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2165485090019865826L;
	
	public Line()
	{
		super();
	}
	
	public Line(Point p)
	{
		super(p);
	}
	
	public Line(Point p1, Point p2)
	{
		super(p1, p2);
	}
	
	//set the points for the line
	@Override
	public void setPoints(Point p1, Point p2)
	{
		if(p1 == null)
			p1 = new Point(0, 0);
		else
			p1 = new Point(p1);
		
		if(p2 == null)
			p2 = new Point(0, 0);
		else
			p2 = new Point(p2);
		
		this.p1 = p1;
		this.p2 = p2;
		
		int xLo = Math.min(p1.x, p2.x);
		int xHi = Math.max(p1.x, p2.x);
		
		int yLo = Math.min(p1.y, p2.y);
		int yHi = Math.max(p1.y, p2.y);
		
		int width = xHi - xLo;
		int height = yHi - yLo;
		
		computeOrientation();
		
		//deals with "regular shapes" which is done by holding down shift
		if(isRegularShape())
		{
			int edge = Math.min(width, height);
			
			width = edge;
			height = edge;
			
			if(orientation == Orientation.NORTH_EAST)
			{
				xLo = p1.x - edge;	
			}
			else if(orientation == Orientation.SOUTH_WEST)
			{
				yLo = p1.y - edge;
			}
			else if(orientation == Orientation.SOUTH_EAST)
			{
				xLo = p1.x - edge;	
				yLo = p1.y - edge;
			}
		}
		
		setBounds(xLo, yLo, width, height);
	}
	
	//paint the line
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		computeOrientation();
		
		g.setColor(getForeground());
		
		if(orientation == Orientation.NORTH_WEST ||
				orientation == Orientation.SOUTH_EAST)
			g.drawLine(0, 0, getWidth() - 1, getHeight() - 1);
		
		else if(orientation == Orientation.NORTH_EAST ||
				orientation == Orientation.SOUTH_WEST)
			g.drawLine(0, getHeight() - 1, getWidth() - 1, 0);
	}
	
	//paint the outline of the shape
	@Override
	protected void paintOutline(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		int edge = SELECTION_BOX_EDGE_LEN;
		
		if(orientation == Orientation.NORTH_WEST ||
				orientation == Orientation.SOUTH_EAST)
		{
			// top left
			g.fillRect(0, 0, edge, edge);
			
			//bottom right
			g.fillRect(
					getWidth() - edge - 1, getHeight() - edge - 1, edge, edge);
		}
		else
		{
			// top right
			g.fillRect(getWidth() - edge - 1, 0, edge, edge);
			
			//bottom left
			g.fillRect(0, getHeight() - edge - 1, edge, edge);
		}
	}
}
