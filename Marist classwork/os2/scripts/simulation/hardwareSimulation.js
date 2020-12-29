/* ------------  
   HardwareSimulation.js

   Requires global.js.
   
   Routines for the simulation, NOT for the OS itself.
   This (and simulation scripts) is the only place that we should see "web" code, like 
   DOM manipulation and Javascript event handling, and so on.  (index.html is the only place for markup.)
   
   This code references page numbers in the text book: 
   Operating System Concepts 8th editiion by Silberschatz, Galvin, and Gagne.  ISBN 978-0-470-12872-5
   ------------ */


//
// Simulation Events
//
function simInit()
{
	// Get a global reference to the canvas.  TODO: Move this stuff into a Display Device Driver?
	CANVAS  = document.getElementById('display');
	// Get a global reference to the drawing context.
	DRAWING_CONTEXT = CANVAS.getContext('2d');
	// Enable the added-in canvas text functions (see canvastext.js for provenance and details).
	CanvasTextFunctions.enable(DRAWING_CONTEXT);
	// Clear the log text box.
	document.getElementById("divLog").innerHTML="";
    // Make sure the start button isn't disabled because Firefox saved its state across refreshes.
	document.getElementById("btnStartOS").disabled = false;
	// Set focus on the start button.
	document.getElementById("btnStartOS").focus();
	
	document.getElementById("btnHaltOS").disabled = true;
	
	document.getElementById("btnReset").disabled = true;
	
	document.getElementById("btnUpdateDebug").disabled = true;
	
	document.getElementById("btnSingleStep").disabled = true;
	document.getElementById("btnRunContinuously").disabled = true;
	
	document.getElementById("btnClearLog").disabled = true;
}

function simBtnStartOS_click(btn)
{
	// Disable the start button...
	btn.disabled = true;
	
	// ... change the value ...
	btn.value = "OS has started."
	
	simStartOS();
}

function simStartOS()
{
	// .. enable the Emergency Halt button ...
    document.getElementById("btnHaltOS").disabled = false;
	
	// .. and the Reset button ...
	document.getElementById("btnReset").disabled = false;
	
    // .. set focus on the OS console display ... 
    document.getElementById("display").focus();
    
	document.getElementById("btnUpdateDebug").disabled = false;
	
	document.getElementById("btnSingleStep").disabled = false;
	document.getElementById("btnRunContinuously").disabled = true;
	
    document.getElementById("btnClearLog").disabled = false;
	
    // .. and call the OS Kernel Bootstrap routine.
    krnBootstrap();
}

function simBtnHaltOS_click(btn)
{
    simLog("emergency halt");
    simLog("Attempting Kernel shutdown.");
    // Call the OS sutdown routine.
	
	document.getElementById("btnHaltOS").disabled = true;
	
    krnShutdown();
    // TODO: Is there anything we need to do here?
}


function simBtnReset_click(btn)
{
	simDisableTimerInterrupt();
	simStartOS();
}


//
// Simulation Services
//
function simLog(msg, source)
{
	var sourceClass = null;
	
    // Check the source.
    if (!source)
    {
        source = "?";
    }
	
	sourceClass = determineLogClass(msg, source);
	
    // Note the OS CLOCK.
    var tick = OS_CLOCK;
	
    // Note the REAL clock in milliseconds since January 1, 1970.
    var now = new Date().getTime();
	
    // Build the log string.
    var str = [
			"[", tick,   "],",
            "[", now,    "],",
            "[", source, "],",
            "[", msg,    "]\n"
		].join('');
    
	// Update the log console.
    var divLog = document.getElementById("divLog");
	var spanLastLine = document.getElementById("spanLastLine");
	
	// The class should be 'log_entry',
	// a downcased-version of the source,
	// and 'log_top_entry'.
	var spanClass = [
			"log_entry",
			sourceClass,
			"log_top_entry"
		].join(' ');
	
	var spanNewLine = [
			'<span id="spanLastLine" ',
				   'class="', spanClass , '">',
				str,
			'</span>'
		].join('');
	
	if (spanLastLine)
	{
		spanLastLine.removeAttribute('id');
		
		var currentClass = spanLastLine.getAttribute('class');
		
		var newClass = currentClass.replace(/log_top_entry/, '');
		
		spanLastLine.setAttribute('class', newClass);
	}
	
	divLog.innerHTML = [
			spanNewLine,
			"<br />",
			divLog.innerHTML
		].join('');
}

function determineLogClass(msg, source)
{
	if ("?" == source)
	{
		source = "unknown";
    }
	
	return "log_source_" + source.toLowerCase();
}

function simBtnClearLog_click(btn)
{
	document.getElementById("divLog").innerHTML = "";
	
	simLog("log cleared", "log")
}

function simBtnSingleStep_click(btn)
{
	document.getElementById("btnRunContinuously").disabled = false;
	
	_CPU.singleStep();
}

function simBtnRunContinuously_click(btn)
{
	document.getElementById("btnRunContinuously").disabled = true;
	
	_CPU.resume();
}

function simUpdateDebug()
{
	var divDebug = document.getElementById("divDebug");
	
	divDebug.innerHTML = '';
	
	var output = [
			// Registers
			"CPU Registers:\n",
			"    Acc:   ", _CPU.getAcc(), "\n",
			"      X:   ", _CPU.getX(), "\n",
			"      Y:   ", _CPU.getY(), "\n",
			"     IR: 0x", _CPU.getIR().toString(16).toUpperCase(), "\n",
			"     PC:   ", _CPU.getPC(), "\n",
			"      C:   ", _CPU.getC(),  "\n",
			"      Z:   ", _CPU.getZ(),  "\n",
			"      B:   ", _CPU.getB(),  "\n",
			" - Base: 0x", _CPU.getBase().toString(16).toUpperCase(),  "\n",
			" -Limit:   ", _CPU.getLimit(),  "\n\n",
			
			"CPU Clock: ", _CPU.getTimerTicks(), " ",
				(_CPU.isSingleStepMode() ? "(single step)" : ""), "\n",
			
			"Time slice counter: ", krnGetTimeSliceCounter(), "\n\n",
			
			"Scheduler: ", _Scheduler.toString(), "\n",
			
			"Free pages: ", _MM.getFreePageCount(),
				" / ", _MM.getPageCount(), "\n\n",
			
			"     Current PID: ",
				((krnHasCurrentPCB()) ?
						krnGetCurrentPCB().pid() : "none"), "\n",
			" Ready processes: ", _ReadyQueue.size(), ", PIDs: ",
				_ReadyQueue.toArray().map(collectPID).join(', '), "\n",
			
			"Loaded processes: ", _LoadedPCBs.length, ", PIDs: ",
				_LoadedPCBs.map(collectPID).join(', '), "\n",
			
			"\n\nMemory:\n"
		];
	
	// First couple words in memory
	for (var i = 0; i < MEMORY_WORD_COUNT; i += 0x08)
	{
		var lineAddress = i.toString(16);
		
		while (lineAddress.length < 3)
		{
			lineAddress = "0" + lineAddress;
		}
		
		output.push(lineAddress);
		output.push(":   ")
		
		for (var j = i; j < i + 0x08; j++)
		{
			var word = _MainMemory.get(j, 1)[0];
			
			word = word.toString(16).toUpperCase();
			
			while (word.length < 2)
			{
				word = "0" + word;
			}
			
			output.push(word);
			output.push(" ");
		}
		
		output.push("\n");
	}
	
	html =
		output.join('').
			replace(/ /g, "&nbsp;").
			replace(/\n/g, "<br />");
	
	divDebug.innerHTML = html;
}

function simGetUserProgram()
{
	var program = document.getElementById("taUserProgram").value.trim();
	
	var rawWords = null;
	
	if (0 == program.length)
	{
		return null;
	}
	else
	{
		// TODO: figure out why the regex produces [""] when the input
		// is empty, instead of an empty array.
		rawWords = program.split(/\s+/g);
	}
	
	var words = new Array(rawWords.length);
	
	for (var i = 0; i < rawWords.length; i++)
	{
		var rawWord = rawWords[i];
		
		// Sigh...JS sucks... parstInt("cx", 16); will ignore the 'x',
		// or any trailing values that don't make sense and just return the
		// part that does make sense, so now we have to go through every
		// damn character and make sure they're all hex digits.
		// I like to be safe, not stupid with this stuff.
		for (var j = 0; j < rawWord.length - 1; j++)
		{
			digit = parseInt(rawWord.substr(j, 1), 16);
			
			if (isNaN(digit))
			{
				error(
					"simGetUserProgram",
					"error reading user program: ",
					"words must contain only valid hex digits: '",
					rawWord, "'[", j, "] = '", j,"'");
			}
		}
		
		if (2 != rawWord.length)
		{
			error(
				"simGetUserProgram",
				"error reading user program: ",
				"words must have a length of 2, found: '",
				rawWord, "' (", rawWord.length,")");
		}
		
		var word = parseInt(rawWord, 16);
		words[i] = word;
	}
	
	return words;
}

//
// Simulate the HARDWARE Interrupt Request Line.
// (See pages 560-561 in text book.)
//

//
// Timer Interrupt
//
function simEnableTimerInterrupt() // Page 23.
{
    // We'll need the global _hardwareClockId later to
	// clear the interval in simDisableTimerInterrupt;
    _hardwareClockId = setInterval(simOnClockTick, CLOCK_INTERVAL);   
}

function simDisableTimerInterrupt()
{
    clearInterval(_hardwareClockId);
}

function simOnClockTick()
{
    krnInterruptDispatcher(TIMER_IRQ);
}


//
// Keyboard Interrupt
//
function simEnableKeyboardInterrupt()
{
    // Listen for key presses in the document and call the
	// simulation processor, which will in turn call the
    // os interrupt handler.
	document.addEventListener("keypress", simOnKeypress, false);
}

function simDisableKeyboardInterrupt()
{
	document.removeEventListener("keypress", simOnKeypress, false);
}

function simOnKeypress(event)
{
    if (event.target.id == "display")
    {
		event.preventDefault();
		
        var params = {
				"which" : event.which,
				"keyCode" : event.keyCode,
				"shifted" : event.shiftKey
			};
		
        // Call the OS Interrupt Dispatcher.
        krnInterruptDispatcher(KEYBOARD_IRQ, params);
    }
}


//
// xxx Interrupt
//
