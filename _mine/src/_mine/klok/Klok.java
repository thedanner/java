package _mine.klok;

import javax.swing.JFrame;

public class Klok {
    public static void main(String[] args) {
    	int dim = 360;
    	
		JFrame f = new JFrame();
		f.setSize(dim, dim);
		f.setTitle("OMFG A CLOCK");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		KlokPanel kp = new KlokPanel();
		kp.setSize(dim, dim);
		
		f.getContentPane().add(kp);
		
		f.setVisible(true);
	}
}
