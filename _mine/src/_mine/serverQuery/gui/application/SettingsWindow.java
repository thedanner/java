/* SettingsWindow.java */
package _mine.serverQuery.gui.application;

import static _mine.serverQuery.gui.ComponentFactory.createButton;
import static _mine.serverQuery.gui.ComponentFactory.createCheckBox;
import static _mine.serverQuery.gui.ComponentFactory.createComboBox;
import static _mine.serverQuery.gui.ComponentFactory.createLabel;
import static _mine.serverQuery.gui.ComponentFactory.createSpinner;
import static _mine.serverQuery.gui.ComponentFactory.createSpinnerModel;
import static _mine.serverQuery.gui.ComponentFactory.createTextField;
import static _mine.serverQuery.util.properties.Vars.KEY_LOCAL_PORT;
import static _mine.serverQuery.util.properties.Vars.KEY_ON_LAUNCH_ACTION;
import static _mine.serverQuery.util.properties.Vars.KEY_PATH_TO_EXECUTABLE;
import static _mine.serverQuery.util.properties.Vars.KEY_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.KEY_SHOW_CONFIRM_DIALOG;
import static _mine.serverQuery.util.properties.Vars.KEY_UPDATE_INTERVAL;
import static _mine.serverQuery.util.properties.Vars.MAXIMUM_LOCAL_PORT;
import static _mine.serverQuery.util.properties.Vars.MAXIMUM_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.MAXIMUM_UPDATE_INTERVAL;
import static _mine.serverQuery.util.properties.Vars.MINIMUM_LOCAL_PORT;
import static _mine.serverQuery.util.properties.Vars.MINIMUM_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.MINIMUM_UPDATE_INTERVAL;
import static _mine.serverQuery.util.properties.Vars.VALUE_EXECUTABLE_PATH;
import static _mine.serverQuery.util.properties.Vars.VALUE_LOCAL_PORT;
import static _mine.serverQuery.util.properties.Vars.VALUE_ON_LAUNCH_ACTION;
import static _mine.serverQuery.util.properties.Vars.VALUE_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.VALUE_SHOW_CONFIRM_DIALOG;
import static _mine.serverQuery.util.properties.Vars.VALUE_UPDATE_INTERVAL;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.WindowConstants;

import _mine.serverQuery.application.Frame;
import _mine.serverQuery.net.ConnectionManager;
import _mine.serverQuery.util.CommonMethods;
import _mine.serverQuery.util.properties.PropertyManager;

/**
 * This is the GUI window that is shown when configuring the application version
 * of the <tt>ServerQuery</tt> program's settings.
 * 
 * @author Dan
 * @version May 11, 2006
 */
public class SettingsWindow extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -8513754107437901731L;
	
	public static final int ON_LAUNCH_DO_NOTHING = 0;
	public static final int ON_LAUNCH_MINIMIZE = 1;
	public static final int ON_LAUNCH_QUIT = 2;
	public static final int ON_LAUNCH_DEFAULT = ON_LAUNCH_DO_NOTHING;
	
	private String[] onLaunchChoicesText;
	
	private PropertyManager properties;
	
	private Frame parent;
	
	private ConnectionManager cm;
	
	private int onLaunchAction;
	
	private boolean locationSet;
	
	// gui elements
	/* the standalone frame to place the panel in */
	private JFrame frame;
	
	/* "servers to show" label and spinner */
	private JLabel serversToShowLabel;
	private JSpinner serversToShowSpinner;
	
	/* executable field label, text field, and browse button */
	private JLabel pathLabel;
	private JTextField pathTextField;
	private JButton browseButton;
	
	/* local port label and spinner */
	private JLabel localPortLabel;
	private JSpinner localPortSpinner;
	
	/* update interval label and spinner */
	private JLabel updateIntervalLabel;
	private JSpinner updateIntervalSpinner;
	
	/* on launch operation */
	private JLabel onLaunchLabel;
	private JComboBox onLaunchChoices;
	
	/* show confirm dialog */
	private JLabel showConfirmLabel;
	private JCheckBox showConfirmCheckBox;
	
	/* buttons */
	private JButton confirmButton;
	private JButton cancelButton;
	private JButton applyButton;
	private JButton discardChangesButton;
	private JButton restoreDefaultsButton;
	
	public SettingsWindow(Frame parent, PropertyManager properties)
	{
		super();
		
		this.parent = parent;
		this.properties = properties;
		
		init();
	}
	
	private void init()
	{
		this.cm = parent.getConnectionManager();
		this.onLaunchAction = parent.getOnLaunchAction();
		this.locationSet = false;
		
		this.onLaunchChoicesText = new String[]
		{ "Do nothing (default)", "Minimize", "Quit" };
		
		initComponents();
		initFrame();
		updateValues();
	}
	
	private void initComponents()
	{
		initServersToShow();
		initExecutablePath();
		initLocalPort();
		initUpdateInterval();
		initOnLaunch();
		initShowConfirmDialog();
		initButtons();
	}
	
	private void initFrame()
	{
		setupLayoutManager();
		
		frame = new JFrame();
		frame.setTitle("Edit Settings");
		frame.setContentPane(this);
		
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				cancelAction();
			}
		});
		frame.pack();
	}
	
	private void initServersToShow()
	{
		int serverCount = cm.getServerCount();
		if (serverCount == 0)
			serverCount = MAXIMUM_SERVERS_TO_SHOW;
		
		String text = "Servers to show at once:  ";
		String toolTip = "Servers to show at once, "
				+ "essentially the size of the window.  [" + "Default: "
				+ VALUE_SERVERS_TO_SHOW + ", minimum: "
				+ MINIMUM_SERVERS_TO_SHOW + ", maximum: "
				+ MAXIMUM_SERVERS_TO_SHOW + "]";
		
		int serversToShow = parent.getServersToShow();
		SpinnerModel model = createSpinnerModel(serversToShow,
				MINIMUM_SERVERS_TO_SHOW, MAXIMUM_SERVERS_TO_SHOW);
		serversToShowSpinner = createSpinner(model, toolTip);
		serversToShowLabel = createLabel(text, serversToShowSpinner);
	}
	
	private void initExecutablePath()
	{
		String text = "Enemy Territory executable:  ";
		String toolTip = "The executable to use when connecting to a server.";
		
		String executablePath = parent.getExecutablePath();
		pathTextField = createTextField(executablePath, toolTip);
		pathLabel = createLabel(text, pathTextField);
		
		text = "Change... ";
		toolTip = "Browse to select the executable file.";
		browseButton = createButton(text, toolTip, this);
	}
	
	private void initLocalPort()
	{
		int min = MINIMUM_LOCAL_PORT;
		int max = MAXIMUM_LOCAL_PORT;
		int defn = VALUE_LOCAL_PORT;
		int localPort = getVal(cm.getLocalPort(), min, max, defn);
		
		String text = "Local port:  ";
		String toolTip = "The local port to initiate"
				+ "connections to the servers with.  [" + "Default: " + defn
				+ ", " + "minimum: " + min + ", " + "maximum: " + max + "]";
		
		SpinnerModel model = createSpinnerModel(localPort, min, max);
		localPortSpinner = createSpinner(model, toolTip);
		localPortLabel = createLabel(text, localPortSpinner);
	}
	
	private void initUpdateInterval()
	{
		int min = MINIMUM_UPDATE_INTERVAL;
		int max = MAXIMUM_UPDATE_INTERVAL;
		int defn = VALUE_UPDATE_INTERVAL;
		int updateInterval = getVal(cm.getUpdateInterval(), min, max, defn);
		
		String text = "Update interval:  ";
		String toolTip = "The frequency, in seconds, that information is "
				+ "refreshed from the servers.  [" + "Default: " + defn
				+ ", minimum: " + min + ", maximim: " + max + "]";
		
		SpinnerModel model = createSpinnerModel(updateInterval, min, max);
		updateIntervalSpinner = createSpinner(model, toolTip);
		updateIntervalLabel = createLabel(text, updateIntervalSpinner);
	}
	
	private void initOnLaunch()
	{
		String text = "On launch:  ";
		
		onLaunchChoices = createComboBox(onLaunchChoicesText, onLaunchAction);
		onLaunchLabel = createLabel(text, onLaunchChoices);
	}
	
	private void initShowConfirmDialog()
	{
		String text = "Show confirmation dialog when joining?  ";
		String text2 = "If checked, a prompt will be shown when joining a server";
		
		showConfirmCheckBox = createCheckBox(text2);
		showConfirmLabel = createLabel(text, showConfirmCheckBox);
	}
	
	private void initButtons()
	{
		// save button
		String text = "Save changes";
		String toolTip = "Verify and apply changes, and close this window.";
		confirmButton = createButton(text, toolTip, this);
		
		// cancel button
		text = "Cancel";
		toolTip = "Discard unapplied changes and close this window.";
		cancelButton = createButton(text, toolTip, this);
		
		// apply button
		text = "Apply";
		toolTip = "Verify and apply changes.";
		applyButton = createButton(text, toolTip, this);
		
		// discard button
		text = "Discard changes";
		toolTip = "Discards unapplied changes.";
		discardChangesButton = createButton(text, toolTip, this);
		
		// defualts button
		text = "Restore defaults";
		toolTip = "Resets the settings to their defaults";
		restoreDefaultsButton = createButton(text, toolTip, this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if (src == browseButton)
		{
			changeButtonAction();
		}
		else if (src == confirmButton)
		{
			saveAction();
		}
		else if (src == cancelButton)
		{
			cancelAction();
		}
		else if (src == applyButton)
		{
			applyAction();
		}
		else if (src == discardChangesButton)
		{
			discardChanges();
		}
		else if (src == restoreDefaultsButton)
		{
			restoreDefaults();
		}
	}
	
	private void setupLayoutManager()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.75;
		c.weighty = 0.75;
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(2, 1, 1, 2);
		
		// first row, 2 components
		c.gridx = 0;
		c.gridy = 0;
		add(serversToShowLabel, c);
		
		c.gridx = 1;
		c.gridwidth = 2;
		add(serversToShowSpinner, c);
		
		// second row, 3 components
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 1;
		add(pathLabel, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		add(pathTextField, c);
		
		c.gridx = 2;
		c.gridy = 1;
		add(browseButton, c);
		
		// third row, 2 components
		c.gridx = 0;
		c.gridy = 2;
		add(localPortLabel, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = 2;
		add(localPortSpinner, c);
		
		// fourth row, 2 components
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 3;
		add(updateIntervalLabel, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = 2;
		add(updateIntervalSpinner, c);
		
		// fifth row
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 4;
		add(onLaunchLabel, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = 2;
		add(onLaunchChoices, c);
		
		// sixth row
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 5;
		add(showConfirmLabel, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = 2;
		add(showConfirmCheckBox, c);
		
		// second to last row, 3 components
		c.gridwidth = 1;
		c.ipady = 10;
		
		c.gridx = 0;
		c.gridy++;
		add(confirmButton, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		add(cancelButton, c);
		
		add(applyButton, c);
		
		// last row, 2 components
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridx = 0;
		c.gridy++;
		add(discardChangesButton, c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		add(restoreDefaultsButton, c);
	}
	
	private void updateValues()
	{
		int val = properties.getInt(KEY_SERVERS_TO_SHOW, VALUE_SERVERS_TO_SHOW);
		serversToShowSpinner.setValue(new Integer(val));
		
		String text = properties.get(KEY_PATH_TO_EXECUTABLE,
				VALUE_EXECUTABLE_PATH);
		pathTextField.setText(text);
		
		val = properties.getInt(KEY_LOCAL_PORT, VALUE_LOCAL_PORT);
		localPortSpinner.setValue(new Integer(val));
		
		val = properties.getInt(KEY_UPDATE_INTERVAL, VALUE_UPDATE_INTERVAL);
		updateIntervalSpinner.setValue(new Integer(val));
		
		boolean val2 = properties.getBoolean(KEY_SHOW_CONFIRM_DIALOG,
				VALUE_SHOW_CONFIRM_DIALOG);
		showConfirmCheckBox.setSelected(val2);
	}
	
	private boolean updateSettings()
	{
		parent.updateDisplay();
		
		if (!updateServersToShow())
		{
			return false;
		}
		if (!updateExecutablePath())
		{
			return false;
		}
		if (!updateLocalPort())
		{
			return false;
		}
		if (!updateInterval())
		{
			return false;
		}
		if (!updateOnLaunch())
		{
			return false;
		}
		if (!updateShowConfirmDialog())
		{
			return false;
		}
		
		return true;
	}
	
	private void discardChanges()
	{
		serversToShowSpinner.setValue(parent.getServersToShow());
		pathTextField.setText(parent.getExecutablePath());
		localPortSpinner.setValue(parent.getConnectionManager().getLocalPort());
		updateIntervalSpinner.setValue(parent.getConnectionManager()
				.getUpdateInterval());
		onLaunchChoices.setSelectedIndex(parent.getOnLaunchAction());
		showConfirmCheckBox.setSelected(parent.getShowConfirmDialog());
	}
	
	private void restoreDefaults()
	{
		serversToShowSpinner.setValue(new Integer(VALUE_SERVERS_TO_SHOW));
		pathTextField.setText(VALUE_EXECUTABLE_PATH);
		localPortSpinner.setValue(new Integer(VALUE_LOCAL_PORT));
		updateIntervalSpinner.setValue(new Integer(VALUE_UPDATE_INTERVAL));
		onLaunchChoices.setSelectedIndex(VALUE_ON_LAUNCH_ACTION);
		showConfirmCheckBox.setSelected(VALUE_SHOW_CONFIRM_DIALOG);
	}
	
	private boolean updateServersToShow()
	{
		int val = ((Integer) serversToShowSpinner.getValue()).intValue();
		parent.setServersToShow(val);
		properties.putInt(KEY_SERVERS_TO_SHOW, val);
		return true;
	}
	
	private boolean updateExecutablePath()
	{
		String path = pathTextField.getText();
		parent.setExecutablePath(path);
		properties.put(KEY_PATH_TO_EXECUTABLE, path);
		return true;
	}
	
	private boolean updateLocalPort()
	{
		int val = ((Integer) localPortSpinner.getValue()).intValue();
		cm.setLocalPort(val);
		properties.putInt(KEY_LOCAL_PORT, val);
		return true;
	}
	
	private boolean updateInterval()
	{
		int val = ((Integer) updateIntervalSpinner.getValue()).intValue();
		cm.setUpdateInterval(val);
		properties.putInt(KEY_UPDATE_INTERVAL, val);
		return true;
	}
	
	private boolean updateOnLaunch()
	{
		int val = onLaunchChoices.getSelectedIndex();
		parent.setOnCloseAction(val);
		properties.putInt(KEY_ON_LAUNCH_ACTION, val);
		return true;
	}
	
	private boolean updateShowConfirmDialog()
	{
		boolean val = showConfirmCheckBox.isSelected();
		parent.setShowConfirmDialog(val);
		properties.putBoolean(KEY_SHOW_CONFIRM_DIALOG, val);
		return true;
	}
	
	private void changeButtonAction()
	{
		parent.chooseExecutable();
	}
	
	private void saveAction()
	{
		if (updateSettings())
		{
			frame.setVisible(false);
		}
	}
	
	private void cancelAction()
	{
		discardChanges();
		frame.setVisible(false);
	}
	
	private void applyAction()
	{
		updateSettings();
	}
	
	private void setupLocation()
	{
		CommonMethods.c_setupLocation(this, parent.getGuiContext());
	}
	
	public int getVal(int val, int min, int max, int defn)
	{
		if (max < min || min > max)
		{
			throw new IllegalArgumentException(
					"lower <= upper is false (lower=" + min + ", upper=" + max
							+ ")");
		}
		
		if (val < min || val > max)
		{
			return defn;
		}
		
		return val;
	}
	
	public void showWindow()
	{
		if (!locationSet)
		{
			setupLocation();
			locationSet = true;
		}
		
		updateValues();
		frame.setVisible(true);
	}
	
	public void setExecutablePath(String newPath)
	{
		pathTextField.setText(newPath);
	}
}
