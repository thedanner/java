package capping.jscribe.document;

import java.awt.print.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import capping.jscribe.document.shapes.DocumentObject;
import capping.util.SwingWorker;

/**
 * 
 * 
 */
public class Document implements Serializable, Printable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7633274255096614231L;
	
	//Declare a Vector of Sheets called sheetList.
	private Vector<Sheet> sheetList;
	//Declare the Sheet that is the currently selected Sheet.
	private Sheet activeSheet;
	//The preferred Dimension of the Sheet.
	private Dimension preferredSize;
	
	/**
	 * 
	 * 
	 */
	public Document()
	{
		//Instantiate the Vector of Sheets
		sheetList = new Vector<Sheet>();
		//Set the other two variables to null
		activeSheet = null;
		preferredSize = null;
	}
	
	/**
	 * Return the preferred Dimension
	 * 
	 */
	public Dimension getPreferredSize()
	{
		return preferredSize;
	}

	/**
	 * Set the preferred Dimension
	 * 
	 */
	public void setPreferredSize(Dimension preferredSize)
	{
		this.preferredSize = preferredSize;
	}
	
	/**
	 * Make a new Sheet and add it to the sheetList
	 * 
	 */
	public Sheet createSheet(String title)
	{
		Sheet s = new Sheet(title);
		
		sheetList.add(s);
		
		return s;
	}
	
	/**
	 * Remove the passed Sheet from the sheetList
	 * 
	 */
	public void removeSheet(Sheet s)
	{
		sheetList.remove(s);
	}
	
	/**
	 * Remove the passed Sheet index from the sheetList
	 * 
	 */
	public void removeSheet(int index)
	{
		sheetList.remove(index);
	}
	
	/**
	 * Return the Sheet whose name is passed
	 * 
	 */
	public Sheet getSheetByName(String name)
	{
		//Scan through all the Sheets in the sheetList.
		for(Sheet sheet : sheetList)
		{
			//If the passed name is the same as the Sheet title,
			//return that sheet.
			if(name.equals(sheet.getTitle()))
				return sheet;
		}
		
		return null;
	}
	
	/**
	 * Return the Sheet whose index is passed
	 * 
	 */
	public Sheet getSheet(int index)
	{
		return sheetList.get(index);
	}
	
	/**
	 * Return the integer index of the passed Sheet
	 * 
	 */
	public int getIndexOfSheet(Sheet s)
	{
		return sheetList.indexOf(s);
	}
	
	/**
	 * Convert the entire Vector sheetList to an Array and return it
	 * 
	 */
	public Sheet[] getSheets(Sheet[] sheets)
	{
		return sheetList.toArray(sheets);
	}
	
	/**
	 * Return the number of Sheets in the sheetList
	 * 
	 */
	public int getSheetCount()
	{
		return sheetList.size();
	}
	
	/**
	 * Make the passed sheet the active sheet
	 * 
	 */
	public void setActiveSheet(Sheet sheet)
	{
		activeSheet = sheet;
	}
	
	/**
	 * Set the passed sheet based on its index to the active sheet
	 * 
	 */
	public void setActiveSheet(int index)
	{
		setActiveSheet(sheetList.get(index));
	}
	
	/**
	 * Return the active sheet
	 * 
	 */
	public Sheet getActiveSheet()
	{
		return activeSheet;
	}
	
	// object methods defaulting to active sheet.
	/**
	 * Add the passed object to the current active sheet
	 * 
	 */
	public void addObject(DocumentObject object)
	{
		addObject(object, getActiveSheet());
	}
	
	/**
	 * Remove the passed object from the current active sheet
	 * Returns true if the object was found and successfully removed,
	 * and returns false otherwise
	 */
	public boolean removeObject(DocumentObject object)
	{
		return removeObject(object, getActiveSheet());
	}
	
	/**
	 * Remove the object whose index is passed and then return that object
	 * 
	 */
	public DocumentObject removeObject(int index)
	{
		return removeObject(index, getActiveSheet());
	}
	
	/**
	 * Return the index of the passed object
	 * 
	 */
	public int indexOfObject(DocumentObject object)
	{
		return indexOfObject(object, getActiveSheet());
	}
	
	/**
	 * Return the object whose index is passed
	 * 
	 */
	public DocumentObject getObject(int index)
	{
		return getObject(index, getActiveSheet());
	}
	
	/**
	 * Return all of the objects in an array
	 * 
	 */
	public DocumentObject[] getAllObjects()
	{
		return getAllObjects(getActiveSheet());
	}
	
	// object methods with a sheet specified by index
	/**
	 * Add a passed object to the passed sheet by its sheet index
	 * 
	 */
	public void addObject(DocumentObject object, int sheetIndex)
	{
		addObject(object, getSheet(sheetIndex));
	}
	
	/**
	 * Remove the passed object from the passed sheet by its sheet index
	 * Returns true if the remove was successful, and false otherwise
	 * 
	 */
	public boolean removeObject(DocumentObject object, int sheetIndex)
	{
		return removeObject(object, getSheet(sheetIndex));
	}
	
	/**
	 * Remove the object whose index was passed from the sheet whose index was
	 * passed, and return that object
	 * 
	 */
	public DocumentObject removeObject(int objectIndex, int sheetIndex)
	{
		return removeObject(objectIndex, getSheet(sheetIndex));
	}
	
	/**
	 * Return the index of the passed object within the sheet whose index
	 * was passed
	 * 
	 */
	public int indexOfObject(DocumentObject object, int sheetIndex)
	{
		return indexOfObject(object, getSheet(sheetIndex));
	}
	
	/**
	 * Return the object whose index and sheet index was passed
	 * 
	 */
	public DocumentObject getObject(int objectIndex, int sheetIndex)
	{
		return getObject(objectIndex, getSheet(objectIndex));
	}
	
	/**
	 * Return an array of all the objects within the passed sheet index
	 * 
	 */
	public DocumentObject[] getAllObjects(int sheetIndex)
	{
		return getAllObjects(getSheet(sheetIndex));
	}
	
	// object methods with the specified sheet
	/**
	 * Add the passed object to the passed Sheet
	 * 
	 */
	public void addObject(DocumentObject object, Sheet s)
	{
		s.addObject(object);
	}
	
	/**
	 * Remove the passed object from the passed Sheet
	 * Return true if the object was successfully deleted
	 */
	public boolean removeObject(DocumentObject object, Sheet s)
	{
		return s.removeObject(object);
	}
	
	/**
	 * Remove the object whose index and Sheet has been passed and
	 * return taht object
	 */
	public DocumentObject removeObject(int index, Sheet s)
	{
		return s.removeObject(index);
	}
	
	/**
	 * Return the index of the object which was passed and within the passed
	 * sheet
	 */
	public int indexOfObject(DocumentObject object, Sheet s)
	{
		return s.indexOfObject(object);
	}
	
	/**
	 * Return the object whose index and Sheet was passed
	 * 
	 */
	public DocumentObject getObject(int index, Sheet s)
	{
		return s.getObject(index);
	}
	
	/**
	 * Return an array of all the objects in the passed sheet
	 * 
	 */
	public DocumentObject[] getAllObjects(Sheet s)
	{
		return s.getAllObjects();
	}
	
	// z-order stuff
	/**
	 * For all the Sheets in the sheet list, set all of their Z-orders...
	 * The Z-order is the order of the object from front to back
	 */
	public void setAllZOrders()
	{
		for(Sheet sheet : sheetList)
		{
			sheet.setAllZOrders();
		}
	}
	
	/**
	 * For all the Sheets in the sheet list, store all of their Z-orders
	 * 
	 */
	public void saveAllZOrders()
	{
		for(Sheet sheet : sheetList)
		{
			sheet.computeAllZOrders();
		}
	}
	
	// compact pre-serialization
	/**
	 * Reorganizes the collection of objects
	 * 
	 */
	public void compact()
	{
		Sheet[] sheets = new Sheet[sheetList.size()];
		
		sheets = sheetList.toArray(sheets);
		
		sheetList = new Vector<Sheet>(sheets.length * 2);
		
		for(Sheet sheet : sheets)
			sheetList.add(sheet);
	}
	
	/**
	 * Save the document
	 * 
	 */
	public void save(File f)
	throws FileNotFoundException, IOException 
	{
		compact();
		
		for(Sheet sheet : sheetList)
		{
			sheet.compact();
			sheet.computeAllZOrders();
		}
		
		//Create a new ObjectOutputStream.
		ObjectOutputStream out =
			new ObjectOutputStream(
				new FileOutputStream(f));
		
		//Write the object from the document to the output file.
		out.writeObject(this);
		
		//Make sure you close the output file when done.
		out.close();
	}
	
	/**
	 * Open a document
	 * 
	 */
	public static Document open(File f)
	throws IOException, ClassNotFoundException
	{
		//Make a new ObjectInputStream
		ObjectInputStream in =
			new ObjectInputStream(
					new FileInputStream(f));
		
		//Load the document's objects
		Document doc = (Document) in.readObject();
				
		//Return the loaded document
		return doc;
	}
	
	/**
	 * Print a document
	 * 
	 */
	public void print()
	{
		//Make a new PrinterJob.
		final PrinterJob printJob = PrinterJob.getPrinterJob();
		//Set the print job as printable.
		printJob.setPrintable(this);
		
		//If the print dialog returns true (user confirms print),
		//then proceed with printing.
		if(printJob.printDialog())
		{
			//We use a Swing Worker so the GUI does not lock up while printing.
			new SwingWorker()
			{
				@Override
				public Object construct() {
					try 
					{
						//Try printing the job.
						printJob.print();
					}
					catch(PrinterException e)
					{
						e.printStackTrace();
					}
					
					return null;
				}
			}.start();
		}
	}
	
	/**
	 * Prints each sheet
	 * 
	 */
	@Override
	public int print(Graphics g, PageFormat pf, int pg)
	throws PrinterException
	{
		//Create the graphics layout to be printed
		Graphics gPrint = g.create(80, 80,
				g.getClipBounds().width, g.getClipBounds().height);
		
		//As long as the passed number of printed pages is less than the
		//count of sheets, proceed with printing the sheet
		if(pg < getSheetCount())
		{
			//Print all the objects in the sheet
			for(DocumentObject obj : getSheet(pg).getAllObjects())
			{
				//Create another graphic layout for the object
				Graphics g2 = gPrint.create(
						obj.getX(), obj.getY(),
						obj.getWidth(), obj.getHeight());
				
				//Paint the object onto the graphic layout
				obj.paint(g2);
				
				//Discard the no longer needed graphic layout
				g2.dispose();
			}
			
			//Discard the no longer needed graphic layout
			gPrint.dispose();
			
			return Printable.PAGE_EXISTS;
		}
		
		//Discard the no longer needed graphic layout
		gPrint.dispose();
		
		return Printable.NO_SUCH_PAGE;
	}
}
