package compilerDesign.hw4.scope;

import compilerDesign.hw4.grammar.Grammar4;

public class Entry 
{
	// e = Entry for dan
	
	public enum Type { INT, CHAR };
	
	private String name;
	private Type entryType;
	private String value; //need for later usage in suckXML
	private String scope; // not using cuz i forget what it does
	
	Entry(String name)
	{
		this(name, null);
	}
	
	Entry(String name, Type entryType)
	{
		this.name = name;
		this.entryType = entryType;
	}
	
	public void displayEntry(StringBuilder buf)
	{
		buf
		.append("      Name  : ").append(name)
		.append(Grammar4.NEWLINE)
		.append("      Type  : ").append(entryType)
		;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getScope() 
	{
		return scope;
	}
	
	public void setScope(String scope) 
	{
		this.scope = scope;
	}
	
	public Type getType() 
	{
		return entryType;
	}
	
	public void setType(Type entryType) 
	{
		this.entryType = entryType;
	}
	
	public String getValue() 
	{
		return value;
	}
	
	public void setValue(String value) 
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return "Name: " + name + ", Type: " + entryType + ", Value: " + value;
		
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Entry)
		{
			Entry e = (Entry) obj;
			
			if (e.name.equals(name) && e.entryType == entryType)
				return true;
		}
		
		return false;
	}
}
