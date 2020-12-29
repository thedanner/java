/* CompFactory.java */
package _mine.serverQuery.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import _mine.serverQuery.util.Launcher;

/**
 * 
 * @author Dan
 * @version Jan 21, 2006
 */
public class ComponentFactory {
	/**
	 * 
	 * @param mn
	 * @param text
	 * @return
	 */
	public static JMenu createMenu(int mn, String text) {
		return createMenu(mn, text, null);
	}
	
	/**
	 * 
	 * @param mn
	 * @param text
	 * @param toolTip
	 * @return
	 */
	public static JMenu createMenu(int mn, String text, String toolTip) {
		JMenu menu = new JMenu(text);
		
		menu.setMnemonic(mn);
		menu.setToolTipText(toolTip);
		
		return menu;
	}
	
	/**
	 * 
	 * @param mn
	 * @param text
	 * @param al
	 * @return
	 */
	public static JMenuItem createMenuItem(
			int mn, String text, ActionListener al) {
		return createMenuItem(mn, text, null, al);
	}
	
	/**
	 * 
	 * @param mn
	 * @param text
	 * @param toolTip
	 * @param al
	 * @return
	 */
	public static JMenuItem createMenuItem(
			int mn, String text, String toolTip, ActionListener al) {
		JMenuItem menuItem = new JMenuItem(text, mn);
		
		menuItem.setToolTipText(toolTip);
		menuItem.addActionListener(al);
		
		return menuItem;
	}
	
	/**
	 * 
	 * @param mn
	 * @param text
	 * @param toolTip
	 * @return
	 */
	public static JMenuItem createURLMenuItem(
			int mn, String text, String toolTip) {
		JMenuItem menuItem = new JMenuItem();
		
		menuItem.setMnemonic(mn);
		menuItem.setText(text);
		menuItem.setToolTipText(toolTip);
		
		final String url = toolTip; 
		
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Launcher.launchURL(url);
			}
		});
		return menuItem;
	}
	
	/**
	 * 
	 *
	 */
	public static JSeparator createSeparator() {
		return new JSeparator();
	}
	
	/**
	 * 
	 * @param text
	 * @param toolTipText
	 * @return
	 */
	public static JLabel createLabel(String text, Component labelFor) {
		JLabel label = new JLabel(text, JLabel.RIGHT);
		label.setLabelFor(labelFor);
		return label;
	}
	
	/**
	 * 
	 * @param text
	 * @param toolTip
	 * @return
	 */
	public static JButton createButton(
			String text, String toolTip, ActionListener l) {
		JButton button = new JButton(text);
		button.setToolTipText(toolTip);
		button.addActionListener(l);
		return button;
	}
	
	/**
	 * 
	 * @param model
	 * @param toolTip
	 * @return
	 */
	public static JSpinner createSpinner(SpinnerModel model, String toolTip) {
		JSpinner spinner = new JSpinner(model);
		spinner.setToolTipText(toolTip);
		return spinner;
	}
	
	/**
	 * 
	 * @param tooltip
	 * @return
	 */
	public static JTextField createTextField(String tooltip) {
		return createTextField(null, tooltip);
	}
	
	/**
	 * 
	 * @param text
	 * @param toolTip 
	 * @return
	 */
	public static JTextField createTextField(String text, String toolTip) {
		JTextField tf = new JTextField(text, 15);
		tf.setToolTipText(toolTip);
		return tf;
	}
	
	/**
	 * 
	 * @param items
	 * @param initiallySelected
	 * @return
	 */
	public static JComboBox createComboBox(
			Object[] items, int initiallySelected) {
		JComboBox comboBox = new JComboBox(items);
		comboBox.setSelectedIndex(initiallySelected);
		return comboBox;
	}
	
	/**
	 * 
	 * @param val
	 * @param min
	 * @param max
	 * @return
	 */
	public static SpinnerNumberModel createSpinnerModel(
			int val, int min, int max) {
		return createSpinnerModel(val, min, max, 1);
	}
	
	/**
	 * 
	 * @param val
	 * @param min
	 * @param max
	 * @param step
	 */
	public static SpinnerNumberModel createSpinnerModel(
			int val, int min, int max, int step) {
		return new SpinnerNumberModel(val, min, max, step);
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static JCheckBox createCheckBox(String text) {
		return new JCheckBox(text);
	}
}
