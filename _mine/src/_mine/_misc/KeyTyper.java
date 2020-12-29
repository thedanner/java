package _mine._misc;

import static java.awt.event.KeyEvent.VK_TAB;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * 
 * TODO: select key to type when the token is reached.
 * 
 * @author me
 */
public class KeyTyper extends JFrame implements ActionListener
{
	private static final int START_DELAY_SECONDS = 3;
	
	private static final long serialVersionUID = 7711304495321708614L;
	
	private static final String APP_NAME = "Key Typer";
	
	private JLabel keyLabel;
	private JTextField keyField;
	
	private JLabel tokenLabel;
	private JTextField tokenField;
	
	private JLabel pressTabLabel;
	private JCheckBox pressTabCheckBox;
	
	private JButton exitButton;
	private JButton execButton;
	
	private transient boolean running;
	
	private Robot robot;
	
	private SwingWorker<Boolean, Object> worker;
	
	public KeyTyper() throws AWTException
	{
		super(APP_NAME + " - ready");
		
		running = false;
		robot = new Robot();
		
		init();
	}
	
	private void init()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel cp = new JPanel(new GridLayout(4, 2));
		
		keyLabel = new JLabel("Key  ", JLabel.RIGHT);
		keyField = new JTextField();
		
		tokenLabel = new JLabel("Token  ", JLabel.RIGHT);
		tokenField = new JTextField("-");
		
		pressTabLabel = new JLabel("Simulate [TAB] at token", JLabel.RIGHT);
		
		pressTabCheckBox = new JCheckBox();
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		
		execButton = new JButton("Go");
		execButton.addActionListener(this);
		
		cp.add(keyLabel);
		cp.add(keyField);
		cp.add(tokenLabel);
		cp.add(tokenField);
		cp.add(pressTabLabel);
		cp.add(pressTabCheckBox);
		cp.add(execButton);
		cp.add(exitButton);
		
		setContentPane(cp);
		
		pack();
		
		setMinimumSize(getSize());
		
		setSize(400, 200);
	}
	
	private void initWorker()
	{
		worker = new SwingWorker<Boolean, Object>()
		{
			@Override
			protected Boolean doInBackground() throws Exception
			{
				boolean retVal = doPaste();
				
				running = false;
				
				return retVal;
			}
			
			private boolean doPaste()
			{
				for (int i = START_DELAY_SECONDS; i > 0; i--)
				{
					if (!running)
					{
						setTitle(APP_NAME + " - cancelled");
						execButton.setText("Go");
						pressTabCheckBox.setEnabled(true);
						
						return false;
					}
					else
					{
						setTitle(APP_NAME + " - starting in " + i + " sec");
						execButton.setText("Starting... (in " + i
								+ " sec, cick to cancel)");
						pressTabCheckBox.setEnabled(false);
						
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				
				if (!running)
				{
					setTitle(APP_NAME + " - cancelled");
					execButton.setText("Go");
					pressTabCheckBox.setEnabled(true);
					
					return false;
				}
				
				try
				{
					String key = keyField.getText();
					String token = tokenField.getText();
					
					String[] keyParts = key.split(token);
					
					boolean pressTab = pressTabCheckBox.isSelected();
					
					setTitle(APP_NAME + " - working");
					execButton.setText("Abort");
					
					for (String s : keyParts)
					{
						boolean isLastPart = (s == keyParts[keyParts.length - 1]);
						
						if (!running)
						{
							setTitle(APP_NAME + " - canceled");
							execButton.setText("Go");
							pressTabCheckBox.setEnabled(true);
							
							return false;
						}
						
						for (int i = 0; i < s.length(); i++)
						{
							if (running)
							{
								int code = getCodePoint(s.charAt(i));
								
								robot.keyPress(code);
								robot.keyRelease(code);
							}
							else
							{
								setTitle(APP_NAME + " - aborted");
								execButton.setText("Go");
								pressTabCheckBox.setEnabled(true);
								
								return false;
							}
						}
						
						if (pressTab && !isLastPart)
						{
							robot.keyPress(VK_TAB);
							robot.keyRelease(VK_TAB);
						}
					}
					
					setTitle(APP_NAME + " - done");
					execButton.setText("Go");
					pressTabCheckBox.setEnabled(true);
					
					return true;
				}
				catch (Exception e)
				{
					setTitle(APP_NAME + " - error: " + e);
					execButton.setText("Go");
					
					e.printStackTrace();
					
					return false;
				}
			}
		};
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if (execButton == src)
		{
			doExec();
		}
		else if (exitButton == src)
		{
			System.exit(0);
		}
	}
	
	private void doExec()
	{
		try
		{
			if (!running)
			{
				worker = null;
				initWorker();
				
				running = true;
				
				worker.execute();
			}
			else
			{
				execButton.setText("Go");
				
				running = false;
				
				worker.cancel(false);
				worker = null;
			}
		}
		catch (Exception e)
		{
			setTitle(APP_NAME + " - error: " + e);
			
			e.printStackTrace();
		}
	}
	
	private static int getCodePoint(char c)
	{
		int codePoint = c;
		
		if (c >= '0' && c <= '9')
		{
			codePoint = KeyEvent.VK_0 + (c - '0');
		}
		
		if (c >= 'a' && c <= 'z')
		{
			codePoint = c - ('a' - 'A');
		}
		
		if (c >= 'A' && c <= 'Z')
		{
			codePoint = KeyEvent.VK_A + (c - 'A');
		}
		
		System.out.println(c + " => " + codePoint);
		
		return codePoint;
	}
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					new KeyTyper().setVisible(true);
				}
				catch (AWTException e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
