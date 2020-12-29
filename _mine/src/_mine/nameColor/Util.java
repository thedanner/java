/* Text.java
 * 29 Sep 2005 */
package _mine.nameColor;

import java.awt.Color;
import java.util.Hashtable;
import java.util.regex.Pattern;

/**
 * 
 * @author Dan
 * @version 29 Sept 2005
 */
public class Util {
	/* background color */
	public static final Color BG_COLOR = new Color(145, 145, 145);
	
	/* the default color for painting text */
	public static final Color DEFAULT_COLOR = Color.BLACK;
	
	/* the regular expression containing color codes to filter
	 * match, etc. */
	private static final String regex = 
		//"\\^([0-9a-zA-Z]|x[0-9a-fA-F]{6}|[=\\[\\]!#$%&()*+-/?@\'])";
		"\\^([0-9a-zA-Z]|x[0-9a-fA-F]{6}|\\p{Punct})";
	
	/* a hastable containing colors outlined in the array */
	private static Hashtable<String, Color> colorTable;
	
	/* a collection of tokens and their
	 * corresponding hex values*/
	private static final String[] colors = {
			"^!#a05c0f", // ^! = Orange
			"^##730073", // ^# = Purple
			"^$#0068d7", // ^$ = Light Blue : original #0070f7
			"^%#5a134f", // ^% = Purple
			"^&#0063ef", // ^& = Light Blue : original #0073ff
			"^(#005A29", // ^( = Dark Green
			"^)#ff0029", // ^) = Foxy Red
			"^*#bbbbbb", // ^* = Gray
			"^+#993400", // ^+ = Dark Brown
			"^-#747228", // ^- = Olive
			"^/#dbdf70", // ^/ = Beige
			"^[#c5c5c5", // ^[ = Light Gray
			"^]#454c16", // ^] = Olive
			"^=#454c16", // ^= = Olive
			"^0#000000", // ^0 = Black
			"^1#da0120", // ^1 = Red
			"^2#20d926", // ^2 = Green : original #00b906
			"^3#e8ff19", // ^3 = Yellow
			"^4#170bdb", // ^4 = Blue
			"^5#23c2c6", // ^5 = Cyan
			"^6#e201db", // ^6 = Pink
			"^7#ffffff", // ^7 = White
			"^8#aa5c07", // ^8 = Orange : original #ca7c27
			"^9#c5c5c5", // ^9 = Gray : original #757575
			"^?#670504", // ^? = Dark Brown
			"^@#623307", // ^@ = Brown
			"^a#fbaf63", // ^a = Orange : original #eb9f53
			"^b#106f59", // ^b = Turquoise
			"^c#5a134f", // ^c = Purple
			"^d#035aff", // ^d = Light Blue
			"^e#681ea7", // ^e = Purple
			"^f#80c7f1", // ^f = Light Blue : original #5097c1
			"^g#bedac4", // ^g = Light Green
			"^h#024d2c", // ^h = Dark Green
			"^i#7d081b", // ^i = Dark Red
			"^j#90243e", // ^j = Claret
			"^k#743313", // ^k = Brown
			"^l#c7c07e", // ^l = Light Brown : original #a7905e
			"^m#454c16", // ^m = Olive : original #555c26
			"^n#ceccb7", // ^n = Beige : original #aeac97
			"^o#c0bf7f", // ^o = Beige
			"^p#000000", // ^p = Black
			"^q#da0120", // ^q = Red
			"^r#20d926", // ^r = Green : original #00b906
			"^s#e8ff19", // ^s = Yellow
			"^t#170bdb", // ^t = Blue
			"^u#23c2c6", // ^u = Cyan
			"^v#e201db", // ^v = Pink
			"^w#ffffff", // ^w = White
			"^x#ca7c27", // ^x = Orange
			"^y#cacaca", // ^y = Gray : original #757575
			"^z#c5c5c5", // ^z = Light Gray
			// begin other specific colors, similar to individual constants
			"^P#400080", // purple
			"^B#0000B8", // blue
			"^L#000000", // black
			"^R#da0120", // red
			"^G#20d926", // green
			"^A#cacaca"  // gray
	};
	
	/**
	 * Static initializer
	 */
	static {
		setupHashtable();
	}
	
	/**
	 * 
	 *
	 */
	private static void setupHashtable() {
		colorTable = new Hashtable<String, Color>(colors.length);
		
		for(int i = 0; i < colors.length; i++) {
			String s = colors[i];
			
			String key = s.substring(0, s.lastIndexOf("#"));
			String value = s.substring(s.lastIndexOf("#") + 1);
			
			Color color = hexToColor(value);
			
			colorTable.put(key, color);
		}
	}
	/**
	 * Takes a given color value in hex and returns a new
	 * <tt>Color</tt> object with the given colors.
	 * @param s a <tt>String</tt> representation of a number
	 * in hex.  The parameter must be in the format of "RRGGBB",
	 * with an optional character in the front (like "#", so the
	 * full <tt>String</tt> would be "#RRGGBB"). RGB values must
	 * be used, and must be in hex.
	 * @return a new <tt>Color</tt> object based on the
	 * provided string. 
	 */
	
	public static Color hexToColor(String s) {
		if(s.length() < 6 || s.length() > 7)
			throw new IllegalArgumentException(
				"hex string must be two chars long: " +
				s + " (" + s.length() + ")");
		// removes first character if s is 7 chars long,
		// which should be when the first character is "#"
		if(s.length() == 7) s = s.substring(1);
		
		// returns a new color object using the provided hex string
		return new Color(
				hexToInt(s.substring(0, 2)),  // R value
				hexToInt(s.substring(2, 4)),  // G value
				hexToInt(s.substring(4))  // B value
				);
	}
	
	/**
	 * Takes a two-digit hex number and converts it into an
	 * integer in the decimal system.  All characters are
	 * converted to lowercase before getting the int value.
	 * @param hex a two character hex <tt>String</tt> to convert
	 * to an int.
	 * @return the correspinding decimal value (int) for the parameter.
	 */
	public static int hexToInt(String hex) {
		if(hex.length() != 2) throw new IllegalArgumentException(
				"Hex must be two chars long: " + hex + " (" + hex.length() + ")");
		
		char a = Character.toLowerCase(hex.charAt(0));
		char b = Character.toLowerCase(hex.charAt(1));
		
		return 16 * intValue(a) + intValue(b);
	}
	
	/**
	 * Gets the hex value for the given decimal <tt>char</tt>.
	 * @param c a digit in hex (0-9, a-f).
	 * @return the corresponding decimal value.
	 */
	public static int intValue(char c) {
		switch(c) {
			case '0': return 0; 
			case '1': return 1; 
			case '2': return 2; 
			case '3': return 3; 
			case '4': return 4; 
			case '5': return 5; 
			case '6': return 6; 
			case '7': return 7; 
			case '8': return 8; 
			case '9': return 9; 
			case 'a': return 10; 
			case 'b': return 11; 
			case 'c': return 12; 
			case 'd': return 13;
			case 'e': return 14; 
			case 'f': return 15;
			default: throw new IllegalArgumentException(
					"Invalid hex character: " + c);
 		}
	}
	
	/**
	 * Strips the Wolfenstein:Enemy Territory color tokens.
	 * @param s the <tt>String</tt> to remove the color codes from.
	 * @return a clean <tt>String</tt> with no color codes.
	 */
	public static String removeColorTags(String s) {
		return s.replaceAll(getRegex(), "");
	}
	
	/**
	 * Takes the given color code for the Wolfenstein:Enemy Territory
	 * and returns a <tt>Color</tt> object with the corresonding color.
	 * @param s a token that represents a color in Enemy Territory.
	 * @return a new <tt>Color</tt> object which corresponds to the
	 * provided color token.
	 */
	public static Color resolveColor(String s) {
		return (Color)colorTable.get(s);
	}
	
	/**
	 * Returns a new <tt>Pattern</tt> object that uses the
	 * regex containing the pattern to remove, etc.
	 * Wolfenstein:Enemy Territory color tokens.  This is
	 * primarily a convenience method. 
	 * @return a new <tt>Pattern</tt> object.  This method works the same
	 * as calling <tt>Pattern.compile(Colors.getRegex())</tt> directly.
	 */
	public static Pattern getPattern() {
		return Pattern.compile(getRegex());
	}
	
	/**
	 * Returns the regex <tt>String</tt> that is used in the
	 * <tt>Pattern</tt> objects.  The return value of this method can be
	 * directly used in a fashion similar to
	 * <tt>Pattern.compile(Colors.getRegex())</tt>.s
	 * @return the regex <tt>String</tt>.
	 */
	public static String getRegex() {
		return regex;
	}
}
