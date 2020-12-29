package capping.jscribe.gui;

import java.util.Vector;

import javax.swing.JOptionPane;

import capping.jscribe.SplashScreen;
import capping.util.SwingWorker;

public class GuiController {
	public static final int MAX_OPEN_DOCUMENT_FRAMES = 100;
	public static final String DEFAULT_DOCUMENT_TITLE = "Untitled";
	
	private static GuiController instance;
	
	private int newDocumentCounter;
	
	private Vector<DocumentFrame> windows;
	
	GuiController()
	{
		newDocumentCounter = 1;
		
		windows = new Vector<DocumentFrame>();
	}
	
	//shows the new window
	public void showNewDocumentFrame()
	{
		//stops the user from opening more than 100 windows
		if(windows.size() >= MAX_OPEN_DOCUMENT_FRAMES) {
			JOptionPane.showMessageDialog(null,
					"HOLY WINDOW!!!\nSorry, but I think you've had enough " +
					"windows for one day!",
					"Too many windows",
					JOptionPane.ERROR_MESSAGE
					);
			
			return;
		}
		
		SwingWorker sw = new SwingWorker()
		{
			@Override
			public Object construct()
			{
				SplashScreen.showSplash();
				
				DocumentFrame docFrame = createNewDocumentFrame();
				
				SplashScreen.hideSplash();
				
				return docFrame;
			}
			
			@Override
			public void finished()
			{
				DocumentFrame docFrame = (DocumentFrame) get();
				
				docFrame.setVisible(true);
				
				docFrame.requestFocusInWindow();
			}
		};
		
		sw.start();
	}
	
	//creates the new document frame
	private DocumentFrame createNewDocumentFrame()
	{ 
		DocumentFrame frame = new DocumentFrame();
		
		frame.setDocumentTitle(DEFAULT_DOCUMENT_TITLE + newDocumentCounter);
		
		newDocumentCounter++;
		
		windows.add(frame);
		
		return frame;
	}
	
	//exits all the window
	public void exitAll()
	{
		int response = -1;
		
		for(int i = windows.size() - 1;
				i >= 0 &&
				response != JOptionPane.CANCEL_OPTION; i--)
		{
			response = windows.get(i).closeDocument();
		}
	}
	
	//remove the document frame
	public void removeDocumentFrame(DocumentFrame frame)
	{
		windows.remove(frame);
		
		if(windows.size() == 0)
			System.exit(0);
	}
	
	
	public static GuiController getInstance()
	{
		if(instance == null)
			instance = new GuiController();
		
		return instance;
	}
}
