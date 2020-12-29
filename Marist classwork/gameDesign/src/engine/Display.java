package engine;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public class Display extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Display()
	{
		super();
	}
	
	public Display(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
	}
	
	public Display(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
	}
	
	public Display(LayoutManager layout)
	{
		super(layout);
	}
	
	
}
