package capping.jscribe.gui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class DragScroller implements MouseMotionListener
{
	private JComponent comp;
	
	public DragScroller(JComponent comp)
	{
		if(comp == null)
			throw new NullPointerException("comp (arg0) is null)");
		
		this.comp = comp;
		
		comp.setAutoscrolls(true); //enable synthetic drag events
		comp.addMouseMotionListener(this); //handle mouse drags
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		//The user is dragging us, so scroll!
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		comp.scrollRectToVisible(r);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) { }
}
