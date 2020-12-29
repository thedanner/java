package capping.jscribe.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import capping.jscribe.document.Sheet;
import capping.jscribe.document.shapes.DocumentObject;
import capping.jscribe.gui.DocumentFrame.ShapeDrawer;

public class DocumentEditorPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4549724667210426482L;
	
	// Use a default gridline interval of 10px.
	public static final int DEFAULT_GRIDLINE_INTERVAL = 10;
	
	// Use a light blue for gridlines.
	public static final Color DEFAULT_GRIDLINE_COLOR =
	new Color(190, 190, 255);
	
	// Sets size of program window.
	public static final Dimension PREFERRED_SIZE =
		new Dimension(500, 300);
	
	private boolean drawGridlines;
	private int gridlineInterval;
	private Color gridlineColor;
	
	private ShapeDrawer shapeDrawer;
	
	private Sheet sheet;
	
	public DocumentEditorPane()
	{
		this(null, null);
	}
	
	public DocumentEditorPane(Sheet sheet, ShapeDrawer shapeDrawer)
	{
		super();
		 
		drawGridlines = true;
		gridlineInterval = DEFAULT_GRIDLINE_INTERVAL;
		gridlineColor = DEFAULT_GRIDLINE_COLOR;
		
		this.sheet = sheet;
		
		setShapeDrawer(shapeDrawer);
		
		setLayout(null);
		
		new DragScroller(this);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(drawGridlines)
			drawGridlines(g);
	}
	
	private void drawGridlines(Graphics g)
	{
		// We only want to draw gridlines in the visible area,
		// so figure out where that is.
		Rectangle r = getVisibleRect();
		
		// Default gridline color.
		g.setColor(gridlineColor);
		
		// Get the range of visible x-coords.
		int xLo = r.x;
		int xHi = r.x + r.width;
		
		// Get the range of visible y-coords.
		int yLo = r.y;
		int yHi = r.y + r.height;
		
		// If xLo isn't a multiple of the gridline interval,
		// drop the remainder (-= % GRIDLINE_INTERVAL)
		// and round up to the next multiple (+ GRIDLINE_INTERVAL).
		// This is to avoid painting gridlines that won't be visible.
		if(xLo % gridlineInterval != 0) {
			xLo -= xLo % gridlineInterval;
			xLo += gridlineInterval;
		}
		
		// same for yLo
		if(yLo % gridlineInterval != 0) {
			yLo -= yLo % gridlineInterval;
			yLo += gridlineInterval;
		}
		
		// for the visible x-coords, draw gridlines at the
		// appropriate intervals
		for(int x = xLo ; x < xHi; x += gridlineInterval)
			g.drawLine(x, r.y, x, yHi);
		
		// do the same for the visible y-coords
		for(int y = yLo; y < yHi; y += gridlineInterval)
			g.drawLine(r.x, y, xHi, y);
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize)
	{
		super.setPreferredSize(preferredSize);
	}
	
	public Sheet getSheet()
	{
		return sheet;
	}
	
	public void setSheet(Sheet sheet)
	{
		this.sheet = sheet;
	}
	
	// interface to document
	
	public void addObject(DocumentObject object)
	{
		add(object);
		sheet.addObject(object);
	}
	
	public boolean removeObject(DocumentObject object)
	{
		remove(object);
		return sheet.removeObject(object);
	}
	
	public DocumentObject removeObject(int index)
	{
		DocumentObject obj = sheet.removeObject(index);
		
		remove(obj);
		
		return obj;
	}
	
	public int indexOfObject(DocumentObject object)
	{
		return sheet.indexOfObject(object);
	}
	
	public DocumentObject getObject(int index)
	{
		return sheet.getObject(index);
	}
	
	public DocumentObject[] getAllObjects()
	{
		return sheet.getAllObjects();
	}
	
	public boolean isDrawGridlines()
	{
		return drawGridlines;
	}
	
	public void setDrawGridlines(boolean drawGridlines)
	{
		this.drawGridlines = drawGridlines;
	}
	
	public Color getGridlineColor()
	{
		return gridlineColor;
	}
	
	public void setGridlineColor(Color gridlineColor)
	{
		this.gridlineColor = gridlineColor;
	}
	
	public int getGridlineInterval()
	{
		return gridlineInterval;
	}
	
	public void setGridlineInterval(int gridlineInterval)
	{
		this.gridlineInterval = gridlineInterval;
	}
	
	public ShapeDrawer getShapeDrawer()
	{
		return shapeDrawer;
	}
	
	public void setShapeDrawer(ShapeDrawer shapeDrawer)
	{
		this.shapeDrawer = shapeDrawer;
		
		addMouseListener(shapeDrawer);
		addMouseMotionListener(shapeDrawer);
		addKeyListener(shapeDrawer);
	}
}
