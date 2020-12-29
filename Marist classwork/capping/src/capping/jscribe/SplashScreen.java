package capping.jscribe;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * The initial splash screen at the open of each new window
 * It basically just shows a loading bar so that the user knows something is loading
 */
public class SplashScreen extends JDialog implements Runnable
{
	private static final long serialVersionUID = -528336945342574507L;
	
	//We only want one instance.
	private static SplashScreen instance = new SplashScreen();;
	
	//JLabel containing the loading... message.
	private JLabel message;
	
	/**
	 * Makes a new splash screen
	 *
	 */
	private SplashScreen()
	{
		//Call parent's default constructor.
		super();
		
		//Don't let the user click anywhere else in program while visible 
		setModal(true);
		
		//Make the splash screen always on top of other programs.
		setAlwaysOnTop(true);
		
		//Set the loading message.
		message = new JLabel("Loading, please wait...", JLabel.CENTER);
		
		//Make a new progress bar.
		JProgressBar progresBar = new JProgressBar();
		progresBar.setIndeterminate(true);
		
		//Set the title of the splash screen.
		setTitle("JScribe");
		
		//Do not let the splash screen close because it is just showing the
		//progress of openning a window.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//Do not allow the splash screen to be resized either.
		setResizable(false);
		
		//Set the size of the splash screen to a suitable size.
		setSize(200, 75);
		
		//Set the layout for the splash screen to allow room for the message
		//and progress bar.
		getContentPane().setLayout(new GridLayout(0, 1));
		//Add the message to the splash screen.
		getContentPane().add(message);
		//Add the progress bar to the splash screen.
		getContentPane().add(progresBar);
	}
	
	@Override
	public void run()
	{
		setLocationRelativeTo(null);
		//Make the splash screen visible.
		setVisible(true);
	}
	
	/**
	 * This dialog is always modal.
	 * @return true, always.
	 */
	@Override
	public boolean isModal()
	{
		return true;
	}
	
	public static void showSplash()
	{
		new Thread(instance).start();
	}
	
	/**
	 * Get rid of the splash screen.
	 * 
	 */
	public static void hideSplash()
	{
		if(instance != null)
		{
			SwingUtilities.invokeLater(new SplashCloser());
		}
	}
	
	private static final class SplashCloser implements Runnable
	{
		@Override
		public void run()
		{
			instance.dispose();
		}
	}
}
