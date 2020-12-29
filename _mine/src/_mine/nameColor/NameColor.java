/* NameColor.java */
package _mine.nameColor;

import java.awt.GridLayout;

import javax.swing.JApplet;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Dan
 * @version Apr 22, 2006
 */
public class NameColor extends JApplet implements DocumentListener
{
	private static final long serialVersionUID = -3923980036969666691L;
	
	private JTextField nameField;
	
	private NameDisplay nameDisplay;
	
	private int width;
	
	private int height;
	
	/** Initializes the applet NameColor */
	@Override
	public void init()
	{
		// Execute a job on the event-dispatching thread:
		// creating this applet's GUI.
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					createGUI();
				}
			});
		}
		catch (Exception e)
		{
			System.err.println("createGUI didn't successfully complete");
			e.printStackTrace();
		}
	}
	
	@Override
	public void start()
	{
		System.gc();
	}
	
	@Override
	public void destroy()
	{
		nameField = null;
		nameDisplay = null;
	}
	
	public void createGUI()
	{
		width = 400;
		height = 80;
		
		resize(width, height); // doesn't work in browsers
		setLayout(new GridLayout(2, 1));
		
		nameDisplay = new NameDisplay(this);
		
		add(nameDisplay);
		
		// text field for the name
		nameField = new JTextField("^4Insert ^@Your ^#Name ^1Here.");
		nameField.getDocument().addDocumentListener(this);
		
		add(nameField);
	}
	
	public synchronized String getNameText()
	{
		try
		{
			return nameField.getText();
		}
		catch (NullPointerException e)
		{
			return "";
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent e)
	{
		documentChanged();
	}
	
	@Override
	public void insertUpdate(DocumentEvent e)
	{
		documentChanged();
	}
	
	@Override
	public void removeUpdate(DocumentEvent e)
	{
		documentChanged();
	}
	
	private void documentChanged()
	{
		nameDisplay.repaint();
	}
}
