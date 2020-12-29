package algorithms.hw4;

import java.util.ArrayList;

/**
 * Algorithm Analysis and Design
 * 
 * Homework #4.6 - Exact fit knapsack - Electoral votes
 * 
 * @author Dan Mangiarelli
 */
public class ExactFitKnapsack
{
	private static ExactFitKnapsackEntry[][] lookup;
	
	private static void init(int n, int k)
	{
		lookup = new ExactFitKnapsackEntry[n + 1][k + 1];
		
		// initialize the lookup table
		for (int i = 0; i <= n; i++)
		{
			for (int j = 0; j <= k; j++)
			{
				lookup[i][j] = new ExactFitKnapsackEntry();
			}
		}
	}
	
	public static boolean exactFit(int[] s, int n, int k)
	{
		init(n, k);
		
		lookup[0][0].exists = true;
		
		// now traverse from top to bottom and left to right,
		// filling in the value for the [i, j]th cell as we traverse
		for (int i = 1; i <= n; i++)
		{
			for (int j = 0; j <= k; j++) 
			{
				lookup[i][j].exists = false;
				lookup[i][j].belongs = false;
				
				if (lookup[i - 1][j].exists)
				{
					// solution exists that does not include ith item
					lookup[i][j].exists = true; 
				}
				
				if (( (j - s[i]) >= 0) && lookup[i - 1][j - s[i] ].exists)
				{
					lookup[i][j].exists = true;
					lookup[i][j].belongs = true;
				}
			}
		}
		
		// filling in table entries now complete
		return lookup[n][k].exists;
	}
	
	public static ArrayList<Integer> getSolutionSet(int[] s, int n, int k)
	{
		// Start at  i = n, j = K -- the last cell in the table
		int i = n;
		int j = k;
		
		ArrayList<Integer> solutions = new ArrayList<Integer>();
		
		// while you have not yet reached the top row
		while (i != 0)
		{
			while (i != 0 && !lookup[i][j].belongs)
			{
				// move up the table
				i--;
			}
			
			if (i != 0)
			{
				// add item i, with size S[i] to the solution set.
				
				// The added item is the index of a state with
				// s[i] electoral votes.
				solutions.add(i);
				
				// We already added this item, so move down.
				i --;
			}
			
			if (j >= s[i + 1])
			{
				j -= s[i + 1]; // slide over to the left
			}
		}
		
		return solutions;
	}
	
	public static void main(String[] args)
	{
		int n = 51; // number of items
		
		ArrayList<Integer> votes = new ArrayList<Integer>(n);
		ArrayList<String> states = new ArrayList<String>(n);
		
		votes.add( 9); states.add("Alabama");
		votes.add( 3); states.add("Alaska");
		votes.add(10); states.add("Arizona");
		votes.add( 6); states.add("Arkansas");//
		votes.add(55); states.add("California");
		votes.add( 9); states.add("Colorado");
		votes.add( 7); states.add("Connectict");
		votes.add( 3); states.add("Delaware");//
		votes.add(27); states.add("Florida");
		votes.add(15); states.add("Georgia");
		
		votes.add( 4); states.add("Hawaii");
		votes.add( 4); states.add("Idaho");//
		votes.add(21); states.add("Illinois");
		votes.add(11); states.add("Indiana");
		votes.add( 7); states.add("Iowa");
		votes.add( 6); states.add("Kansas");//
		votes.add( 8); states.add("Kentucky");
		votes.add( 9); states.add("Louisiana");
		votes.add( 4); states.add("Maine");
		votes.add(10); states.add("Maryland");//
		
		votes.add(12); states.add("Massachusetts");
		votes.add(17); states.add("Michigan");
		votes.add(10); states.add("Minnesota");
		votes.add( 6); states.add("Mississippi");//
		votes.add(11); states.add("Missouri");
		votes.add( 3); states.add("Montana");
		votes.add( 5); states.add("Nebraska");
		votes.add( 5); states.add("Nevada");//
		votes.add( 4); states.add("New Hampshire");
		votes.add(15); states.add("New Jersey");
		
		votes.add( 5); states.add("New Mexico");
		votes.add(31); states.add("New York");//
		votes.add(15); states.add("North Carolina");
		votes.add( 3); states.add("North Dakota");
		votes.add(20); states.add("Ohio");
		votes.add( 7); states.add("Oklahoma");//
		votes.add( 7); states.add("Oregon");
		votes.add(21); states.add("Pennsylvania");
		votes.add( 4); states.add("Rhode Island");
		votes.add( 8); states.add("South Carolina");//
		
		votes.add( 3); states.add("South Dakota");
		votes.add(11); states.add("Tennessee");
		votes.add(34); states.add("Texas");
		votes.add( 5); states.add("Utah");//
		votes.add( 3); states.add("Vermont");
		votes.add(13); states.add("Virginia");
		votes.add(11); states.add("Washington");
		votes.add( 3); states.add("Washington, D.C.");
		votes.add( 5); states.add("West Virginia");
		votes.add(10); states.add("Wisconsin");
		
		votes.add( 3); states.add("Wyoming");
		
		int sum = 0;
		
		for (int i : votes)
		{
			sum += i;
		}
		
		// Half of the total electoral votes, meaning a tie.
		int k = sum / 2;
		
		int[] s = new int[n + 1];
		
		s[0] = 0;
		
		for (int i = 0; i < n; i++)
		{
			s[i + 1] = votes.get(i);
		}
		
		exactFit(s, n, k);
		
		ArrayList<Integer> solution = getSolutionSet(s, n, k);
		
		System.out.println("The indexes of the solution states: " + solution.toString());
		
		int voteTotal = 0;
		
		for (int i : solution)
		{
			System.out.println(states.get(i - 1) + " with " + votes.get(i - 1) + " votes.");
			
			voteTotal += votes.get(i - 1);
		}
		
		System.out.println("The number of votes in the solution set: " + voteTotal);
		System.out.println("The number of votes we were expecting: " + k);
	}
}

class ExactFitKnapsackEntry
{
	public boolean exists;
	public boolean belongs;
	
	public ExactFitKnapsackEntry()
	{
		exists = false;
		belongs = false;
	}
}

/*
The indexes of the solution states: [51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 19]
Wyoming with 3 votes.
Wisconsin with 10 votes.
West Virginia with 5 votes.
Washington, D.C. with 3 votes.
Washington with 11 votes.
Virginia with 13 votes.
Vermont with 3 votes.
Utah with 5 votes.
Texas with 34 votes.
Tennessee with 11 votes.
South Dakota with 3 votes.
South Carolina with 8 votes.
Rhode Island with 4 votes.
Pennsylvania with 21 votes.
Oregon with 7 votes.
Oklahoma with 7 votes.
Ohio with 20 votes.
North Dakota with 3 votes.
North Carolina with 15 votes.
New York with 31 votes.
New Mexico with 5 votes.
New Jersey with 15 votes.
New Hampshire with 4 votes.
Nevada with 5 votes.
Nebraska with 5 votes.
Montana with 3 votes.
Missouri with 11 votes.
Maine with 4 votes.
The number of votes in the solution set: 269
The number of votes we were expecting: 269
*/