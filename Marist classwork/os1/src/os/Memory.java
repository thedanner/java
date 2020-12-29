/**
 * 
 */
package os;

/**
 * @author Craiger
 *
 */
public class Memory
{
	public static int MEMORY_SIZE = 300;
	public static final int WORD_SIZE = 4;
	
	private int[][] memArray;
	
	private boolean[] blockScheme;
	private int blocksInUse;
	private int pageTableOffset;
	private RandomNumGen ring;
	
	public Memory()
	{
		memArray = new int[MEMORY_SIZE][WORD_SIZE];
		blockScheme = new boolean[30]; //rofls who does an array of booleans?
		ring = new RandomNumGen();
		
		for(int b = 0; b < 30; b++)
			blockScheme[b] = false; //don't hate
		
		blocksInUse = 0;
		pageTableOffset = 0;
		
		for(int i = 0; i < MEMORY_SIZE; i++)
		{
			for(int j = 0; j < WORD_SIZE; j++)    
				memArray[i][j] = 0;
		}
	}
	
	public int[] get(int va, int ptr)
	{
		int ra = realAddress(va, ptr);
		int ret[] = new int[WORD_SIZE];
		
		// idiot check, will (not) give insightful error help
		if(ra < 0 || ra > 300)
		{
			System.out.println("set method: ra=" + ra + ", pt=" + ptr + ", va=" + va);
			System.out.println(printMemAndPtr(ptr));
			
			throw new IndexOutOfBoundsException("ra way out of range: " + ra);
		}
		
		for(int i = 0; i < WORD_SIZE; i++)
			ret[i] = memArray[ra][i];
		
		return ret;
	}
	
	public int getFirstByte(int va, int ptr)
	{
		int word = ptr * 10;
		int off = va / 10;
		
		return memArray[word + off][0];
	}
	
	public void setFirstByteZero(int va, int ptr)
	{
		int word = ptr * 10;
		int off = va / 10;
		
		memArray[word + off][0] = 0;
	}
	
	public void set(int va, int[] data, int ptr)
	{
		//int blockNum = 0;
		
		if(data.length != WORD_SIZE)
		{
			throw new IllegalArgumentException(
					"the data array must have a length of " + WORD_SIZE);
		}
		
		/*
		if(!assignedBlock(ptr, va))
		{
			System.out.println("THIS SHOULD NEVER HAPPEN");
			blockNum = getFreeBlock();
			addToPageTable(ptr, blockNum, va);
			addBlock(blockNum);
		}
		*/
		
		int ra = realAddress(va, ptr);
		
		if(ra > 300) // idiot check, will give insightful error help
		{
			System.out.println("set method: ra=" + ra + ", pt=" + ptr + ", va=" + va);
			System.out.println(printMemAndPtr(ptr));
			
			throw new IllegalArgumentException("ra=" + ra);
		}
		
		for (int i = 0; i < WORD_SIZE; i++)		
			memArray[ra][i] = data[i];
	}
	
	public void setNewPage(int ptr, int va)
	{
		int blockNum = getFreeBlock();
		clearBlock(blockNum);
		addToPageTable(ptr, blockNum, va);
		addBlock(blockNum);
	}
	
	public void loadPageTable(int ptr)
	{
		for(int i = 0; i < 10; i++)
		{
			for(int c = 0; c < 4; c++)
				memArray[ptr * 10 + i][c] = 1;
		}    
	}
	
	private boolean assignedBlock(int ptr, int va)
	{
		boolean hasBeenAssigned = false;
		int moo = va / 10; //what the fuck is moo?!?!
		
		if(memArray[ptr * 10 + moo][0] == 0)
			hasBeenAssigned = true;
		
		return hasBeenAssigned;
	}
	
	public void loadProgramCards(int ptr, String card)
	{	
		int blockNum = getFreeBlock(); 
		
		for (int i = 0; i < 10; i++)
		{
			for(int c = 0; c < 4; c++)
				memArray[blockNum * 10 + i][c] = card.charAt((i * 4) + c);
		}
		
		int wordNum = ptr * 10 + pageTableOffset;
		int[] word = memArray[wordNum];
		
		word[0] = 0; // set page table (super important)
		word[1] = 0; // set page table
		word[2] = blockNum / 10; // set page table
		word[3] = blockNum % 10; // set page table
		
		pageTableOffset++;
	}
	
	public int[][] getBlock(int va, int ptr)
	{
		int[][] data = new int[10][WORD_SIZE];		
		int ra = realAddress(va, ptr);				
		
		for(int i = 0; i < 10; i++)
		{
			for(int c = 0; c < 4; c++)
				data[i][c] = memArray[ra + i][c]; // lawlz forgot the + i durrr
		}
		
		return data;
	}
	
	public void setBlock(int ptr, int va, int[][] dataCard)
	{
		int blockNum = 0;
		
		//int ra = realAddress(va, ptr); // debug
		
		if(assignedBlock(ptr, va))    
		{
			blockNum = (memArray[ptr * 10 + (va/10)][2]) * 10;
			blockNum += memArray[ptr * 10 + (va/10)][3];
		}	  
		else
			blockNum = getFreeBlock();
		
		addToPageTable(ptr, blockNum, va);
		
		for(int i = 0; i < 10; i++)
		{
			for(int c = 0; c < 4; c++)
				memArray[blockNum * 10 + i][c] = dataCard[i][c];
		}
	}
	
	public void addToPageTable(int ptr, int blockNum, int va)
	{
		//System.out.println("PTR : " + ptr + " blk num :" + blockNum);
		memArray[ptr * 10 + va / 10][0] = 0; // set page table (THIS IS REALLY IMPORTANT)
		memArray[ptr * 10 + va / 10][1] = 0; // set page table (not as important)
		memArray[ptr * 10 + va / 10][2] = blockNum / 10; // set page table
		memArray[ptr * 10 + va / 10][3] = blockNum % 10; // set page table
	}
	
	public int realAddress(int va, int ptr)
	{
		int ra = -1;
		
		ra  = (memArray[ptr * 10 + va / 10][2]) * 10;	
		ra += (memArray[ptr * 10 + va / 10][3]);
		
		return (ra * 10) + va % 10;
	}
	
	//********************************************************
	//super advanced memory management system
	
	public int getFreeBlock()
	{
		int blockNum = ring.fetch();
		return blockNum;
	}
	
	public boolean isFreeFrameAvailable()
	{
		return !ring.isEmpty();
	}
	
	public void viewBlockScheme()
	{
		System.out.println("---Block Scheme---");
		System.out.println("-Free Blocks-");
		System.out.println("-Used Blocks-");		
	}
	
	private void addBlock(int blockNum)
	{
		blockScheme[blockNum] = true;
		blocksInUse++;
	}
	
	public void releaseProgram(int ptr) 
	{
		//first release any blocks program was using
		for(int i = 0; i < 10; i++)
		{
			if(memArray[ptr * 10 + i][0] != 1) // 1=not in use, was never allocated, so don't release
			{
				int ra = memArray[ptr * 10 + i][2] * 10;
				ra += memArray[ptr * 10 + i][3];
				releaseBlock(ra); //XXX should print to trace that this block is being released
			}
		}
		
		//second, release page table
		releaseBlock(ptr); //XXX should print to trace that this block is being released
	}
	
	public double percentBlocksInUse()
	{		
		return (blocksInUse / 30.0);
	}
	
	public int currentBlocksInUse()
	{
		return blocksInUse;
	}
	
	private void releaseBlock(int blockNum)
	{
		if (ring.freeFrameList.size() > 29)
		{
			System.err.println("# OF FREE FRAMEZ: " + ring.freeFrameList.size());
		}
		
		if (ring.freeFrameList.contains(blockNum))
		{
			System.err.println("Re-adding an existing blockNum: " + blockNum);
		}
		
		if (blockNum >= 30)
		{
			System.err.println("blockNum >= 30: " + blockNum);
		}
		
		ring.addBack(blockNum);
	}
	
	private void clearBlock(int blockNum)
	{
		blockScheme[blockNum] = false;
		blocksInUse--;
		
		for(int i = 0; i < 10; i++)
		{
			for(int c = 0; c < 4; c++)
			{
				memArray[blockNum * 10 + i][c] = (int) ' ';
			}
		}
	}
	
	//end super advanced memory management system	
	//********************************************************
	
	public String printMemAndPtr(int ptr)
	{
		for(int i=0; i<30; i++)
		{
			if(i < 10)
				System.out.print(i + " : ");
			else
				System.out.print(i + ": ");
			
			for(int c=0; c<10; c++)
			{
				if (i != ptr)
				{
					for(int e=0; e<4; e++)
						System.out.print((char)memArray[i *10 + c][e]);
				}
				else
					for(int e=0; e<4; e++)
						System.out.print(memArray[i *10 + c][e]);
			}
			System.out.println();
		}
		
		String temp = "--- Page Table-- @ addr: " + ptr + "\n";
		
		for (int i = 0; i < 10; i++)
		{
			for(int c = 0; c < 4; c++)
				temp = temp +  memArray[ptr * 10 + i][c];
			
			temp = temp + "--" + i + "\n";
		}
		
		return temp;
	}	
	
	public void clear()
	{
		pageTableOffset = 0;
	}
	
	/*
	public void clearRNG()
	{
		ring = new RandomNumGen();
	}
	*/
}
