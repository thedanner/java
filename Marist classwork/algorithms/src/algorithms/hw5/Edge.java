package algorithms.hw5;

public class Edge
{
	public int vert1, vert2;
	public double cost;
	
	public Edge(int vert1, int vert2, int cost)
	{
		this.vert1 = vert1;
		this.vert2 = vert2;
		this.cost = cost;
	}
	
	@Override
	public String toString()
	{
		return cost + " between " + vert1 + ", " + vert2;
	}
}
