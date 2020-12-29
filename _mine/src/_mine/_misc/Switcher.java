package _mine._misc;

public class Switcher
{
	// Static isn't necessary; only final is.
	public static final int WON = 1;
	public static final int TOO = 2;
	public static final int TREE = 3;
	
	// Enums should work, too.
	enum Numbers {
		MOO, FOO, BAR 
	};
	
	public void test()
	{
		int _int = 4;
		Numbers _enum = Numbers.FOO;
		
		switch (_int)
		{
		case WON: System.out.println("WON"); break;
		case TOO: System.out.println("TOO"); break;
		case TREE: System.out.println("TREE"); break;
		default: System.out.println("none");
		}
		
		switch (_enum)
		{
		case MOO: System.out.println("MOO"); break;
		case FOO: System.out.println("FOO"); break;
		case BAR: System.out.println("BAR"); break;
		default: System.out.println("none X either");
		}
	}
	
	public static void main(String[] args)
	{
		new Switcher().test();
	}
}
