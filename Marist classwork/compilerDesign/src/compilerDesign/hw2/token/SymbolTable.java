package compilerDesign.hw2.token;

import java.util.Hashtable;

public class SymbolTable {
	private Hashtable<String, Object> table;
	
	public SymbolTable() {
		table = new Hashtable<String, Object>();
	}
	
	public Object addEntry(String name, Object val) {
		return table.put(name, val);
	}
	
	public Object setEntry(String name, Object val) {
		return table.put(name, val);
	}
	
	public Object computeValue(Token t) {
		return table.get(t.getSourceToken());
	}
	
	public void clear() {
		table.clear();
	}
}
