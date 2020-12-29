package ads.treestuff;

import java.util.Comparator;

/**
 * package teneyck.treestuff
 */

/**
 * A class for comparing two String Objects
 * @author Jim Ten Eyck
 * @date August 11, 2006
 *
 */
public class StringComparator implements Comparator<String> {
	
	/* Return the result of comparing lhs with rhs
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @param lhs is the first String Object
	 * @param rhs is the second String Object
	 * @return <0 if lhs is lexicograhically less than rhs etc.
	 */
	public int compare(String lhs, String rhs) {
		// TODO Auto-generated method stub
		return lhs.compareTo(rhs);
	}
}
