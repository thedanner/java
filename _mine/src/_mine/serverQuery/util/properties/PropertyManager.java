/* PropertyManager.java */
package _mine.serverQuery.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.util.Util;

/**
 * 
 * @author Dan
 * @version Mar 7, 2006
 */
public class PropertyManager {
	/* the (relative) path to the save properties file */
	public static final File PROPERTIES_FILE =
		new File("sq-prefs.ini");
	
	/*  */
	private static PropertyManager instance;
	
	/*  */
	private ServerQuery parent;
	
	/*  */
	private File propertiesFile;
	
	/*  */
	private Properties properties;
	
	/**
	 * 
	 *
	 */
	public PropertyManager(ServerQuery parent) {
		this(parent, null);
	}
	
	/**
	 * 
	 *
	 */
	public PropertyManager(ServerQuery parent, File propertiesFile) {
		this.parent = parent;
		this.propertiesFile = propertiesFile;
		this.properties = new Properties();
		
		if(parent == null)
			throw new NullPointerException("parent cannot be null");
		
		readProperties();
	}
	
	/**
	 * 
	 * 
	 */
	public synchronized void readProperties() {
		if(propertiesFile != null && propertiesFile.exists()) {
			try {
				properties.load(new FileInputStream(propertiesFile));
				compareVersion();
			} catch(FileNotFoundException e) {
				System.err.println("Cannot find properties file, " +
						"reverting to defaults.");
				e.printStackTrace();
				Vars.generateNewProperties(this);
			} catch(IOException e) {
				e.printStackTrace();
				Vars.generateNewProperties(this);
			}
		} else {
			// no prefs file found
			if(!parent.isApplet())
				Util.pl("Reverting to default settings.");
			Vars.generateNewProperties(this);
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void compareVersion() throws IOException {
		int ver = getInt(Vars.KEY_PROPERTIES_VERSION, -1);
		if(ver != Vars.VALUE_PROPERTIES_VERSION) {
			Util.pl("Stored properties version (" + ver + ") is " +
					"incompatible with current application version. " +
					"Resetting the properties file.");
			clean(ver);
			Vars.generateNewProperties(this);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public synchronized boolean store() {
		if(propertiesFile != null) {
			try {
				properties.store(new FileOutputStream(propertiesFile), null);
				return true;
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 
	 *
	 */
	public synchronized void clean(int ver) {
		try {
			Preferences.userRoot().node("hyd-sq").clear();
		} catch(BackingStoreException e) { }
		
		properties.clear();
	}
	
	/**
	 * 
	 */
	public synchronized void put(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * 
	 */
	public synchronized String get(String key, String def) {
		return properties.getProperty(key, def);
	}
	
	/**
	 * 
	 */
	public synchronized void remove(String key) {
		properties.remove(key);
	}
	
	/**
	 * 
	 */
	public synchronized void clear() {
		properties.clear();
	}
	
	/**
	 * 
	 */
	public synchronized void putInt(String key, int value) {
		if(key == null)
			throw new IllegalArgumentException("Key cannot be null " +
					"[" + key + "]");
		properties.put(key, new Integer(value).toString());
	}
	
	/**
	 * 
	 */
	public synchronized int getInt(String key, int def) {
		try {
			return new Integer((String)properties.get(key)).intValue();
		} catch(Exception e) {
			return def;
		}
	}
	
	/**
	 * 
	 */
	public synchronized void putLong(String key, long value) {
		properties.put(key, new Long(value).toString());
	}
	
	/**
	 * 
	 */
	public synchronized long getLong(String key, long def) {
		try {
			return new Long((String)properties.get(key)).longValue();
		} catch(Exception e) {
			return def;
		}
	}
	
	/**
	 * 
	 */
	public synchronized void putBoolean(String key, boolean value) {
		properties.put(key, new Boolean(value).toString());
	}
	
	/**
	 * 
	 */
	public synchronized boolean getBoolean(String key, boolean def) {
		try {
			return new Boolean((String)properties.get(key)).booleanValue();
		} catch(Exception e) {
			return def;
		}
	}
	
	/**
	 * 
	 */
	public synchronized void putFloat(String key, float value) {
		properties.put(key, new Float(value).toString());
	}
	
	/**
	 * 
	 */
	public synchronized float getFloat(String key, float def) {
		try {
			return new Float((String)properties.get(key)).floatValue();
		} catch(Exception e) {
			return def;
		}
	}
	
	/**
	 * 
	 */
	public synchronized void putDouble(String key, double value) {
		properties.put(key, new Double(value).toString());
	}
	
	/**
	 * 
	 */
	public synchronized double getDouble(String key, double def) {
		try {
			return new Double((String)properties.get(key)).doubleValue();
		} catch(Exception e) {
			return def;
		}
	}
	
	/**
	 * 
	 */
	@Override
	public synchronized String toString() {
		return properties.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public static PropertyManager getInstance(ServerQuery parent) {
		if(instance == null) {
			if(parent.isApplet())
				instance = new PropertyManager(parent);
			else {
				File f = PROPERTIES_FILE;
				instance = new PropertyManager(parent, f);
			}
		}
		return instance;
	}
}
