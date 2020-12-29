/* Date.java
 * 8 Oct 2005 */
package _mine.ipLookup;

import java.util.Calendar;

import static java.util.Calendar.*;

public class Date {
	public static final String TOKEN = " ";
	
	public static final int YEAR_INDEX = 0;
	public static final int MONTH_INDEX= 1;
	public static final int DAY_INDEX = 2;
	public static final int HOUR_INDEX = 3;
	public static final int MINUTE_INDEX = 4;
	public static final int SECOND_INDEX = 5;
	public static final int NUM_FIELDS = 6;
	
	public static final int SEC_PER_MIN = 60;
	public static final int SEC_PER_HR = SEC_PER_MIN * 60;
	public static final int SEC_PER_DAY = SEC_PER_HR * 24;
	public static final int SEC_PER_MO = SEC_PER_DAY * 30;
	public static final int SEC_PER_YR = SEC_PER_MO * 12;
	
	private int[] fields;
	
	public Date() {
		fields = new int[NUM_FIELDS];
		setDate();
	}
	
	public Date(int month, int day, int year,
			int hour, int minute, int second) {
		fields = new int[NUM_FIELDS];
		setDate(month, day, year, hour, minute, second);
	}
	
	public void setDate() {
		Calendar c = Calendar.getInstance();
		int mo = c.get(MONTH);
		int d = c.get(DAY_OF_MONTH);
		int y = c.get(YEAR);
		int h = c.get(HOUR);
		int mi = c.get(MINUTE);
		int s = c.get(SECOND);
		
		setDate(mo, d, y, h, mi, s);
	}
	
	public void setDate(int month, int day, int year,
			int hour, int minute, int second) {
		set(MONTH_INDEX, month);
		set(DAY_INDEX, day);
		set(YEAR_INDEX, year);
		set(HOUR_INDEX, hour);
		set(MINUTE_INDEX, minute);
		set(SECOND_INDEX, second);
	}
	
	public void set(int field, int value) {
		fields[field] = value;
	}
	
	public int get(int field) {
		return fields[field];
	}
	
	public static Date parse(String dateStr) {
		String[] valueStr = dateStr.split(TOKEN);
		int[] values = new int[valueStr.length];
		for(int i = 0; i < valueStr.length; i++) {
			values[i] = Integer.parseInt(valueStr[i]);
		}
		
		return new Date(
				values[MONTH_INDEX],
				values[DAY_INDEX],
				values[YEAR_INDEX],
				values[HOUR_INDEX],
				values[MINUTE_INDEX],
				values[SECOND_INDEX]
		);
	}
	
	public long getDifference(Date otherDate) {
 		long dif = 0L;
		
		dif += Math.abs(get(SECOND_INDEX) - otherDate.get(SECOND_INDEX));
		dif += Math.abs((get(MINUTE_INDEX) - otherDate.get(MINUTE_INDEX))) * SEC_PER_MIN;
		dif += Math.abs((get(HOUR_INDEX) - otherDate.get(HOUR_INDEX))) * SEC_PER_HR;
		dif += Math.abs((get(DAY_INDEX) - otherDate.get(DAY_INDEX))) * SEC_PER_DAY;
		dif += Math.abs((get(MONTH_INDEX) - otherDate.get(MONTH_INDEX))) * SEC_PER_MO;
		dif += Math.abs((get(YEAR_INDEX) - otherDate.get(YEAR_INDEX))) * SEC_PER_YR;
		
		return dif;
	}
	
	@Override
	public String toString() {
		return get(YEAR_INDEX) + TOKEN +
			get(MONTH_INDEX) + TOKEN +
			get(DAY_INDEX) + TOKEN +
			get(HOUR_INDEX) + TOKEN +
			get(MINUTE_INDEX) + TOKEN +
			get(SECOND_INDEX);
	}
}
