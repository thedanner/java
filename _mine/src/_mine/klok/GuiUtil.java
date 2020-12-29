package _mine.klok;

import java.awt.Graphics;
import java.awt.Point;

public class GuiUtil {
	static void drawLine(Graphics g, Point p1, Point p2) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	static Point getOuterCirclePoint(double angle,
			int cX, int cY, int cR) {
		// some trig with Pascal's Triangle
		// a^2 + b^2 = h^2, h = cR
		
		double a = cR * Math.sin(angle);
		double b = cR * Math.cos(angle);
		
		double x = cX + cR + b;
		double y = cY + cR + a;
		
		int iX = (int) Math.round(x);
		int iY = (int) Math.round(y);
		
		return new Point(iX, iY);
	}
	
	static Point getInnerCirclePoint(double angle,
			int cX, int cY, int cR, int lengthOffset) {
		
		// same math as outer circle version,
		// except h (in a^2 + b^2 = h^2) is cR - tickLen
		return getOuterCirclePoint(
				angle, cX + lengthOffset, cY + lengthOffset, cR - lengthOffset);
	}
}
