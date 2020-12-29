package compilerDesign.hw5;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class SplashScreen extends JDialog implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -528336945342574507L;
	
	private static SplashScreen instance = new SplashScreen();
	
	private JLabel message;
	
	private SplashScreen()
	{
		super();
		
		setModal(true);
		
		message = new JLabel("Loading, please wait...", JLabel.CENTER);
		
		JProgressBar progresBar = new JProgressBar();
		progresBar.setIndeterminate(true);
		
		setTitle("Compiler Design");
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		
		setSize(200, 75);
		
		getContentPane().setLayout(new GridLayout(0, 1));
		getContentPane().add(message);
		getContentPane().add(progresBar);
	}
	
	public void run()
	{
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void showSplash()
	{
		new Thread(instance).start();
	}
	
	public static void hideSplash()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				instance.dispose();
			}
		});
	}
	
	public static String getMessage()
	{
		return instance.message.getText();
	}
	
	public static boolean setMessage(String message)
	{
		if(instance == null)
			return false;
		
		instance.message.setText(message);
		
		return true;
	}
	
	@Override
	public boolean isModal()
	{
		return true;
	}
}
