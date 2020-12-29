package compilerDesign.hw2.grammar;

import compilerDesign.hw2.token.Token;

public class TheGrammarRules {
	//FIRST
	public static boolean isFirstProgram(Token t) {
		return isFirstStatement(t);
	}
	
	public static boolean isFirstStatement(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return c == 'P' || c == '{' || isFirstId(t);
	}
	
	public static boolean isFirstStatementList(Token t) {
		return isFirstStatement(t);
	}
	
	public static boolean isFirstExpression(Token t) {
		return isFirstDigit(t) || isFirstId(t);
	}
	
	public static boolean isFirstDigit(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isDigit(c);
	}
	
	public static boolean isFirstOp(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isOp(c);
	}
	
	public static boolean isFirstId(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isId(c);
	}
	
	//FOLLOW
	public static boolean isFollowProgram(Token t) {
		return isEOF(t) || isFollowStatement(t);
	}
	
	public static boolean isFollowStatement(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return c == '(' || c == '=' || c == '}' ||
		isFirstStatementList(t) || isEOF(t);
	}
	
	public static boolean isFollowStatementList(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return c == '}';
	}
	
	public static boolean isFollowExpression(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return c == ')' || isFirstOp(t) || isFollowStatement(t);
	}
	
	public static boolean isFollowDigit(Token t) {
		return isFirstOp(t) || isFollowExpression(t);
	}
	
	public static boolean isFollowOp(Token t) {
		return isFirstExpression(t);
	}
	
	public static boolean isFollowId(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return c == '=' || isFollowExpression(t);
	}
	
	//TOKENS
	// Individual terminal checks
	public static boolean isId(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isId(c);
	}
	
	public static boolean isDigit(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isDigit(c);
	}
	
	public static boolean isOp(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isOp(c);
	}
	
	// operators
	public static boolean isAssignOp(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isAssignOp(c);
	}
	
	public static boolean isAddOp(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isAddOp(c);
	}
	
	public static boolean isSubOp(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isSubOp(c);
	}
	
	// parens
	public static boolean isParen(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isParen(c);
	}
	
	public static boolean isLeftParen(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isLeftParen(c);
	}
	
	public static boolean isRightParen(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isRightParen(c);
	}
	
	// braces
	public static boolean isBrace(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isBrace(c);
	}
	
	public static boolean isLeftBrace(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isLeftBrace(c);
	}
	
	public static boolean isRightBrace(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isRightBrace(c);
	}
	
	public static boolean isPrint(Token t) {
		char c = t.getSourceToken().charAt(0);
		
		return isPrint(c);
	}
	
	//CHARS
	
	//Individual terminal checks
	public static boolean isId(char c) {
		return c >= 'a' && c <= 'z';
	}
	
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isOp(char c) {
		return isAddOp(c) || isSubOp(c);
	}
	
	// operators
	public static boolean isAssignOp(char c) {
		return c == '=';
	}
	
	public static boolean isAddOp(char c) {
		return c == '+';
	}
	
	public static boolean isSubOp(char c) {
		return c == '-';
	}
	
	// parens
	public static boolean isParen(char c) {
		return isLeftParen(c) || isRightParen(c);
	}
	
	public static boolean isLeftParen(char c) {
		return c == '(';
	}
	
	public static boolean isRightParen(char c) {
		return c == ')';
	}
	
	// braces
	public static boolean isBrace(char c) {
		return isLeftBrace(c) || isRightBrace(c);
	}
	
	public static boolean isLeftBrace(char c) {
		return c == '{';
	}
	
	public static boolean isRightBrace(char c) {
		return c == '}';
	}
	
	public static boolean isPrint(char c) {
		return c == 'P';
	}
	
	public static boolean isEOF(Token t) {
		return Token.EOF.equals(t);
	}
}
