var softwareInterruptCounter = 0;

const KRN_SW_INT_PRINT_INTEGER = softwareInterruptCounter ++;
const KRN_SW_INT_PRINT_STRING = softwareInterruptCounter ++;
const KRN_SW_INT_END_PROCESS = softwareInterruptCounter ++;
const KRN_SW_INT_TIME_SLICE_UP = softwareInterruptCounter ++;
const KRN_SW_INT_PROCESS_READY = softwareInterruptCounter ++;

var _SoftwareInterruptQueue = null;

function krnEnableSoftwareInterrupts()
{
	_SoftwareInterruptQueue = new Queue();
}

function krnDisableSoftwareInterrupts()
{
	_SoftwareInterruptQueue = null;
}

function krnHandleSoftwareInterrupt()
{
	if (krnHasUnservicedSoftwareInterrupts())
	{
		var nextInterrupt = _SoftwareInterruptQueue.dequeue();
		
		var irq = nextInterrupt[0];
		var args = nextInterrupt[1];
		
		krnTrace(join("Handling software interrupt~ ", irq), "OS");
		
		switch (irq)
		{
			case KRN_SW_INT_PRINT_INTEGER :
				krnSwInterrupt_printInteger(args);
				break;
			
			case KRN_SW_INT_PRINT_STRING :
				krnSwInterrupt_printString(args);
				break;
			
			case KRN_SW_INT_END_PROCESS :
				krnSwInterrupt_endProcess(args);
				break;
			
			case KRN_SW_INT_TIME_SLICE_UP :
				krnSwInterrupt_timeSliceUp(args);
				break;
			
			case KRN_SW_INT_PROCESS_READY :
				krnSwInterrupt_processReady(args);
				break;
			
			default:
				error("krnHandleSoftwareInterrupt",
					"unknown software IRQ: ", irq);
		}
	}
}

function krnHasUnservicedSoftwareInterrupts()
{
	return ! _SoftwareInterruptQueue.isEmpty();
}

function krnEnqueueSoftwareInterrupt(isr)
{
	var args = sliceArgs(arguments, 1);
	
	_SoftwareInterruptQueue.enqueue([isr, args]);
}

// Implementations

function krnSwInterrupt_printInteger(args) // Hah, 'print int', it rhymes!!
{
	// Delegation !!
	krnSwInterrupt_printString(args);
}

function krnSwInterrupt_printString(args)
{
	if (1 != args.length)
	{
		error("krnSwInterrupt_PrintString",
			"expecting only one argument, got ", args.length)
	}
	
	_StdOut.advanceLine();
	_StdOut.putText(args[0]);
	_StdOut.advanceLine();
	_Shell.putPrompt();
}

function krnSwInterrupt_endProcess(args)
{
	krnEndProcess(args[0], args[1]);
}

function krnSwInterrupt_timeSliceUp(args)
{
	krnTimeSliceUp(args[0]);
}

function krnSwInterrupt_processReady(args)
{
	krnProcessReady();
}
