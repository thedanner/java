package capping.jscribe.document.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

public class Group extends DocumentObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8473548533533172114L;
	
	private Vector<DocumentObject> objects; 	
	
	public Group()
	{
		super();
		
		objects = new Vector<DocumentObject>();		
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// Paint "backwards" so the object's index is treated as its z-order.
		// That is, objects with a lower index, and therefore lower z-order,
		// are painted on top of components with a higher z-order.
		for(int i = objects.size() - 1; i >= 0; i--)
		{
			DocumentObject obj = objects.get(i);
			
			int x = obj.getX();
			int y = obj.getY();
			int w = obj.getWidth();
			int h = obj.getHeight();
			
			Graphics g2 = g.create(x, y, w, h);
			
			obj.paint(g2);
			
			g2.dispose();
		}
	}
	
	//add object to the group
	public void addObject(DocumentObject object)
	{
		object.setSelected(false);
		objects.add(object);		 
	}
	
	//remove an object from a group
	public DocumentObject removeObject(DocumentObject object)
	{
		objects.remove(object);
		
		object.setLocation(
				getX() + object.getX(), getY() + object.getY());
		
		return object;
	}
	
	//remove the object from a group
	public DocumentObject removeObject(int position)
	{
		return removeObject(
				objects.get(position));
	}
	
	//returns all the objects in a group
	public DocumentObject[] getAllObjects()
	{
		return objects.toArray(
				new DocumentObject[getObjectCount()]);
	}
	
	//returns the number of shapes in the group
	public int getObjectCount()
	{
		return objects.size();
	}
	
	//remove all objects from the group
	public DocumentObject[] removeAllObjects()
	{
		DocumentObject[] objectArray = getAllObjects();
		
		while(objects.size() > 0)
			removeObject(0);
		
		objects.clear();
		
		return objectArray;
	}
	
	//make bounds box
	public void computeBounds()
	{
		int xLo = Integer.MAX_VALUE;
		int yLo = Integer.MAX_VALUE;
		
		int xHi = Integer.MIN_VALUE;
		int yHi = Integer.MIN_VALUE;
		
		for(DocumentObject obj : objects)
		{
			xLo = Math.min(xLo, obj.getX());
			yLo = Math.min(yLo, obj.getY());
			
			xHi = Math.max(xHi, obj.getX() + obj.getWidth());
			yHi = Math.max(yHi, obj.getY() + obj.getHeight());
		}
		
		setBounds(xLo, yLo, xHi - xLo, yHi - yLo);
		
		for(DocumentObject obj : objects)
			obj.setLocation(obj.getX() - xLo, obj.getY() - yLo);
	}
	
	
	@Override
	public Color getForeground()
	{
		return null;
	}
	
	@Override
	public Color getBackground()
	{
		return null;
	}
	
	@Override
	public Group clone() throws CloneNotSupportedException
	{
		Group group = (Group) super.clone();
		
		group.objects = new Vector<DocumentObject>(objects.capacity());
		
		for(DocumentObject obj : objects)
			group.objects.add(obj.clone());
		
		return group;
	}
}
