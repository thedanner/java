package _mine._misc;

public class BoxingTest {
	static boolean compare1() //returns true
	{
		Integer a = 300;
		Integer b = 400;
		return a < b;
	}
	
	static boolean compare2() //returns true
	{
		Integer a = 127;
		Integer b = 127;
		return b == a;
	}
	
	static boolean compare3() //returns false (!)
	{
		Integer a = 128;
		Integer b = 128;
		return b == a;		
	}
	
	public static void main(String[] args) {
		boolean comparisonTrue = false;
		
		int i = 0;
		do
		{
			Integer a = i;
			Integer b = i;
			
			comparisonTrue = (a == b);
			
			System.out.printf("%1$d: %2$b%n", i, comparisonTrue);
			
			i++;
		} while (comparisonTrue);
		
		i = -1;
		do
		{
			Integer a = i;
			Integer b = i;
			
			comparisonTrue = (a == b);
			
			System.out.printf("%1$d: %2$b%n", i, comparisonTrue);
			
			i--;
		} while (comparisonTrue);
	}
}
