/* PokGui.java */
package _mine.pokMan;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

/**
 * This class is the driver class for the game of PokMan.  This class ties all
 * other class associated with rnning this game together: <tt>HighScores</tt>,
 * <tt>Game</tt>, <tt>Pokman</tt>, and <tt>Ghost</tt>.  All gui graphics elements
 * are implemented using the cool-looking <tt>javax.Swing</tt> graphics (well, at
 * least they are "cool looking" in jdk 1.5.0, they don't look as good in prior
 * to version that version).
 * <br>
 * When <tt>init()</tt> is called, a small gui is constructed.
 * That giu provides controls to see the top 3 scores in the main window,
 * a button to access the full "Top Ten" list (the high scores are only
 * implemented as a teaser element, partly because the highest possible score
 * attainable is a 100.  There is an option to set the speed to one of three
 * settings, and a button to trigger the method to set the lives for the next
 * game.  After the initial game is created, there is an option to restart it.
 * @author Dan M.
 * @version Apr 22, 2006
 */
public class PokGui extends JApplet implements ActionListener {
	private static final long serialVersionUID = 3209830097931342820L;
	
	public static final int HIGH_SCORES_LIST_SIZE = 10;
	public static final int HIGH_SCORES_LIST_COLUMNS = 3;
	public static final int TOP_SCORES_LIST_SIZE = 3;
	public static final int TOP_SCORES_LIST_COLUMNS = 3;
	
	public static final  int SPEED_MIN = 2;
	public static final  int SPEED_MAX = 15;
	public static final  int SPEED_DEFAULT = 5;
	
	public static final  int LIVES_MIN = 1;
	public static final  int LIVES_MAX = 100;
	public static final  int LIVES_DEFAULT = 3;
	
	// instance fields 
	private Game game;
	private HighScores scores;
	private boolean fullScreenCapable;
	
	private JFrame highScoresWindow;
	private JPanel highScoresPane;
	private JLabel[][] highScoresLabels;
	
	private JPanel topScoresPane;
	private JLabel[][] topScoresLabels;
	
	private JLabel speedLabel;
	private JSpinner speedSpinner;
	
	private JLabel livesLabel;
	private JSpinner livesSpinner;
	
	private JButton startFullScreenButton;
	private JButton startWindowedButton;
	private JButton scoresButton;
	// end of variables declaration
	
	/** Initializes the applet PokGui */
	@Override
	public void init() {
		initComponents();
		
		fullScreenCapable = checkFullScreenCapabilities();
	}
	
	/**
	 * 
	 */
	@Override
	public void start() {
		System.gc();
	}
	
	/**
	 * 
	 */
	@Override
	public void stop() {
		if(game != null)
			game.stop();
	}
	
	/**
	 * 
	 */
	@Override
	public void destroy() {
		scores = null;
		game = null;
	}
	
	public void setSpeed(int speed) {
		if(speedSpinner != null)
			speedSpinner.setValue(speed);
	}
	
	public int getSpeed() {
		return (Integer)speedSpinner.getValue();
	}
	
	/**
	 * This method is called from within the init() method to
	 * initialize the form.
	 */
	private void initComponents() {
		// resizes the window to an apporpriate size
		// does nothing in web pages
		setSize(300, 300);
		
		// for "high scores" list (for demo purposes)
		scores = new HighScores();
		
		initSpeedSpinner();
		initLivesSpinner();
		initHighScores();
		initTopScores();
		initButtons();
		
		addComponents();
	}
	
	private void initSpeedSpinner() {
		int val = SPEED_DEFAULT;
		int min = SPEED_MIN;
		int max = SPEED_MAX;
		
		String text = "Game speed: ";
		String toolTip = "The speed at which the game should run.  " +
				"[Default: " + val + " | min: " + min + " | max: " + max + "]";
		
		speedLabel = createLabel(text, JLabel.RIGHT);
		SpinnerModel model = createSpinnerModel(val, min, max);
		speedSpinner = createSpinner(model, toolTip);
		
		speedLabel.setLabelFor(speedSpinner);
	}
	
	private void initLivesSpinner() {
		int val = LIVES_DEFAULT;
		int min = LIVES_MIN;
		int max = LIVES_MAX;
		
		String text = "Number of lives: ";
		String toolTip = "The amount of lives PokMan should have.  " +
				"[Default: " + val + " | min: " + min + " | max: " + max + "]";
		
		livesLabel = createLabel(text, JLabel.RIGHT);
		SpinnerModel model = createSpinnerModel(val, min, max);
		livesSpinner = createSpinner(model, toolTip);
		
		livesLabel.setLabelFor(livesSpinner);
	}
	
	private void initHighScores() {
		// scores information components init
		highScoresWindow = new JFrame();
		highScoresPane = (JPanel)highScoresWindow.getContentPane();
		
		int rows = HIGH_SCORES_LIST_SIZE;
		int cols = HIGH_SCORES_LIST_COLUMNS;
		highScoresLabels = new JLabel[rows][cols];
		generateScoresLabels(rows, cols, highScoresPane, highScoresLabels);
		
		highScoresWindow.setTitle("Top Ten List");
		highScoresWindow.pack();
		
		TitledBorder border = BorderFactory.createTitledBorder(null,
				"Top Ten", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font(
						"Arial", Font.BOLD, 12), Color.RED);
		
		highScoresPane.setBorder(border);
	}
	
	private void initTopScores() {
		int rows = TOP_SCORES_LIST_SIZE;
		int cols = TOP_SCORES_LIST_COLUMNS;
		topScoresPane = new JPanel();
		topScoresLabels = new JLabel[rows][cols];
		generateScoresLabels(rows, cols, topScoresPane, topScoresLabels);
		
		TitledBorder border = BorderFactory.createTitledBorder(null,
				"Top Ten", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font(
						"Arial", Font.BOLD, 12), Color.RED);
		
		topScoresPane.setBorder(border);
	}
	
	private void initButtons() {
		// start button
		String text = "Start Game (Full Screen)";
		String toolTip = null;
		startFullScreenButton = createButton(text, toolTip, this);
		
		// start button
		text = "Start Game (Windowed)";
		startWindowedButton = createButton(text, toolTip, this);
		
		// show top ten list
		text = "View Top Ten...";
		toolTip = "Displays a popup of the Top Ten list.";
		scoresButton = createButton(text, toolTip, this);
	}
	
	private void addComponents() {
		JPanel cp = (JPanel)getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// helps components center and spread "properly"
		c.weightx = 0.75;
		c.weighty = 1.0;
		c.ipadx = 200;
		c.ipady = 5;
		c.insets = new Insets(4, 2, 4, 2);
		
		// top scores list
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		cp.add(topScoresPane, c);
		
		// speed label
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 1;
		c.gridwidth = 1;
		cp.add(speedLabel, c);
		// speed spinner
		c.gridx = GridBagConstraints.RELATIVE;
		cp.add(speedSpinner, c);
		
		// lives label
		c.gridx = 0;
		c.gridy = 2;
		cp.add(livesLabel, c);
		// lives spinner
		c.gridx = GridBagConstraints.RELATIVE;
		cp.add(livesSpinner, c);
		
		// adding buttons
		
		// scores button
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		cp.add(scoresButton, c);
		
		// start fullscreen button
		cp.add(startFullScreenButton, c);
		
		// start windowed button
		cp.add(startWindowedButton, c);
	}
	
	/**
	 * Displays the window that contains the Top Ten scores.
	 * @param e the event that triggers this action
	 */
	private void viewScoresAction() {
		// Displays a pop up window that contains the top ten scores
		highScoresWindow.setVisible(true);
	}
	
	/**
	 * Configures and displays a new window (<tt>JFrame</tt>) and <tt>JPane<tt>
	 * to display the <tt>PokMan</tt> game in.
	 * @param fullScreen 
	 */
	private void startGame(boolean fullScreen) {
		resetGame();
		
		int speed = (Integer)speedSpinner.getValue();
		int lives = (Integer)livesSpinner.getValue();
		
		game = new Game(speed, lives, this);
		
		try {
			game.start(fullScreen);
		}
		// NOTE: the problem that resulted in the the following hack may have
		// been fixed.  It is left intact in case something else goes wrong.
		// --Occasionally for some unknown reason game.init() [sic] throws
		// --a NullPointerException.
		// --This was implemented to handle the strange and uncommon occurance
		catch(Exception e) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(this,
					"Exception: " + e +
					"\nPlease try to start the game again.");
		}
	}
	
	/**
	 * Destroys the game applet and allows the user to restart the game.
	 */
	public void resetGame() {
		if(game != null)
			game.stop();
		
		game = null;
		
		// hopefully, a preventitive for massive memory usage and slowdowns
		System.gc();
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	private String getOrdinal(int n) {
		// first, find out if the hundreds + ones value of n
		// is in the teens (10 <= n % 100 <= 19),
		// in this case, the ordinal should always be "th"
		if(n % 100 >= 10 && n % 100 <= 19)
			n = 0;
		
		// isolate ones digit and use that to determine the correct string
		switch(n % 10) {
		case 1: return "st";
		case 2: return "nd";
		case 3: return "rd";
		default: return "th";
		}
	}
	
	/**
	 * 
	 * @param rows
	 * @param cols
	 * @param pane
	 * @param labelGrid
	 */
	private void generateScoresLabels(int rows, int cols,
			JPanel pane, JLabel[][] labelGrid) {
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		labelGrid = new JLabel[rows][cols];
		for(int i = 0; i < labelGrid.length; i++) {
			int j = 0;
			int n = i + 1;
			HighScore s = scores.getScore(i);
			
			if(s != null) {
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 1.0;
				c.weighty = 1.0;
				c.ipadx = 15;
				c.insets = new Insets(0, 3, 5, 3);
				
				// place label
				JLabel curr = createLabel(n + getOrdinal(n), JLabel.RIGHT);
				labelGrid[i][j++] = curr;
				
				c.gridy = i;
				c.gridx = 0;
				pane.add(curr, c);
				
				// name label
				curr = createLabel(s.getName(), JLabel.CENTER);
				labelGrid[i][j++] = curr;
				
				c.gridx = GridBagConstraints.RELATIVE;
				pane.add(curr, c);
				
				// score label
				curr = createLabel(
						Integer.toString(s.getScore()), JLabel.LEFT);
				labelGrid[i][j++] = curr;
				pane.add(curr, c);
			}
		}
	}
	
	public void setStartButtonsEnabled(boolean newState) {
		startFullScreenButton.setEnabled(fullScreenCapable && newState);
		startWindowedButton.setEnabled(newState);
	}
	
	private boolean checkFullScreenCapabilities() {
		String toolTipMessage = "";
		
		boolean fsAllowed = GraphicsCapabilities.isFullScreenAllowed();
		boolean fsSupported = GraphicsCapabilities.isFullScreenSupported();
		boolean fsDisplayChangeSupported =
			GraphicsCapabilities.isDisplayChangeSupported();
		
		if(!fsAllowed)
			toolTipMessage += "fullscreen not allowed";
		
		if(!fsSupported) {
			if(toolTipMessage.length() > 0)
				toolTipMessage += "; ";
			toolTipMessage += "fullscreen not supported";
		}
	
		if(!fsDisplayChangeSupported) {
			if(toolTipMessage.length() > 0)
				toolTipMessage += "; ";
			toolTipMessage += "display change not supported";
		}
		
		boolean fullscreenCapable = fsAllowed && fsSupported; 
		
		String newText = "";
		
		if(!fullscreenCapable)
			newText = " [error, see tooltip]";
		
		startFullScreenButton.setEnabled(fullscreenCapable);
		startFullScreenButton.setText(
				startFullScreenButton.getText() + newText);
		startFullScreenButton.setToolTipText(toolTipMessage);
		
		return fullscreenCapable;
	}
	
	/**
	 * 
	 * @param model
	 * @param toolTip
	 * @return
	 */
	private JSpinner createSpinner(SpinnerModel model, String toolTip) {
		JSpinner spinner = new JSpinner(model);
		spinner.setToolTipText(toolTip);
		return spinner;
	}
	
	/**
	 * 
	 * @param val
	 * @param min
	 * @param max
	 * @return
	 */
	private SpinnerNumberModel createSpinnerModel(int val, int min, int max) {
		return createSpinnerModel(val, min, max, 1);
	}
	
	/**
	 * 
	 * @param val
	 * @param min
	 * @param max
	 * @param step
	 */
	private SpinnerNumberModel createSpinnerModel(int val, int min, int max, int step) {
		return new SpinnerNumberModel(val, min, max, step);
	}
	
	/**
	 * 
	 * @param text
	 * @param toolTip
	 * @param l
	 * @return
	 */
	private JButton createButton(String text, String toolTip, ActionListener l) {
		JButton button = new JButton(text);
		button.setToolTipText(toolTip);
		button.addActionListener(l);
		return button;
	}
	
	/**
	 * 
	 * @param text
	 * @param horizAlign
	 * @return
	 */
	private JLabel createLabel(String text, int horizAlign) {
		JLabel label = new JLabel(text, horizAlign);
		return label;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src == startFullScreenButton) {
			startGame(true);
			
			return;
		}
		
		if(src == startWindowedButton) {
			startGame(false);
			
			return;
		}
		
		if(src == scoresButton) {
			viewScoresAction();
			
			return;
		}
	}
}