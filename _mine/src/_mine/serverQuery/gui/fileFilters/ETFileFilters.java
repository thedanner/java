/* ETFileFilters.java */
package _mine.serverQuery.gui.fileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import _mine.serverQuery.util.Util;

/**
 * @author Dan
 * @version Dec 20, 2005
 */
public class ETFileFilters {
	/**
	 * 
	 * @return
	 */
	public static FileFilter[] getFilters() {
		if(Util.isWindowsOS()) {
			return new FileFilter[] {
					new ETFileFilter_win(),
					new ETFileFilterType2_win(),
					new ExeFileFilter_win()
			};
		} else if(Util.isLinuxOS()) {
			return new FileFilter[] {
					new ETFileFilter_linux()
			};
		} else {
			return new FileFilter[] {
					new Filter_all()
			};
		}
	}
}

class ETFileFilter_win extends FileFilter {
	private String description = "ET excutable, \"ET.exe\"";
	
	@Override
	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory())
				return true;
			if(f.getName().equalsIgnoreCase("ET.exe"))
				return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + "$" + getDescription();
	}
}

class ETFileFilterType2_win extends FileFilter {
	private String description = "Any files that start with \"ET\" (\"ET*\")";
	
	@Override
	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory())
				return true;
			
			String n = f.getName().toLowerCase();
			
			if(n.startsWith("et") && n.endsWith(".exe"))
				return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + "$" + getDescription();
	}
}

class ExeFileFilter_win extends FileFilter {
	private String description = "Executable files, \"*.exe\"";
	
	@Override
	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory())
				return true;
			if(f.getName().toLowerCase().endsWith(".exe"))
				return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + "$" + getDescription();
	}
}

class ETFileFilter_linux extends FileFilter {
	private String description = "ET binary, \"et\"";
	
	@Override
	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory())
				return true;
			if(f.getName().equalsIgnoreCase("et"))
				return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + "$" + getDescription();
	}
}

class Filter_all extends FileFilter {
	private String description = "All files, \"*.*\"";
	
	@Override
	public boolean accept(File f) {
		if(f != null)
			return true;
		return false;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + "$" + getDescription();
	}
}
