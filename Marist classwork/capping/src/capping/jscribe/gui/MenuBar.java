package capping.jscribe.gui;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_F1;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class MenuBar extends JMenuBar implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4673710902986707239L;
	
	private static Vector<MenuBar> instances = new Vector<MenuBar>();
	
	private DocumentFrame docFrame;
	
	private JMenu fileMenu;
	private JMenu newMenu;
	private JMenuItem newTabMenuItem;
	private JMenuItem newWindowMenuItem;
	private JMenuItem closeTabMenuItem;
	private JMenuItem closeWindowMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem printMenuItem;
	private JMenuItem exitProgramMenuItem;
	
	private JMenu editMenu;
	//private JMenuItem undoMenuItem;
	//private JMenuItem redoMenuItem;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem selectAllMenuItem;
	
	private JMenu helpMenu;
	private JMenuItem helpMenuItem;
	private JMenuItem aboutMenuItem;
	
	MenuBar(DocumentFrame docFrame)
	{
		super();
		
		instances.add(this);
		
		if(docFrame == null)
			throw new NullPointerException("docFrame (arg0) is null");
		
		this.docFrame = docFrame;
		
		initMenus();
	}
	
	//initialize the menu items
	private void initMenus()
	{
		initFileMenu();
		initEditMenu();
		initHelpMenu();
		
		
		setCopyEnabled(false);
		
		setPasteEnabled(DocumentFrame.clipboardHasContents());
	}
	
	//create the File Menu
	private void initFileMenu() 
	{
		fileMenu = createMenu("File", VK_F);
		
		newMenu = createMenu("New", VK_N);
		
		newWindowMenuItem = createMenuItem("Window", VK_N, CTRL_DOWN_MASK);
		newTabMenuItem = createMenuItem("Tab", VK_T, CTRL_DOWN_MASK);
		
		closeTabMenuItem = createMenuItem("Close Tab", VK_W, CTRL_DOWN_MASK);
		closeWindowMenuItem = createMenuItem("Close Window");
		
		openMenuItem = createMenuItem("Open", VK_O, CTRL_DOWN_MASK);
		saveMenuItem = createMenuItem("Save", VK_S, CTRL_DOWN_MASK);
		saveAsMenuItem = createMenuItem("Save As...", VK_A,
				CTRL_DOWN_MASK + SHIFT_DOWN_MASK);
		printMenuItem = createMenuItem("Print...", VK_P, CTRL_DOWN_MASK);
		
		exitProgramMenuItem = createMenuItem("Exit Program");
		
		newMenu.add(newWindowMenuItem);
		newMenu.add(newTabMenuItem);
		
		// now add everything
		fileMenu.add(newMenu);
		fileMenu.add(openMenuItem);
		
		fileMenu.addSeparator();
		fileMenu.add(closeTabMenuItem);
		fileMenu.add(closeWindowMenuItem);
		
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		
		fileMenu.addSeparator();
		fileMenu.add(printMenuItem);
		
		fileMenu.addSeparator();
		fileMenu.add(exitProgramMenuItem);
		
		add(fileMenu);
	}
	
	//create the Edit menu
	private void initEditMenu() {
		editMenu = createMenu("Edit", VK_E);
		
		
		cutMenuItem = createMenuItem("Cut", VK_X, CTRL_DOWN_MASK);
		copyMenuItem = createMenuItem("Copy", VK_C, CTRL_DOWN_MASK);
		pasteMenuItem = createMenuItem("Paste", VK_V, CTRL_DOWN_MASK);
		deleteMenuItem = createMenuItem("Delete", VK_D,
				createKeyStroke(VK_DELETE, 0));
		
		selectAllMenuItem = createMenuItem("Select All", VK_A, CTRL_DOWN_MASK);		
		
		
		editMenu.add(cutMenuItem);
		editMenu.add(copyMenuItem);
		editMenu.add(pasteMenuItem);
		editMenu.add(deleteMenuItem);
		
		editMenu.addSeparator();
		editMenu.add(selectAllMenuItem);
		
		add(editMenu);
	}
	
	//create Help Menu
	private void initHelpMenu() {
		helpMenu = createMenu("Help", VK_H);
		
		helpMenuItem = createMenuItem("Help", VK_H,
				KeyStroke.getKeyStroke(VK_F1, 0));
		aboutMenuItem = createMenuItem("About", VK_A);
		
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);
		
		add(helpMenu);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		fileMenuActionPerformed(e);
		editMenuActionPerformed(e);
	}
	
	//action performed for File menu
	private void fileMenuActionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if(newTabMenuItem == src)
		{
			docFrame.openNewTab();
		}
		
		else if(newWindowMenuItem == src)
		{
			GuiController.getInstance().showNewDocumentFrame();
		}
		
		else if(closeTabMenuItem == src)
		{
			docFrame.closeCurrentTab();
		}
		
		else if(closeWindowMenuItem == src)
		{
			docFrame.closeDocument();
		}
		
		else if(openMenuItem == src)
		{
			docFrame.openDocument();
		}
		
		else if(saveMenuItem == src)
		{
			docFrame.saveDocument();
		}
		
		else if(saveAsMenuItem == src)
		{
			docFrame.saveDocumentAs();
		}
		
		else if(printMenuItem == src)
		{
			docFrame.printDocument();
		} 
		
		else if(exitProgramMenuItem == src)
		{
			GuiController.getInstance().exitAll();
		}
		
		else if(helpMenuItem == src)
		{
			JOptionPane.showMessageDialog(docFrame,
					"Sorry, we were unable to finish the " +
					"help option in the alloted time.\nPlease " +
					"try again in the next release!\n\nThank you " +
					"again for choosing JScribe!", "Help", JOptionPane.INFORMATION_MESSAGE);
		}
		
		else if(aboutMenuItem == src)
		{			
			JOptionPane.showMessageDialog(docFrame,
					"JScribe\n\nCreated by:\n\n" +
					"Dan Mangiarelli\nCraig Fargione\n" +
					"Peter Fela\nBryan Masson\n\n" +
					"Thank you for choosing JScribe, have a nice day!",
					"About",
					JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	//action performed for Edit menu
	private void editMenuActionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		
		if(cutMenuItem == src)
		{
			docFrame.cut();
		}
		
		else if(copyMenuItem == src)
		{
			docFrame.copy();
		}
		
		else if(pasteMenuItem == src)
		{
			docFrame.paste();
		}
		
		else if(deleteMenuItem == src)
		{
			docFrame.delete();
		}
		
		else if(selectAllMenuItem == src)
		{
			docFrame.selectAll();
		}
	}
	
	
	//enable copy
	public void setCopyEnabled(boolean enabled)
	{
		copyMenuItem.setEnabled(enabled);
		cutMenuItem.setEnabled(enabled);
	}
	
	//enable paste
	public void setPasteEnabled(boolean enabled)
	{
		for(MenuBar menu : instances)
			menu.pasteMenuItem.setEnabled(enabled);
	}
	
	// factory methods
	public KeyStroke createKeyStroke(int keyCode, int modifiers)
	{
		return KeyStroke.getKeyStroke(keyCode, modifiers);
	}
	
	public JMenu createMenu(String text)
	{
		return createMenu(text, null);
	}
	
	public JMenu createMenu(String text, Integer mnemonic)
	{
		JMenu menu = new JMenu(text);
		
		menu.addActionListener(this);
		
		if(mnemonic != null)
			menu.setMnemonic(mnemonic);
		
		return menu;
	}
	
	public JMenuItem createMenuItem(String text)
	{
		return createMenuItem(text, null, null);
	}
	
	public JMenuItem createMenuItem(String text, Integer mnemonic)
	{
		return createMenuItem(text, mnemonic, null);
	}
	
	public JMenuItem createMenuItem(
			String text, Integer mnemonic, int accelMod)
	{
		
		return createMenuItem(
				text, mnemonic, createKeyStroke(mnemonic, accelMod));
	}
	
	public JMenuItem createMenuItem(
			String text, Integer mnemonic, KeyStroke accel)
	{
		JMenuItem item = new JMenuItem(text);
		
		item.addActionListener(this);
		
		if(mnemonic != null) {
			item.setMnemonic(mnemonic);
			item.setAccelerator(accel);
		}
		
		return item;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		instances.remove(this);
		
		super.finalize();
	}
}
