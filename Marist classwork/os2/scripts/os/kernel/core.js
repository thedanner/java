/* ------------
   Kernel.js
   
   Requires globals.js
   
   Routines for the Operataing System, NOT the simulation.
   
   This code references page numbers in the text book: 
   Operating System Concepts 8th editiion by Silberschatz, Galvin, and Gagne.  ISBN 978-0-470-12872-5   
   ------------ */

// Give this some time to get data before the OS really starts.
krnDiskDriveDeviceDriver =
	new DeviceDriver_DiskDrive();

krnDiskDriveDeviceDriver.driverEntry();

//
// OS Startup and Shutdown Routines   
//
function krnBootstrap() // Page 8.
{
	krnInitLogging();
	
	// Use simLog because we ALWAYS want this, even if _Trace is off.
	simLog("bootstrap");
	
//	_KernelBuffers = new Array();
	_KernelInputQueue = new Queue();
	
	// Initialize some global variables.
	
	// Memory initialization.
	_MainMemory = new MainMemory();
	_MA = new MemoryAccessor(_MainMemory);
	_MM = new MemoryManager(_MA);
	
	_CPU = new CPU(_MM);
	
	// Start off in kernel mode.
	krnSetMode(Mode.KERNEL);
	
	_KernelBuffers = new Array();
	
	_Console = new Console();
	_StdIn = _Console;
	_StdOut = _Console;
	
	_StatusMessage = "Running";
	
	// Support for processes (queues, etc.) and the scheduler.
	krnInitProcesses();
	krnInitProcessScheduler();
	
	
	// Load the Keyboard Device Driver
	krnTrace("Loading the keyboard device driver.");
	// Construct it.
	krnKeyboardDriver = new DeviceDriver_Keyboard();
	
	// Call the driverEntry() initialization routine.
	krnKeyboardDriver.driverEntry();
	krnTrace(krnKeyboardDriver.status);
	
	// Do the same for the HDD driver.
	krnTrace("Loading the disk drive device driver.");
	
	/*
	// As a hack for asnych crap, move this up.
	krnDiskDriveDeviceDriver =
		new DeviceDriver_DiskDrive();
	
	krnDiskDriveDeviceDriver.driverEntry();
	*/
	
	krnTrace(krnDiskDriveDeviceDriver.status);
	
	// And the file system driver.
	krnTrace("Loading the file system driver.");
	
	krnFileSystemDriver =
		new Driver_FileSystem(krnDiskDriveDeviceDriver);
	
	krnFileSystemDriver.driverEntry();
	krnTrace(krnFileSystemDriver.status);
	
	
	// Penultimately, enable the Interrupts.  
	krnTrace("Enabling the interrupts.");
	krnEnableInterrupts();
	
	// Ultimately, launch the shell.
	krnTrace("Creating and Launching the shell.");
	
	_Shell = new Shell();
}

function krnResetSystem()
{
	krnTrace("UH OH! SOMEONE PRESSED THE BIG RED BUTTON!", "OS");
	krnTrace("HOLD ON, WE'RE RESTARTING!  IT'S GONNA BE A ROUGH RIDE!", "OS");
	// Ironically, it's not even red.
	
	// Important, otherwise the clock speed will double every
	// time someone hits reset, until the world implodes.
	simDisableTimerInterrupt();
	
	krnBootstrap();
}

function krnShutdown()
{
    krnTrace("Begin shutdown");
    // TODO: Check for running processes.
	// Alert if there are some, alert and stop.  Else...    
    // ... Disable the Interruupts.
    krnTrace("Disabling the interrupts.");
    krnDisableInterrupts();
    // 
    // Unload the Device Drivers?
    // More?
    //
    krnTrace("end shutdown");
}


// 
// Interrupt Handling
// 
function krnEnableInterrupts()
{
	krnEnableSoftwareInterrupts();
	
    // Hardware
    // --------
    // Timer
    simEnableTimerInterrupt();      // Page 23.  
    // Keyboard
    simEnableKeyboardInterrupt();
    
    // Software
    // --------
	
	_SoftwareInterruptQueue = new Queue();
}

function krnDisableInterrupts()
{
	// Hardware
    // --------
    // Timer
    simDisableTimerInterrupt();
    // Keyboard
    simDisableKeyboardInterrupt();
    
    // Software
    // --------
	
	krnDisableSoftwareInterrupts();
}

function krnInterruptDispatcher(irq, params)    // This is the Interrupt Handler Routine.  Page 8.
{
    // Trace our entrance here so we can compute Interrupt Latency by analyzing the log file later on.  Page 766.
    // But only NOT for clock ticks... that's too much.
    if (irq != TIMER_IRQ)
    {
        krnTrace(join("Handling IRQ~ ", irq));
    }
	
    // 1. Save the current state.
    // 
    // 2. Invoke the requested Interrupt Service Routine via Switch/Case rather than an Interrupt Vector.  TODO: Use Interrupt Vector in the future?
    //    Note: There is no need to "dismiss" or acknowledge the interrupts in our design here.  Maybe the hardware simulation will grow to support/require that in the future.
    switch (irq)
    {
        case TIMER_IRQ: 
            krnTimerTick();                  // Kernel routine
            break;
		
        case KEYBOARD_IRQ: 
            krnKeyboardDriver.isr(params);   // Kernel mode device driver
            _StdIn.handleInput();
            break;
		
		case HARD_DISK_DRIVE_IRQ:
			krnDiskDriveDeviceDriver.isr(params);
			break;
		
		case FILE_SYSTEM_IRQ:
			krnFileSystemDriver.isr(params);
			break;
		
        default:
            krnTrapError("Invalid Interrupt Request. irq=" + irq + " params=[" + params + "]");
    }
    //
    // 3. Restore the saved state.  TODO: Question: Should we restore the state via IRET in the ISR instead of here? p560.
}

function krnTimerTick()  // The (built-in, as opposed to device driver) timer Interrupt Service Routine.
{
    // Increment the clock.
    OS_CLOCK++;
	
	_Console.updateTaskBar();
	
	_CPU.doCycle();
	
    // TODO: Check multiprogramming parameters and enfore quantum here.
	//Call the scheduler if necessary.
    // TODO: Accumulate CPU usage and profiling statistics here.
}   



//
// System Calls... that generate software interrupts via tha Application Programming Interface library routines.
//
// Some ideas:
// - CreateFile
// - ReadFile
// - WriteFile
// - DeleteFile
// - Format
