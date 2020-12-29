// A scheduler object, and some interface kernel-level commands.

const SCHEDULER_DEFAULT_ROUND_ROBIN_QUANTUM = 6;

const SCHEDULER_HIGHEST_PRIORITY = 24;
const SCHEDULER_LOWEST_PRIORITY = 1;
const SCHEDULER_DEFAULT_PRIORITY = 12;

function ProcessScheduler()
{
	const QUANTUM_FOR_FCFS = 0xffffffff;
	
	// Set this pointer to the algorithm desired.
	var pickNextProcess;
	
	var roundRobinTimeQuantum = null;
	
	var isRR;
	var isFCFS;
	var isPriority;
	
	this.init = function ()
	{
		this.setRoundRobin(
			SCHEDULER_DEFAULT_ROUND_ROBIN_QUANTUM);
	}
	
	this.isTimeSliceUp = function (value)
	{
		return isRR ? value > roundRobinTimeQuantum : false;
	}
	
	// Where all the fun stuff happens.
	this.timeSliceUp = function ()
	{
		if (!isRR) 
		{
			return;
		}
		
		krnTrace("time slice expired", "scheduler");
		
		// Only switch out if there's another process ready.
		if (!_ReadyQueue.isEmpty()) 
		{
			krnTrace(
				"another process is available, switching it in",
				"scheduler");
			
			var currentPCB = switchOutCurrent();
			
			switchInNext();
			
			_ReadyQueue.enqueue(currentPCB);
			
			// Reset the timer only if there was a context switch.
			// This way, if a new process comes in next cycle, it
			// can get time right away.
			krnResetTimeSliceCounter();
		}
	}
	
	this.processReady = function ()
	{
		// A new process is on the ready queue.
		// Only switch to it if we're not already busy.
		if (!krnHasCurrentPCB())
		{
			switchInNext();
		}
	}
	
	// If there's another process waiting, start it.
	// Otherwise, there's nothing to do.
	this.processEnded = function ()
	{
		this.processReady();
	}	
	
	
	function _rr_pickNextProcess()
	{
		if (!_ReadyQueue.isEmpty()) 
		{
			return _ReadyQueue.dequeue();
		}
		
		return null;
	}
	
	function _priority_pickNextProcess()
	{
		if (!_ReadyQueue.isEmpty()) 
		{
			var readyPCBs = _ReadyQueue.toArray();
			
			var highestPriority = SCHEDULER_LOWEST_PRIORITY - 1;
			var nextPCB = null;
			
			for (var i = 0; i < readyPCBs.length; i++) 
			{
				if (readyPCBs[i].priority() > highestPriority) 
				{
					nextPCB = readyPCBs[i];
					highestPriority = nextPCB.priority();
				}
			}
			
			_ReadyQueue.remove(nextPCB);
			
			return nextPCB;
		}
		
		return null;
	}
	
	function switchOutCurrent()
	{
		var currentPCB = krnGetCurrentPCB();
		
		currentPCB.readFromCPU(_CPU);
		
		krnSetCurrentPCB(null);
		
		krnTrace(
			join("switching out PID ", currentPCB.pid()),
			"scheduler");
		
		return currentPCB;
	}
	
	function switchInNext()
	{
		if (!_ReadyQueue.isEmpty()) 
		{
			var nextPCB = pickNextProcess();
			
			nextPCB.writeToCPU(_CPU);
			
			krnSetCurrentPCB(nextPCB);
			
			krnResetTimeSliceCounter();
			
			krnTrace(
				join("switching in PID ", nextPCB.pid()),
				"scheduler");
			
			return nextPCB;
		}
	}
	
	// Algorithms and policies
	
	this.setRoundRobin = function (quantum)
	{
		if (quantum) 
		{
			this.setRoundRobinQuantum(quantum);
		}
		
		krnTrace("algorithm set: round robin", "scheduler");
		
		pickNextProcess = _rr_pickNextProcess;
		
		isRR = true;
		isFCFS = false;
		isPriority = false;
	}
	
	this.setRoundRobinQuantum = function (newQuantum)
	{
		if ("number" !== typeof(newQuantum) &&
			newQuantum <= 0)
		{
			error("Scheduler.setRoundRobinQuantum",
				"quantum must be > 0: ", newQuantum);
		}
		
		roundRobinTimeQuantum = newQuantum;
		
		krnTrace(
			join("round robin quantum set to ", newQuantum),
			"scheduler");
	}
	
	this.getRoundRobinQuantum = function ()
	{
		return roundRobinTimeQuantum;
	}
	
	this.setFCFS = function ()
	{
		krnTrace("algorithm set: FCFS", "scheduler");
		this.setRoundRobin(QUANTUM_FOR_FCFS);
		
		isRR = false;
		isFCFS = true;
		isPriority = false;
	}
	
	this.setPriority = function()
	{
		krnTrace("algorithm set: priority", "scheduler");
		
		pickNextProcess = _priority_pickNextProcess;
		
		isRR = false;
		isFCFS = false;
		isPriority = true;
	}
	
	this.toString = function()
	{
		if (isRR) 
		{
			return join("round robin, q=", this.getRoundRobinQuantum());
		}
		else if (isFCFS) 
		{
			return "fcfs";
		}
		else if (isPriority) 
		{
			return "priority";
		}
	}
	
	this.init();
}


// Kernel Stuff

var _TimeSliceCounter = null;

function krnInitProcessScheduler()
{
	_Scheduler = new ProcessScheduler();
	_TimeSliceCounter = 0;
}

function krnSetPriorityScheduling()
{
	_Scheduler.setPriority();
}

function krnSetFCFSScheduling()
{
	_Scheduler.setFCFS();
}

function krnSetRRSScheduling()
{
	_Scheduler.setRoundRobin();
}

function krnSetSchedulerQuantum(quantum)
{
	_Scheduler.setRoundRobinQuantum(quantum);
}

function krnGetSchedulerQuantum()
{
	return _Scheduler.getRoundRobinQuantum();
}

function krnGetTimeSliceCounter()
{
	return _TimeSliceCounter;
}

function krnTimeSliceCounterTick()
{
	_TimeSliceCounter ++;
	
	return _TimeSliceCounter;
}

function krnResetTimeSliceCounter()
{
	_TimeSliceCounter = 0;
}

function krnIsTimeSliceUp()
{
	// Compare the currently runnign process's time to the q.
	return _Scheduler.isTimeSliceUp(krnGetTimeSliceCounter());
}

function krnCheckTimeSlice()
{
	// Compare the currently runnign process's time to the q.
	if (krnIsTimeSliceUp()) 
	{
		_Scheduler.timeSliceUp();
	}
}

function krnTimeSliceUp(pid)
{
	// If the process terminated, normally or otherwise, or was already
	// switched out, don't bother switching out the current PCB.
	if (krnGetCurrentPCB().pid() == pid)
	{
		_Scheduler.timeSliceUp();
	}
}

function krnProcessReady()
{
	_Scheduler.processReady();
}

function krnProcessEnded()
{
	_Scheduler.processEnded();
}
