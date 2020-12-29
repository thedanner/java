package capping.jscribe.document.shapes;

import java.awt.Point;

/**
 * @author Craig
 *
 */
public abstract class Shape extends DocumentObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5939856468502752488L;
	
	protected boolean regularShape;
	
	public Shape()
	{
		super();
	}
	
	public Shape(Point p)
	{
		super(p);
	}
	
	public Shape(Point p1, Point p2)
	{
		super(p1, p2);
		
		regularShape = false;
	}
	
	//set the points of the shape
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
		
		//deals with "regular shapes", done by holding down shift while dragging
		//shape
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
	
	//checks to see if it is a regular shape
	public boolean isRegularShape()
	{
		return regularShape;
	}
	
	//makes the shape regular
	public void setRegularShape(boolean regularShape)
	{
		this.regularShape = regularShape;
		
		setPoints(p1, p2);
	}
}
