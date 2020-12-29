package os;

import java.util.LinkedList;

import os.MonstahOS.EM_Code;
import os.MonstahOS.IO_Mode;

public class ProgramControlBlock 
{
	public String processName;
	public int ptr;
	public int lineLimit;
	public int lineCount;
	public int timeLimit;
	public int timeCount;
	public int ic;
	public boolean c;
	
	public IO_Mode ioMode;
	public EM_Code[] em;

	public LinkedList<Integer> dataCards;
	public LinkedList<Integer> output;	
	public LinkedList<Integer> programCards;
	
	public int[] r;
	public int[] ir;
	
	public ProgramControlBlock()
	{
		processName = "";
		ptr = 0;
		lineLimit = 0;
		lineCount = 0;
		timeLimit = 0;
		timeCount = 0;
		ic = 0;
		c = false;
		
		ioMode = IO_Mode.NONE;
		em = new EM_Code[0];
		
		dataCards = new LinkedList<Integer>();
		output = new LinkedList<Integer>();
		programCards = new LinkedList<Integer>();
		
		setR(null);
		setIR(null);
	}
	
	public ProgramControlBlock(String proccessName, int ptr, int ll, int lc, int tl, int tc, int ic, int[] r, int[] ir)
	{
		this.processName = proccessName;
		this.ptr = ptr;
		this.lineLimit = ll;
		this.lineCount = 0;
		this.timeLimit = tl;
		this.timeCount = 0;
		this.ic = 0;
		
		setR(r);
		setIR(ir);
	}
	
	// gogo automatic getter and setters
	public String getProcessName() {
		return processName;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public int getPtr() {
		return ptr;
	}
	
	public void setPtr(int ptr) {
		this.ptr = ptr;
	}
	
	public int getLineLimit() {
		return lineLimit;
	}
	
	public void setLineLimit(int lineLimit) {
		this.lineLimit = lineLimit;
	}
	
	public int getLineCount() {
		return lineCount;
	}
	
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	
	public int getTimeLimit() {
		return timeLimit;
	}
	
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	public int getTimeCount() {
		return timeCount;
	}
	
	public void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}
	
	public IO_Mode getIO_Mode() {
		return ioMode;
	}
	
	public void setIO_Mode(IO_Mode ioMode) {
		this.ioMode = ioMode;
	}
	
	public int getIc() {
		return ic;
	}
	
	public void setIc(int ic) {
		this.ic = ic;
	}
	
	public int[] getR() {
		return r;
	}
	
	public void setR(int[] r) {
		if (r == null)
		{
			this.r = null;
			return;
		}
		
		this.r = new int[r.length];
		
		for (int i = 0; i < r.length; i++)
			this.r[i] = r[i];
	}
	
	public int[] getIr() {
		return ir;
	}
	
	public void setIR(int[] ir) {
		if (ir == null)
		{
			this.ir = null;
			return;
		}
		
		this.ir = new int[ir.length];
		
		for (int i = 0; i < ir.length; i++)
			this.ir[i] = ir[i];
	}
	
	/*public LinkedList<String> getDataCards()
	{
		return dataCards;
	}
	
	public void setDataCards(LinkedList<String> dataCards)
	{
		//this.dataCards = new LinkedList<String>(dataCards);
		this.dataCards = dataCards;
	}
	
	public LinkedList<String> getOutput()
	{
		return output;
	}
	
	public void setOutput(LinkedList<String> output)
	{
		//this.output = new LinkedList<String>(output);
		this.output = output;
	}*/
	
	public void setC(boolean c) {
		this.c = c;
	}
	
	public boolean getC(){
		return c;
	}
	
	public EM_Code[] getEM() {
		return em;
	}
	
	public void setEM(EM_Code... em) {
		if (em == null)
		{
			this.em = null;
			return;
		}
		
		this.em = new EM_Code[em.length];
		
		for (int i = 0; i < em.length; i++)
			this.em[i] = em[i];
	}
	
	public boolean hasEM_Code(EM_Code code)
	{
		if (null == em)
		{
			return false;
		}
		
		for (EM_Code myCode : em)
		{
			if (myCode == code)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return processName;
	}
}
