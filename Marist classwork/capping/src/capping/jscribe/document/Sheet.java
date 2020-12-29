package capping.jscribe.document;

import java.awt.Color;
import java.io.Serializable;
import java.util.Vector;

import capping.jscribe.document.shapes.DocumentObject;

/**
 * This is the actual sheet class for each tabbed sheet
 * 
 */
public class Sheet implements Serializable
{
	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -5445201692621125993L;
	
	//Declare the Vector of objects.
	private Vector<DocumentObject> objectList;
	//Declare the Vector of the objects' Z-orders.
	private Vector<Integer> objectListZOrder;
	
	//Color for the background.
	private Color background;
	
	//The string that will hold the title of the sheet.
	private String title;
	
	/**
	 * Constructor
	 * 
	 */
	public Sheet(String title)
	{
		//Instantiate the Vectors
		objectList = new Vector<DocumentObject>();
		objectListZOrder = new Vector<Integer>();
		
		//Set the background as white
		background = Color.WHITE;
		
		//Set the title as the passed Sheet title
		setTitle(title);
	}
	
	/**
	 * Add a passed object to the sheet
	 * 
	 */
	public void addObject(DocumentObject object)
	{
		objectList.add(object);
		objectListZOrder.add(object.getMyZOrder());
	}
	
	/**
	 * Remove the passed object from the sheet
	 * Return true if successful remove
	 */
	public boolean removeObject(DocumentObject object)
	{
		objectListZOrder.remove(object);
		return objectList.remove(object);
	}
	
	/**
	 * Remove and return the object from the sheet whose index was passed
	 * 
	 */
	public DocumentObject removeObject(int index)
	{
		objectListZOrder.remove(index);
		return objectList.remove(index);
	}
	
	/**
	 * Return the index of the passed object
	 * 
	 */
	public int indexOfObject(DocumentObject object)
	{
		return objectList.indexOf(object);
	}
	
	/**
	 * Return the object of the sheets object whose index was passed
	 * 
	 */
	public DocumentObject getObject(int index)
	{
		return objectList.get(index);
	}
	
	/**
	 * Load all the Z-orders and set them for each object in the sheet
	 * 
	 */
	public void setAllZOrders()
	{
		for(int i = 0; i < objectList.size(); i++)
		{
			objectList.get(i).setMyZOrder(
					objectListZOrder.get(i));
		}
	}
	
	/**
	 * Set the objects' Z-orders
	 * 
	 */
	public void computeAllZOrders()
	{
		for(int i = 0; i < objectList.size(); i++)
		{
			int zOrder = objectList.get(i).getMyZOrder();
			
			objectListZOrder.set(i, zOrder);
		}
	}
	
	/**
	 * Return an array of all the objects in the Sheet
	 * 
	 */
	public DocumentObject[] getAllObjects()
	{
		return objectList.toArray(new DocumentObject[objectList.size()]);
	}
	
	/**
	 * Return the number of objects in the Sheet
	 * 
	 */
	public int getObjectCount()
	{
		return objectList.size();
	}
	
	/**
	 * Return the Color of the background
	 * 
	 */
	public Color getBackground()
	{
		return background;
	}
	
	/**
	 * Set the background to the passed background
	 * 
	 */
	public void setBackground(Color background)
	{
		this.background = background;
	}
	
	/**
	 * Return the title of the sheet
	 * 
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Set the title of the sheet
	 * 
	 */
	public void setTitle(String title)
	{
		if(title == null)
			throw new NullPointerException("title is null");
		
		this.title = title;
	}
	
	/**
	 * Reorganizes the collection of objects
	 * 
	 */
	public void compact()
	{
		DocumentObject[] objects = new DocumentObject[objectList.size()];
		
		objects = objectList.toArray(objects);
		
		objectList = new Vector<DocumentObject>(objects.length * 2);
		
		for(DocumentObject obj : objects)
			objectList.add(obj);
	}
}
