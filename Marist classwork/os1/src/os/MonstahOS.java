package os;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 
 * 
 * @author Craig Fargione, Dan Mangiarelli, Dave Weisfelner
 * @version Sept 23, 2007
 */
public class MonstahOS
{
	private static final String N = String.format("%n");
	//private static final int IDEAL_NUM_PROGS = 3;
	private static boolean TRACE = true;
	private static final int BUFFER_COUNT = 29;
	private static final int OUTPUT_BLANK_LINE_PADDING = 2;
	
	private static final Buffer F_P = new Buffer("$prog");
	private static final Buffer F_D = new Buffer("$data");
	private static final Buffer EOJ = new Buffer("$$EOJ");
	
	int loopFinder = 0;
	int finishedPrograms = 0;
	
	private enum SI_State
	{
		NONE,		// si = 0
		READ,		// si = 1
		WRITE,		// si = 2
		TERMINATE	// si = 3
	};
	
	private enum TI_State
	{
		NONE,					// ti = 0, snafu
		TIME_SLICE_EXCEEDED,	// ti = 1, time slice exceeded
		TIME_LIMIT_EXCEEDED		// ti = 2, time limit exceeded
	};
	
	private enum PI_State
	{
		NONE,				// pi = 0
		OPERATION_ERROR,	// pi = 1
		OPERAND_ERROR,		// pi = 2
		PAGE_FAULT			// pi = 3
	};
	
	public enum EM_Code
	{
		NORMAL_TERMINATION,		// em = 0
		OUT_OF_DATA,			// em = 1
		LINE_LIMIT_EXCEEDED,	// em = 2
		TIME_LIMIT_EXCEEDED,	// em = 3
		OPERATION_CODE_ERROR,	// em = 4
		OPERAND_ERROR,			// em = 5
		INVALID_PAGE_FAULT		// em = 6
	};
	
	private enum Task 
	{
		NONE,
		IS,
		OS,
		LD,
		RD,
		WT
	};
	
	public enum IO_Mode
	{
		NONE,
		READ,
		WRITE
	}
	
	public enum F_Mode
	{
		NONE,
		PROGRAM_CARDS,
		DATA_CARDS
	}
	
	private Memory memory;
	
	// start PCB
	private ProgramControlBlock pcb;
	
	//debugging "globals"
	private char[] readable_ir;
	private char[] readable_r;
	
	// End PCB
	
	private SI_State si;
	private TI_State ti;
	private PI_State pi;
	private int ioi;
	private F_Mode f;
	
	private Drum drum;
	
	private Reader in;
	private Writer out;
	
	private LinkedList<ProgramControlBlock> rq; //ready queue
	private LinkedList<ProgramControlBlock> lq; //load queue
	private LinkedList<ProgramControlBlock> tq; //terminate queue	
	private LinkedList<ProgramControlBlock> ioq; //input output queue
	private LinkedList<ProgramControlBlock> mq; //memory queue
	
	private LinkedList<Buffer> ebq; //empty buffer queue
	private LinkedList<Buffer> ifbq; //input buffer queue
	private LinkedList<Buffer> ofbq; //output buffer queue
	
	//private LinkedList<Steak> bbq; //food queue
	//XXX private int programCounter;
	private int time_slice;
	
	private channelOne ch1;
	private channelTwo ch2;
	private channelThree ch3;
	
	private LinkedList<ProgramControlBlock> ch1_loadingPCB_Queue;
	
	private Buffer ch3_cur_buffer;
	private int ch3_cur_trackNum = 0;
	private int ch3_cur_blockNum = 0; // for WT task
	
	int masterModeLoopKiller = 0;
	
	private Task task; // what to do next in the channels
	
	//private boolean print_mem = true;  //just a toggle
	
	private boolean osRunning;
	private boolean eof;
	//XXX private boolean firstPrint;
	private int traceCycleCount;	
	//private boolean startExecuting;
	
	public MonstahOS(String inFile, String outFile) throws IOException
	{
		memory = new Memory();
		
		//init();
		
		rq = new LinkedList<ProgramControlBlock>();
		lq = new LinkedList<ProgramControlBlock>();
		tq = new LinkedList<ProgramControlBlock>();
		ioq = new LinkedList<ProgramControlBlock>();
		mq = new LinkedList<ProgramControlBlock>();
		
		ch1_loadingPCB_Queue = new LinkedList<ProgramControlBlock>();
		
		ifbq = new LinkedList<Buffer>();
		ofbq = new LinkedList<Buffer>();
		ebq = new LinkedList<Buffer>();
		
		fillBufferQueue();
		
		f = F_Mode.NONE;
		
		readable_ir = new char[4];
		readable_r = new char[4];
		
		drum = new Drum();
		
		//XXX programCounter = 0;
		traceCycleCount = 0;		
		
		ch1 = new channelOne();
		ch2 = new channelTwo();
		ch3 = new channelThree();
		
		task = Task.NONE;
		
		si = SI_State.NONE; // ill let danner fix this at 3am
		ti = TI_State.NONE;
		pi = PI_State.NONE;
		ioi = 1;
		//task = Task.IS; // this gets set after ch1 runs and triggers ch3
		
		in = new Reader(inFile);
		out = new Writer(outFile);
		
		osRunning = true;
		eof = false;
		//XXX firstPrint = true;
		
		//load the first 3 lines into OS
		// i didn't think sharma would have liked seeing this,
		// so it's commented for now
		// --arg, i idiot, i forgot he told us to do this, sorry davey
		// ...and yet it's still commented, oh boy i thought i was
		//   done with this foreber...it's 2008-10-29...
		//loadThree();
		
		masterMode();
	}
	
	private void fillBufferQueue()
	{
		for (int i = 0; i < BUFFER_COUNT; i++)
		{
			ebq.addLast(new Buffer());
		}
	}
	
	private void init(ProgramControlBlock pcb)
	{
		pcb.ir = new int[4];
		pcb.r = new int[4];
		pcb.c = false;
		pcb.ic = 0;
		pcb.ptr = 0;
		
		pcb.processName = "";
		pcb.lineLimit = 0;
		pcb.timeLimit = 0;
		pcb.timeCount = 0;
		pcb.lineCount = 0;
		time_slice = 0;
		
		pcb.ioMode = IO_Mode.NONE;
		pcb.em = null;
		//startExecuting = false;
	}
	
	public void masterMode()
	{
		while (osRunning)
		{
			if(TRACE)
				out.println(traceFormat() + "ENTERING:  Master Mode");
			
			if (null == pcb && !rq.isEmpty())
			{
				loadNextPCB(rq);
			}
			
			if (null != pcb)
			{
				//TI = 0 || 1 and SI != 0
				if ((ti == TI_State.NONE || ti == TI_State.TIME_SLICE_EXCEEDED) && si != SI_State.NONE)
				{
					if(si == SI_State.READ)
					{
						pcb.ioMode = IO_Mode.READ;
						saveCurrentPCB(ioq);
						//read();
					}
					
					else if(si == SI_State.WRITE)
					{
						pcb.ioMode = IO_Mode.WRITE;
						saveCurrentPCB(ioq);
						//write();
					}
					
					else if(si == SI_State.TERMINATE)
					{
						setEM(EM_Code.NORMAL_TERMINATION);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					si = SI_State.NONE;
				}
				
				//TI = 2 and SI != 0
				else if (ti == TI_State.TIME_LIMIT_EXCEEDED && si != SI_State.NONE)
				{
					if(si == SI_State.READ)
					{
						setEM(EM_Code.TIME_LIMIT_EXCEEDED);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					else if(si == SI_State.WRITE)
					{
						pcb.ioMode = IO_Mode.WRITE;
						
						setEM(EM_Code.TIME_LIMIT_EXCEEDED);
						
						saveCurrentPCB(ioq);
						
						// I think this is taken care of in ir3_WT().
						// Since we set the em above, that code is checked when
						// its write request is serviced.  If the em array
						// contains EM_Code.TIME_LIMIT_EXCEEDED (god only knows
						// why sharmie didn't have a line limit exceeded
						// error) (actually, em should technically only contain
						// that one reason)...
						// Anyway, if em has that error, it's placed on
						// the TQ, otherwise, the PCB goes back on the RQ.
						//saveCurrentPCB(tq);
						
						//write();
						//terminate();
					}
					
					else if(si == SI_State.TERMINATE)
					{
						setEM(EM_Code.NORMAL_TERMINATION);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					si = SI_State.NONE;
					ti = TI_State.NONE;
				}
				
				//TI = 0 || 1 and PI != 0
				else if (pi != PI_State.NONE &&
						(ti == TI_State.NONE || ti == TI_State.TIME_SLICE_EXCEEDED))
				{
					if(pi == PI_State.OPERATION_ERROR)
					{
						setEM(EM_Code.OPERATION_CODE_ERROR);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					else if(pi == PI_State.OPERAND_ERROR)
					{
						setEM(EM_Code.OPERAND_ERROR);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					else if(pi == PI_State.PAGE_FAULT)
					{
						//needs more work 
						//saveCurrentPCB(tq);
						String mnemonic = getMnemoincFromIR();
						
						boolean validPageFault = 
							"GD".equals(mnemonic) || "SR".equals(mnemonic);
						
						if (validPageFault)
						{
							if(TRACE)
								out.println(traceFormat() + "Valid Page Fault - " + printMos());
							
							if (memory.isFreeFrameAvailable())
							{
								int va = getOperandFromIR();
								memory.setFirstByteZero(va, pcb.ptr);
								memory.setNewPage(pcb.ptr, va);
								
								pcb.ic--;
								
								if(TRACE)
									out.println("Decrementing IC");
							}
							else
							{
								System.err.println("Program added to MQ");
								saveCurrentPCB(mq);
							}
						}
						else
						{
							setEM(EM_Code.INVALID_PAGE_FAULT);
							
							saveCurrentPCB(tq);
							//terminate();
						}
					}
					
					pi = PI_State.NONE;
				}
				
				//TI = 2 and PI != 0
				else if (ti == TI_State.TIME_LIMIT_EXCEEDED && pi != PI_State.NONE)
				{
					if(pi == PI_State.OPERATION_ERROR)
					{
						setEM(	EM_Code.TIME_LIMIT_EXCEEDED,
								EM_Code.OPERATION_CODE_ERROR);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					else if(pi == PI_State.OPERAND_ERROR)
					{
						setEM(	EM_Code.TIME_LIMIT_EXCEEDED,
								EM_Code.OPERAND_ERROR);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					else if(pi == PI_State.PAGE_FAULT)
					{
						setEM(EM_Code.TIME_LIMIT_EXCEEDED);
						
						saveCurrentPCB(tq);
						//terminate();
					}
					
					pi = PI_State.NONE;	
					ti = TI_State.NONE;
				}
				
				if (ti == TI_State.TIME_SLICE_EXCEEDED)
				{
					saveCurrentPCB(rq);
					loadNextPCB(rq);
					
					ti = TI_State.NONE;
				}
			}
			
			if (!tq.isEmpty())
			{
				tryToStartChannel(ch3);
			}
			
			if(TRACE)
				out.print(" ioi= " + ioi + ": ");
			
			if (0 == ioi)
			{
				if(TRACE)
					out.println("do nothing");
			}
			else if (1 == ioi)
			{
				if(TRACE)
					out.println("ir1()");
				
				ch1.setBusy(false);
				
				ir1();
			}
			else if (2 == ioi)
			{
				if(TRACE)
					out.println("ir2()");
				
				ch2.setBusy(false);
				
				ir2();
			}
			else if (3 == ioi)
			{
				if(TRACE)
					out.println("ir2(), ir1()");
				
				ch2.setBusy(false);
				ch1.setBusy(false);
				
				ir2();
				ir1();
			}
			else if (4 == ioi)
			{
				if(TRACE)
					out.println("ir3()");
				
				ch3.setBusy(false);
				
				ir3();
			}
			else if (5 == ioi)
			{
				if(TRACE)
					out.println("ir1(), ir3()");
				
				ch1.setBusy(false);
				ch3.setBusy(false);
				
				ir1();
				ir3();
			}
			else if (6 == ioi)
			{
				if(TRACE)
					out.println("ir3(), ir2()");
				
				ch3.setBusy(false);
				ch2.setBusy(false);
				
				ir3();
				ir2();
			}
			else if (7 == ioi)
			{
				if(TRACE)
					out.println("ir2(), ir1(), ir3()");
				
				ch2.setBusy(false);
				ch1.setBusy(false);
				ch3.setBusy(false);
				
				ir2();
				ir1();
				ir3();
			}
			else
			{
				out.flush();
				throw new IllegalStateException("you fucked up, asshole: ioi=" + ioi);
			}
			
			if (finishedPrograms == 9) 
			{
				//try to run something after 5 programs terminate
				//IR1();
				finishedPrograms++; //small hack to continue running
			}
			
			if (osRunning)
			{
				executeUserProgram();
			}
			
			//System.out.println(loopFinder++);
			if ((masterModeLoopKiller++) > 10000)
			{
				System.err.println("Infinite MM loop");
				System.exit(0);
			}
			
			// Logic to stop the OS when we're done (I think).
			boolean allQueuesEmpty = 
				rq.isEmpty() &&
				lq.isEmpty() &&
				tq.isEmpty() &&
				ioq.isEmpty() &&
				mq.isEmpty() &&
				ifbq.isEmpty() &&
				ofbq.isEmpty();
			
			// We want the OS to stop if we've read all of the input file,
			// and all of the queues are empty:
			//		boolean stopOS = (eof && allQueuesEmpty);
			// Apply some theorem or rule or some shit to change that around,
			// and it becomes something like:
			//		boolean osRunning = !(eof && allQueuesEmpty)
			//		                  = (!eof || !allQueuesEmpty)
			// Take your pick.
			// ---(is this logic right?)
			osRunning = (!eof || !allQueuesEmpty);
		}
		
		out.println();
		out.println("OS TERMINATING.  HAVE A GOOD DAY!");
		
		out.flush();
	}
	
	public void terminate(ProgramControlBlock terminatingPCB)
	{
		if (ebq.size() >= 2)
		{
			String emString = emToString(terminatingPCB.em);
			
			Buffer outputLine1 = ebq.removeFirst();
			Buffer outputLine2 = ebq.removeFirst();
			
			outputLine1.setBuffer(String.format(
					"ID%1$s   %2$-30s  %3$04d  %4$04d",
					terminatingPCB.processName, emString,
					terminatingPCB.timeLimit, terminatingPCB.lineLimit));
			
			int cAsInt = terminatingPCB.c ? 1 : 0;
			
			outputLine2.setBuffer(String.format(
					"IC = %1$02d  IR = \"%2$4s\"  R = \"%3$4s\"  C = %4$1d  %5$04d  %6$04d",
					terminatingPCB.ic,
					charForm(terminatingPCB.ir), charForm(terminatingPCB.r),
					cAsInt, terminatingPCB.timeCount, terminatingPCB.lineCount));
			
			ofbq.addLast(outputLine1);
			ofbq.addLast(outputLine2);
		}
		
		//memory.releaseProgram(terminatingPCB.ptr);
		
		/*
		String emString = emToString(terminatingPCB.em);
		
		if (programCounter >= 0 && !firstPrint)
		{
			if(TRACE)
				out.println(traceFormat() +
						terminatingPCB.processName + " Terminated - " +
						emString + " " + printMos());
			
			//DONT TOUCH
			out.printf(
					"ID%1$s   %2$-30s  %3$04d  %4$04d%n",
					terminatingPCB.processName, emString,
					terminatingPCB.timeLimit, terminatingPCB.lineLimit);
			
			int cAsInt = pcb.c ? 1 : 0;
			
			//i touched! but what did i change?? i guess you'll never know muuhahahhaa
			// but ... svn says you didn't change anything, liar
			
			out.printf(
					"IC = %1$02d  IR = \"%2$4s\"  R = \"%3$4s\"  C = %4$1d  %5$04d  %6$04d%n",
					terminatingPCB.ic,
					charForm(terminatingPCB.ir), charForm(terminatingPCB.r),
					cAsInt, terminatingPCB.timeCount, terminatingPCB.lineCount);
			//CAN TOUCH
			
			int newLineBreak = 0;
			
			while (!pcb.output.isEmpty()) 
			{
				//if(newLineBreak % 40 == 0)
				out.println();
				
				String temp = "";			
				String track_val[][] = drum.returnTrack(terminatingPCB.output.getFirst());
				
				for (int i=0; i<10; i++)
				{
					for (int c=0; c<4; c++)
						temp = temp + track_val[i][c];
				}
				
				out.print(temp);
				drum.deleteTrack(terminatingPCB.output.removeFirst());
				
				newLineBreak++;
			}
			
			//em = null;
			
			out.println().println().println();
			firstPrint = false;
			
			memory.releaseProgram(terminatingPCB.ptr);
		}
		
		terminatingPCB.dataCards = new LinkedList<Integer>();
		terminatingPCB.output = new LinkedList<Integer>();
		
		firstPrint = false;
		
		//load();
		finishedPrograms++; //testing variable
		programCounter--;
		
		//if(traceCount > 500)
		//	TRACE = false;
		 */
	}
	
	/*
	 * public void loadThree() //whatever, it works { //first line
	 * in.processNextLine(); String line = in.getBuffer();
	 * 
	 * System.out.println(line);
	 * 
	 * memory.clear(); //we need this (clears offset, not memory, poor name, my
	 * bad) pcb.ptr = memory.getFreeBlock(); memory.loadPageTable(pcb.ptr);
	 * 
	 * pcb.processName = line.substring(4, 8); String tlStr = line.substring(8,
	 * 12); String llStr = line.substring(12, 16);
	 * 
	 * pcb.timeLimit = Integer.parseInt(tlStr); pcb.lineLimit =
	 * Integer.parseInt(llStr);
	 * 
	 * if(TRACE) out.println(traceFormat() + "$JOB Encountered: - Create new
	 * PCB").print(printMos());
	 * 
	 * //second line in.processNextLine(); line = in.getBuffer();
	 * 
	 * if (line.startsWith("$DTA") || line.startsWith("$EOJ")) { //do nothing, so
	 * sue me i forgot hot write a nor statement, !|? System.err.println("This
	 * message should never print unless we tested a non formatted input file"); }
	 * 
	 * else //has to be program card at this point {
	 * memory.loadProgramCards(pcb.ptr, line);
	 * 
	 * if(TRACE) out.println(traceFormat() + "Program Card: - \"" + line + "\"" +
	 * printMos()); }
	 * 
	 * //third line, this one gets messy in.processNextLine(); line =
	 * in.getBuffer(); boolean isProgramCard = true; if(line.startsWith("$DTA")) {
	 * //System.out.println("DTA encountered: switch to loading data cards");
	 * isProgramCard = false; }
	 * 
	 * else if(line.startsWith("$EOJ")) { System.out.println("Error: EOJ
	 * encountered, no program to run"); }
	 * 
	 * else //program card || data card { if (isProgramCard) {
	 * memory.loadProgramCards(pcb.ptr, line);
	 * 
	 * if(TRACE) out.println(traceFormat() + "Program Card: - \"" + line + "\"" +
	 * printMos()); } //else datacard, but skipping for now since i know it isnt } }
	 */
	
	private void ir1()
	{
		if(TRACE)
			out.println("Entering Channel One");
		
		Buffer inputBuffer = null;
		
		if (!ebq.isEmpty())
		{
			in.processNextLine();
			
			String line = in.getBuffer();
			
			// When line is null, we hit EOF.
			if (null == line)
			{
				//if(TRACE)
				//	out.println("EOF !!!!!!"); //XXX output
				
				eof = true;
			}
			else
			{
				if(TRACE)
					out.println("Line read from file: \"" + line + "\""); //XXX output
				
				inputBuffer = ebq.removeFirst();
				inputBuffer.setBuffer(line);
				
				ifbq.addLast(inputBuffer);
			}
		}
		else
		{
			// Wait till next time ch1 runs.
			out.println("**No input spooling done: no empty buffers");
		}
		
		if (!eof/* && !ebq.isEmpty()*/)
		{
			//get next empty buffer...but why
			startChannel(ch1); // i dont get it
		}
		
		// handle the IFB we JUST read in; the LAST one on IFBQ
		if (!ifbq.isEmpty())
		{
			Buffer ifb = inputBuffer;//ifbq.getFirst();
			
			String line = ifb.getBuffer();
			
			//stuff
			if (line.startsWith("$JOB"))
			{
				ProgramControlBlock loadingPCB = new ProgramControlBlock();
				
				init(loadingPCB);
				
				
				
				// allocate frame for PT,
				// init PT, PTR
				loadingPCB.ptr = memory.getFreeBlock();
				memory.loadPageTable(loadingPCB.ptr);
				//isProgramCard = true;
				
				loadingPCB.processName = line.substring(4, 8);
				String tlStr = line.substring(8, 12);
				String llStr = line.substring(12, 16);
				
				loadingPCB.timeLimit = Integer.parseInt(tlStr);
				loadingPCB.lineLimit = Integer.parseInt(llStr);
				
				//f = F_Mode.PROGRAM_CARDS; // to follow
				
				ebq.addLast(ifbq.removeLast()); //deletes and returns the buffer
				
				ch1_loadingPCB_Queue.addLast(loadingPCB);
				
				ifbq.addLast(F_P);
			}
			
			else if(line.startsWith("$DTA"))
			{
				//isProgramCard = false;
				//f = F_Mode.DATA_CARDS; // to follow
				
				ebq.addLast(ifbq.removeLast()); //deletes and returns the buffer to eb queue
				
				ifbq.addLast(F_D);
			}
			
			else if(line.startsWith("$EOJ"))
			{
				//lq.addLast(ch1_loadingPCB_Queue.removeFirst());
				
				memory.clear(); //we need this
				
				ebq.addLast(ifbq.removeLast()); //deletes and returns the buffer
				
				if (ifbq.isEmpty())
				{
					lq.addLast(ch1_loadingPCB_Queue.removeFirst());
				}
				else
				{
					ifbq.addLast(EOJ);
				}
			}
			
			// only "start" ch3 if we have an IFB sticking around?
			else
			{
				//ifbq.addLast(ifb);
				
				//ch1 kicks 3 into gear right?
				// only if it's not busy i think
				tryToStartChannel(ch3);
			}
		}
		else if (!eof)
		{
			System.err.println("No buffers on ifb(q) --in ir1() ");
		}
		
		if (eof && ifbq.isEmpty())
		{
			ioi -= channelOne.VALUE;
		}
	}
	
	private void ir2()
	{
		if(TRACE)
			out.println("Entering Channel Two");
		
		if (!ofbq.isEmpty())
		{
			Buffer outputLine = ofbq.removeFirst();
			
			// prints buff0r
			
			//if(TRACE)
			//	out.print("K----- ");
			
			out.println(outputLine.getBuffer()); //XXX
			
			ebq.addLast(outputLine);
		}
		
		if (!ofbq.isEmpty())
		{
			startChannel(ch2);
		}
		else
		{
			ioi -= channelTwo.VALUE;
		}
	}
	
	private void ir3()
	{
		// First, complete the assigned task and the follow up action for
		// ch3 for each possible task, and then assign new task to it in
		// priority order.
		
		if(TRACE)
			out.println("Entering Channel Three");
		
		if (Task.IS == task)
		{
			ir3_IS();
		}
		else if (Task.OS == task)
		{
			ir3_OS();
		}
		else if (Task.LD == task)
		{
			ir3_LD();
		}
		else if (Task.RD == task)
		{
			ir3_RD();
		}
		else if (Task.WT == task)
		{
			ir3_WT();
		}
		
		task = Task.NONE;
		
		// "(Now Assign New Task in Priority Order)"
		
		if (!ifbq.isEmpty())
		{
			if (ifbq.getFirst() == F_P)
			{
				f = F_Mode.PROGRAM_CARDS;
				ifbq.removeFirst();
			}
			else if (ifbq.getFirst() == F_D)
			{
				f = F_Mode.DATA_CARDS;
				ifbq.removeFirst();	
			}
			else if (ifbq.getFirst() == EOJ)
			{
				lq.addLast(ch1_loadingPCB_Queue.removeFirst());
				ifbq.removeFirst();	
			}
		}
		
		// output spool first
		if (!tq.isEmpty() && !ebq.isEmpty())
		{
			ir3_outputSpool();
			
			/*
			if (!ebq.isEmpty()) //XXX redundant?
			{
				ir3_outputSpool();
			}
			else
			{
				//not sure this is right,
				//i belive if there are no empty buffers we just skip it,
				//not print an error
				out.println("**No output spooling done: no empty buffers");
			}
			*/
		}
		// input spool next
		else if (!ifbq.isEmpty() && drum.hasEmptyTrack())
		{
			ir3_inputSpool();
		}
		// load next
		else if (!lq.isEmpty() && memory.isFreeFrameAvailable())
		{
			ir3_load();
		}
		// now I/O
		else if (!ioq.isEmpty())
		{
			ir3_io();
		}
		
		///*
		if (Task.NONE == task)
		{
			ioi -= channelThree.VALUE;
		}
		//*/
	}
	
	// ------------------------------------
	// BEGIN IR3 / CHANNEL 3 HELPER METHODS
	// ------------------------------------
	
	private void ir3_IS()
	{
		int curTrack = ch3_cur_trackNum;
		
		String trackArray[][] = new String[10][4]; //slightly stupid way but whatever
		String temp = ch3_cur_buffer.getBuffer();
		int x = 0;
		
		for(int i = 0; i<10; i++)
		{
			for(int c = 0; c<4; c++)
			{
				trackArray[i][c] = "" + temp.charAt(x); // (i*10 + c) anyone?
				x++; //so what, leave me alove it works
			}
		}
		
		drum.writeTrack(curTrack, trackArray); //write da buffer
		
		if (F_Mode.PROGRAM_CARDS == f)
		{
			ch1_loadingPCB_Queue.getFirst().programCards.addLast(curTrack);
		}
		else if (F_Mode.DATA_CARDS == f)
		{
			ch1_loadingPCB_Queue.getFirst().dataCards.addLast(curTrack);
		}
		
		ebq.addLast(ch3_cur_buffer); //move da buffer
		ch3_cur_buffer = null;
		
		if (!ifbq.isEmpty() && EOJ == ifbq.getFirst())
		{
			rq.addLast(ch1_loadingPCB_Queue.removeFirst());
			ifbq.removeFirst();
		}
	}
	
	private void ir3_OS()
	{
		//this is probably how we know what track to use...probably
		int trackNum = ch3_cur_trackNum;
		
		ProgramControlBlock terminatingPCB = tq.getFirst();
		
		//if (terminatingPCB.processName.startsWith("0503"))
		//	System.out.println("OMFG MOO");
		
		if (trackNum != -1)
		{
			String bufferData = "";
			
			String[][] temp = new String[10][4];
			temp = drum.returnTrack(trackNum);
			
			for (int i=0; i<10; i++)
			{
				for(int c=0; c<4; c++)
					bufferData = bufferData + temp[i][c];
			}
			
			ch3_cur_buffer.setBuffer(bufferData);
			ofbq.addLast(ch3_cur_buffer);
			
			drum.deleteTrack(trackNum);
		}
		else
		{
			ebq.addLast(ch3_cur_buffer);
		}
		
		// decrement line count in PCB?!?  why? how?
		
		// if we just got the last line of output
		if (terminatingPCB.output.isEmpty())
		{
			int numBlankLinesPrinted = 0;
			
			while (numBlankLinesPrinted < OUTPUT_BLANK_LINE_PADDING &&
					(!ebq.isEmpty()))
			{
				Buffer blankLine = ebq.removeFirst();
				blankLine.setBuffer(""); // clear whatever was there
				ofbq.addLast(blankLine);
				
				numBlankLinesPrinted++;
			}
			
			if (numBlankLinesPrinted == OUTPUT_BLANK_LINE_PADDING)
			{
				//if(TRACE)
				//	out.println("Got two EBs for blank lines: EBs left=" + ebq.size());
			}
			else
			{
				System.err.println("Only got " + numBlankLinesPrinted +
						" empty bufferz, i wanted " + OUTPUT_BLANK_LINE_PADDING);
			}
			
			memory.releaseProgram(terminatingPCB.ptr);
			
			//XXX CALL TERMINATE HERE ??
			terminate(terminatingPCB);
			
			// actually take it off of the TQ
			tq.removeFirst();
			
			// in case of abnormal termination
			while (!terminatingPCB.output.isEmpty())
			{
				drum.deleteTrack(terminatingPCB.output.removeFirst());
			}
			
			while (!terminatingPCB.programCards.isEmpty())
			{
				drum.deleteTrack(terminatingPCB.programCards.removeFirst());
			}
			
			while (!terminatingPCB.dataCards.isEmpty())
			{
				drum.deleteTrack(terminatingPCB.dataCards.removeFirst());
			}
		}
		
		//XXX should we start ch2 here?
		tryToStartChannel(ch2);
	}
	
	private void ir3_LD()
	{
		ProgramControlBlock pcbToLoad = lq.getFirst();
		
		// According to our impl. of ir3_loadNext(), ch3_cur_trackNum
		// holds the next track of the next set of program cards on the drum.
		// So, we can't really check to see if that list is empty here.
		
		//this is done by setting the "global" track num in
		//int trackNum = cur_programcard.removeFirst();
		//'set' portion of ch3
		String temp[][] = new String[10][4];
		temp = drum.returnTrack(ch3_cur_trackNum);
		String line = "";
		
		for (int i=0; i<10; i++)
		{
			for(int c=0; c<4; c++)
				line = line + temp[i][c];
		}
		
		memory.loadProgramCards(pcbToLoad.ptr, line);
		drum.deleteTrack(ch3_cur_trackNum);
		
		// the if statement takes the place of:
		// "Decrement count in PCB
		// If zero, place PCB on RQ after ... "
		if(pcbToLoad.programCards.isEmpty()) // this is ok
		{
			// what's the over under on this working right?
			lq.removeFirst();
			rq.addLast(pcbToLoad);
		}
	}
	
	private void ir3_RD()
	{
		ProgramControlBlock readingPCB = ioq.removeFirst();
		
		if(readingPCB.dataCards.isEmpty())
			System.err.println("Error: No data card to read");
		
		else
		{
			//get next track AND decrement (via removing)
			int trackNum = readingPCB.dataCards.removeFirst();
			
			String temp[][] = new String[10][4];
			temp = drum.returnTrack(trackNum);
			String line = "";
			
			for (int i=0; i<10; i++)
			{
				for(int c=0; c<4; c++)
					line = line + temp[i][c];
			}
			
			int va = getOperandFromIR(readingPCB);
			
			memory.setBlock(readingPCB.ptr, va, convertStringToIntArray(line));
			drum.deleteTrack(trackNum);
			time_slice = 0; //sharmy says...
			
			// what's the over under on this working right?
			rq.addLast(readingPCB);
		}
	}
	
	private void ir3_WT()
	{
		//figure out what mem block gets written and which track
		
		ProgramControlBlock writingPCB = ioq.removeFirst();
		
		int va = ch3_cur_blockNum; //figure(d) this out
		int trackNum = ch3_cur_trackNum; //figure(d) this out
		
		int temp[][] = new int[10][4];
		String tempString[][] = new String[10][4];
		
		//not sure va is right, if it is then it's easy
		temp = memory.getBlock(va, writingPCB.ptr);
		
		for(int i=0; i<10; i++)
		{
			for(int c=0; c<4; c++)
				tempString[i][c] = "" + (char)temp[i][c];
		}
		
		drum.writeTrack(trackNum, tempString);
		
		writingPCB.output.addLast(trackNum);
		
		writingPCB.lineCount++;
		
		if (TI_State.TIME_LIMIT_EXCEEDED == ti) //lol wtf is TI = 3????
		{
			setEM(writingPCB, EM_Code.TIME_LIMIT_EXCEEDED);
			tq.addLast(writingPCB);
		}
		else
		{
			time_slice = 0;
			
			if (writingPCB.hasEM_Code(EM_Code.TIME_LIMIT_EXCEEDED))
			{
				tq.addLast(writingPCB);
			}
			else
			{
				rq.addLast(writingPCB);
			}
		}
		
	}
	
	// begin ASSIGN TASK
	
	private void ir3_outputSpool()
	{
		ProgramControlBlock terminatingPCB = tq.getFirst();
		
		ch3_cur_buffer = ebq.removeFirst();
		
		if (!terminatingPCB.output.isEmpty())
		{
			ch3_cur_trackNum = terminatingPCB.output.removeFirst();
		}
		else
		{
			ch3_cur_trackNum = -1;
		}
		
		//ProgramControlBlock currentPCB = tq.getFirst();
		//stuff to do here
		
		task = Task.OS;
		startChannel(ch3);
	}
	
	private void ir3_inputSpool()
	{
		ch3_cur_buffer = ifbq.removeFirst();
		ch3_cur_trackNum = drum.findEmptyTrack();
		
		drum.setTrackUsed(ch3_cur_trackNum, true);
		
		task = Task.IS; //fuck enumeration
		startChannel(ch3);
	}
	
	private void ir3_load()
	{
		ProgramControlBlock pcbToLoad = lq.getFirst();
		
		//this is where the track num comes from
		ch3_cur_trackNum = pcbToLoad.programCards.removeFirst();
		
		//ok here's the deal with this, we currently allocate a frame and
		//update the page table in memory.loadProgramCards(ptr, card)
		//which I do in the "LD" task portion of ch3...
		//we may want to revist and rethink this but technically it's
		//taken care of...
		
		task = Task.LD; //fuck enumeration
		startChannel(ch3);
	}
	
	private void ir3_io()
	{
		ProgramControlBlock pcbForIO = ioq.getFirst();
		
		if (IO_Mode.READ == pcbForIO.ioMode)
		{
			if(pcbForIO.dataCards.isEmpty())
			{
				setEM(pcbForIO, EM_Code.TIME_LIMIT_EXCEEDED); //tried to grab too much data
				
				ioq.removeFirst();
				tq.addLast(pcbForIO);
			}
			else
			{
				//ch3_cur_trackNum = drum.findEmptyTrack(); // not used for RD
				
				//int va = getOperandFromIR();
				//task = Task.GD;
				task = Task.RD; // I think this is what sharma means
				startChannel(ch3);	  	
			}
		}
		else if (IO_Mode.WRITE == pcbForIO.ioMode)
		{
			if (pcbForIO.lineCount > pcbForIO.lineLimit)
			{
				setEM(pcbForIO, EM_Code.LINE_LIMIT_EXCEEDED);
				
				ioq.removeFirst();
				tq.addLast(pcbForIO);
			}
			else
			{
				ch3_cur_trackNum = drum.findEmptyTrack();
				
				//XXX DO WE NEED TO MARK THIS AS READ ?
				drum.setTrackUsed(ch3_cur_trackNum, true);
				
				//update pcb, with what?
				ch3_cur_blockNum = getOperandFromIR(pcbForIO);
				//task = Task.PD
				task = Task.WT; // i think this is what sharmy means
				startChannel(ch3);
			}
		}
		// do it everywhere
		// haxor the world
		//tryToStartChannel(ch3);
	}
	
	// ---------------------------
	// END IR3 / CHANNEL 3 METHODS
	// ---------------------------
	
	private void tryToStartChannel(Channel channel)
	{
		if (!channel.isBusy())
		{
			ioi += channel.value();
			startChannel(channel);
		}
	}
	
	private void startChannel(Channel channel)//goomba fuck you
	{
		ioi -= channel.value();
		channel.resetTimer();
		channel.setBusy(true);
	}
	
	public void startExecution()
	{
		pcb.ic = 0; //new int[] { 0,0 } ; // owl
	}
	
	public void executeUserProgram() // slave mode
	{	
		while (	SI_State.NONE == si &&
				PI_State.NONE == pi &&
				TI_State.NONE == ti &&
				null != pcb)
		{
			masterModeLoopKiller = 0;
			
			//out.println("ic: " + pcb.ic);
			pcb.ir = memory.get(pcb.ic, pcb.ptr);
			
			pcb.ic++;
			pcb.timeCount++;
			
			if(TRACE)
				out.println(traceFormat() + "ENTERING:  Slave Mode");
			
			for(int i=0; i<4; i++)
				readable_ir[i] = (char) pcb.ir[i];
			//incrementCycleCounters(); //old increment cycle counters			
			//traceCycleCount++;
			
			String mnemonic = getMnemoincFromIR();			
			
			boolean isValidMnemonic = OpCodes.isValidOpCode(mnemonic);
			
			if (isValidMnemonic)
			{
				boolean isHalt = "H".equals(mnemonic);
				int va = 0;
				int memoryAssigned = 0;
				
				if (!isHalt)
				{
					va = getOperandFromIR();
					memoryAssigned = memory.getFirstByte(va, pcb.ptr);
				}
				
				if (va == -1) // check for invalid operand
				{
					pi = PI_State.OPERAND_ERROR; //pi = 2
				}
				
				else if (memoryAssigned == 0 || isHalt)
				{
					if(TRACE)
						out.println(traceFormat() + "Executing next operation");
					
					executeOpcode();
					
					// time limit/slice exceeded
					if(time_slice >= 10) //10 because we essentially start at 1
					{
						ti = TI_State.TIME_SLICE_EXCEEDED; //ti = 1
					}
					
					boolean underLimit = (pcb.timeCount < pcb.timeLimit);
					
					if(!underLimit)
					{
						ti = TI_State.TIME_LIMIT_EXCEEDED; //ti = 2
					}
				}
				else
				{
					pi = PI_State.PAGE_FAULT; // pi = 3
				}
			}
			else
			{
				pi = PI_State.OPERATION_ERROR; // pi = 1
			}
		}
		
		traceCycleCount++;

		simulation();
	}
	
	private void executeOpcode()
	{
		String mnemonic = OpCodes.getMnemonicFor(pcb.ir[0], pcb.ir[1]);
		
		if(mnemonic != null)
		{
			if("LR".equals(mnemonic))
			{
				pcb.r = memory.get(getOperandFromIR(), pcb.ptr);
				
				for(int i =0; i<4; i++) //debugging
					readable_r[i] = (char) pcb.r[i];
				
				if(TRACE)
					out.println(traceFormat() + "Executed: Load Register - " + printMos());				
			}
			else if("SR".equals(mnemonic))
			{
				memory.set(getOperandFromIR(), pcb.r, pcb.ptr);
				
				for(int i =0; i<4; i++) //debugging
					readable_r[i] = (char) pcb.r[i];
				
				if(TRACE)
					out.println(traceFormat() + "Executed: Store Register - " + printMos());				
			}
			else if("CR".equals(mnemonic))
			{	  
				int[] memoryVal = memory.get(getOperandFromIR(), pcb.ptr); 
				
				pcb.c = Arrays.equals(memoryVal, pcb.r);
				
				if(TRACE)
					out.println(traceFormat() + "Executed: Compare Register - " + printMos());
			}
			else if("BT".equals(mnemonic))
			{
				if (pcb.c)
				{
					pcb.ic = getOperandFromIR();
					
					if(TRACE)
						out.println(traceFormat() + "Executed: Branch To (true) - " + printMos());
				}
				else
				{
					if(TRACE)
						out.println(traceFormat() + "Executed: Branch To (false) - " + printMos());
				}
			}
			else if("GD".equals(mnemonic))
			{
				si = SI_State.READ;
				
				if(TRACE)
					out.println(traceFormat() + "Executed: Get Data, Set SI to 1 - " + printMos());
			}
			else if("PD".equals(mnemonic))
			{
				si = SI_State.WRITE;
				
				if(TRACE)
					out.println(traceFormat() + "Executed: Print Data, Set SI to 2 - " + printMos());
			}
			else if("H".equals(mnemonic))
			{
				time_slice = 0;
				si = SI_State.TERMINATE;
				
				if(TRACE)
					out.println(traceFormat() + "Executed: Halt, Set SI to 3 - " + printMos());
			}
			else
			{
				pi = PI_State.OPERATION_ERROR;
				
				if(TRACE)
					out.println(traceFormat() + "Operation error - " + printMos());
			}
		}
	}
	
	private void simulation()
	{
		for (Channel ch : new Channel[] {ch1, ch2, ch3})
		{
			if (ch.isBusy())
			{
				ch.incrementTimer();
				
				if (ch.getTimer() >= ch.getMaxTime())
				{
					//(set channel completion interrupt -- that's what ioi is)
					ioi += ch.value();
				}
			}
		}
	}
	
	private int getOperandFromIR()
	{
		return getOperandFromIR(this.pcb);
	}
	
	private int getOperandFromIR(ProgramControlBlock pcb)
	{
		int a = Character.digit(pcb.ir[2], 10);
		int b = Character.digit(pcb.ir[3], 10);
		
		if (a == -1 || b == -1)
			return -1;
		
		return a * 10 + b;
	}
	
	private String getMnemoincFromIR()
	{
		return OpCodes.getMnemonicFor(pcb.ir[0], pcb.ir[1]);
	}
	
	private int[][] convertStringToIntArray(String card) 
	{
		if (card == null)
			throw new NullPointerException("card can't be null");
		
		int[][] ret = new int[10][Memory.WORD_SIZE];
		
		int lo = 0;
		
		for(int i = 0; i < 10; i++)
		{
			String word = card.substring(lo, lo + 4);
			ret[i] = convertStringToWord(word);
			lo += 4;
		}
		
		return ret;
	}
	
	private int[] convertStringToWord(String card)
	{
		int[] ret = new int[Memory.WORD_SIZE];
		
		for(int i = 0; i < Memory.WORD_SIZE; i++)
		{
			if(card.length() > i)
				ret[i] = (int) card.charAt(i);
			else
				ret[i] = 0;
		}
		
		return ret;
	}
	
	private void saveCurrentPCB(LinkedList<ProgramControlBlock> queue)
	{
		if (null == pcb)
			throw new NullPointerException("pcb");
		
		queue.addLast(pcb);
		pcb = null;
		
		String queueName = null;
		
		if (queue == rq)
			queueName = "RQ";
		
		else if (queue == lq)
			queueName = "LQ";
		
		else if (queue == tq)
			queueName = "TQ";
		
		else if (queue == ioq)
		{
			queueName = "IOQ";
			
			tryToStartChannel(ch3);
		}
		
		else if (queue == mq)
			queueName = "MQ";
		
		if(TRACE)
			out.println(traceFormat() + "Saved Current PCB to " +
					queueName + "  " + printMos());
	}
	
	private void loadNextPCB(LinkedList<ProgramControlBlock> queue)
	{
		if (!queue.isEmpty())
		{
			pcb = queue.removeFirst();
		}
		
		if (TRACE)
			out.println(traceFormat() + "Remove First PCB from RQ - " + printMos());
	}
	
	//Trace related stuff
	private String traceFormat()
	{
		String pidOutput = null;
		
		if (pcb == null || pcb.processName.length() == 0)
			pidOutput = "None";
		else
			pidOutput = pcb.processName;
		
		return N + "Cycle #" + traceCycleCount + ": " +
		"Job ID: " + pidOutput + " - ";
	}
	
	private String printMos()
	{
		return printMos(this.pcb);
	}
	
	private String printMos(ProgramControlBlock pcb)
	{
		StringBuilder out = new StringBuilder();
		
		ProgramControlBlock pcbToPrint = null;
		
		// if a pcb isn't currently being executed, make a new dummy
		// pcb and have the defaults spit out later in this method
		if (null == pcb)
		{
			pcbToPrint = new ProgramControlBlock();
			init(pcbToPrint); // use the default values
		}
		else
		{
			pcbToPrint = pcb; // use the current pcb
		}
		
		int siX = 0;
		int piX = 0;
		int tiX = 0;
		
		if(si == SI_State.NONE)
			siX = 0;
		else if(si == SI_State.READ)
			siX = 1;
		else if(si == SI_State.WRITE)
			siX = 2;
		else if(si == SI_State.TERMINATE)
			siX = 3;
		else
			siX = 9;
		
		if(pi == PI_State.NONE)
			piX = 0;
		else if(pi == PI_State.OPERAND_ERROR)
			piX = 1;
		else if(pi == PI_State.OPERATION_ERROR)
			piX = 2;
		else if(pi == PI_State.PAGE_FAULT)
			piX = 3;
		else
			piX = 9;
		
		if(ti == TI_State.NONE)
			tiX = 0;
		else if(ti == TI_State.TIME_SLICE_EXCEEDED)
			tiX = 1;
		else if(ti == TI_State.TIME_LIMIT_EXCEEDED)
			tiX = 2;		
		else
			tiX = 9;
		
		if(siX == 9 || piX == 9 || tiX == 9)
		{
			throw new IllegalStateException(
					"This should never happen, " +
					"si=" + siX + ", pi=" + piX + ", ti=" + tiX);
		}
		
		int cX = pcbToPrint.c ? 1 : 0; 
		
		out
		.append(N)
		.append("IC: ").append(pcbToPrint.ic)
		.append(", IR: \"").append(charForm(pcbToPrint.ir))
		.append("\", R: \"").append(charForm(pcbToPrint.r))
		.append("\"").append(N).append("PTR: ").append(pcbToPrint.ptr)
		.append(", TS: ").append(time_slice)
		.append(", SI: ").append(siX)
		.append(", PI: ").append(piX)
		.append(", TI: ").append(tiX)
		.append(", C: ").append(cX)
		.append(", IOI: ").append(ioi)
		.append(N);
		
		return out.toString();
	}
	
	private String charForm(int[] x)
	{
		char[] out = new char[x.length];
		
		for(int i = 0; i < x.length; i++)
			out[i] = (char) x[i];
		
		return new String(out);
	}
	
	private void setEM(EM_Code... codes)
	{
		setEM(this.pcb, codes);
	}
	
	private void setEM(ProgramControlBlock pcb, EM_Code... codes)
	{
		pcb.em = codes;
	}
	
	
	private String emToString(EM_Code[] em)
	{
		if (em == null || em.length == 0)
		{
			em = new EM_Code[] { EM_Code.NORMAL_TERMINATION };
		}
		
		StringBuilder out = new StringBuilder();
		
		for (int i = 0; i < em.length; i++)
		{
			EM_Code code = em[i];
			
			if (EM_Code.NORMAL_TERMINATION == code)
				out.append("Normal Termination");
			else if (EM_Code.OUT_OF_DATA == code)
				out.append("Out of Data");
			else if (EM_Code.LINE_LIMIT_EXCEEDED == code)
				out.append("Line Limit Exceeded");
			else if (EM_Code.TIME_LIMIT_EXCEEDED == code)
				out.append("Time Limit Exceeded");
			else if (EM_Code.OPERATION_CODE_ERROR == code)
				out.append("Operation Code Error");
			else if (EM_Code.OPERAND_ERROR == code)
				out.append("Operand Error");
			else if (EM_Code.INVALID_PAGE_FAULT == code)
				out.append("Invalid Page Fault");
			else
				out.append("Unknown Error Code");
			
			if (out.length() != 0 && i != em.length - 1)
				out.append(", ");
		}
		
		return out.toString();
	}
	
	public static void main(String[] args) throws IOException
	{
		String inFile = ".\\os\\!docs\\Phase3-UserPrograms.txt";
		
		String outFile = "C:\\out.txt";
		
		if(args.length >= 1)
			inFile = args[0];
		
		if(args.length >= 2)
			outFile = args[1];
		
		new MonstahOS(inFile, outFile);
	}
}

class Steak
{
	public enum Cooked {
		NOT, RARE, MEDIUM_RARE, MEDIUM, MEDIUM_WELL, WELL_DONE
	}
	
	boolean useA1;
	Cooked cooked;
	
	public Steak()
	{
		this(Cooked.NOT);
	}
	
	public Steak(Cooked cooked)
	{
		useA1 = true;
		this.cooked = cooked;
	}
	
	public void getEatenBy(String me)
	{
		System.out.println("Moo died so " + me + " can have steak.");
	}
}
