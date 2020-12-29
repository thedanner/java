package capping.jscribe.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialogs
{
	/*
	 * This will display a message if an error occurs during saving 
	 * a file. 
	 */
	public static void showSaveErrorDialog(Component parent, Exception e)
	{
		showErrorDialog(parent,
				"An error has occured while saving the file:\n" +
				e.getMessage(),
				"Error saving file"
				);
	}
	
	/*
	 * This will display an error message if a file the user is 
	 * attempting to restore is missing or nonexistant.
	 */			
	public static void showFileNotFoundErrorDialog(
			Component parent, Exception e)
	{
		showErrorDialog(parent,
				"The file cannot be found:\n" +
				e.getMessage(),
				"File not found"
				);
	}
	
	/*
	 * This will display an error if a file cannot be opened in JScribe.
	 */
	public static void showOpenErrorDialog(
			Component parent, Exception e)
	{
		showErrorDialog(parent,
				"An error has occured while opening the file:\n" +
				e.getMessage(),
				"Error opening file"
				);
	}
	
	/*
	 * This will display an error if the file a user is attempting to
	 * restore is corrupt.
	 */
	public static void showCorrupedFileDialog(
			Component parent, Exception e)
	{
		showErrorDialog(parent,
				"The file is malformed or corrupted:\n" +
				e.getMessage(),
				"File corrupted"
				);
	}
	
	/*
	 * This will display a generic error message if the error does not
	 * fit any of the criteria of the above.
	 */
	public static void showErrorDialog(
			Component parent, Object message, String title)
	{
		JOptionPane.showMessageDialog(parent,
				message.toString(),
				title,
				JOptionPane.ERROR_MESSAGE
				);
	}
}
