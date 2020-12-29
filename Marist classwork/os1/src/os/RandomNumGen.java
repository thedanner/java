package os;

import java.util.ArrayList;
import java.util.Random;

public class RandomNumGen
{
	public ArrayList<Integer> freeFrameList;
	private int numFrames;
	private Random rng;
	
	public RandomNumGen()
	{
		numFrames = 30;
		rng = new Random();//XXX RAND SEED milk
		freeFrameList = new ArrayList<Integer>(numFrames);		
		
		for(int i = 0; i < numFrames; i++)
			freeFrameList.add(i);
	}
	
	public int fetch()
	{
		int numFrames = freeFrameList.size();
		int index = rng.nextInt(numFrames);
		
		int blockNum = freeFrameList.remove(index);
		
		return blockNum; 
	}
	
	public void addBack(int returningBlock)
	{
		freeFrameList.add(returningBlock);
	}
	
	public void printArrayList()
	{
		for (int i = 0; i < 30; i++)
			System.out.print(freeFrameList.get(i));
		
		System.out.println();
	}
	
	public boolean isEmpty()
	{
		return freeFrameList.isEmpty();
	}
	
	@Override
	public String toString() {
		return freeFrameList.size() + ": " + freeFrameList.toString();
	}
}
