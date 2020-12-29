package _mine.serverQuery.gui;

import static _mine.serverQuery.gui.ComponentFactory.createButton;
import static _mine.serverQuery.gui.ComponentFactory.createLabel;
import static _mine.serverQuery.gui.ComponentFactory.createTextField;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.gui.fileFilters.ETFileFilters;

/**
 * 
 * @author Dan
 * @verion May 20, 2006
 */
public class PathDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -637264166810018104L;

	private ServerQuery parent;
	
	private JFileChooser fc;
	
	private JLabel pathLabel;
	private JTextField pathTextField;
	
	private JButton okButton;
	private JButton cancelButton;
	private JButton browseButton;
	
	private String value;
	
	/**
	 * 
	 *
	 */
	public PathDialog(ServerQuery parent) {
		super();
		
		this.parent = parent;
		
		this.init();
	}
	
	/**
	 * 
	 *
	 */
	private void init() {
		value = parent.getExecutablePath();
		
		setModal(true);
		
		initComponents();
		initFrame();
	}
	
	/**
	 * 
	 *
	 */
	private void initComponents() {
		initPathText();
		initButtons();
	}
	
	/**
	 * 
	 *
	 */
	private void initPathText() {
		String toolTip = "This is the command that will be executed when " +
		"trying to connect to a server.";
		pathTextField = createTextField(toolTip);
		
		toolTip = "Current executable: ";
		pathLabel = createLabel(toolTip, pathTextField);
	}
	
	/**
	 * 
	 *
	 */
	private void initButtons() {
		String text = "OK";
		String toolTip = "Accept the current file.";
		okButton = createButton(text, toolTip, this);
		
		text = "Cancel";
		toolTip = "Close this dialog and abandon changes.";
		cancelButton = createButton(text, toolTip, this);
		
		text = "Browse...";
		toolTip = "Brose to the location of the executable.";
		browseButton = createButton(text, toolTip, this);
	}
	
	/**
	 * 
	 *
	 */
	private void initFrame() {
		setupLayoutManager();
		
		setTitle("Set executable path");
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				cancelAction();
			}
		});
		pack();
	}
	
	/**
	 * 
	 *
	 */
	private void setupLayoutManager() {
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.75;
		c.weighty = 0.75;
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(2, 2, 2, 2);
		
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(pathLabel, c);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx++;
		
		cp.add(pathTextField, c);
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy++;
		cp.add(okButton, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		cp.add(cancelButton, c);
		cp.add(browseButton, c);
	}
	
	/**
	 * 
	 *
	 */
	private void okAction() {
		applyAction();
	}
	
	/**
	 * 
	 *
	 */
	private void cancelAction() {
		value = null;
	}
	
	/**
	 * 
	 *
	 */
	private void browseAction() {
		if(fc == null) {
			fc = new JFileChooser();
			fc.setFileSelectionMode(
					JFileChooser.FILES_ONLY);
			
			FileFilter[] filters = ETFileFilters.getFilters();
			for(FileFilter f : filters)
				fc.setFileFilter(f);
		}
		
		File f = new File(pathTextField.getText());
		
		// traverses until a real directory,
		// or the file system's root is found
		
		try {
			while(!f.exists()) {
				f = f.getParentFile();
			}
		} catch(NullPointerException e) {
			f = new File(pathTextField.getText());
		}
		fc.setCurrentDirectory(f);
		
		int retState =
			fc.showOpenDialog(this);
		if(retState == JFileChooser.APPROVE_OPTION)
			pathTextField.setText(fc.getSelectedFile().toString());
	}
	
	/**
	 * 
	 *
	 */
	private void applyAction() {
		value = pathTextField.getText();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src == okButton) {
			okAction();
			dispose();
		} else if(src == cancelButton) {
			cancelAction();
			dispose();
		} else if(src == browseButton) {
			browseAction();
		}
	}
	
	/**
	 * 
	 *
	 */
	@Override
	public void setVisible(boolean newState) {
		if(newState)
			pathTextField.setText(parent.getExecutablePath());
		
		super.setVisible(newState);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static String getExecutablePath(ServerQuery parent) {
		return getExecutablePath(parent, parent.getGuiContext());
    }
	
	/**
	 * 
	 * @param parent
	 * @param parentComponent
	 * @return
	 */
	public static String getExecutablePath(ServerQuery parent,
			Component parentComponent) {
		if(parent == null)
			throw new NullPointerException("ServerQuery annot be null");
		
        PathDialog dlg = new PathDialog(parent);
        dlg.setLocationRelativeTo(parentComponent);
        dlg.setVisible(true);
        return dlg.getValue();
    }
}
