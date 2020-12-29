package _mine._misc;

import java.io.File;
import java.io.FileFilter;

public class EmptyDirScrubber implements FileFilter
{
	//private static boolean DRY_RUN = false;
	private static boolean DRY_RUN = true;
	
	private static EmptyDirScrubber dirInstance = new EmptyDirScrubber(
			Mode.Directories);
	
	private Mode mode;
	
	private enum Mode
	{
		Directories, Files
	}
	
	private EmptyDirScrubber(Mode mode)
	{
		this.mode = mode;
	}
	
	@Override
	public boolean accept(File pathname)
	{
		switch (mode)
		{
		case Directories:
			return pathname.isDirectory();
		case Files:
			return !pathname.isFile();
		default:
			throw new IllegalArgumentException("mode not set");
		}
	}
	
	// Useful static stuffs.
	public static void deleteEmptyDirs(File rootPath, boolean dryRun)
	{
		deleteEmptyDirsRecursively(rootPath, dryRun);
	}
	
	private static void deleteEmptyDirsRecursively(File rootPath, boolean dryRun)
	{
		// First, do a depth-first search.  Once we're as deep as the tree,
		// we start traveling back up the tree, deleting empty directories
		// as we go.
		if (rootPath.list().length > 0)
		{
			// If the current directory has no files, it is a candidate
			// for removal. We have to check if it really is.
			File[] dirChildren = rootPath.listFiles(dirInstance);
			
			for (int i = 0; i < dirChildren.length; i++)
			{
				deleteEmptyDirsRecursively(dirChildren[i], dryRun);
			}
		}
		
		if (0 == rootPath.list().length)
		{
			if (rootPath.canWrite())
			{
				message(
						String.format("cannot delete '%s'", rootPath.getPath()),
						dryRun);
			}
			else
			{
    			boolean deleteResult = delete(rootPath, dryRun);
    			
    			if (deleteResult)
    			{
    				
    				message(
    						String.format("deleteing '%s'", rootPath.getPath()),
    						dryRun);
    			}
    			else
    			{
    				message(
    						String.format("could not delete '%s'", rootPath.getPath()),
    						dryRun);
    			}
			}
		}
	}
	
	private static boolean delete(File path, boolean dryRun)
	{
		if (dryRun)
		{
			return path.canWrite();
		}
		else
		{
			return path.delete();
		}
	}
	
	private static void message(String message, boolean dryRun)
	{
		if (dryRun)
		{
			System.out.print("[dry run] ");
		}
		
		System.out.println(message);
	}
	
	public static void main(String[] args)
	{
		deleteEmptyDirs(new File("D:\\media\\music"), DRY_RUN);
		deleteEmptyDirs(new File("D:\\media\\music_old"), DRY_RUN);
	}
}
