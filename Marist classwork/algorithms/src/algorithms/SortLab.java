package algorithms;

import java.util.Random;

/**
 * Sort Lab implementation exercises (#3, #4, #5).
 * 
 * @author Dan Mangiarelli
 */
public class SortLab
{
	private static final int INSERTION_SORT_THRESHOLD = 12;
	
	private static Random rand = new Random();
	private static int cmprs = 0;
	
	// Quicksort 1
	public static void quicksort1(int[] array)
	{
		quicksort1(array, 0, array.length);
	}
	
	public static void quicksort1(int[] array, int start, int end)
	{
		int split;
		
		if ((end - start) > 1)
		{
			split = partition(array, start, end);
			quicksort1(array, start, split);
			quicksort1(array, split + 1, end);
		}
	} // end quicksort
	
	public static int partition(int[] array, int start, int end)
	{
		int pivot, i, j;
		
		pivot = rand.nextInt(end - start) + start;
		
		// locate pivot in well known position -- at front of the list
		swap(array, start, pivot);
		
		i = start + 1;
		j = end - 1;
		
		while (i <= j)
		{
			while ((i <= j) && array[i] <= array[start])
			{
				cmprs++;
				i++;
			}
			
			while (array[j] > array[start])
			{
				cmprs++;
				j--;
			}
			
			if (i < j)
			{
				swap(array, i, j);
			}
		}
		
		// put pivot into its right position
		swap(array, start, j);
		
		return j;
	} // end partition
	// END quicksort 1
	
	
	// Quicksort 2
	public static void quicksort2(int[] array)
	{
		quicksort2(array, 0, array.length);
	}
	
	public static void quicksort2(int[] array, int start, int end)
	{
		int split = -1;
		
		if ((end - start) > 1)
		{
			split = partition(array, start, end);
			
			if (split - start <= INSERTION_SORT_THRESHOLD)
			{
				insertionSort(array, start, split);
			}
			else
			{
				quicksort1(array, start, split);
			}
			
			if (end - split + 1 <= INSERTION_SORT_THRESHOLD)
			{
				insertionSort(array, split + 1, end);
			}
			else
			{
				quicksort1(array, split + 1, end);
			}
		}
	}
	// END quicksort 2
	
	/**
	 * Sort Lab #3.
	 */
	public static void insertionSort(int[] A, int lo, int hi)
	{
		int i, j, temp;
		
		j = lo + 1;
		
		// invariant: A[0..j-1] is sorted
		while (j < hi)
		{
			i = j - 1;
			
			temp = A[j];
			
			// invariant: for all k: i < k < j : A[k] > temp
			while ((i >= 0) && (A[i] > temp))
			{
				cmprs++;
				A[i + 1] = A[i];
				i--;
			}
			
			A[i + 1] = temp;
			
			cmprs++;
			
			j++;
		}
	} // end insertSort
	
	public static void swap(int[] array, int i, int j)
	{
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	// Helper methods
	public static void randomizeArray(int[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			array[i] = rand.nextInt();
		}
	}
	
	public static int loVal(int[] array)
	{
		int lo = Integer.MAX_VALUE;
		
		for (int i : array)
		{
			lo = Math.min(lo, i);
		}
		
		return lo;
	}
	
	public static int hiVal(int[] array)
	{
		int hi = Integer.MIN_VALUE;
		
		for (int i : array)
		{
			hi = Math.max(hi, i);
		}
		
		return hi;
	}
	
	public static long sum(int[] array)
	{
		long sum = 0L;
		
		for (int i : array)
		{
			sum += i;
		}
		
		return sum;
	}
	
	public static double avg(int[] array)
	{
		return sum(array) / ((double) array.length);
	}
	
	public static double standardDeviation(int[] A)
	{
		long sum = 0L;
		long summation = 0L;
		
		for (int i : A)
		{
			sum += (long) i;
			summation += (long) i * i;
		}
		
		summation /= A.length;
		
		double avg = (sum / ((double) A.length));
		
		double result = summation - (avg * avg);
		
		result = Math.sqrt(result);
		
		return result;
	}
	
	public static int getAndResetCounter()
	{
		int val = cmprs;
		cmprs = 0;
		
		return val;
	}
	
	// main
	public static void main(String[] args)
	{
		int[] arr = new int[4096];
		int[] numCompares = new int[50];
		
		for (int i = 0; i < numCompares.length; i++)
		{
			randomizeArray(arr);
			quicksort1(arr);
			numCompares[i] = getAndResetCounter();
		}
		
		System.out.printf("After %1$s iterations, " +
				"the number of compares are as follows:%n%n",
				numCompares.length);
		
		String intFmtStr = "%1$20s : %2$,d%n";
		String floatFmtStr = "%1$20s : %2$,.2f%n";
		
		System.out.println("Quicksort 1:");
		System.out.printf(floatFmtStr, "Average", avg(numCompares));
		System.out.printf(intFmtStr, "Min (best)", loVal(numCompares));
		System.out.printf(intFmtStr, "Max (worst)", hiVal(numCompares));
		System.out.printf(floatFmtStr, "Standard deviation",
				standardDeviation(numCompares));
		
		System.out.println();
		
		// quicksort 2
		
		for (int i = 0; i < numCompares.length; i++)
		{
			randomizeArray(arr);
			quicksort2(arr);
			numCompares[i] = getAndResetCounter();
		}
		
		System.out.println("Quicksort 2 with Insertion Sort:");
		System.out.printf(floatFmtStr, "Average", avg(numCompares));
		System.out.printf(intFmtStr, "Min (best)", loVal(numCompares));
		System.out.printf(intFmtStr, "Max (worst)", hiVal(numCompares));
		System.out.printf(floatFmtStr, "Standard deviation",
				standardDeviation(numCompares));
	}
}

/*
 * 2 Sample runs: After 50 iterations, the number of compares are as follows:
 * Quicksort: Average : 56,086.72 Min (best) : 51,530 Max (worst) : 62,258
 * Standard deviation : 2,224.58
 * 
 * Quicksort with Insertion Sort: Average : 58,045.34 Min (best) : 53,668 Max
 * (worst) : 66,718 Standard deviation : 2,566.16
 * --------------------------------------- After 50 iterations, the number of
 * compares are as follows: Quicksort: Average : 56,681.98 Min (best) : 52,733
 * Max (worst) : 64,002 Standard deviation : 2,342.29
 * 
 * Quicksort with Insertion Sort: Average : 58,703.90 Min (best) : 54,442 Max
 * (worst) : 65,439 Standard deviation : 2,330.84
 * --------------------------------------- COMMENTARY: (SortLab # 5)
 * 
 * While the number of element comparisons for the "optimized" quicksort
 * implementation exceeds that of the standard quicksort, the improvement
 * doesn't lie solely within the comparison count. The value lies with the
 * reduced overhead of an iterative algorithm versus a recursive one. Quicksort,
 * being recursive, requires more memory (and stack space) for each recursive
 * call than insertion sort does. Insertion sort is acceptable, even
 * preferrable, for small data sets (say, < 12 elements) for this reason.
 */
