package _mine._misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StringSortTest {
	static Random randy = new Random();
	
	static String join(String[] array, String delim)
	{
		StringBuffer sb = join(array, delim, new StringBuffer());
		return sb.toString();
	}
	
	static StringBuffer join(String[] array, String delim, StringBuffer sb)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (i != 0)
			{
				sb.append(delim);
			}
			
			sb.append(array[i]);
		}
		
		return sb;
	}
	
	public static String[] randomizeArray(String[] stringArray)
	{
		List<String> stringList = Arrays.asList(stringArray);
		stringList = new ArrayList<String>(stringList); // bad style abuse
		ArrayList<String> returnList = new ArrayList<String>(stringList.size());
		
		while (!stringList.isEmpty()) {
			returnList.add(stringList.remove(randy.nextInt(stringList.size())));
		}
		
		return returnList.toArray(new String[] {});
	}
	
	public static void main(String[] args)
	{
		String[] names = {
				"file1",
				"file2",
				"file3",
				"file8",
				"file10",
				"file20",
				"file30",
				"file5",
				"file15",
				"file25",
				"file35",
				"file40",
				"file60",
				"file90" };
		
		String[] randomNames = randomizeArray(names);
		
		System.out.println("Unsorted:");
		System.out.println(join(randomNames, String.format("%n")));
		System.out.println();
		System.out.println("-------------------------");
		System.out.println();
		
		String[] builtinSortedNames = Arrays.copyOf(randomNames, randomNames.length);
		Arrays.sort(builtinSortedNames);
		
		String[] naturallySortedNames = Arrays.copyOf(randomNames, randomNames.length);
		Arrays.sort(naturallySortedNames, Strings.getNaturalComparator());
		
		System.out.println("SORTED (builtin):");
		System.out.println(join(builtinSortedNames, String.format("%n")));
		System.out.println("-------------------------");
		System.out.println();

		System.out.println("SORTED (natural):");
		System.out.println(join(naturallySortedNames, String.format("%n")));
	}
}
