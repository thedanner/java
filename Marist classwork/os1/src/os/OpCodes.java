/**
 * 
 */
package os;

/**
 * @author Craig
 *
 */
public class OpCodes
{
	public static final String [] OPCODES = new String[] {
		"LR",	//Load Register
		"SR",	//Store Register
		"CR",	//Compare Register
		"BT",	//Branch True
		"GD",	//Get Data
		"PD",	//Put/Print Data
		"H"		//Halt the program
	};
	
	public static String getMnemonicFor(int digitA, int digitB)
	{
		char a = (char)digitA;
		char b = (char)digitB;
		
		if(b == ' ' || b == 'X')
			return a+"";
		
		return new String(new char[] { a , b } );
	}
	
	public static int[] getOpCodeFor(String mnemonic)
	{
		if(mnemonic.length() == 0 || mnemonic.length() > 3)
		{
			throw new IllegalArgumentException(
					"the mnemonic must be 1 or 2 characters");
		}
		
		int[] ret = new int[2];
		
		ret[0] = mnemonic.codePointAt(0);
		
		if(mnemonic.length() != 1)
			ret[1] = mnemonic.codePointAt(1);
		else
			ret[1] = 0;
		
		return ret;
	}
	
	public static boolean isValidOpCode(String mnemonic)
	{
		for (String opcode : OPCODES)
		{
			if (opcode.equals(mnemonic))
				return true;
		}
		
		return false;
	}
}
