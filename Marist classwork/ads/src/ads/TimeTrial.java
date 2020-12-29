package ads;

import java.util.Random;

import ads.skiplist.SkipList;
import ads.treestuff.AVLtree;
import ads.treestuff.BinarySearchTree;
import ads.treestuff.RedBlackTree;
import ads.treestuff.StringComparator;

public class TimeTrial
{
	private static Random randy = new Random (12357L);
	
	private static long startTime = 0L;
	
	public static void startTimer()
	{
		startTime = System.nanoTime();
	}
	
	public static long stopTimer()
	{
		return System.nanoTime() - startTime;
	}
	
	public static String generateRandomString()
	{
		StringBuffer st = new StringBuffer(4);
		
		for (int j = 0; j < 4; j++)
		{
			int c = Math.abs(randy.nextInt()) % 26;
			char ch = (char)(c + 65);
			st.append(ch);
		}
		
		return st.toString();
	}
	
	public static String[] generateRandomStringArray(int length)
	{
		BinarySearchTree<String> uniq =
			new BinarySearchTree<String>(new StringComparator());
		
		String[] output = new String[length];
		
		for (int i = 0; i < length; i++)
		{
			String word = generateRandomString();
			
			try
			{
				uniq.add(word);
				output[i] = word;
			}
			catch (IllegalArgumentException e)
			{
				i--;
			}
		}
		
		return output;
	}
	
	public static void runTests(
			int size, StringComparator comp, int skipListMaxLevel)
	{
		String[] testStrings = generateRandomStringArray(size);
		
		SkipList<String> skip =
			new SkipList<String>(comp, 0.5, skipListMaxLevel);
		BinarySearchTree<String> bst = new BinarySearchTree<String>(comp);
		AVLtree<String> avl = new AVLtree<String>(comp);
		RedBlackTree<String> rbt = new RedBlackTree<String>(comp);
		OrderedVector<String> ov = new OrderedVector<String>(comp);
		
		System.out.print("Running tests for item count: ");
		System.out.println(size);
		
		startTimer();
		for (String word : testStrings)
		{
			skip.add(word);
		}
		System.out.print("Skip insert time (in nanos): ");
		System.out.println(stopTimer());
		startTimer();
		for (String word : testStrings)
		{
			skip.contains(word);
		}
		System.out.print("       find time (in nanos): ");
		System.out.println(stopTimer());
		
		startTimer();
		for (String word : testStrings)
		{
			bst.add(word);
		}
		System.out.print("BST  insert time (in nanos): ");
		System.out.println(stopTimer());
		startTimer();
		for (String word : testStrings)
		{
			bst.contains(word);
		}
		System.out.print("       find time (in nanos): ");
		System.out.println(stopTimer());
		
		startTimer();
		for (String word : testStrings)
		{
			avl.add(word);
		}
		System.out.print("AVL  insert time (in nanos): ");
		System.out.println(stopTimer());
		startTimer();
		for (String word : testStrings)
		{
			avl.contains(word);
		}
		System.out.print("       find time (in nanos): ");
		System.out.println(stopTimer());
		
		startTimer();
		for (String word : testStrings)
		{
			rbt.add(word);
		}
		System.out.print("RBT  insert time (in nanos): ");
		System.out.println(stopTimer());
		startTimer();
		for (String word : testStrings)
		{
			rbt.contains(word);
		}
		System.out.print("       find time (in nanos): ");
		System.out.println(stopTimer());
		
		startTimer();
		for (String word : testStrings)
		{
			ov.add(word);
		}
		System.out.print("OVec insert time (in nanos): ");
		System.out.println(stopTimer());
		startTimer();
		for (String word : testStrings)
		{
			ov.contains(word);
		}
		System.out.print("       find time (in nanos): ");
		System.out.println(stopTimer());
		
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		StringComparator comp = new StringComparator();
		
		runTests( 1000, comp, 10);
		runTests( 4096, comp, 12);
		runTests(16536, comp, 16);
	}
}
