/* Keybord.java */
package _mine.serverQuery.util;

import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_F1;
import static java.awt.event.KeyEvent.VK_F2;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.gui.Dialog;
import _mine.serverQuery.net.ConnectionManager;

/**
 * @author Dan
 * @version June 27, 2005
 */
public class Keyboard implements KeyListener {
	/*  */
	private ServerQuery parent;
	
	/*  */
	private Dialog dialog;
	
	/*  */
	private ConnectionManager cm;
	
	/**
	 * 
	 * @param parent
	 * @param dialog
	 * @param cm
	 */
	public Keyboard(ServerQuery parent, Dialog dialog, ConnectionManager cm) {
		this.parent = parent;
		this.dialog = dialog;
		this.cm = cm;
	}
	
	/**
	 * 
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == VK_LEFT) {
			parent.adjustMinServerIndex(-1);
			return;
		}
		if(key == VK_RIGHT) {
			parent.adjustMinServerIndex(1);
			return;
		}
		if(key == VK_F1) {
			Dialog.showHelpDialog(parent);
			return;
		}
		if(key == VK_F2) {
			Dialog.showAboutDialog(parent, parent.getVersion());
			return;
		}
		if(key == VK_S) {
			dialog.showPathAction();
			return;
		}
		if(key == VK_C) {
			parent.chooseExecutable();
			return;
		}
		if(key == VK_L) {
			dialog.listPlayers(cm.getServerIPs(),
					cm.getServers(), cm.getPlayers());
			return;
		}
		if(cm.listIsGood() && (
				key >= VK_1 &&
				key < 
					Math.min(VK_9, VK_1 + cm.getServerIPs().length))
		) {
			joinServerAction(key - VK_1);
			return;
		}
	}
	
	/**
	 * 
	 * @param index
	 */
	public void joinServerAction(int index) {
		parent.connectToServer(index);
	}
	
	/**
	 * 
	 */
	@Override
	public void keyTyped(KeyEvent e) {}
	
	/**
	 * 
	 */
	@Override
	public void keyReleased(KeyEvent e) {}
}
