/* Project4Main.java */
package compilerDesign.hw5;

import java.awt.EventQueue;

/**
 * Main class for the final Homework Project.
 * @author Dan Mangiarelli, Dave Weisfelner
 */
public class ProjectFinalMain
{
	/**
	 * It's back (again):
	 * Really, really verbose way of getting the path to the grammar spec for
	 * this assignment. We're assuming that the a plaintext file will contain a
	 * human readable representation of a grammar. If it's in the same
	 * directory as a class file, use that class file as the first argument
	 * (obtained from static .class or object.getClass() ) and the name of
	 * grammar file as the second. This will return a string that can be used
	 * by getClass().getResourceAsStream(String) to read a file, and works for
	 * resources within a JAR file.
	 */
	public static<T> String getResourcePath(Class<T> c, String file)
	{
		String name = '/' + c.getPackage().getName().replace('.', '/') + '/'
				+ file;
		
		return name;
	}
	
	public static void main(String[] args)
	{
		final String grammarPath = getResourcePath(
				ProjectFinalMain.class, "grammar_spec.txt");
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				SplashScreen.showSplash();
				
				new ProjectFinalGui(grammarPath).setVisible(true);
				
				SplashScreen.hideSplash();
			}
		});
	}
}
