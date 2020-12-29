package capping.jscribe.document.shapes;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JComponent;

public abstract class DocumentObject extends JComponent implements Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7865703437317499347L;
	
	protected static final int SELECTION_BOX_EDGE_LEN = 4;
	
	protected enum Orientation
	{
		NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
		
		// extended Orientations for figures with height or width == 1
		NORTH, EAST, SOUTH, WEST
	}
	
	protected Point p1;
	protected Point p2;
	
	protected Orientation orientation;
	
	protected boolean selected;
	
	public DocumentObject()
	{
		this(null);
	}
	
	public DocumentObject(Point p)
	{
		this(p, p);
	}
	
	public DocumentObject(Point p1, Point p2)
	{
		super();
		
		selected = false;
		
		orientation = Orientation.NORTH_WEST;
		
		setPoints(p1, p2);
	}
	
	public void setPoints(Point p)
	{
		if(p == null)
			p = new Point(0, 0);
		
		setPoints(p, new Point(p));
	}
	
	//sets the coordinate points of the shapes.
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
		
		setBounds(xLo, yLo, width, height);
	}
	
	//returns p1
	public Point getP1()
	{
		return p1;
	}
	
	//sets points p and p2
	public void setP1(Point p)
	{
		setPoints(p, p2);
	}
	
	//returns p2
	public Point getP2()
	{
		return p2;
	}
	
	//sets p2
	public void setP2(Point p)
	{
		setPoints(p1, p);
	}
	
	//sets the bounds box for each shape
	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		if(width == 0)
			width++;
		
		if(height == 0)
			height++;
		
		super.setBounds(x, y, width, height);
		
		/*
		// top left to bottom right, or the other way around
		if(orientation == Orientation.NORTH_WEST ||
				orientation == null)
		{
			p1.x = x;
			p1.y = y;
			
			p2.x = x + width;
			p2.y = y + height;
		}
		else if(orientation == Orientation.SOUTH_EAST) 
		{
			p1.x = x + width;
			p1.y = y + height;
			
			p2.x = x;
			p2.y = y;
		}
		// top right to bottom left, or the other way around
		else if(orientation == Orientation.NORTH_EAST)
		{
			p1.x = x + width;
			p1.y = y;
			
			p2.x = x;
			p2.y = y + height;
		}
		else if(orientation == Orientation.SOUTH_WEST)
		{
			p1.x = x;
			p1.y = y + height;
			
			p2.x = x + width;
			p2.y = y;
		}
		//*/
	}
	
	//paints the shape
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	//paints the border
	@Override
	protected void paintBorder(Graphics g)
	{
		super.paintBorder(g);
		
		if(selected)
		{
			paintOutline(g);
		}
	}
	
	//paints the outline of the shape
	protected void paintOutline(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		int xMid = getWidth() / 2;
		int yMid = getHeight() / 2;
		
		int edge = SELECTION_BOX_EDGE_LEN;
		int half = edge / 2;
		
		// top left
		g.fillRect(0, 0, edge, edge);
		
		// top mid
		g.fillRect(xMid - half, 0, edge, edge);
		
		// top right
		g.fillRect(getWidth() - edge - 1, 0, edge, edge);
		
		//mid left
		g.fillRect(0, yMid - half, edge, edge);
		
		//mid right
		g.fillRect(getWidth() - edge - 1, yMid - half, edge, edge);
		
		//bottom left
		g.fillRect(0, getHeight() - edge - 1, edge, edge);
		
		//bottom mid
		g.fillRect(xMid - half, getHeight() - edge - 1, edge, edge);
		
		//bottom right
		g.fillRect(
				getWidth() - edge - 1, getHeight() - edge - 1, edge, edge);
	}
	
	//return true if the shape is selected
	public boolean isSelected()
	{
		return selected;
	}
	
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	//returns the orientation of the shape
	public Orientation getOrientation()
	{
		return orientation;
	}
	
	//this computes the orientation
	protected Orientation computeOrientation()
	{
		int xLo = Math.min(p1.x, p2.x);
		int yLo = Math.min(p1.y, p2.y);
		
		if(xLo == p1.x && yLo == p1.y)
			orientation = Orientation.NORTH_WEST;
		
		else if(xLo == p2.x && yLo == p2.y)
			orientation = Orientation.SOUTH_EAST;
		
		else if(xLo == p1.x && yLo == p2.y)
			orientation = Orientation.SOUTH_WEST;
		
		else if(xLo == p2.x && yLo == p1.y)
			orientation = Orientation.NORTH_EAST;
		
		else
			orientation = null;
		
		return orientation;
	}
	
	//returns the ZOrder of the shapes
	public int getMyZOrder()
	{
		Container parent = getParent();
		
		if(parent == null)
			return 0;
		
		return parent.getComponentZOrder(this);
	}
	
	//sets the ZOrder of the shapes
	public void setMyZOrder(int index)
	{
		Container parent = getParent();
		
		parent.setComponentZOrder(this, index);
	}
	
	@Override
	public DocumentObject clone() throws CloneNotSupportedException
	{
		return (DocumentObject) super.clone();
	}
	
	//puts the objects in the right ZOrder
	public static void sortObjectsByZOrder(DocumentObject[] objectList)
	{
		Arrays.sort(objectList, new ZOrderSorter());
	}
	
	private static class ZOrderSorter implements Comparator<DocumentObject>
	{
		@Override
		public int compare(DocumentObject o1, DocumentObject o2)
		{
			return o1.getMyZOrder() - o2.getMyZOrder();
		}
	}
}
