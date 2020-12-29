package capping.jscribe.document.shapes;

import java.awt.Graphics;
import java.io.File;

import javax.swing.ImageIcon;

/**
 * @author Craig
 *
 */
public class Image extends DocumentObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7203553693284085752L;
	
	private int width;
	private int height;
	
	protected ImageIcon image;
	
	public Image(File filename)
	{
		this(filename.toString());
	}
	
	public Image(String filename)
	{
		super();
		
		image = new ImageIcon(filename);
		
		width = image.getIconWidth();
		height = image.getIconHeight();
		
		setSize(width, height);
	}
	
	//sets the bounds for the image being insterted
	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		width = this.width;
		height = this.height;
		
		super.setBounds(x, y, width, height);
	}
	
	//paint the image
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		
		image.paintIcon(this, g, 0, 0);
	}
}
