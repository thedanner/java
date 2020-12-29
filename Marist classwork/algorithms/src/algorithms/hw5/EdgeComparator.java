package algorithms.hw5;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge>
{
	@Override
	public int compare(Edge o1, Edge o2) {
		int difference = (int) Math.round(o1.cost - o2.cost);
		
		return difference;
	}
}
