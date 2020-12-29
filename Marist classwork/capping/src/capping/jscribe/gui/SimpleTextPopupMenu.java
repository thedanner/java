package capping.jscribe.gui;

import static java.awt.event.KeyEvent.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class SimpleTextPopupMenu extends JPopupMenu
implements ActionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2678569742201756277L;
	
	private JTextComponent textComp;
	
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem selectAllMenuItem;
	
	SimpleTextPopupMenu(JTextComponent textComp) {
		super();
		
		this.textComp = textComp;
		
		textComp.addMouseListener(this);
		
		init();
	}
	
	private void init() {
		undoMenuItem = createMenuItem("Undo", VK_U);
		redoMenuItem = createMenuItem("Redo");
		
		cutMenuItem = createMenuItem("Cut", VK_X);
		copyMenuItem = createMenuItem("Copy", VK_C);
		pasteMenuItem = createMenuItem("Paste", VK_V);
		deleteMenuItem = createMenuItem("Delete", VK_D);
		
		selectAllMenuItem = createMenuItem("Select All", VK_A);
		
		// post init, before adding the menu items
		undoMenuItem.setEnabled(false);
		redoMenuItem.setEnabled(false);
		
		// now add everything
		add(undoMenuItem);
		add(redoMenuItem);
		
		addSeparator();
		add(cutMenuItem);
		add(copyMenuItem);
		add(pasteMenuItem);
		add(deleteMenuItem);
		
		addSeparator();
		add(selectAllMenuItem);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		//XXX add event handlers
		if(undoMenuItem == src) {
			
		} else if(redoMenuItem == src) {
		
		} else if(cutMenuItem == src) {
			textComp.cut();
		} else if(copyMenuItem == src) {
			textComp.copy();
		} else if(pasteMenuItem == src) {
			textComp.paste();
		} else if(deleteMenuItem == src) {
			try {
				textComp.getDocument().remove(getStart(), getLen());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} else if(selectAllMenuItem == src) {
			textComp.selectAll();
		}
	}
	
	private int getStart() {
		return textComp.getSelectionStart();
	}
	
	private int getLen() {
		return textComp.getSelectionEnd() - getStart();
	}
	
	private void updateEnabledMenuItems() {
		//XXX set undo/redo enabled or disabled as appropriate
		
		if(getLen() > 0) {
			cutMenuItem.setEnabled(true);
			copyMenuItem.setEnabled(true);
			deleteMenuItem.setEnabled(true);
		} else {
			cutMenuItem.setEnabled(false);
			copyMenuItem.setEnabled(false);
			deleteMenuItem.setEnabled(false);
		}
	}
	
	// methods for the listener required to show the popup menu
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if(e.isPopupTrigger()) {
			updateEnabledMenuItems();
			
			show(e.getComponent(),
					e.getX(), e.getY());
		}
	}
	
	// factory methods
	public JMenuItem createMenuItem(String text) {
		return createMenuItem(text, null);
	}
	
	public JMenuItem createMenuItem(String text, Integer mnemonic) {
		JMenuItem item = new JMenuItem(text);
		
		item.addActionListener(this);
		
		if(mnemonic != null)
			item.setMnemonic(mnemonic);
		
		return item;
	}
}
