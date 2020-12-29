package compilerDesign.hw2.grammar;

import static compilerDesign.hw2.grammar.TheGrammarRules.*;

import java.util.LinkedList;

import compilerDesign.hw2.RecursiveDescentParserGui;
import compilerDesign.hw2.token.Token;

public class TheGrammar extends Grammar<Token> {
	// temporary token counter
	private static int tokC = 0;
	
	public static final int ID_TOKEN = tokC++;
	public static final int DIGIT_TOKEN = tokC++;
	public static final int ASSIGN_OP_TOKEN = tokC++;
	
	public static final int ADD_OP_TOKEN = tokC++;
	public static final int SUB_OP_TOKEN = tokC++;
	
	public static final int LEFT_PAREN_TOKEN = tokC++;
	public static final int RIGHT_PAREN_TOKEN = tokC++;
	
	public static final int LEFT_BRACE_TOKEN = tokC++;
	public static final int RIGHT_BRACE_TOKEN = tokC++;
	
	public static final int PRINT_TOKEN = tokC++;
	
	public TheGrammar() {
		super();
	}
	
	public TheGrammar(RecursiveDescentParserGui comp) {
		super(comp);
	}
	
	@Override
	public void run(String source) {
		tokens = lex(source);
		
		int numLexErrors = displayLexResults();
		
		if(numLexErrors == 0) {
			parseOutput = parse();
			
			displayParseResults();
		}
	}
	
	@Override
	protected int getTokenType(String tokenStr) {
		char c = tokenStr.charAt(0);
		
		if(isId(c))
			return ID_TOKEN;
		
		if(isDigit(c))
			return DIGIT_TOKEN;
		
		if(isAssignOp(c))
			return ASSIGN_OP_TOKEN;
		
		if(isAddOp(c))
			return ADD_OP_TOKEN;
		
		if(isSubOp(c))
			return SUB_OP_TOKEN;
		
		if(isLeftParen(c))
			return LEFT_PAREN_TOKEN;
		
		if(isRightParen(c))
			return RIGHT_PAREN_TOKEN;
		
		if(isLeftBrace(c))
			return LEFT_PAREN_TOKEN;
		
		if(isRightBrace(c))
			return RIGHT_PAREN_TOKEN;
		
		if(isPrint(c))
			return PRINT_TOKEN;
		
		return -1;
	}
	
	@Override
	protected LinkedList<Token> lex(String source) {
		LinkedList<Token> t = tokens;
		
		String[] lines = source.split("[\r]\n");
		
		// i = line number
		for(int i = 0; i < lines.length; i++) {
			// Replace all whitespace with a single space.
			// This is to preserve the position on the line
			// where the token appeared. 
			String line = lines[i].replaceAll("\\s", " ");
			
			// j = position on line
			for(int j = 0; j < line.length(); j++) {
				char c = line.charAt(j);
				
				// (potentially) create the token if it's not a space
				if(c != ' ') {
					int tokenType = getTokenType(c+"");
					
					// add 1 to i and j to make the numbers
					// "human readable" (1 based, not 0 based)
					Token token = new Token(
							c+"", tokenType, i + 1, j + 1, null);
					
					if(tokenType == -1)
						logError("Invalid character: " + c, token);
					
					if(tokenType == DIGIT_TOKEN)
						// make sure c is viewed as a string
						// and not implicitly cast to an int
						token.setValue(new Integer(c+""));
				
					t.add(token);
				}
			}
		}
		
		t.add(Token.EOF);
		
		return t;
	}
	
	@Override
	protected Program parse() {
		return parseProgram();
	}
	
	private void displayTokens() {
		out.append("Lex returned tokens for parsing: ");
		
		for(Token t : tokens)
			out.append(t.getSourceToken());
		
		out.appendln();
		out.appendln();
	}
	
	private int displayLexResults() {
		int numErrors = errorLog.size();
		
		if(numErrors > 0) {
			String errorStr = String.format(
					"Errors found while Lexing: %1$s",
					numErrors);
			
			out.appendln(errorStr);
			out.appendln();
			
			for(CompileError err : errorLog)
				out.appendln("Error: " + err.toString());
			
			out.appendln();
		} else {
			displayTokens();
		}
		
		return numErrors;
	}
	
	private int displayParseResults() {
		int numErrors = errorLog.size();
		
		String errorStr = String.format(
				"Errors found while parsing: %1$s",
				numErrors);
		
		out.appendln();
		out.appendln(errorStr);
		out.appendln();
		
		if(numErrors > 0) {
			for(CompileError err : errorLog)
				out.appendln("Error: " + err.toString());
			
			out.appendln();
		}
		
		return numErrors;
	}
	
	//BEGIN PARSE METHODS
	private Program parseProgram() {
		out.appendlnV("in parseProgram()");
		
		Program p = null;
		
		Statement s = parseStatement();
		
		if(s != null) {
			p = new StatementProgram(s);
			
			getNextToken();
			
			if(!isEOF(currentToken)) {
				logError(String.format(
						"Expecting EOF, found '%1$s'",
						currentToken.getSourceToken()), currentToken);
				
				return null;
			}
		}
		
		return p;
	}
	
	private Statement parseStatement() {
		out.appendlnV("in parseStatement()");
		
		getNextToken();
		
		out.appendV("Expecting ");
		
		Statement s = null;
		
		if(isPrint(currentToken)) {
			out.appendlnV("token 'P'");
			s = parsePrintStatement();
			
		} else if(isLeftBrace(currentToken)) {
			out.appendlnV("token '{'");
			s = parseCompoundStatement();
			
		} else if(isId(currentToken)) {
			out.appendlnV("an identifier");
			s = parseAssignmentStatement();
			
		} else {
			logError(String.format(
					"Expecting one of 'P', '{', or an identifier, " +
					"found '%1$s'",
					currentToken.getSourceToken()), currentToken);
			
			out.appendlnV("one of 'P', '{', or an identifier (not found)");
			
			s = null;
		}
		
		return s;
	}
	
	private StatementList parseStatementList() {
		out.appendlnV("in parseStatementList()");
		
		out.appendV("Expecting ");
		
		StatementList s = null;
		
		if(isFirstStatementList(nextToken)) {
			out.appendlnV("a statement");
			s = parsePairStatementList();
			
		} else if(isFollowStatementList(nextToken)) {
			out.appendlnV("token '}'");
			s = new EmptyStatementList();
			
		} else {
			logError(String.format(
					"Expecting one of 'P', '{', or an identifier," +
					"found '%1$s'",
					nextToken.getSourceToken()), nextToken);
			
			out.appendlnV("one of 'P', '{', or an identifier (not found)");
			
			s = null;
		}
		
		return s;
	}
	
	private Expression parseExpression() {
		out.appendlnV("in parseExpression()");
		
		getNextToken();
		
		out.appendV("Expecting ");
		
		Expression e = null;
		
		if(isDigit(currentToken)) {
			out.appendlnV("and found a digit");
			e = new Digit((Integer)currentToken.getValue());
			
			if(isOp(nextToken)) {
				out.appendlnV("Found an operator (next token)");
				
				e = parseOperationExpression();
				
				if(e == null)
					return null;
			}
		} else if(isId(currentToken)) {
			out.appendlnV("and found an identifier");
			e = new Id(currentToken.getSourceToken());
			
		} else {
			logError(String.format(
					"Expecting a digit or an identifier, " +
					"found '%1$s'",
					currentToken.getSourceToken()), currentToken);
			
			out.appendlnV("a digit or an identifier (not found)");
			e = null;
		}
		
		return e;
	}
	
	// Statement types
	private PrintStatement parsePrintStatement () {
		PrintStatement p = null;
		
		out.appendlnV("Found token 'P' (PrintStatement)");
		
		// assume currentToken already contains 'P'
		getNextToken();
		
		boolean error = false;
		
		if(!isLeftParen(currentToken)) {
			logError(String.format(
					"Expecting '(', found '%1$s'",
					currentToken.getSourceToken()), currentToken);
			
			error = true;
			
			getPrevToken();
			
			currentToken.setSourceString("(");
			
			out.append("**Assuming previous ");
		} else {
			out.appendV("Found ");
		}
		
		String message = "token '(' (left paren)";
		
		if(error)
			out.appendln(message);
		else
			out.appendlnV(message);
		
		Expression e = parseExpression();
		
		if(e == null)
			return null;
		
		// close the printStatement
		getNextToken();
		
		if(isRightParen(currentToken)) {
			p = new PrintStatement(e);
		} else {
			logError(String.format(
					"Expecting ')', found '%1$s'",
					currentToken.getSourceToken()), currentToken);
			
			return null;
		}
		
		return p;
	}
	
	private AssignmentStatement parseAssignmentStatement() {
		AssignmentStatement s = null;
		
		String id = currentToken.getSourceToken();
		
		out.appendlnV("Found an identifier");
		
		getNextToken();
		
		boolean error = false;
		
		if(!isAssignOp(currentToken)) {
			logError(String.format(
					"Expecting '=', found '%1$s'",
					currentToken.getSourceToken()), currentToken);
			
			error = true;
			
			getPrevToken();
			
			currentToken.setSourceString("=");
			
			out.append("**Assuming previous ");
		} else {
			out.appendV("Found ");
		}
		
		String message = "token '=' (AssignmentStatement)";
		
		if(error)
			out.appendln(message);
		else
			out.appendlnV(message);
		
		Expression e = parseExpression();
		
		if(e == null)
			return null;
		
		s = new AssignmentStatement(new Id(id), e);
		
		return s;
	}
	
	private CompoundStatement parseCompoundStatement() {
		CompoundStatement s = null;
		
		out.appendlnV("Found token '{'");
		
		if(isFirstStatementList(currentToken)) {
			out.appendlnV("Found a statement list");
			
			StatementList sl = parseStatementList();
			
			if(sl == null)
				return null;
			
			// close the CompoundStatement
			getNextToken();
			
			if(isRightBrace(currentToken)) {
				s = new CompoundStatement(sl);
			} else {
				logError(String.format(
						"Expecting ')', found '%1$s'",
						currentToken.getSourceToken()), currentToken);
				
				return null;
			}
			
			return s;
		} else {
			logError(String.format(
					"Expecting one of 'P', '{', or an identifier, " +
					"found '%1$s'",
					currentToken.getSourceToken()), currentToken);
			
			return null;
		}
	}
	
	// StatementList types
	private PairStatementList parsePairStatementList() {
		PairStatementList sl = null;
		
		if(isFirstStatement(nextToken)) {
			out.appendlnV("Found a statement");
			
			Statement s = parseStatement();
			
			if(s == null)
				return null;
			
			StatementList sl2 = parseStatementList();
			
			if(sl2 == null)
				return null;
			
			if(sl2 instanceof EmptyStatementList)
				sl = new PairStatementList(s, sl2);
			
			return sl;
		} else {
			return null;
		}
	}
	
	// Expression types
	private OperationExpression parseOperationExpression() {
		OperationExpression e = null;
		
		int left = (Integer)currentToken.getValue();
		
		getNextToken();
		
		int op = getTokenType(currentToken.getSourceToken());
		
		char opChar = 0;
		
		if(op == ADD_OP_TOKEN)
			opChar = '+';
		else if(op == SUB_OP_TOKEN)
			opChar = '-';
			
		out.appendV("Expecting an operator, found '" + opChar + "'");
		
		Expression e2 = parseExpression();
		
		if(e2 == null)
			return null;
		
		e = new OperationExpression(left, op, e2);
		
		return e;
	}
}

//THE PROGRAM
abstract class Program {} 

class StatementProgram extends Program {
	Statement stm;
	
	public StatementProgram(Statement s) {
		stm = s;
	}
}

//STATEMENTS
abstract class Statement {}

class PrintStatement extends Statement {
	Expression exp;
	
	public PrintStatement(Expression e) {
		exp = e;
	}
}

class AssignmentStatement extends Statement {
	Id id;
	Expression exp;
	
	public AssignmentStatement(Id i, Expression e) {
		id = i;
		exp = e;
	}
}

class CompoundStatement extends Statement {
	StatementList stmlist;
	
	public CompoundStatement(StatementList sl) {
		stmlist = sl;
	}
}

//STATEMENT LISTS
abstract class StatementList {}

class PairStatementList extends StatementList {
	Statement stm;
	StatementList stmlist;
	
	public PairStatementList() {
		this(null, null);
	}
	
	public PairStatementList(Statement s, StatementList sl) {
		stm = s;
		stmlist = sl;
	}
}

class EmptyStatementList extends StatementList {
	public EmptyStatementList() { }
}

//EXPRESSIONS
abstract class Expression {}

class OperationExpression extends Expression {
	int left;
	int op;
	Expression right;
	
	public OperationExpression(int l, int o, Expression r) {
		left = l;
		op = o;
		right = r;
	}
}

class Digit extends Expression {
	int digit;
	
	public Digit(int d) {
		digit = d;
	}
}

class Id extends Expression {
	String id;
	
	public Id(String i) {
		id = i;
	}
}
