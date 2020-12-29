package game.persistence;

import game.persistence.recordStore.RecordStore;
import game.persistence.recordStore.RecordStoreException;
import game.persistence.recordStore.RecordStoreNotFoundException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.prefs.BackingStoreException;

/**
 * 
 */
public class RecordStoreFacade
{
	public static final char KEY_VALUE_SEPARATOR = '=';
	
	private String storeName;
	
	private RecordStore rs;
	
	public RecordStoreFacade(String storeName)
	throws RecordStoreException, BackingStoreException, RecordStoreNotFoundException
	{
		this.storeName = storeName;
		this.rs = RecordStore.openRecordStore(storeName, true);
		
		readFromStore();
	}
	
	public String getStoreName()
	{
		return storeName;
	}
	
	// Getters
	public String get(String key)
	{
		return rs.getString(key);
	}
	
	public int getInt(String key)
	{
		if (!this.hasKey(key))
		{
			throw new MissingKeyException(key);
		}
		
		return Integer.parseInt(this.get(key));
	}
	
	public double getDouble(String key)
	{
		if (!this.hasKey(key))
		{
			throw new MissingKeyException(key);
		}
		
		return Double.parseDouble(this.get(key));
	}
	
	public boolean getBoolean(String key)
	{
		if (!this.hasKey(key))
		{
			throw new MissingKeyException(key);
		}
		
		String value = this.get(key);
		
		boolean state = false;
		
		if (value.equalsIgnoreCase("true"))
		{
			state = true;
		}
		else if (value.equalsIgnoreCase("false"))
		{
			state = false;
		}
		else
		{
			throw new InvalidValueTypeException(key, "Boolean", value);
		}
		
		return state;
	}
	
	// Setters
	public void put(String key, String value)
	{
		try
		{
			writeSetting(key, value);
		}
		catch (RecordStoreException ex)
		{
			ex.printStackTrace();
			//TODO log stack trace
		}
	}
	
	public void putInt(String key, int value)
	{
		this.put(key, Integer.toString(value));
	}
	
	public void putDouble(String key, int value)
	{
		this.put(key, Double.toString(value));
	}
	
	public void putBoolean(String key, boolean value)
	{
		this.put(key, "" + value);
	}
	
	// Other info accessors
	public int size()
	{
		return recordIds.size();
	}
	
	public boolean hasKey(String key)
	{
		return (values.containsKey(key));
	}
	
	public String remove(String key) throws RecordStoreException
	{
		String value = (String) values.get(key);
		
		if (this.hasKey(key))
		{
			int recordId = ((Integer) recordIds.get(key)).intValue();
			
			rs.deleteRecord(recordId);
		}
		
		recordIds.remove(key);
		values.remove(key);
		
		return value;
	}
	
	public void removeAll() throws RecordStoreException
	{
		String[] keys = this.getKeys();
		
		for (int i = 0; i < keys.length; i++)
		{
			this.remove(keys[i]);
		}
	}
	
	public String[] getKeys()
	{
		Enumeration<String> keysEnum = recordIds.keys();
		
		String[] keys = new String[this.size()];
		
		for (int i = 0; keysEnum.hasMoreElements(); i++)
		{
			keys[i] = (String) keysEnum.nextElement();
		}
		
		return keys;
	}
	
	public String[][] getPairs()
	{
		String[] keys = this.getKeys();
		String[][] list = new String[values.size()][];
		
		for (int i = 0; i < list.length; i++)
		{
			list[i] = new String[]
			{ keys[i], this.get(keys[i]) };
		}
		
		return list;
	}
	
	private void readFromStore() throws RecordStoreException
	{
		RecordEnumeration recs = rs.enumerateRecords(null, null, false);
		
		while (recs.hasNextElement())
		{
			int recordId = recs.nextRecordId();
			
			byte[] recordData = rs.getRecord(recordId);
			
			String record = new String(recordData);
			
			int separatorIndex = record.indexOf(KEY_VALUE_SEPARATOR);
			
			if (separatorIndex >= 0)
			{
				String key = record.substring(0, separatorIndex);
				
				// Don't include the separator.
				String value = record.substring(separatorIndex + 1);
				
				recordIds.put(key, new Integer(recordId));
				values.put(key, value);
			}
		}
	}
	
	private void writeSetting(String key, String value)
			throws RecordStoreException
	{
		int recordId = -1;
		
		if (recordIds.containsKey(key))
		{
			recordId = ((Integer) recordIds.get(key)).intValue();
			
			rs.deleteRecord(recordId);
		}
		
		String dataToStore = key + KEY_VALUE_SEPARATOR + value;
		
		byte[] data = dataToStore.getBytes();
		recordId = rs.addRecord(data, 0, data.length);
		
		recordIds.put(key, new Integer(recordId));
		values.put(key, value);
	}
}
