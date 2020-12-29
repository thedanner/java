package _mine.serverQuery.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.event.MouseInputListener;

/**
 * 
 * @author Dan
 * @version Jun 22, 2005
 */
public class DragMover implements MouseInputListener {
	private Component mainComp;
	private LinkedList<Component> otherComps;
	private int x;
	private int y;
	private boolean acceptNextMove;
	
	/**
	 * Creates a new DragMover object.  Any components that an object of this
	 * class is added to (as a MouseListener) will move the specified
	 * component whenever the main mouse button is clicked and dragged inside
	 * the specified component.  Instances of this class must be manually added
	 * to each component that will move the specified component, except that
	 * component itself (the listener will be added to the component to
	 * be moved while dragging, it must be added manually, via the addToComp()
	 * method in this class, to each of its children.)
	 * @param mainComp the component to be moved by dragging the mouse
	 * inside its bounds.
	 */
	public DragMover(Component mainComp) {
		if(mainComp == null)
			throw new NullPointerException(
					"component cannot be null");
		this.mainComp = mainComp;
		
		otherComps = new LinkedList<Component>();
		
		x = 0;
		y = 0;
		acceptNextMove = false;
		
		addToComp(mainComp);
	}
	
	public void addToComp(Component c) {
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
		
		otherComps.add(c);
	}
	
	public void uninstallAll() {
		for(Component c : otherComps) {
			c.removeMouseListener(this);
			c.removeMouseMotionListener(this);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(acceptNextMove) {
			int xOff = e.getX() - x;
			int yOff = e.getY() - y;
			
			mainComp.setLocation(mainComp.getX() + xOff, mainComp.getY() + yOff);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			acceptNextMove = true;
			x = e.getX();
			y = e.getY();
		}
	}
	
	public String c(int x, int y) {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
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
