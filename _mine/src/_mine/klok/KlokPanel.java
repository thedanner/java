package _mine.klok;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Calendar;

import javax.swing.JComponent;

public class KlokPanel extends JComponent {
	private static final long serialVersionUID = -8820041826802922785L;
	
	static final int NUM_HOURS_PER_DAY = 24;
	static final int NUM_MINUTES_PER_HOUR = 60;
	//static final int NUM_SECONDS_PER_MINUTE = 60;
	
	static final int NUM_HOUR_INTERVALS = NUM_HOURS_PER_DAY / 2;
	static final int NUM_MINUTE_INTERVALS = NUM_MINUTES_PER_HOUR;
	//static final int NUM_SECOND_INTERVALS = NUM_SECONDS_PER_MINUTE;
	
	static final int X = 10;
	static final int Y = 10;
	static final int R = 150;
	
	static final Point CENTER = new Point(X + R, Y + R);
	static final Circle CIRCLE = new Circle(X, Y, R);
	
	static final int HOUR_TICK_LENGTH = 30;
	static final int MINUTE_TICK_LENGTH = 10;
	
	private KlokHand hourHand;
	private KlokHand minuteHand;
	private KlokHand secondHand;
	
	public KlokPanel() {
		super();
		
		init();
	}
	
	public void init() {
		this.hourHand = KlokHand.createHourHand(CIRCLE);
		this.minuteHand = KlokHand.createMinuteHand(CIRCLE);
		this.secondHand = KlokHand.createSecondHand(CIRCLE);
		
		this.hourHand.setSize(getSize());
		this.minuteHand.setSize(getSize());
		this.secondHand.setSize(getSize());
		
		this.add(hourHand);
		this.add(minuteHand);
		this.add(secondHand);
		
		this.hourHand.setVisible(true);
		this.minuteHand.setVisible(true);
		this.secondHand.setVisible(true);
		
		KlokHand.createRepaintTimer(this);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		hourHand.setSize(width, height);
		minuteHand.setSize(width, height);
		secondHand.setSize(width, height);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		drawTickMarks(g);
		drawDigitalTime(g);
	}
	
	private void drawTickMarks(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawOval(X, Y, 2*R, 2*R);
		
		g.setColor(Color.BLUE);
		g.fillOval(X + R - 5, Y + R - 5, 10, 10);
		
		g.setColor(Color.RED);
		drawTickMarksOnClircle(g, NUM_MINUTE_INTERVALS, MINUTE_TICK_LENGTH);
		
		g.setColor(Color.BLACK);
		drawTickMarksOnClircle(g, NUM_HOUR_INTERVALS, HOUR_TICK_LENGTH);
	}
	
	private void drawTickMarksOnClircle(
			Graphics g, int numIntervals, int tickLen) {
		Point p1 = null, p2 = null;
		
		double angle = (2 * Math.PI) / numIntervals;
		
		for(int i = 1; i <= numIntervals; i++) {
			p1 = GuiUtil.getOuterCirclePoint(i * angle, X, Y, R);
			p2 = GuiUtil.getInnerCirclePoint(i * angle, X, Y, R, tickLen);
			
			GuiUtil.drawLine(g, p1, p2);
		}
	}
	
	private void drawDigitalTime(Graphics g) {
		g.setColor(Color.RED);
		
		int x = 10;
		int y = 320;
		
		String timeOfDayStr =
			(KlokHand.getTimeValue(Calendar.AM_PM) == Calendar.AM) ?
					"AM" : "PM";
		
		String timeStr = String.format("%1$02d:%2$02d:%3$02d %4$s",
				KlokHand.getTimeValue(Calendar.HOUR),
				KlokHand.getTimeValue(Calendar.MINUTE),
				KlokHand.getTimeValue(Calendar.SECOND),
				timeOfDayStr);
		
		g.drawString(timeStr, x, y);
	}
}
