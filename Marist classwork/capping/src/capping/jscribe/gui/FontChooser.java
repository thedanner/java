/* FontChooser.java */
package capping.jscribe.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Dan Mangiarelli
 */
public class FontChooser extends JDialog implements ActionListener {
	private static final long serialVersionUID = -242025161961090379L;
	
	// These fields define the component properties
	String family;           // The name of the font family
	int style;               // The font style
	int size;                // The font size
	Font selectedFont;       // The Font they correspond to
	
	// This is the list of all font families on the system
	String[] fontFamilies;
	
	// The various Swing components used in the dialog
	JComboBox families;
	FontStyleSelector styles;
	JComboBox sizes;
	
	JTextPane preview;
	
	JTextField textEntryField;
	JButton approveButton;
	JButton cancelButton;
	
	// The size "names" to appear in the size menu
	static final String[] DEFAULT_SIZES = new String[] {
		"8", "10", "12", "14", "18", "20", "24", "28", "32", 
		"40", "48", "56", "64", "72"
	};
	
	// This is the default preview string displayed in the dialog box
	static final String defaultPreviewString = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" + 
		"abcdefghijklmnopqrstuvwxyz\n" + 
		"1234567890!@#$%^&*()_-=+[]{}<,.>\n" +
		"The quick brown fox jumps over the lazy dog.";
	
	public FontChooser() {
		this(null);
	}
	
	/** Create a font chooser dialog for the specified frame. */
	public FontChooser(Frame owner) {
		super(owner, "Font");
		
		// This dialog must be used as a modal dialog.  In order to be used
		// as a modeless dialog, it would have to fire a PropertyChangeEvent
		// whenever the selected font changed, so that applications could be 
		// notified of the user's selections.
		setModal(true);
		
		// Figure out what fonts are available on the system
		GraphicsEnvironment env =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		fontFamilies = env.getAvailableFontFamilyNames();
		
		// Set initial values for the properties
		family = fontFamilies[0];
		style = Font.PLAIN;
		size = 18;
		selectedFont = new Font(family, style, size);
		
		// Create Swing objects that allow the iser to select font family,
		// style, and size.
		families = createComboBox(fontFamilies, 0);
		JPanel familiesPanel = createPanel("Font: ", families);
		
		styles = new FontStyleSelector(this);
		JPanel stylesPanel = createPanel("Style: ", styles);
		
		sizes = createComboBox(DEFAULT_SIZES, 4);
		sizes.setEditable(true);
		JPanel sizesPanel = createPanel("Size: ", sizes);
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		topPanel.add(familiesPanel);
		topPanel.add(stylesPanel);
		topPanel.add(sizesPanel);
		
		// Create a component to preview the font.
		preview = new JTextPane();
		preview.setText(defaultPreviewString);
		preview.setFont(selectedFont);
		preview.setEditable(false);
		
		textEntryField = new JTextField(15);
		textEntryField.setFont(selectedFont);
		textEntryField.setEditable(true);
		
		// Add a default style with no special formatting.
		preview.addStyle("Plain", null);
		
		// Add a style which underlines text.
		Style u = preview.addStyle("Underline", null);
		StyleConstants.setUnderline(u, true);
		
		// Create buttons to dismiss the dialog, and set handlers on them
		approveButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		
		approveButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		bottomPanel.add(new JLabel("Enter text: ", JLabel.RIGHT));
		bottomPanel.add(textEntryField);
		bottomPanel.add(approveButton);
		bottomPanel.add(cancelButton);
		
		// Put the choosers at the top, the buttons at the bottom, and
		// the preview in the middle.
		Container contentPane = getContentPane();
		contentPane.add(new JScrollPane(preview), BorderLayout.CENTER);
		contentPane.add(topPanel, BorderLayout.NORTH);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		pack();
	}
	
	/**
	 * Call this method after show() to obtain the user's selection.  If the
	 * user used the "Cancel" button, this will return null
	 **/
	public Font getSelectedFont() {
		return selectedFont;
	}
	
	// These are other property getter methods
	public String getFontFamily() {
		return family;
	}
	
	public int getFontStyle() {
		return style;
	}
	
	public int getFontSize() {
		return size;
	}
	
	public String getEnteredText() {
		return textEntryField.getText();
	}
	
	public boolean isUnderlined() {
		return styles.underlineCheckbox.isSelected();
	}
	
	// The property setter methods are a little more complicated.
	// Note that none of these setter methods update the corresponding
	// ItemChooser components as they ought to.
	public void setFontFamily(String name) { 
		family = name; 
		changeFont();
	}
	
	public void setFontStyle(int style) {
		this.style = style;
		changeFont();
	}
	
	public void setFontSize(int size) {
		this.size = size;
		changeFont();
	}
	
	public void setEnteredText(String t) {
		textEntryField.setText(t);
	}
	
	public void setUnderlined(boolean newState) {
		styles.underlineCheckbox.setSelected(newState);
	}
	
	public void setSelectedFont(Font font) {
		selectedFont = font;
		family = font.getFamily();
		style = font.getStyle();
		size = font.getSize();
		preview.setFont(font);
		textEntryField.setFont(font);
	}
	
	// This method is called when the family, style, or size changes
	protected void changeFont() {
		selectedFont = new Font(family, style, size);
		preview.setFont(selectedFont);
		textEntryField.setFont(selectedFont);
		
		pack();
	}
	
	// Override this inherited method to prevent anyone from making us modeless
	@Override
	public boolean isModal() {
		return true;
	}
	
	public void showDialog() {
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src == approveButton)
			dispose();
		else if(src == families) {
			String family = (String) families.getSelectedItem();
			setFontFamily(family);
		}
		else if(src == styles.boldCheckbox ||
				src == styles.italicCheckbox) {
			setFontStyle(styles.getStyle());
		}
		else if(src == styles.underlineCheckbox) {
			StyledDocument doc = preview.getStyledDocument();
			
			if(styles.isUnderlined())
				doc.setCharacterAttributes(
						0, doc.getLength(),
						doc.getStyle("Underline"), true);
			else
				doc.setCharacterAttributes(
						0, doc.getLength(),
						doc.getStyle("Plain"), true);
		}
		else if(src == sizes) {
			String sizeStr = (String)sizes.getSelectedItem();
			
			try {
				int val = Integer.parseInt(sizeStr);
				setFontSize(val);
			} catch(NumberFormatException ex) {
				sizes.setSelectedItem(size + "");
			}
		}
		else if(src == approveButton) {
			dispose();
		}
		else if(src == cancelButton) {
			selectedFont = null;
			dispose();
		}
	}
	
	private JPanel createPanel(String labelText, Component comp) {
		JPanel jp = new JPanel(new BorderLayout(5, 5));
		
		jp.add(new JLabel(labelText, JLabel.RIGHT), BorderLayout.WEST);
		jp.add(comp, BorderLayout.CENTER);
		
		return jp;
	}
	
	private JComboBox createComboBox(String[] items, int defaultSelection) {
		JComboBox cb = new JComboBox(items);
		
		cb.setSelectedIndex(defaultSelection);
		cb.addActionListener(this);
		
		return cb;
	}
}
