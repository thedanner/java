package compilerDesign.hw2;

import compilerDesign.hw2.grammar.TheGrammar;

public class RecursiveDescentParserMain {
	public static void main(String[] args) {
		String grammarPath =
			RecursiveDescentParserGui.
			getResourcePath(
					RecursiveDescentParserMain.class, "grammar_spec.txt");
		
		new RecursiveDescentParserGui(
				grammarPath, new TheGrammar()).setVisible(true);
	}
}
