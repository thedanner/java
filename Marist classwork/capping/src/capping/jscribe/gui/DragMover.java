package capping.jscribe.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.event.MouseInputListener;

/**
 * 
 * @author Dan
 * @version 1.0, 31 Mar 2007
 */
public class DragMover implements MouseInputListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4058225562709126847L;
	
	private Vector<Component> comps;
	private DocumentFrame docFrame;
	private int x;
	private int y;
	private boolean acceptNextMove;
	
	/**
	 * Creates a new DragMover object.  Any components that an instance of this
	 * class is added to (as a {@code MouseListener} <em>and</em>
	 * {@code MouseMotionListener}-- this is done automatically) will be able
	 * to be moved by left-clicking (specifically, the main mouse button) and
	 * dragging inside that component whenever.  If the component has a parent,
	 * it will not be moved beyond that parent's bounds (no part of the
	 * component will be moved outside those bounds).
	 * @param comp the component to be moved by dragging the mouse
	 * inside its bounds.
	 */
	public DragMover(DocumentFrame docFrame)
	{
		this.docFrame = docFrame;
		
		x = 0;
		y = 0;
		acceptNextMove = false;
		
		comps = new Vector<Component>();
	}
	
	public void install(Component comp)
	{
		if(!isMouseListenerInstalled(comp))
			comp.addMouseListener(this);
		
		if(!isMouseMotionListenerInstalled(comp))
			comp.addMouseMotionListener(this);
		
		if(!comps.contains(comp))
			comps.add(comp);
	}
	
	public boolean isMouseListenerInstalled(Component comp)
	{
		for(MouseListener l : comp.getMouseListeners())
		{
			if(this == l)
				return true;
		}
		
		return false;
	}
	
	public boolean isMouseMotionListenerInstalled(Component comp)
	{
		for(MouseMotionListener l : comp.getMouseMotionListeners())
		{
			if(this == l)
				return true;
		}
		
		return false;
	}
	
	public void remove(Component comp)
	{
		comp.removeMouseListener(this);
		comp.removeMouseMotionListener(this);
		
		comps.remove(comp);
	}
	
	public void removeAll()
	{
		for(Component comp : comps)
		{
			comp.removeMouseListener(this);
			comp.removeMouseMotionListener(this);
		}
		
		comps.clear();
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(acceptNextMove)
		{
			Component comp = (Component) e.getSource();
			
			int xOff = e.getX() - x;
			int yOff = e.getY() - y;
			
			int newX = comp.getX() + xOff;
			int newY = comp.getY() + yOff;
			
			Container parent = comp.getParent();
			
			if(parent != null && !(parent instanceof Window))
			{
				if(newX < 0)
					newX = 0;
				
				if(newY < 0)
					newY = 0;
				
				if(newX + comp.getWidth() > parent.getWidth())
					newX = parent.getWidth() - comp.getWidth();
				
				if(newY + comp.getHeight() > parent.getHeight())
					newY = parent.getHeight() - comp.getHeight();
			}
			
			comp.setLocation(newX, newY);
			
			docFrame.setDirty(true);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if(docFrame.getNextObject() == null &&
				e.getButton() == MouseEvent.BUTTON1)
		{
			Component comp = (Component) e.getSource();
			
			Container parent = comp.getParent();
			
			acceptNextMove = true;
			
			x = e.getX();
			y = e.getY();
			
			parent.setComponentZOrder(comp, 0);
			parent.repaint();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		acceptNextMove = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
}
