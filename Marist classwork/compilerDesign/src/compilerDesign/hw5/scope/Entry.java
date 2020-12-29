package compilerDesign.hw5.scope;

import compilerDesign.hw5.grammar.Grammar5;

public class Entry 
{
	// e = Entry for dan
	
	public enum Type { INT, CHAR };
	
	private String name;
	private Type entryType;
	
	Entry(String name)
	{
		this(name, null);
	}
	
	Entry(String name, Type entryType)
	{
		this.name = name;
		this.entryType = entryType;
	}
	
	public void display(StringBuilder buf)
	{
		buf
		.append("      Name  : ").append(name)
		.append(Grammar5.NEWLINE)
		.append("      Type  : ").append(entryType)
		.append(Grammar5.NEWLINE)
		.append(Grammar5.NEWLINE)
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
	
	public Type getType() 
	{
		return entryType;
	}
	
	public void setType(Type entryType) 
	{
		this.entryType = entryType;
	}
	
	@Override
	public String toString()
	{
		return "Name: " + name + ", Type: " + entryType;
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
