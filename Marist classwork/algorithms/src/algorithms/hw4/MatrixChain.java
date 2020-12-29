package algorithms.hw4;

/**
 * Algorithm Analysis and Design
 * 
 * Homework #4.5 - Matrix Chain algorithm comparison
 * 
 * @author Dan Mangiarelli
 */
public class MatrixChain
{
	private MatrixChainEntry[][] lookup;
	
	public MatrixChain()
	{ }
	
	private void init(int n)
	{
		lookup = null; // for gc
		
		lookup = new MatrixChainEntry[n + 1][n + 1];
		
		for (int i = 0; i <= n; i++)
		{
			for (int j = 0; j <= n; j++)
			{
				lookup[i][j] = new MatrixChainEntry();
				
				lookup[i][j].count = Long.MAX_VALUE;
				lookup[i][j].location = i;
			}
		}
	}
	
	public long matrixChainDynamic(int[] p, int n)
	{
		init(n);
		
		// set the diagonal values
		for (int index = 0; index < lookup.length; index++)
		{ 
			lookup[index][index].count = 0;
			lookup[index][index].location = index;
		}
		
		int i = 0;
		int j = 0;
		
		// for each length
		for (int l = 2; l <= n; l++)
		{
			// for each initial placement
			for (i = 1; i <= n - l +1; i++)
			{
				j = i + l - 1;
				
				lookup[i][j].count = Long.MAX_VALUE;
				lookup[i][j].location = i;
				
				//for each subinterval in the interval i..j
				for (int k = i; k < j; k++)
				{
					long temp =
						lookup[i    ][k].count +
						lookup[k + 1][j].count +
						p[i - 1] * p[k] * p[j];
					
					if (temp < lookup[i][j].count)
					{
						lookup[i][j].count = temp;
						lookup[i][j].location = k;
					}
				}
			}
		}
		
		return lookup[i][j].count;
	}
	
	public long matrixChainMemoized(int[] p, int n)
	{
		init(n);
		
		for (int i = 0; i <= n; i++)
		{
			for (int j = i; j <= n; j++)
			{
				lookup[i][j].count = Long.MAX_VALUE;
				lookup[i][j].location = i;
			}
		}
		
		return matrixChainMemoizedLookup(p, 1, n);
	}
	
	public long matrixChainMemoizedLookup(int p[], int i, int j)
	{
		if (i == j)
			return 0;
		
		if (lookup[i][j].count < Long.MAX_VALUE)
			return lookup[i][j].count;
		
		for (int k = i; k < j; k++)
		{
			long temp =
				matrixChainMemoizedLookup(p, i,     k) +
				matrixChainMemoizedLookup(p, k + 1, j) +
				p[i - 1] * p[k] * p[j];
			
			if (temp < lookup[i][j].count)
			{
				lookup[i][j].count = temp;
				lookup[i][j].location = k;
			}
		}
		
		return lookup[i][j].count;
	}
	
	public static void main(String[] args)
	{
		final int SIMULATIONS_TO_RUN = 1000;
		
		int n = 17;
		
		int[] p =
		{
				12,  21, 65, 18, 24,
				93, 121, 16, 41, 31,
				47,   5, 47, 29, 76,
				18,  72, 15
		};
		
		MatrixChain matrix = new MatrixChain();
		
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < SIMULATIONS_TO_RUN; i++)
			matrix.matrixChainDynamic(p, n);			
		
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - start;
		
		System.out.printf(
				" The dynamic version took %1$s ms.%n",
				timeTaken);
		
		start = System.currentTimeMillis();
		
		for(int i = 0; i < SIMULATIONS_TO_RUN; i++)
			matrix.matrixChainMemoized(p, n);
		
		endTime = System.currentTimeMillis();
		timeTaken = endTime - start;
		
		System.out.printf(
				"The memoized version took %1$s ms.%n",
				timeTaken);
	}
}

class MatrixChainEntry
{
	public long count;
	public long location;
	
	public MatrixChainEntry()
	{
		count = 0L;
		location = 0L;
	}
}

/* Several runs of the program produced these results
 The dynamic version took 32 ms.
The memoized version took 46 ms.

 The dynamic version took 31 ms.
The memoized version took 47 ms.

 The dynamic version took 32 ms.
The memoized version took 32 ms.
// really good case for memoized?

 The dynamic version took 16 ms.
The memoized version took 47 ms.

 The dynamic version took 46 ms.
The memoized version took 47 ms.
// really bad case for dynamic?

 The dynamic version took 62 ms.
The memoized version took 47 ms.
// even worse for dynamic...
*/
