package _mine._misc;

import java.util.regex.Pattern;

public class Splitter
{
	public static final String TEST_SENTENCE =
			"The quick brown, fox jumped over, the lazy dog.";
	
	private static final String PATTERN = ",";
	
	private static void printArray(String[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			System.out.printf("%d4: '%s'%n", i, array[i]);
		}
		
	}
	
	public static void main(String[] args)
	{
		printArray(Pattern.compile(PATTERN).split(TEST_SENTENCE));
		System.out.println("---------");
		printArray(TEST_SENTENCE.split(PATTERN));
	}
}
