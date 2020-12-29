package compilerDesign.hw5.jasmin;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compilerDesign.hw5.ProjectFinalMain;
import compilerDesign.hw5.grammar.Grammar5;
import compilerDesign.hw5.scope.Entry.Type;

public class JasminMaker
{
	private LinkedList<String> code;
	private LinkedList<String> tempCode;
	
	private boolean inExpr;
	private boolean inStmt;
	
	private String lastId;
	private Type lastType;
	private char lastOperator;
	
	private int currentStackSize;
	private int requiredStackSize;
	
	private Hashtable<String, Integer> lookupTable;
	
	public JasminMaker()
	{
		code = new LinkedList<String>();
		tempCode = new LinkedList<String>();
		
		inExpr = false;
		inStmt = false;
		
		lastId = null;
		lastOperator = '\0';
		
		currentStackSize = 0;
		requiredStackSize = 0;
		
		lookupTable = new Hashtable<String, Integer>();
	}
	
	public void add(String s)
	{
		if(inExpr)
			tempCode.add(s);
	}
	
	public void startPrint()
	{
		inStmt = true;
		
		startExpression();
		
		code.add("Print");
	}
	
	public void stopPrint()
	{
		inStmt = false;
		
		stopExpression();
		
		code.add("StopPrint");
	}
	
	public void startExpression()
	{
		inExpr = true;
	}
	
	public void stopExpression()
	{
		stopExpression(null, -1);
	}
	
	public void stopExpression(String id, int scopeId)
	{
		inExpr = false;
		
		int codeIndex = code.size();
		
		for(String s : tempCode)
			code.add(codeIndex, s);
		
		if(id != null)
		{
			String uid = getUniqueId(id, scopeId);
			
			code.add(codeIndex, uid);
		}
		
		code.add(";");
		
		tempCode.clear();
	}
	
	public void addDeclaration(String id, int scopeId)
	{
		int size = lookupTable.size();
		
		id = getUniqueId(id, scopeId);
		
		lookupTable.put(id, size);
	}
	
	public String[] getJasminCode(String s)
	{
		if(s.length() >= 0)
		{
			char c = s.charAt(0);
			
			if(s.length() > 1)
			{
				if(Grammar5.isCharList(s))
				{
					lastType = Type.CHAR;
					
					return new String[] {
						push("ldc " + s)};
				}
				
				else if(s.equals("Print"))
				{
					inStmt = true;
					
					return new String[] {
						push("getstatic java/lang/System/out Ljava/io/PrintStream;")};
				}
				
				else if(s.equals("StopPrint"))
				{
					inStmt = false;
					
					String loadType = null;
					
					if(lastType == Type.INT)
						loadType = "i";
					else if(lastType == Type.CHAR)
						loadType = "a";
					
					String printType = null;
					
					if(lastType == Type.INT)
						printType = "I";
					else if(lastType == Type.CHAR)
						printType = "Ljava/lang/String;";
					
					String lastId = this.lastId;
					
					this.lastId = null;
					
					if(lastId != null)
					{
						return new String[] {
								push(loadType + "load " + getCorrespondingLocal(lastId)),
								pop2("invokevirtual java/io/PrintStream/println(" + printType + ")V")};
					}
					
					return new String[] {
							pop2("invokevirtual java/io/PrintStream/println(" + printType + ")V")};
				}
				
				else
				{
					lastId = s;
				}
			}
			
			else if(Character.isDigit(c))
			{
				if(lastOperator == '\0')
					return new String[] { push("bipush " + c) };
				
				lastType = Type.INT;
				
				Vector<String> retVal = new Vector<String>();
				
				retVal.add(push("bipush " + c));
				
				if(lastOperator == '+')
					retVal.add(pop("iadd"));
				
				else if(lastOperator == '-')
					retVal.add(pop("isub"));
				
				lastOperator = '\0';
				
				return retVal.toArray(new String[retVal.size()]);
			}
			
			else if(c == '+')
			{
				lastOperator = c;
			}
			
			else if(c == '-')
			{
				lastOperator = c;
			}
			
			else if(c == ';')
			{
				if(lastId != null && !inStmt)
				{
					String type = null;
					
					if(lastType == Type.INT)
						type = "i";
					else if(lastType == Type.CHAR)
						type = "a";
					
					String retVal = pop(type + "store " + getCorrespondingLocal(lastId));
					
					return new String[] { retVal };
				}
			}
		}
		
		return null;
	}
	
	public LinkedList<String> generateCode()
	{
		LinkedList<String> tempCode = new LinkedList<String>();
		
		for(int i = 0; i < code.size(); i++)
		{
			String[] jCode = getJasminCode(code.get(i)); 
			
			if(jCode != null)
			{
				for(String jCodeLine : jCode)
				
				tempCode.add(jCodeLine);
			}
		}
		
		return tempCode;
	}
	
	public void writeToFile(String outPath) throws IOException
	{
		String templatePath = ProjectFinalMain.
		getResourcePath(getClass(), "jasmin_template.txt");
		
		InputStream templateStream =
			getClass().getResourceAsStream(templatePath);
		
		BufferedReader templateReader = new BufferedReader(
				new InputStreamReader(templateStream));
		
		writeToFile(templateReader, outPath);
	}
	
	private void writeToFile(BufferedReader in, String outPath)
	throws IOException
	{
		PrintStream  out = new PrintStream(
				new FileOutputStream(outPath));
		
		String line = in.readLine();
		
		Pattern p = Pattern.compile("%([a-zA-Z0-9\\_]*)%");
		
		LinkedList<String> generatedCode = generateCode();
		
		while(line != null)
		{
			Matcher m = p.matcher(line);
			
			if(m.find())
			{
				if(m.group().equals("%stack_limit%"))
					line = m.replaceAll(""+requiredStackSize);
				
				else if(m.group().equals("%locals_limit%"))
					line = m.replaceAll(""+lookupTable.size());
				
				else if(m.group().equals("%code%"))
				{
					for(String lineOfCode : generatedCode)
						out.println("   " + lineOfCode);
					
					line = "   ; end generated code";
				}
			}
			
			out.println(line);
			
			line = in.readLine();
		}
		
		out.close();
	}
	
	public void reset()
	{
		code.clear();
		tempCode.clear();
		
		currentStackSize = 0;
		requiredStackSize = 0;
		
		lookupTable.clear();
	}
	
	private int getCorrespondingLocal(String uid)
	{
		if(uid == null)
			throw new NullPointerException("uid is null");
		
		return lookupTable.get(uid);
	}
	
	private String push(String value)
	{
		currentStackSize ++;
		
		requiredStackSize = Math.max(requiredStackSize, currentStackSize);
		
		return value;
	}
	
	private String pop2(String value)
	{
		return pop(value, 2);
	}
	
	private String pop(String value)
	{
		return pop(value, 1);
	}
	
	private String pop(String value, int spaces)
	{
		currentStackSize -= spaces;
		
		return value;
	}
	
	public static String getUniqueId(String id, int scopeId)
	{
		if(id == null)
			throw new NullPointerException("name is null");
		
		return scopeId + id;
	}
}
