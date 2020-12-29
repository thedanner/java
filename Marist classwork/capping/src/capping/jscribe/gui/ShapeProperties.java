package capping.jscribe.gui;

import java.awt.Color;

/**
 * The shape properties class
 * 
 */
public class ShapeProperties
{
	private Color foreground = Color.RED;
	private Color background = Color.BLUE;
	
	/**
	 * Constructor
	 * 
	 */
	public ShapeProperties()
	{
		//default foreground and background
		foreground = Color.RED;
		background = Color.BLUE;
	}
	
	//Return the current background color
	public Color getBackground()
	{
		return background;
	}
	
	//Set the current background
	public void setBackground(Color background)
	{
		this.background = background;
	}
	
	//Return the current foreground color
	public Color getForeground()
	{
		return foreground;
	}
	
	//Set the current foreground
	public void setForeground(Color foreground)
	{
		this.foreground = foreground;
	}
}
