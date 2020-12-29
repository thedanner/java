package game.persistence.recordStore;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class RecordStore
{
	private Preferences prefs;
	
	private RecordStore(Preferences prefs)
	{
		if (null == prefs)
		{
			throw new NullPointerException("'prefs' cannot be null");
		}
		
		this.prefs = prefs;
	}
	
	public String getString(String key)
	{
		return prefs.get(key, null);
	}
	
	public boolean getBoolean(String key)
	{
		return prefs.getBoolean(key, false);
	}
	
	public int getInt(String key)
	{
		return prefs.getInt(key, 0);
	}
	
	public long getLong(String key)
	{
		return prefs.getLong(key, 0L);
	}
	
	public float getFloat(String key)
	{
		return prefs.getFloat(key, 0.0f);
	}
	
	public double getDouble(String key)
	{
		return prefs.getDouble(key, 0.0);
	}
	
	public void setString(String key, String value)
	{
		prefs.put(key, value);
	}
	
	public void setBoolean(String key, boolean value)
	{
		prefs.putBoolean(key, value);
	}
	
	public void setInt(String key, int value)
	{
		prefs.putInt(key, value);
	}
	
	public void setLong(String key, long value)
	{
		prefs.putLong(key, value);
	}
	
	public void setFloat(String key, float value)
	{
		prefs.putFloat(key, value);
	}
	
	public void setDouble(String key, double value)
	{
		prefs.putDouble(key, value);
	}
	
	public void deleteRecord(String key)
	{
		prefs.remove(key);
	}
	
	public void hasKey()
	{
		prefs.
	}
	
	public static void deleteRecordStore(String name) throws BackingStoreException
	{
		String fullNodeName = resolveNodeName(name);
		
		if (Preferences.userRoot().nodeExists(fullNodeName))
		{
			Preferences.userRoot().node(fullNodeName).removeNode();
		}
	}
	
	public static RecordStore openRecordStore(String name, boolean createIfNecessary)
	throws BackingStoreException, RecordStoreNotFoundException
	{
		String fullNodeName = resolveNodeName(name);
		
		if (!createIfNecessary)
		{
			if (!Preferences.userRoot().nodeExists(fullNodeName))
			{
				throw new RecordStoreNotFoundException(name + "(" + fullNodeName + ")");
			}
		}
		
		Preferences store =
			Preferences.userRoot().node(fullNodeName);
		
		return new RecordStore(store);
	}
	
	private static String resolveNodeName(String name)
	{
		return "/recordStore/MIDPort/" + name;
	}
}
