package algorithms.hw4;

/**
 * Algorithm Analysis and Design
 * 
 * Homework #4.4 - Sorting algorithm
 * 
 * @author Dan Mangiarelli
 */
public class Sorts
{
	public static void superSort(int[] A)
	{
		superSort(A, 0, A.length);
	}
	
	public static void superSort(int[] A, int off, int len)
	{
		sort(A, off, len);
	}
	
	/**
	 * This is a modified quicksort.  It's designed to better handle the
	 * cases that generally cause other quicksort algorithms to degrade in
	 * performance.
	 * 
	 * It is the same algorithm as found in java.util.Arrays.sort(), with an
	 * attempt made at simplifying the code (but not the logic) for readability
	 * in some cases to understand the algorithm, and how it achieved the
	 * "honor" of being the default algorithm used for sorting arrays in Java
	 * (as of v1.6). Auxiliary methods are also included in this class, with
	 * their names changed or their code "simplified" for readability, as
	 * above.  The algorithm itself remains intact.
	 * 
	 * @see java.util.Arrays.sort()
	 */
	public static void sort(int[] array, int off, int len)
	{
		// insertion sort for when the array is small enough
		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && array[j - 1] > array[j]; j--)
					swap(array, j, j - 1);
			return;
		}
		
		// Choose a partition element, v
		int mid = off + (len >> 1);    // Small arrays, middle element
		
		if (len > 7)
		{
			int len2 = off; // size
			int n = off + len - 1; // end index
			
			if (len > 40)
			{
				// Big arrays, pseudomedian of 9
				int s = len / 8;
				len2 = median(array, len2,      len2 + s, len2 + 2 * s);
				mid  = median(array, mid - s,   mid,      mid + s);
				n    = median(array, n - 2 * s, n - s,    n);
			}
			mid = median(array, len2, mid, n); // Mid-size, med of 3
		}
		
		// as indicated above, v is the partition element
		int v = array[mid];
		
		// Establish Invariant: v* (<v)* (>v)* v*
		int a = off;			// "length" (copy of offset)
		int b = a;				// first index
		int c = off + len - 1;	// end index
		int d = c;				// copy of end index
		
		while(b <= c)
		{
			while (b <= c && array[b] <= v)
			{
				if (array[b] == v)
				{
					swap(array, a++, b);
				}
				
				b++;
			}
			
			while (c >= b && array[c] >= v)
			{
				if (array[c] == v)
				{
					swap(array, c, d--);
				}
				
				c--;
			}
			
			if (b <= c)
			{
				swap(array, b++, c--);
			}
		}
		
		// Swap partition elements back to middle
		int s = 0;
		int n = off + len;
		
		s = Math.min(a - off, b - a);
		swapRange(array, off, b - s, s);
		
		s = Math.min(d - c, n - d - 1);
		swapRange(array, b, n - s, s);
		
		// Recursively sort non-partition-elements
		s = b - a;
		if (s > 1)
		{
			sort(array, off, s);
		}
		
		s = d - c;
		if (s > 1)
		{
			sort(array, n - s, s);
		}
	}
	
	/**
	 * Swaps the values in the two array array indexes specified. 
	 */
	private static void swap(int[] a, int index1, int index2)
	{
		int temp = a[index1];
		
		a[index1] = a[index2];
		a[index2] = temp;
	}
	
	/**
	 * Moves items in an array from loIndex to hiIndex.  The number of items
	 * moved is length (exclusive), which means that the loIndex ranges from
	 * loIndex .. loIndex + length - 1
	 */
	private static void swapRange(int a[], int loIndex, int hiIndex, int length)
	{
		for (int i = 0; i < length; i++, loIndex++, hiIndex++)
			swap(a, loIndex, hiIndex);
	}
	
	/**
	 * Returns the index of the median value of the contents of the array at
	 * the 3 provided indexes.
	 */
	private static int median(int[] a, int indexA, int indexB, int indexC)
	{
		int valA = a[indexA];
		int valB = a[indexB];
		int valC = a[indexC];
		
		// a < b -- c unknown
		if (valA < valB)
		{
			// [a <] b < c
			if (valB < valC)
				return indexB;
			
			// a < c [< b]
			if (valA < valC)
				return indexC;
			
			// c < a < b
			return indexA;
		}
		// here: b < a -- c unknown
		// c < b [< a]
		else if (valB > valC)
		{
			return indexB;
		}
		// here: b < c
		else
		{
			// [b <] c < a
			if (valA > valC)
				return indexC;
			
			// [b <] a < c
			return indexA;
		}
	}
}
