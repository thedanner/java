package compilerDesign.hw2.grammar;

import compilerDesign.hw2.token.Token;

public class CompileError {
	private String message;
	private Token source;
	
	public CompileError(String message, Token source) {
		this.message = message;
		this.source = source;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Token getSource() {
		return source;
	}
	
	public void setSource(Token source) {
		this.source = source;
	}
	
	@Override
	public String toString() {
		return String.format(
				"%1$s (line: %2$s, position: %3$s)",
				message, source.getLine(), source.getPosition());
	}
}
