package _mine.klok;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

public class KlokHand extends JComponent {
	private static final long serialVersionUID = -3236034196042488556L;
	
	//***********************
	// BEGIN INSTANCE FIELDS
	//***********************
	private int calendarTimeField;
	private int smallerCalendarTimeField;
	private int numSmallerTimeUnitIntervals;
	private int numIntervals;
	private int handLength;
	private Circle clockCircle;
	
	private double anglePerTimeUnit;
	private double anglePerSmallerTimeUnit;
	
	public KlokHand(int calendarTimeField, int numIntervals, int handLength,
			Circle clockCircle) {
		this(calendarTimeField, numIntervals, handLength, -1, -1,clockCircle);
	}
	
	public KlokHand(
			int calendarTimeField, int numIntervals, int handLength,
			int smallerCalendarTimeField, int numSmallerTimeUnitIntervals,
			Circle clockCircle) {
		
		super();
		
		this.calendarTimeField = calendarTimeField;
		this.numIntervals = numIntervals;
		this.handLength = handLength;
		this.smallerCalendarTimeField = smallerCalendarTimeField;
		this.numSmallerTimeUnitIntervals = numSmallerTimeUnitIntervals;
		this.clockCircle = clockCircle;
		
		init();
	}
	
	private void init() {
		this.anglePerTimeUnit = (2 * Math.PI) / numIntervals;
		this.anglePerSmallerTimeUnit = -1.0;
		
		if(smallerCalendarTimeField > 0 || numSmallerTimeUnitIntervals > 0) {
			anglePerSmallerTimeUnit =
				(2 * Math.PI) / numSmallerTimeUnitIntervals;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int timeValue = getTimeValue(calendarTimeField);
		
		double currentHandAngle =
			(1.5 * Math.PI) + (anglePerTimeUnit * timeValue);
		
		if(anglePerSmallerTimeUnit > 0) {
			int smallerTimeValue = getTimeValue(smallerCalendarTimeField);
			
			double tempAngle = anglePerSmallerTimeUnit * smallerTimeValue;
			tempAngle /= 2 * Math.PI;
			
			currentHandAngle += anglePerTimeUnit * tempAngle;
		}
		
		Point tip = GuiUtil.getInnerCirclePoint(
				currentHandAngle,
				clockCircle.getX(), clockCircle.getY(),
				clockCircle.getRadius(), clockCircle.getRadius() - handLength);
		
		GuiUtil.drawLine(g, tip, clockCircle.getCenter());
	}
	
	//********************
	// BEGIN STATIC STUFF
	//********************
	
	static final int HOUR_HAND_LENGTH = 60;
	static final int MINUTE_HAND_LENGTH = 80;
	static final int SECOND_HAND_LENGTH = 100;
	
	static final int HOUR_HAND_TICKS = 12;
	static final int MINUTE_HAND_TICKS = 60;
	static final int SECOND_HAND_TICKS = 60;
	
	public static KlokHand createHourHand(Circle c) {
		return new KlokHand(
				Calendar.HOUR, HOUR_HAND_TICKS, HOUR_HAND_LENGTH,
				Calendar.MINUTE, MINUTE_HAND_TICKS,
				c);
	}
	
	public static KlokHand createMinuteHand(Circle c) {
		return new KlokHand(
				Calendar.MINUTE, MINUTE_HAND_TICKS, MINUTE_HAND_LENGTH,
				Calendar.SECOND, SECOND_HAND_TICKS,
				c);
	}
	
	public static KlokHand createSecondHand(Circle c) {
		return new KlokHand(
				Calendar.SECOND, SECOND_HAND_TICKS, SECOND_HAND_LENGTH, c);
	}
	
	private static int currentHour;
	private static int currentMinute;
	private static int currentSecond;
	private static int currentAMPM;
	
	static {
		initTimer();
	}
	
	public static int getTimeValue(int field) {
		switch(field) {
		case Calendar.HOUR: return currentHour;
		case Calendar.MINUTE: return currentMinute;
		case Calendar.SECOND: return currentSecond;
		case Calendar.AM_PM: return currentAMPM;
		default: throw new IllegalArgumentException("unknown field: " + field);
		}
	}
	
	public static Timer createRepaintTimer(final JComponent c) {
		Timer t = new Timer();
		
		int delay = Calendar.getInstance().get(Calendar.MILLISECOND);
		
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				c.repaint();
			}
		}, 1000 - delay, 1000);
		
		return t;
	}
	
	private static void initTimer() {
		resetTimeCounters();
		
		Timer t = new Timer();
		
		int delay = Calendar.getInstance().get(Calendar.MILLISECOND);
		
		t.scheduleAtFixedRate(new TimerTask() {
			private boolean increment = true;
			
			@Override
			public void run() {
				if(increment)
					incrementSeconds();
				
				increment = !increment;
			}
		}, 1000 - delay, 500);
	}
	
	private static void resetTimeCounters() { 
		Calendar c = Calendar.getInstance();
		
		currentHour = c.get(Calendar.HOUR);
		currentMinute = c.get(Calendar.MINUTE);
		currentSecond = c.get(Calendar.SECOND);
		currentAMPM = c.get(Calendar.AM_PM);
	}
	
	private static void incrementSeconds() {
		currentSecond++;
		
		if(currentSecond >= 60) {
			currentSecond = 0;
			currentMinute++;
		}
		
		if(currentMinute >= 60) {
			currentMinute = 0;
			currentHour++;
		}
		
		if(currentHour >= 12) {
			currentHour = 0;
			
			if(currentAMPM == Calendar.AM)
				currentAMPM = Calendar.PM;
			else //if(currentAMPM == Calendar.PM)
				currentAMPM = Calendar.AM;
		}
	}
}
