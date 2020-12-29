package compilerDesign.hw5.scope;

import java.util.LinkedList;
import java.util.ListIterator;

import compilerDesign.hw5.grammar.Grammar5;
import compilerDesign.hw5.scope.Entry.Type;

public class BigScope
{
	private LinkedList<SmallScope> symbolTable;
	private SmallScope currentScope;
	
	public BigScope()
	{
		symbolTable = new LinkedList<SmallScope>();
		
		currentScope = null;
	}
	
	public void addNewScope()
	{
		currentScope = new SmallScope();
		symbolTable.add(currentScope); 
	}
	
	public void closeCurrentScope()
	{
		currentScope.setInScope(false);
		
		int i = symbolTable.size() - 1;
		
		ListIterator<SmallScope> itr = symbolTable.listIterator(i);
		
		while(!currentScope.isVisibleScope() && itr.hasPrevious())
			currentScope = itr.previous();
	}
	
	public void addToCurrentScope(String name, Type et)
	{
		Entry e = new Entry(name, et);
		
		addToCurrentScope(e);
	}
	
	public void addToCurrentScope(Entry e)
	{
		currentScope.addToSmallScope(e);
	}
	
	public String display()
	{
		StringBuilder buf = new StringBuilder();
		
		int i = 0;
		
		//cycle through symbolTable and print each .display()
		for(SmallScope scope : symbolTable)
		{
			buf.append("Scope #" + i).append(Grammar5.NEWLINE);
			
			scope.display(buf);
			
			i++;
		}
		
		return buf.toString();
	}
	
	public Entry getEntryById(String id)
	{
		for(SmallScope scope : symbolTable)
		{
			if (scope.isVisibleScope())
			{
				Entry e = scope.scanScopeFor(id);
				
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
			if (scope.isVisibleScope())
			{
				if( scope.scanScopeFor(e) )
					killDanner = true;
			}
		}
		return killDanner;
	}
	
	public int getScopeId(String id)
	{
		for(SmallScope scope : symbolTable)
		{
			if (scope.isVisibleScope())
			{
				Entry e = scope.scanScopeFor(id);
				
				if(e != null)
					return scope.getScopeId();
			}
		}
		
		return -1;
	}
	
	public int getCurrentScopeId()
	{
		if(currentScope != null)
			return currentScope.getScopeId();
		
		return -1;
	}
	
	public void reset()
	{
		symbolTable.clear();
		currentScope = null;
		
		SmallScope.reset();
	}
}
