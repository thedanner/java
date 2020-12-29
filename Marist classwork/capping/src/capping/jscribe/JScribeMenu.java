/**
 * 
 */
package capping.jscribe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Craig
 *
 */
public class JScribeMenu extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Font normalFont;
	private Font smallFont;
	private JMenuBar mb/*, mb2*/;
	private JMenu theMenu, txtMenu, objMenu;
	private JMenuItem col, save, rest, exit, font, size,
	fColor, circle, ellipse, line, square, rectangle;
	private JTextArea theDisplay;
	private JScrollPane sp;
	private Container viewer;
	//private JFileChooser choose;
	
	
	public JScribeMenu()
	{
		viewer = getContentPane();
		normalFont = new Font("Serif", Font.PLAIN, 20);
		smallFont = new Font("Serif", Font.BOLD, 14);
		mb = new JMenuBar();
		//mb2 = new JMenuBar();
		theMenu = new JMenu("Menu for JScribe (v 0.0.1)");
		txtMenu = new JMenu("Text Menu");
		objMenu = new JMenu("Object Menu");
		col = new JMenuItem("Color Options");
		save = new JMenuItem("Save File");
		rest= new JMenuItem("Restore a Saved File");
		exit = new JMenuItem("Exit JScribe");
		font = new JMenuItem("Font Style");
		size = new JMenuItem("Font Size");
		fColor = new JMenuItem("Font Color");
		circle = new JMenuItem("Circle");
		ellipse = new JMenuItem("Ellipse");
		rectangle = new JMenuItem("Rectangle");
		line = new JMenuItem("Line");
		square = new JMenuItem("Square");
		
		
		theDisplay = new JTextArea(10, 40);
		sp = new JScrollPane(theDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		populateMenu();
	}
	
	private void populateMenu()
	{		
		setJMenuBar(mb);
		mb.add(theMenu);
		theMenu.setFont(normalFont);
		theMenu.add(txtMenu);
		txtMenu.addActionListener(this);
		theMenu.add(objMenu);
		objMenu.addActionListener(this);
		theMenu.add(col);
		col.addActionListener(this);
		theMenu.add(save);
		save.addActionListener(this);
		theMenu.add(rest);
		rest.addActionListener(this);
		
		theMenu.add(exit);
		exit.addActionListener(this);
		
		//XXX TEXT MENU ITEMS
		
		txtMenu.add(font);
		font.addActionListener(this);
		txtMenu.add(size);
		size.addActionListener(this);
		txtMenu.add(fColor);
		fColor.addActionListener(this);
		
		//XXX OBJECT MENU ITEMS
		
		objMenu.add(circle);
		circle.addActionListener(this);
		objMenu.add(ellipse);
		ellipse.addActionListener(this);
		objMenu.add(rectangle);
		rectangle.addActionListener(this);
		objMenu.add(line);
		line.addActionListener(this);
		objMenu.add(square);
		square.addActionListener(this);
		
		JPanel menuPanel = new JPanel();
		
		menuPanel.add(mb, BorderLayout.NORTH);		
		viewer.add(menuPanel, BorderLayout.WEST);	
		theDisplay.setFont(smallFont);
		viewer.add(sp, BorderLayout.CENTER);		
		setTitle("Menu for JScribe (v 0.0.1)");
		setSize(500, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource( ) == txtMenu) 
		{
			
			//String value = JOptionPane.showInputDialog(null, 
			//	"Enter the String to be inserted", 
			//"Insert Dialog Box", JOptionPane.QUESTION_MESSAGE);
			
		}
		
		else if (e.getSource( ) == objMenu) 
		{
			
			
		}
		
		else if (e.getSource() == col) 
		{
			JColorChooser.showDialog(null, "JScribe Colors", Color.white);			
		}
		
		else if (e.getSource() == save) 
		{
			JOptionPane.showMessageDialog(null, "Implement File Saving");
		}
		
		else if (e.getSource()== rest) 
		{
			JOptionPane.showMessageDialog(null, "Implement File Restoring");
		}
		
		else if (e.getSource() == exit) 
		{
			int retVal = JOptionPane.showConfirmDialog(null, "Do you wish to save before exiting?");
			
			if (retVal ==JOptionPane.OK_OPTION)
			{
				JOptionPane.showMessageDialog(null, "Implement File Saving");
			}
			
			else if (retVal == JOptionPane.NO_OPTION)
			{
				System.exit(0);
			}
			
			else if (retVal == JOptionPane.CANCEL_OPTION)
			{
				//DO NOTHING AND RETURN TO PROGRAM
			}
			
		}
				
	}
	
	/*private File chooseFileRestore()
	{
		int i = choose.showOpenDialog();
		
		if(i == JFileChooser.APPROVE_OPTION)
			return choose.getSelectedFile();
		
		return null;
	}*/
	
	/*private File chooseFileSave()
	{
		int i = choose.showSaveDialog();
		
		if(i == JFileChooser.APPROVE_OPTION)
			return choose.getSelectedFile();
		
		return null;
	}*/
	
	public static void main(String[] args)
	{
		/*JScribeMenu menu = */new JScribeMenu();
		//menu.setVisible(true);
	}
	
}
