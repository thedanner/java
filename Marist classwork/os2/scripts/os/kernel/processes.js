const KRN_DEFAULT_KILL_MESSAGE = "KILLED";

var _PidCounter = null;
var _CurrentPCB = null;
var _LoadedPCBs = null;

var _ReadyQueue = null;

var _Scheduler = null;


function krnInitProcesses()
{
	_PidCounter = 0;
	_CurrentPCB = null;
	_LoadedPCBs = [];
	
	_ReadyQueue = new Queue();
}

function krnGetNextPID()
{
	_PidCounter ++;
	
	return _PidCounter;
}

function krnFindLoadedPCB(pid)
{
	for (var i = 0; i < _LoadedPCBs.length; i++) 
	{
		if (_LoadedPCBs[i].pid() == pid) 
		{
			return _LoadedPCBs[i];
		}
	}
	
	return null;
}

function krnFindReadyPCB(pid)
{
	var readyPCBs = _ReadyQueue.toArray();
	
	for (var i = 0; i < readyPCBs.length; i++) 
	{
		if (readyPCBs[i].pid() == pid) 
		{
			return readyPCBs[i];
		}
	}
	
	return null;
}

function krnRemoveLoadedPCB(pcb)
{
	for (var i = 0; i < _LoadedPCBs.length; i++) 
	{
		if (_LoadedPCBs[i] == pcb) 
		{
			_LoadedPCBs.splice(i, 1);
			
			return;
		}
	}
	
	error(
		"krnRemoveLoadedPCB",
		"PCB with PID: ", pcb.pid(), " not found in load list");
}

function krnSetCurrentPCB(pcb)
{
	_CurrentPCB = pcb;
}

function krnGetCurrentPCB()
{
	return _CurrentPCB;
}

function krnHasCurrentPCB()
{
	return (_CurrentPCB != null);
}

function krnLoadProgram(priority)
{
	krnTrace("beginning to load program", "OS")
	
	var words = simGetUserProgram();
	
	if (!words)
	{
		// returns undefined or null
		return words;
	}
	
	// words should be an array of numbers at this point.
	
	if (0 == words.length)
	{
		krnTrace("loading aborted, no program data", "OS");
		
		return null;
	}
	
	var programPage = _MM.allocateFreePage();
	
	if (programPage >= 0)
	{
		krnTrace(
			join("page ", programPage, " allocated to process"),
			"OS");
		
		var nextPID = krnGetNextPID();
		
		krnTrace(
			join("program assigned PID ", nextPID),
			"OS");
		
		var pcb = new PCB(nextPID);
		
		if (priority) 
		{
			pcb.setPriority(priority);
		}
		
		krnTrace(
			join("PID ", nextPID, " has a priority of ", pcb.priority()),
			"OS");
		
		var base = _MM.getBaseAddressOfPage(programPage);
		var limit = MEMORY_PAGE_SIZE;
		
		pcb.setBase(base);
		pcb.setLimit(limit);
		
		_MM.setPage(programPage, words);
		
		_LoadedPCBs.push(pcb);
		
		krnTrace(
			join("PID ", nextPID, "'s memory parameters: base(0x",
				base.toString(16), ",", base, "), limit(0x",
				limit.toString(16), ",", limit, ")"),
			"OS");
		
		return pcb.pid();
	}
	else
	{
		krnTrace("no free pages to allocate for program", "OS");
		
		throw new Error("out of memory");
	}
}

function krnRunAllLoadedProcesses()
{
	krnTrace(
		join("readying all ", _LoadedPCBs.length, " loaded PCBs"),
		"OS");
	
	while (!_LoadedPCBs.isEmpty())
	{
		krnRunPCB(_LoadedPCBs[0]);
	}
}

function krnRunProcess(pid)
{
	var pcbToRun = krnFindLoadedPCB(pid);
	
	if (pcbToRun)
	{
		krnRunPCB(pcbToRun);
	}
	else
	{
		throw new Error(join("no loaded process with PID ", pid));
	}
}

function krnRunPCB(pcb)
{
	krnTrace(
		join("readying loaded PID ", pcb.pid()),
		"OS");
	
	krnRemoveLoadedPCB(pcb);
	_ReadyQueue.enqueue(pcb);
	
	krnEnqueueSoftwareInterrupt(
		KRN_SW_INT_PROCESS_READY);
}

function krnEndAllProcesses()
{
	krnTrace("ending all processes", "OS");
	
	while (!_ReadyQueue.isEmpty()) 
	{
		var pcb = _ReadyQueue.dequeue();
		
		krnEndPCB(pcb, KRN_DEFAULT_KILL_MESSAGE);
	}
	
	while (!_LoadedPCBs.isEmpty()) 
	{
		var pcb = _LoadedPCBs[0]
		
		krnEndPCB(pcb);
		krnRemoveLoadedPCB(pcb);
	}
	
	if (krnHasCurrentPCB()) 
	{
		krnEndPCB(krnGetCurrentPCB(), KRN_DEFAULT_KILL_MESSAGE);
		krnSetCurrentPCB(null);
	}
	
	krnProcessEnded();
}

function krnEndProcess(pid, msg)
{
	krnTrace(
		join("Ending PID ", pid, " with message: ", msg),
		"OS");
	
	var pcbToEnd = null;
	
	if (krnHasCurrentPCB() && krnGetCurrentPCB().pid() == pid) 
	{
		pcbToEnd = krnGetCurrentPCB();
		krnSetCurrentPCB(null);
	}
	else if (krnFindReadyPCB(pid)) 
	{
		pcbToEnd = krnFindReadyPCB(pid);
		_ReadyQueue.remove(pcbToEnd);
	}
	else if (krnFindLoadedPCB(pid))
	{
		pcbToEnd = krnFindLoadedPCB(pid);
		krnRemoveLoadedPCB(pcbToEnd);
	}
	
	// We found the PCB.
	if (pcbToEnd)
	{
		krnEndPCB(pcbToEnd, msg);
	}
	else
	{
		throw new Error(join("no process with PID ", pid));
	}
}

function krnEndPCB(pcb, msg)
{
	// Release the process's associated memory.
	var pageNumber = _MM.getPageNumberOfAddress(pcb.getBase());
	
	_MM.releasePage(pageNumber);
	
	krnTrace(
		join("released page from PID ", pcb.pid()),
		"OS");
	
	krnProcessEnded();
}

function krnGetRunningProcesses()
{
	var processList = [];
	
	// Get the currently running process
	if (krnHasCurrentPCB())
	{
		processList.push(_CurrentPCB);
	}
	
	// Iterate over all the queues.
	
	if (_ReadyQueue.size() > 0)
	{
		rqPCBs = _ReadyQueue.toArray();
		
		for (var i = 0; i < rqPCBs.length; i++)
		{
			processList.push(rqPCBs[i]);
		}
	}
	
	return processList;
}

function krnGetRunningProcessCount()
{
	return krnGetRunningPIDs().length;
}

// --------------
// Util functions
