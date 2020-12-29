package _mine._misc;

public class StringVersusBuilderAndBuffer {
	public static final int NUMBER_OF_ITERATIONS = 50000;
	
	public static void main( String[] args )
	{
		long startTime = System.currentTimeMillis();
		
		String buffer = "";
		
		for ( int i = 0; i < NUMBER_OF_ITERATIONS; i++ )
		{
			buffer += "foo";
		}
		
		System.out.println( "naive impl() : " + ( System.currentTimeMillis() - startTime ) + "ms." );
		
		startTime = System.currentTimeMillis();
		
		StringBuffer stringBuffer = new StringBuffer();
		
		for ( int i = 0; i < NUMBER_OF_ITERATIONS; i++ )
		{
			stringBuffer.append( "foo" );
		}
		
		System.out.println( "StringBuffer() : " + ( System.currentTimeMillis() - startTime ) + "ms." );
		
		startTime = System.currentTimeMillis();
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for ( int i = 0; i < NUMBER_OF_ITERATIONS; i++ )
		{
			stringBuilder.append( "foo" );
		}
		
		System.out.println( "StringBuilder() : " + ( System.currentTimeMillis() - startTime ) + "ms." );	
	}	
}
