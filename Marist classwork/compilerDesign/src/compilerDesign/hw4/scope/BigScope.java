package compilerDesign.hw4.scope;

import java.util.LinkedList;
import java.util.ListIterator;

import compilerDesign.hw4.grammar.Grammar4;
import compilerDesign.hw4.scope.Entry.Type;

public class BigScope
{
	private LinkedList<SmallScope> symbolTable;
	private SmallScope currentScope;
	
	public BigScope()
	{
		symbolTable = new LinkedList<SmallScope>();
	}
	
	public void addNewScope()
	{
		// linked list temp = new smallScope();
		currentScope = new SmallScope();
		symbolTable.add(currentScope); 
	}
	
	public void closeCurrentScope()
	{
		currentScope.setInScope(false);
		
		int i = symbolTable.size() - 1;
		
		ListIterator<SmallScope> itr = symbolTable.listIterator(i);
		
		while(!currentScope.isValidScope() && itr.hasPrevious())
			currentScope = itr.previous();
	}
	
	public void addToCurrentScope(String name, Type et)
	{
		Entry e = new Entry(name, et);
		
		currentScope.addToSmallScope(e);
	}
	
	public void addToCurrentScope(Entry e)
	{
		currentScope.addToSmallScope(e);
	}
	
	public String displayTable()
	{
		StringBuilder buf = new StringBuilder();
		
		int i = 0;
		
		//cycle through symbolTable and print each .displaySmallScope
		for(SmallScope scope : symbolTable)
		{
			buf.append("Scope #" + i).append(Grammar4.NEWLINE);
			
			scope.displaySmallScope(buf);
			
			buf.append(Grammar4.NEWLINE).append(Grammar4.NEWLINE);
			
			i++;
		}
		
		return buf.toString();
	}
	
	public boolean verifyType(String id, Type actual)
	{
		Entry e = getEntryByName(id);
		
		if(e != null && e.getType() == actual)
			return true;
		
		return false;
	}
	
	public Entry getEntryByName(String name)
	{
		for(SmallScope scope : symbolTable)
		{
			if (scope.isValidScope())
			{
				Entry e = scope.getEntryByName(name);
				
				if(e != null)
					return e;
			}
		}
		
		return null;
	}
	
	public boolean isEntryInScope(Entry e)
	{
		//cycle through symbtolTable, check is each small scope
		//is in scope, if not, skip, and see if valid....
		boolean killDanner = false;
		
		for(SmallScope scope : symbolTable)
		{
			if (scope.isValidScope())
			{
				if( scope.scanScopeFor(e) )
					killDanner = true;
			}      
		}
		return killDanner;
	}
	
	public void reset()
	{
		symbolTable.clear();
		currentScope = null;
	}
}
