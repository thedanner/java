package capping.jscribe.gui.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtil
{
	/**
	 * It's back (again):
	 * Really, really verbose way of getting the path to the grammar specification
	 * for this assignment. We're assuming that the a plain text file will contain
	 * a human readable representation of a grammar. If it's in the same
	 * directory as a class file, use that class file as the first argument
	 * (obtained from static .class or object.getClass() ) and the name of
	 * grammar file as the second. This will return a string that can be used
	 * by getClass().getResourceAsStream(String) to read a file, and works for
	 * resources within a JAR file.
	 */
	public static<T> String getResourcePath(Class<T> c, String file)
	{
		String name =
			'/' + c.getPackage().getName().replace('.', '/') + '/' + file;
		
		return name;
	}
	
	public static byte[] getImageData(String imageName) throws IOException
	{
		String imageResourcePath = getResourcePath(
				ResourceUtil.class, imageName);
		
		InputStream in =
			ResourceUtil.class.getResourceAsStream(imageResourcePath);
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		
		byte[] imageData = new byte[4096];
		
		int size = in.read(imageData);
		
		while(size > 0)
		{
			int len = Math.min(size, imageData.length);
			
			byteOut.write(imageData, 0, len);
			
			size = in.read(imageData);
		}
		
		return byteOut.toByteArray();
	}
}
