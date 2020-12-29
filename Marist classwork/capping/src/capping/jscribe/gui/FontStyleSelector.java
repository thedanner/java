/* FontStyleSelector.java */
package capping.jscribe.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * 
 * @author Dan Mangiarelli
 */
public class FontStyleSelector extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3558299284178689593L;
	
	JCheckBox boldCheckbox;
	JCheckBox italicCheckbox;
	
	JCheckBox underlineCheckbox;
	
	public FontStyleSelector(ActionListener al) {
		super(new GridLayout(0, 1));
		
		boldCheckbox = new JCheckBox("<html><b>Bold</b></html>");
		boldCheckbox.addActionListener(al);
		boldCheckbox.setName("bold");
		
		italicCheckbox = new JCheckBox("<html><i>Italic</i></html>");
		italicCheckbox.addActionListener(al);
		boldCheckbox.setName("italic");
		
		underlineCheckbox = new JCheckBox("<html><u>Underline</u></html>");
		underlineCheckbox.addActionListener(al);
		underlineCheckbox.setName("underline");
		
		add(boldCheckbox);
		add(italicCheckbox);
		add(underlineCheckbox);
	}
	
	//set the font style
	public void setStyle(int style) {
		int both = Font.BOLD + Font.ITALIC;
		
		boolean bold = style == Font.BOLD || style == both;
		boolean italics = style == Font.ITALIC || style == both;
		
		boldCheckbox.setSelected(bold);
		italicCheckbox.setSelected(italics);
	}
	
	//returns the font style
	public int getStyle() {
		int style = 0;
		
		if(boldCheckbox.isSelected())
			style += Font.BOLD;
		
		if(italicCheckbox.isSelected())
			style += Font.ITALIC;
		
		return style;
	}
	
	//checks to see if the font is underlined
	public boolean isUnderlined() {
		return underlineCheckbox.isSelected();
	}
	
	//sets the font as underlined
	public void setUnderlined(boolean newState) {
		underlineCheckbox.setSelected(newState);
	}
}
