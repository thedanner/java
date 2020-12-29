package capping.jscribe.document.shapes;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class DocumentObjectSelection implements Transferable, ClipboardOwner
{
	private DocumentObject[] content;
	
	public DocumentObjectSelection(DocumentObject object)
	{
		this(new DocumentObject[] { object });
	}
	
	public DocumentObjectSelection(DocumentObject[] objects)
	{
		content = new DocumentObject[objects.length];
		
		for(int i = 0; i < objects.length; i++)
		{
			try
			{
				content[i] = objects[i].clone();
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public DocumentObject[] getContent()
	{
		return content;
	}
	
	public void setContent(DocumentObject[] content)
	{
		this.content = content;
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor)
	throws UnsupportedFlavorException, IOException
	{
		if(!isDataFlavorSupported(flavor))
			return null;
		
		return this;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] {
				new DataFlavor(DocumentObject.class, "DocumentObject")
		};
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		DataFlavor myFlavor = getTransferDataFlavors()[0];
		
		return flavor.equals(myFlavor);
	}
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
