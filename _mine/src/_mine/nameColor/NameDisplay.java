/* NameDisplay.java */
package _mine.nameColor;

import java.awt.Color;
import java.awt.Graphics;
import java.util.regex.Matcher;

import javax.swing.JPanel;

/**
 * @author Dan
 * @version Feb 7, 2007
 */
public class NameDisplay extends JPanel {
	private static final long serialVersionUID = 2999105274579788838L;

	/*  */
	private int baseX;
	
	/*  */
	private int baseY;
	
	/*  */
	private int curX;
	
	/*  */
	private NameColor parent;
	
	/**
	 * 
	 *
	 */
	public NameDisplay(NameColor parent) {
		this.parent = parent;
		
		init();
	}
	
	/**
	 * 
	 *
	 */
	private void init() {
		this.baseX = 5;
		this.baseY = 2 * baseX + getVerticalTextPoint();
		
		this.curX = baseX;
		
		this.setBackground(Util.BG_COLOR);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		this.baseY = 2 * baseX + getVerticalTextPoint();
		
		// write the name
		write(g, parent.getNameText());
		
		// restore the x-coord
		curX = baseX;
	}
	
	/**
	 * 
	 * @param g
	 * @param s
	 */
	private void write(Graphics g, String s) {
		Matcher m = Util.getPattern().matcher(
				s.subSequence(0, s.length()));
		
		int previous = 0;
		Color c = Util.DEFAULT_COLOR;
		Color cPrev = c;
		
		while(m.find()) {
			c = Util.resolveColor(m.group().toLowerCase());
			if(previous >= 0) write(g, s.substring(previous, m.start()), cPrev);
			previous = m.end();
			cPrev = c;
		}
		
		write(g, s.substring(previous), cPrev);
	}
	
	/**
	 * 
	 * @param g
	 * @param s
	 * @param c
	 */
	private void write(Graphics g, String s, Color c) {
		g.setColor(c);
		writeString(g, Util.removeColorTags(s));
	}
	
	/**
	 * 
	 * @param g
	 * @param s
	 */
	private void writeString(Graphics g, String s) {
		g.drawString(s, curX, baseY);
		
		for(int i = 0; i < s.length(); i++)
			curX += g.getFontMetrics().charWidth(s.charAt(i));
	}
	
	/**
	 * 
	 *
	 */
	private int getVerticalTextPoint() {
		int textHeight = -1;
		
		try {
			textHeight = getFontMetrics(getFont()).getHeight();
		} catch(NullPointerException e) {
			return textHeight;
		}
		int pos = (int) ((getHeight() / 2) + 0.5);
		
		pos -= (int) ((textHeight / 2) + 0.5);
		
		return pos;
	}
}