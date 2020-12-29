package capping.jscribe.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

/**
 * Our version of GUI adapter
 * 
 */
public class GuiWindowAdapter extends WindowAdapter
{
	//The main document frame
	private DocumentFrame docFrame;
	
	//Stores the user response to whether they want to save before closing
	private int lastResponse;
	
	/**
	 * Constructor
	 * 
	 */
	public GuiWindowAdapter(DocumentFrame docFrame)
	{
		//Make sure the document frame is not null
		if(docFrame == null)
			throw new NullPointerException("docFrame is null");
		
		this.docFrame = docFrame;
	}
	
	/**
	 * This function is called when the window begins to be closed
	 * 
	 */
	@Override
	public void windowClosing(WindowEvent e)
	{
		//If there has been changes made, make sure the user doesn't want to save
		if(docFrame.isDirty())
		{
			//Ask if they want to save because isDirty means changes were made
			int msg = JOptionPane.showConfirmDialog(null,
					"Do you wish to save \"" + docFrame.getDocumentTitle() +
					"\" before closing.");
			
			//Store user's response to saving
			lastResponse = msg;
			
			//If they want to save then save
			if(msg == JOptionPane.YES_OPTION)
			{
				boolean saveCompleted = docFrame.saveDocumentAs();
				
				//close
				if(saveCompleted)
					docFrame.dispose();
			}
			else if(msg == JOptionPane.NO_OPTION)
			{
				//don't save and close
				docFrame.dispose();
			}
		}
		// nothing to save, so close
		else
			docFrame.dispose();
	}
	
	/**
	 * When window is closed remove the document frame
	 * 
	 */
	@Override
	public void windowClosed(WindowEvent e)
	{
		GuiController.getInstance().removeDocumentFrame(docFrame);
	}
	
	/**
	 * Return the last response of the user
	 * 
	 */
	public int getLastResponse()
	{
		return lastResponse;
	}
}
