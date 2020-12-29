/* Text.java */
package _mine.serverQuery.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Hashtable;
import java.util.regex.Pattern;

import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.util.properties.PropertyManager;
import _mine.serverQuery.util.properties.Vars;

/**
 * This class contains color constants for convenience and accuracy for their
 * intended purpose, which is usually lighter than the defined colors in the
 * <tt>Color</tt> class. It also contains routines for converting specified RGB
 * colors in the form of a hex string into objects of the <tt>Color</tt> class.
 * Not all colors are web safe, so this needs to be viewed at at least 16-bit
 * color to look accurate.
 * 
 * @author Dan
 * @version Apr 22, 2006
 */
public class Text implements Runnable
{
	/*  */
	private volatile Thread th;
	
	/* the title for the help message box */
	public static final String HELP_BOX_TITLE = "Help";
	
	/* the title for the about message box */
	public static final String ABOUT_BOX_TITLE = "About";
	
	/* the color purple */
	public static final Color PURPLE = new Color(64, 0, 128);
	
	/* the color red */
	public static final Color RED = new Color(210, 0, 0);
	
	/* the color blue */
	public static final Color BLUE = new Color(0, 0, 200);
	
	/* the color black */
	public static final Color BLACK = Color.BLACK;
	
	/* the color green, #20d920, originally (0,200,0) */
	public static final Color GREEN = new Color(32, 233, 26);
	
	/* the color gray */
	public static final Color GRAY = new Color(150, 150, 150);
	
	/* background color for the applet */
	public static final Color BG_COLOR = new Color(145, 145, 145);
	
	/* the default color for painting text */
	public static final Color DEFAULT_COLOR = BLACK;
	// end color constants
	
	/* the default font to use */
	public static final Font DEFAULT_FONT = getDefaultFont();
	
	/*
	 * the regular expression containing color codes to filter match, etc.
	 */
	private static final String regex =
	// "\\^([0-9a-zA-Z]|x[0-9a-fA-F]{6}|[=\\[\\]!#$%&()*+-/?@\'])";
	"\\^([0-9a-zA-Z]|x[0-9a-fA-F]{6}|\\p{Punct})";
	
	/* a hash table containing colors outlined in the array */
	private static Hashtable<String, Color> colorTable;
	
	/*
	 * a collection of tokens and their corresponding hex values
	 */
	private static final String[] colors = { "^!#a05c0f", // ^! = Orange
			"^##730073", // ^# = Purple
			"^$#0068d7", // ^$ = Light Blue : original #0070f7
			"^%#5a134f", // ^% = Purple
			"^&#0063ef", // ^& = Light Blue : original #0073ff
			"^(#005A29", // ^( = Dark Green
			"^)#ff0029", // ^) = Foxy Red
			"^*#bbbbbb", // ^* = Gray
			"^+#993400", // ^+ = Dark Brown
			"^-#545208", // ^- = Olive : original #747228
			"^/#dbdf70", // ^/ = Beige
			"^[#c5c5c5", // ^[ = Light Gray
			"^]#454c16", // ^] = Olive
			"^=#454c16", // ^= = Olive
			"^0#000000", // ^0 = Black
			"^1#da0120", // ^1 = Red
			"^2#30f936", // ^2 = Green : original #00b906
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
			"^d#035adf", // ^d = Light Blue : original #035aff
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
			"^A#cacaca" // gray
	};
	
	static
	{
		setupHashtable();
	}
	
	/**
	 * 
	 *
	 */
	private static void setupHashtable()
	{
		colorTable = new Hashtable<String, Color>(colors.length);
		
		for (int i = 0; i < colors.length; i++)
		{
			String s = colors[i];
			
			String key = s.substring(0, s.lastIndexOf("#"));
			String value = s.substring(s.lastIndexOf("#") + 1);
			
			Color color = hexToColor(value);
			
			colorTable.put(key, color);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static Font getDefaultFont()
	{
		if (Util.isWindowsOS())
		{
			return new Font("Lucida Console", Font.PLAIN, 12);
		}
		else
		{
			return new Font("Arial", Font.BOLD, 11);
		}
	}
	
	/**
	 * Takes a given color value in hex and returns a new <tt>Color</tt> object
	 * with the given colors.
	 * 
	 * @param s
	 *            a <tt>String</tt> representation of a number in hex. The
	 *            parameter must be in the format of "RRGGBB", with an optional
	 *            character in the front (like "#", so the full <tt>String</tt>
	 *            would be "#RRGGBB"). RGB values must be used, and must be in
	 *            hex.
	 * @return a new <tt>Color</tt> object based on the provided string.
	 */
	public static Color hexToColor(String s)
	{
		if (s.length() < 6 || s.length() > 7)
		{
			throw new IllegalArgumentException(
					"hex string must be two chars long: " + s + " ("
							+ s.length() + ")");
		}
		
		// removes first character if s is 7 chars long,
		// which should be when the first character is "#"
		if (s.length() >= 7 && s.charAt(0) == '#')
		{
			s = s.substring(1);
		}
		
		// returns a new color object using the provided hex string
		return new Color(Integer.parseInt(s.substring(0, 2), 16), // R value
				Integer.parseInt(s.substring(2, 4), 16), // G value
				Integer.parseInt(s.substring(4), 16) // B value
		);
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static String colorToHex(Color c)
	{
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		
		String r2 = Integer.toHexString(r);
		String g2 = Integer.toHexString(g);
		String b2 = Integer.toHexString(b);
		
		while (r2.length() < 2)
		{
			r2 = "0" + r2;
		}
		while (g2.length() < 2)
		{
			g2 = "0" + g2;
		}
		while (b2.length() < 2)
		{
			b2 = "0" + b2;
		}
		
		return r2 + g2 + b2;
	}
	
	/**
	 * Strips the color tokens from the given <tt>String</tt>.
	 * 
	 * @param s
	 *            the <tt>String</tt> to remove the color codes from.
	 * @return a clean <tt>String</tt> with no color codes.
	 */
	public static String removeColorTags(String s)
	{
		return s.replaceAll(getRegex(), "");
	}
	
	/**
	 * Takes the given color code for Quake 3 engine games and returns a
	 * <tt>Color</tt> object with the corresponding color.
	 * 
	 * @param s
	 *            a token that represents a color (eg. "<tt>^?</tt>", where '
	 *            <tt>?</tt>' is an alphanumeric character)
	 * @return a new <tt>Color</tt> object which corresponds to the provided
	 *         color token.
	 */
	public static Color resolveColor(String s)
	{
		return colorTable.get(s);
	}
	
	/**
	 * Returns a new <tt>String</tt> object after removing a single quotation
	 * character at the beginning and end of the given <tt>String</tt>.
	 * 
	 * @param s
	 *            the <tt>String</tt> to remove the quotes on
	 * @return a copy of the provided <tt>String</tt> without the single leading
	 *         or trailing quote character (if there was one).
	 */
	public static String trimQuotes(String s)
	{
		if (s.length() <= 2)
		{
			return s;
		}
		
		String q = "\""; // a quote character
		int start = 0;
		int end = s.length();
		
		if (s.startsWith(q))
		{
			start++;
		}
		
		if (s.endsWith(q))
		{
			end--;
		}
		
		return s.substring(start, end);
	}
	
	/**
	 * Returns a new <tt>Pattern</tt> that uses a regex containing a pattern
	 * that can be used to remove, Quake 3 engine game color tokens from a
	 * string.
	 * 
	 * @return a new <tt>Pattern</tt> object that matches Quake 3 game color
	 *         codes.
	 */
	public static Pattern getPattern()
	{
		return Pattern.compile(getRegex());
	}
	
	/**
	 * Returns the regex <tt>String</tt> that is used in the <tt>Pattern</tt>
	 * objects. The return value of this method can be directly used in a
	 * fashion similar to <tt>Pattern.compile(Colors.getRegex())</tt>.s
	 * 
	 * @return the regex <tt>String</tt>.
	 */
	public static String getRegex()
	{
		return regex;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getAboutBoxTitle()
	{
		return "Server Query Help";
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getHelpBoxTitle()
	{
		return "About Server Query";
	}
	
	// **********************************************
	// WHEN USED AS AN OBJECT, THE CODE BELOW IS USED
	// **********************************************
	
	/* the ServerQuery instance which servers as this Object's parent */
	private ServerQuery parent;
	
	/* the font metrics of the main graphics context of the parent */
	private FontMetrics fontMetrics;
	
	/**
	 * 
	 * @param parent
	 */
	public Text(ServerQuery parent)
	{
		this.parent = parent;
		this.start();
	}
	
	/**
	 * 
	 *
	 */
	private void init()
	{
		this.fontMetrics = null;
	}
	
	/**
	 * 
	 * @param fontMetrics
	 */
	public void setFontMetrics(FontMetrics fontMetrics)
	{
		this.fontMetrics = fontMetrics;
	}
	
	/**
	 * 
	 * @return
	 */
	public FontMetrics getFontMetrics()
	{
		if (fontMetrics == null)
		{
			fontMetrics = parent.getDisplayFontMetrics();
		}
		
		if (fontMetrics == null)
		{
			throw new IllegalArgumentException("FontMetrics cannot be null");
		}
		
		return fontMetrics;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTruncationThreshold()
	{
		return parent.getColumnWidth() - 20;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public int drawnStringLength(String s)
	{
		String n = removeColorTags(s);
		int width = 0;
		
		for (int i = 0; i < n.length(); i++)
		{
			width += getFontMetrics().charWidth(n.charAt(i));
		}
		
		return width;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public int truncate(String s)
	{
		return truncate(s, "");
	}
	
	/**
	 * 
	 * @param s
	 * @param toBeAppended
	 *            NOTE: this <tt>String</tt> may not be at the end of <tt>s</tt>
	 *            .
	 * @return
	 */
	public int truncate(String s, String toBeAppended)
	{
		String p = removeColorTags(toBeAppended);
		int t = getTruncationThreshold();
		
		if (drawnStringLength(s) + drawnStringLength(p) <= t)
		{
			return s.length();
		}
		
		for (int i = s.length(); i > 0; i--)
		{
			if (drawnStringLength(s.substring(0, i)) + drawnStringLength(p) <= t)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @param properties
	 */
	public synchronized static void syncSettings(PropertyManager properties)
	{
		if (properties != null)
		{
			properties.put(Vars.KEY_HELP_TEXT_URL, Vars.VALUE_HELP_TEXT_URL);
			properties.put(Vars.KEY_ABOUT_TEXT_URL, Vars.VALUE_ABOUT_TEXT_URL);
		}
	}
	
	public void start()
	{
		if (th == null)
		{
			// define a new thread
			th = new Thread(this);
			// start this thread
			th.start();
		}
	}
	
	public void stop()
	{
		th = null;
	}
	
	/**
	 * 
	 */
	@Override
	public void run()
	{
		Thread thisThread = Thread.currentThread();
		thisThread.setPriority(Thread.MIN_PRIORITY);
		
		init();
		
		while (th == thisThread)
		{
			// required at end of while
			try
			{
				// Stop thread for the "specified" time/10 ms
				Thread.sleep(Vars.THREAD_SLEEP_TIME);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
