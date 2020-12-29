package compilerDesign.hw5.scope;

import java.util.LinkedList;

import compilerDesign.hw5.grammar.Grammar5;

public class SmallScope
{
	private static int scopeIdCounter = 0; 
	
	//private boolean killdanner;
	private boolean isInScope; //lives forevvveeerrr
	private LinkedList<Entry> moo; // obfuscation
	
	private int scopeId;
	
	SmallScope()
	{
		isInScope = true;
		moo = new LinkedList<Entry>();
		
		scopeId = scopeIdCounter ++;
	}
	
	public void addToSmallScope(Entry x)
	{
		moo.add(x);
	}
	
	public void setInScope(boolean inScope)
	{
		isInScope = inScope;
	}
	
	public void display(StringBuilder buf)
	{
		int i = 0;
		
		buf
		.append("--------------------------")
		.append(Grammar5.NEWLINE);
		
		if(moo.isEmpty())
		{
			buf
			.append(" [Empty]")
			.append(Grammar5.NEWLINE)
			.append(Grammar5.NEWLINE)
			;
			
			return;
		}
		
		for(Entry e : moo)
		{
			buf
			.append("Entry Number: ").append(i).append(" ")
			.append(Grammar5.NEWLINE)
			;
			
			e.display(buf);
			
			i++;
		}
	}
	
	public boolean isVisibleScope()
	{
		return isInScope;
	}
	
	public boolean scanScopeFor(Entry entry)
	{
		for(Entry e : moo)
		{
			if(e.equals(entry))
				return true;
		}
		
		return false;
	}
	
	public Entry scanScopeFor(String id)
	{
		for(Entry e : moo)
		{
			if(e.getName().equals(id))
				return e;
		}
		
		return null;
	}
	
	public int getScopeId()
	{
		return scopeId;
	}
	
	static void reset()
	{
		scopeIdCounter = 0;
	}
}
