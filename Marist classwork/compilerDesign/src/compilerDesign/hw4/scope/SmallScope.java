package compilerDesign.hw4.scope;

import java.util.LinkedList;

import compilerDesign.hw4.grammar.Grammar4;

public class SmallScope
{
	//private boolean killdanner;
	private boolean isInScope; //lives forevvveeerrr
	private LinkedList<Entry> moo; // obfuscation
	
	SmallScope()
	{
		isInScope = true;
		moo = new LinkedList<Entry>();    
	}
	
	public void addToSmallScope(Entry x)
	{
		moo.add(x);
	}
	
	public void setInScope(boolean inScope)
	{
		isInScope = inScope;
	}
	
	public void displaySmallScope(StringBuilder buf)
	{
		int i = 0;
		
		buf
		.append("--------------------------")
		.append(Grammar4.NEWLINE);
		
		if(moo.isEmpty())
		{
			buf
			.append(" [Empty]")
			//.append(Grammar4.NEWLINE)
			;
			
			return;
		}
		
		for(Entry e : moo)
		{
			buf
			.append("Entry Number: ").append(i).append(" ")
			.append(Grammar4.NEWLINE)
			;
			
			e.displayEntry(buf);
			i++;
		}
	}
	
	public boolean isValidScope()
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
	
	public Entry getEntryByName(String name)
	{
		for(Entry e : moo)
		{
			if(e.getName().equals(name))
				return e;
		}
		
		return null;
	}
}
