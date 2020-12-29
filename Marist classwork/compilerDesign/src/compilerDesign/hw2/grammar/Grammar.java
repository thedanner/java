package compilerDesign.hw2.grammar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import compilerDesign.hw2.RecursiveDescentParserGui;
import compilerDesign.hw2.token.SymbolTable;
import compilerDesign.hw2.token.Token;

/**
 * 
 * @author Dan
 *
 * @param <T> the type of the lexer's token output
 * @param <V> the generic type of V
 */
public abstract class Grammar<T extends Token> {
	protected RecursiveDescentParserGui out;
	
	protected SymbolTable symbols;
	
	protected LinkedList<T> tokens;
	protected ListIterator<T> tokenItr;
	
	protected T prevToken;
	protected T currentToken;
	protected T nextToken;
	
	protected Program parseOutput;
	protected LinkedList<CompileError> errorLog;
	
	public Grammar() {
		this(null);
	}
	
	public Grammar(RecursiveDescentParserGui comp) {
		out = comp;
		
		init();
	}
	
	private void init() {
		symbols = new SymbolTable();
		
		tokens = new LinkedList<T>();
		tokenItr = null;
		
		prevToken = null;
		currentToken = null;
		nextToken = null;
		
		parseOutput = null;
		errorLog = new LinkedList<CompileError>();
	}
	
	public RecursiveDescentParserGui getOutputComponent() {
		return out;
	}
	
	public void setOutputComponent(RecursiveDescentParserGui comp) {
		out = comp;
	}
	
	public void run(String source) {
		tokens = lex(source);
		parseOutput = parse();
	}
	
	public void reset() {
		symbols.clear();
		
		tokens.clear();
		tokenItr = null;
		
		prevToken = null;
		currentToken = null;
		nextToken = null;
		
		parseOutput = null;
		errorLog.clear();
	}
	
	protected abstract int getTokenType(String tokenStr);
	
	protected abstract LinkedList<T> lex(String source);
	
	protected abstract Program parse();
	
	protected void getNextToken() {
		if(tokenItr == null) {
			tokenItr = tokens.listIterator();
			
			if(tokenItr.hasNext())
				nextToken = tokenItr.next();
		}
		
		prevToken = currentToken;
		currentToken = nextToken;
		
		if(tokenItr.hasNext()) {
			nextToken = tokenItr.next();
			
			out.appendln(String.format(
					"Current token: '%1$s'",
					currentToken.getSourceToken().toString()));
		} else {
			nextToken = null;
		}
	}
	
	protected void getPrevToken() {
		if(tokenItr != null) {
			nextToken = currentToken;
			currentToken = prevToken;
			
			if(tokenItr.hasPrevious())
				prevToken = tokenItr.previous();
		}
	}
	
	protected void logError(String message, Token source) {
		errorLog.add(new CompileError(message, source));
	}
	
	protected Iterator<CompileError> getErrorLogIterator() {
		return errorLog.iterator();
	}
}
