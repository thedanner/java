/* Util.java */
package _mine.serverQuery.util;

/**
 * A collection of various utilities. It also contains conveience methods that
 * mirror the functionality of <tt>System.out.print()/ln()</tt>, with the same
 * parameters.
 * 
 * @author Dan
 * @version Jan 21, 2006
 */
public class Util
{
	/*  */
	private static boolean isWindows = System.getProperty("os.name")
			.startsWith("Windows");
	
	/*  */
	private static boolean isLinux = System.getProperty("os.name").startsWith(
			"Linux");
	
	/**
	 * 
	 * @return
	 */
	public static boolean isWindowsOS()
	{
		return isWindows;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isLinuxOS()
	{
		return isLinux;
	}
	
	public static void p(Object... objects)
	{
		for (Object object : objects)
		{
			System.out.print(object);
		}
	}
	
	public static void pl(Object... objects)
	{
		p(objects);
		
		System.out.println();
	}
	
	/**
	 * 
	 * @param s
	 */
	public static String[] trimStrings(String[] s) {
		if(s == null)
		{
			return null;
		}
		
		String[] s2 = new String[s.length];
		
		for(int i = 0; i < s.length; i++)
		{
			s2[i] = s[i].trim();
		}
		
		return s2;
	}
		
}
