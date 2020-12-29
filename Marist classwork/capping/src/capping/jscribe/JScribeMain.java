package capping.jscribe;

import java.awt.EventQueue;

import javax.swing.JFrame;

import capping.jscribe.gui.GuiController;

/** 
 * The main JScribe
 * 
 */

public class JScribeMain {
	public static final String APP_NAME = "JScribe";
	public static final String APP_VER = "1.0 BETA"; // since Apr 30, 2007
	
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				GuiController.getInstance().showNewDocumentFrame();
			}
		});
	}
}
