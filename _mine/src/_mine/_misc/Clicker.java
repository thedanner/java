package _mine._misc;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Clicker extends JFrame implements ActionListener {
	private static final long serialVersionUID = -4544640770539640544L;
	
	private JButton toggleButton;
	private Timer timer;
	private boolean running;
	private Robot clicker;
	
	public Clicker() throws AWTException {
		running = false;
		
		toggleButton = new JButton("Start");
		toggleButton.addActionListener(this);
		
		clicker = new Robot();
		clicker.setAutoDelay(10);
		
		getContentPane().add(toggleButton);
		
		setTitle("Clicker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(toggleButton == src) {
			running = !running;
			
			if(timer != null)
				timer.cancel();
			
			if(running) {
				toggleButton.setText("Stop");
				
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						click();
					}
				}, 3000, 1000);
			} else {
				toggleButton.setText("Start");
				
				// timer already canceled
				
				timer = null;
			}
			
			return;
		}
	}
	
	private void click() {
		clicker.mousePress(MouseEvent.BUTTON1_MASK);
		clicker.mouseRelease(MouseEvent.BUTTON1_MASK);
	}
	
	public static void main(String[] args) throws AWTException {
		new Clicker().setVisible(true);
	}
}
