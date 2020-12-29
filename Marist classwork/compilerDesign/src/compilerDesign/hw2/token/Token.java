package compilerDesign.hw2.token;

public class Token {
	public static final Token EOF = new Token("$", 0, -1, -1, null);
	
	protected String sourceToken;
	protected int tokenType;
	protected int line;
	protected int position;
	protected Number value;
	
	public Token() {
		this(null, 0, 0, 0, null);
	}
	
	public Token(String sourceToken,
			int tokenType, int line, int position, Number value) {
		
		this.sourceToken = sourceToken;
		this.tokenType = tokenType;
		this.line = line;
		this.position = position;
		this.value = value;
	}
	
	public Token(Token t) {
		this(t.sourceToken, t.tokenType, t.line, t.position, t.value);
	}
	
	public String getSourceToken() {
		return sourceToken;
	}

	public void setSourceString(String sourceToken) {
		this.sourceToken = sourceToken;
	}

	public int getTokenType() {
		return tokenType;
	}

	public void setTokenType(int tokenType) {
		this.tokenType = tokenType;
	}

	public int getLine() {
		return line;
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Number getValue() {
		return value;
	}
	
	public void setValue(Number value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format(
				"%1$s[sourceToken='%2$s',line=%3$s,position=%4$s,value=%5$s]",
				getClass().getName(), sourceToken, line, position, value); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Token) {
			Token other = (Token)obj;
			
			boolean eq =
				sourceToken.equals(other.sourceToken) &&
				line == other.line &&
				position == other.position;
			
			if(value != null && other.value != null)
				eq = value.equals(other.value) & eq;
			
			return eq;
		}
		
		return false;
	}
}
