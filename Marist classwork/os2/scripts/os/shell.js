/* ------------
   Shell.js
   
   The OS Shell - The "command line interface" (CLI) or interpreter for the console.
   ------------ */

// TODO: Write a base class / prototype for system services and let Shell inherit from it.

function Shell()
{
    // Properties
    this.promptStr = ">";
	
    // An "associative array" of 'command name'-'ShellCommand' pairs.
	var commands = {};
	
    var curses = [
			"fuvg","cvff","shpx","phag","pbpxfhpxre","zbgureshpxre","gvgf","ovgpu"
		];
	
    var apologies = [
			"sorry"
		];
	
    // Methods
	
	this.init = function ()
	{
	    var sc = null;
		
	    // Load the command list.
		
	    // ver
	    sc = new ShellCommand();
	    sc.command = "ver";
	    sc.description = "- Displays the current version data."
	    sc.functionPtr = shellVersion;
		registerCommand(sc);
	    
	    // help
	    sc = new ShellCommand();
	    sc.command = "help";
	    sc.description = "[command] - This help command.  See help.  Seek help too."
		sc.manText = "Help displays a list of (hopefully) valid commands.";
	    sc.functionPtr = shellHelp;
		sc.addAlias("halp"); // for the lulz
	    registerCommand(sc);
	   	
	    // shutdown
	    sc = new ShellCommand();
	    sc.command = "shutdown";
	    sc.description = "- Shuts down the OS."
	    sc.functionPtr = shellShutdown;
	    registerCommand(sc);
		
	    // clear
	    sc = new ShellCommand();
	    sc.command = "clear";
	    sc.description = "- Clears the screen."
	    sc.functionPtr = shellClear;
		sc.addAlias("cls");
	    registerCommand(sc);
		
	    // man <topic>
	    sc = new ShellCommand();
	    sc.command = "man";
	    sc.description = "<topic> - Displays the MANual page for <topic>.";
	    sc.functionPtr = shellMan;
	    registerCommand(sc);
	    
	    // trace <on | off>
	    sc = new ShellCommand();
	    sc.command = "trace";
	    sc.description = "<on | off> - Turns the OS trace on or off.";
	    sc.functionPtr = shellTrace;
	    registerCommand(sc);
		
	    // rot13 <string>
	    sc = new ShellCommand();
	    sc.command = "rot13";
	    sc.description = "<string> - Does rot13 obfuscation on <string>.";
	    sc.functionPtr = shellRot13;
	    registerCommand(sc);
		
	    // prompt <string>
	    sc = new ShellCommand();
	    sc.command = "prompt";
	    sc.description = "[string] - Sets the prompt.";
	    sc.functionPtr = shellPrompt;
	    registerCommand(sc);
		
		// date
	    sc = new ShellCommand();
	    sc.command = "date";
	    sc.description = "- Prints today's date";
	    sc.functionPtr = shellDate;
		sc.addAlias("time");
	    registerCommand(sc);
		
		// whereami
	    sc = new ShellCommand();
	    sc.command = "whereami";
	    sc.description = "- States the obvious.  But hey, you asked ...";
	    sc.functionPtr = shellWhereAmI;
	    registerCommand(sc);
		
		// status
	    sc = new ShellCommand();
	    sc.command = "status";
	    sc.description = "[string] - Sets a custom status message";
	    sc.functionPtr = shellStatus;
	    registerCommand(sc);
		
		// ---------
		// PROCESSES
		
		// quantum
	    sc = new ShellCommand();
	    sc.command = "quantum";
	    sc.description = "- Set the time quantum for the round robin scheduler.";
	    sc.functionPtr = shellQuantum;
	    registerCommand(sc);
		
		sc = new ShellCommand();
	    sc.command = "sched";
	    sc.description = "- Set the scheduling algorithm.  Accepts: rr, fcfs, priority.";
	    sc.functionPtr = shellScheduler;
	    registerCommand(sc);
		
		// processes
	    sc = new ShellCommand();
	    sc.command = "ps";
	    sc.description = "- Lists the PIDs of the currently running processes";
	    sc.functionPtr = shellProcesses;
		sc.addAlias("processes");
	    registerCommand(sc);
		
	    // load
	    sc = new ShellCommand();
	    sc.command = "load";
	    sc.description =
			"[priority] - Loads a user programs into memory, and prints its PID";
	    sc.functionPtr = shellLoad;
	    registerCommand(sc);
		
		// run
	    sc = new ShellCommand();
	    sc.command = "run";
	    sc.description = "<pid ...> - Runs the program with the given PID";
	    sc.functionPtr = shellRun;
	    registerCommand(sc);
		
		// kill
	    sc = new ShellCommand();
	    sc.command = "kill";
	    sc.description =
			"<pid ...> - Forcibly terminates the process with the given PIDs";
	    sc.functionPtr = shellKill;
		sc.addAlias("own"); // own a process, hardcore
	    registerCommand(sc);
		
	    //
	    // Display the initial prompt.
	    this.putPrompt();
	}
	
	this.handleInput = function (buffer)
	{
	    krnTrace(join("Shell Command~ ", buffer), "shell");
	    
		// User just hit enter with nothing meaningful.
		var emptyCommand = false;
		
		if (0 == buffer.trim().length)
		{
			emptyCommand = true;
			
			if (_SarcasticMode)
			{
				_StdOut.putText("Clever, nice blank line.  Asshole.");
				_StdOut.advanceLine();
			}
		}
		
		// 
	    // Parse the input...
	    //
		var command = parseInput(buffer);
		
		cmd = command.command;
		args = command.args;
		
		//
	    // Determine the command and execute it.
	    //
		var shellCommand = commands[cmd];
		
	    if (shellCommand)
	    {
			var fn = shellCommand.functionPtr;
			
	        this.execute(fn, args, cmd, shellCommand);
	    }
	    else
	    {
	        // It's not found, so check for curses and apologies before
			// declaring the command invalid.
			
			// Check for curses.
	        if (curses.contains(cmd.rot13())) {
				this.execute(shellCurse);
			}
			// Check for apoligies.
			else if (apologies.contains(cmd))
			{
				this.execute(shellApology);
			}
			// It's just an invalid command.
			else if (emptyCommand)
			{
				_StdOut.advanceLine();
				this.putPrompt();
			}
			else
			{
	            this.execute(invalidCommand);
	        }
	    }
	}
	
	// "Local" functions.
	function registerCommand(shellCommand)
	{
		// Make sure a function pointer is set.
		if (!shellCommand.functionPtr)
		{
			throw new Error(
				"Shell.registerCommand(): " +
				"no function pointer specified for command: " +
				shellCommand.command);
		}
		
		// Register the main command.
		if (commands[shellCommand.command])
		{
			throw new Error("duplicate command name: " + shellCommand.command);
		}
		
		commands[shellCommand.command] = shellCommand;
		
		// Register the aliases.
		for (var i = 0; i < shellCommand.aliases.length; i++)
		{
			alias = shellCommand.aliases[i];
			
			if (commands[alias])
			{
				throw new Error("duplicate command alias: " + alias);
			}
			
			commands[alias] = shellCommand;
		}
	}
	
	this.putPrompt = function ()
	{
	    _StdOut.putText(this.promptStr);
	}
	
	function parseInput(buffer)
	{
	    var cmd = null;
	    var args = [];
		
	    // Remove leading and trailing spaces.
	    buffer = buffer.trim();
		
	    // Separate on spaces so we can determine
		// the command and command-line args, if any.
	    var tempList = buffer.split(/ /);
		
	    // Take the first (zeroth) element and use that as the command.
		// Yes, you can do that to an array in Javascript; see the Queue class.
	    cmd = tempList.shift();
	    
		// Downcase only the command; leave the args intact.
		cmd = cmd.toLowerCase();
		
	    // Now create the args array from what's left.
	    for (var i = 0; i < tempList.length; i++)
	    {
			args.push(tempList[i]);
	    }
		
		var command = new UserCommand({
			"command" : cmd,
			"args" : args
		});
		
		return command;
	}
	
	this.execute = function (fn, args, invokedCommand, shellCommand)
	{
	    // We just got a command, so advance the line... 
	    _StdOut.advanceLine();
		
	    // .. call the command function passing in the args...
	    fn(args, invokedCommand, shellCommand);
		
	    // .. check to see if we need to advance the line again...
	    if (_StdOut.CurrentYPosition > 0)
	    {
	        _StdOut.advanceLine();
	    }
		
	    // ... and finally write the prompt again.
	    this.putPrompt();
	}
	
	//
	// The rest of these functions ARE NOT part of the Shell "class"
	// (prototype, more accurately), as they are not denoted in the
	// constructor.  The idea is that you cannot execute them from
	// elsewhere as shell.xxx .
	//
	// We limit their visibiblity to being local prototypes, as in:
	//     http://javascript.crockford.com/private.html
	
	function ShellCommand(values)
	{
		// Properties
	    this.command = null;
	    this.description = null;
	    this.functionPtr = null;
		this.manText = null;
		// This can be set manually, but it's better
		// to add stuff to this via addAlias() .
		this.aliases = [];
		
		if (values)
		{
			this.command = values["command"];
			this.description = values["description"];
			this.functionPtr = values["functionPtr"];
			this.manText = values["manText"];
		}
		
		this.addAlias = function (command)
		{
			this.aliases.push(command);
		}
		
		this.isAlias = function (command) 
		{
			return this.aliases.indexOf(command);
		}
	}
	
	function UserCommand(values)
	{
		// Properties
		this.command = "";
		this.args = [];
		
		if (values)
		{
			this.command = values["command"];
			this.args = values["args"];
		}
	}
	
	//
	// Shell Command Functions.
	// Again, not part of Shell() class per se', just called from there.
	//
	function invalidCommand()
	{
	    _StdOut.putText("Invalid Command. ");
		
	    if (_SarcasticMode)
	    {
	        _StdOut.putText("Duh. Go back to your Speak & Spell.");
	    }
	    else
	    {
	        _StdOut.putText("Type 'help' for, well... help.");
	    }
	}
	
	function shellCurse()
	{
	    _StdOut.putText("Oh, so that's how it's going to be, eh? Fine.");
	    _StdOut.advanceLine();
	    _StdOut.putText("Bitch.");
	    _SarcasticMode = true;
	}
	
	function shellApology()
	{
	    _StdOut.putText("Okay. I forgive you. This time.");
	    _SarcasticMode = false;
	}
	
	// -----------------------------
	// Begin "actual" shell commands
	
	function shellVersion(args, invokedCommand, shellCommand)
	{
	    _StdOut.putText(APP_NAME, ", version ", APP_VERSION);
	}
	
	function shellHelp(args, invokedCommand, shellCommand)
	{
	    // List all the commands, without their help
		if (0 == args.length)
		{
			for (command in commands)
			{
				var shellCommand = commands[command];
				
				// If the command isn't an alias...
				if (command == shellCommand.command)
				{
					var aliasString = "";
					
					if (!shellCommand.aliases.isEmpty())
					{
						aliasString = join(
								"  [", shellCommand.aliases.join(', '), "]"
							);
					}
					
					_StdOut.putText(shellCommand.command, aliasString);
					_StdOut.advanceLine();
				}
			}
		}
		// Otherwise, list help for the specified command
		else
		{
			for (var i = 0; i < args.length; i++)
			{
				_StdOut.putText("-");
				
				var shellCommand = commands[args[i]];
				
				// A command by the given name or alias exists.
				if (shellCommand)
				{
					var aliasString = "";
					
					if (!shellCommand.aliases.isEmpty())
					{
						aliasString = join(
								"  [", shellCommand.aliases.join(', '), "]"
							);
					}
					
					_StdOut.putText(
							args[i], " ", shellCommand.description,
							aliasString);
					
					_StdOut.advanceLine();
				}
				else
				{
					_StdOut.putText("command ", args[i], " not found");
					_StdOut.advanceLine();
				}
			}
		}
	}
	
	function shellShutdown(args, invokedCommand, shellCommand)
	{
	     _StdOut.putText("Shutting down...");
	    
		 // Call Kernal shutdown routine.
	    krnShutdown();
		
	    // TODO: Stop the final prompt from being displayed.  If possible.
		// Not a high priority.  (Damn OCD!)
	}
	
	function shellClear(args, invokedCommand, shellCommand)
	{
	    _StdOut.clearScreen();
	}
	
	function shellMan(args, invokedCommand, shellCommand)
	{
	    if (args.length > 0)
	    {
	        var topic = args[0];
			
			command = commands[topic];
			
			if (command && command.manText)
			{
				_StdOut.putText(command.manText);
			}
			else
			{
				_StdOut.putText("No manual entry for ", args[0]);
			}
	    }
	    else
	    {
	        _StdOut.putText(invokedCommand, ": please supply a topic");
	    }
	}
	
	function shellTrace(args, invokedCommand, shellCommand)
	{
	    if (args.length > 0)
	    {
	        var setting = args[0];
			
	        switch (setting)
	        {
	            case "on": 
	                if (_Trace && _SarcasticMode)
	                {
	                    _StdOut.putText("Trace is already on, dumbass.");
	                }
	                else
	                {
	                    _Trace = true;
	                    _StdOut.putText("Trace ON");
	                }
	                break;
				
	            case "off": 
	                _Trace = false;
	                _StdOut.putText("Trace OFF");                
	                break;
				
	            default:
	                _StdOut.putText("Invalid arguement.  Usage: trace <on | off>.");
	        }        
	    }
	    else
	    {
	        _StdOut.putText("Usage: ", invokedCommand, " <on | off>");
	    }
	}
	
	function shellRot13(args, invokedCommand, shellCommand)
	{
	    if (args.length > 0)
	    {
			var rot13Array = new Array(args.length);
			
			for (var i = 0; i < args.length; i++)
			{
				// Requires Utils.js for rot13() function.
				rot13Array[i] = args[i].rot13();
			}
			
	        _StdOut.putText(
				join(
					args.join(' '), " = '", rot13Array.join(' '), "'"
				));
	    }
	    else
	    {
	        _StdOut.putText(invokedCommand, ": please supply a string.");
	    }
	}
	
	function shellPrompt(args, invokedCommand, shellCommand)
	{
	    if (args.length > 0)
	    {
			// Modified to use all the args as a prompt.
	        _Shell.promptStr = args.join(' ');
	    }
	    else
	    {
	        _StdOut.putText(invokedCommand, ": please supply a string");
	    }
	}
	
	function shellDate(args, invokedCommand, shellCommand)
	{
		var string = [
				"[",
				krnDate(),
				" ",
				krnTime(),
				"]"
			].join('');
		
		_StdOut.putText(string);
	}
	
	function shellWhereAmI(args, invokedCommand, shellCommand)
	{
		_StdOut.advanceLine();
		
		// Ah, SNL's Celebrity Jeopardy, great stuff....
		// <3 Trebek & Connery
		
		_StdOut.putLines(
				"Sean Connery, where are you right now?",
				"You wrote... good lord, you wrote \"In Doors!\"",
				"That's phenomenal!  Are we recording this?!",
				"Okay, let's look at your wager:",
				"",
				"\"I-Heart-Boobs\".",
				"",
				"That's beautiful...",
				"",
				"That's it for Celebrity Jeopardy.",
				"I'm going home and putting a gun in my mouth.",
				"Good day."
			);
	}
	
	function shellStatus(args, invokedCommand, shellCommand)
	{
		var message = args.join(' ')
		
		krnSetStatus(message);
		
		if (args.length > 0)
		{
			_StdOut.putText("Status message set: ", message);
		}
		else
		{
			_StdOut.putText("Status message cleared");
		}
	}
	
	function shellQuantum(args, invokedCommand, shellCommand)
	{
		// Check the quantum.
		if (0 == args.length)
		{
			_StdOut.putText(
				invokedCommand,
				": the current quantum is ", krnGetSchedulerQuantum());
		}
		// Set the quantum.
		else if (1 == args.length)
		{
			// Restore the default quantum.
			if ("default" === args[0])
			{
				var defaultQuantum = SCHEDULER_DEFAULT_ROUND_ROBIN_QUANTUM;
				
				krnSetSchedulerQuantum(defaultQuantum);
				
				_StdOut.putText("Quantum set to default: ", defaultQuantum);
			}
			// Set the quantum to the specified value, if it's a number
			else
			{
				// Good number specified, set the new quantum.
				if (isIntParseable(args[0]))
				{
					var quantum = parseInt(args[0]);
					
					krnSetSchedulerQuantum(quantum);
					
					_StdOut.putText("Quantum set to ", quantum);
				}
				// Parse failed.
				else
				{
					_StdOut.putText(invokedCommand, ": ");
					
					if (_SarcasticMode)
					{
						_StdOut.putLines(
							"type a NUM-BURR dummy,",
							"not whatever the hell you just did");
					}
					else
					{
						_StdOut.putText("error parsing value");
					}
				}
			}
		}
		// Else, an invalid number of arguments.
		else
		{
			_StdOut.putText(
				invokedCommand, ": only one argument may be specified");
		}
	}
	
	function shellScheduler(args, invokedCommand, shellCommand)
	{
		if (1 != args.length)
		{
			_StdOut.putText(invokedCommand,
				": only one argument may be specified");
		}
		else
		{
			var method = args[0];
			
			switch (method.toLowerCase())
			{
				case "rr" :
					krnSetRRSScheduling();
					break;
				
				case "fcfs" :
					krnSetFCFSScheduling();
					break;
				
				case "priority" :
					krnSetPriorityScheduling();
					break;
				
				default	:
					_StdOut.putText(invokedCommand,
						": invalid method: ", method)
			}
		}
	}
	
	function shellProcesses(args, invokedCommand, shellCommand)
	{
		if (args.length > 0)
		{
			_StdOut.putText(invokedCommand, ": I don't take any arguments");
		}
		else
		{
			var processes = krnGetRunningProcesses();
			
			if (0 == processes.length)
			{
				_StdOut.putText("There are no processes currently running.");
				_StdOut.advanceLine();
			}
			else
			{
				function getPID(element)
				{
					return element.pid();
				}
				
				_StdOut.putText(
					"Running PIDs: ",
					processes.map(collectPID).join(', '));
				_StdOut.advanceLine();
			}
			
			if (!_LoadedPCBs.isEmpty())
			{
				_StdOut.putText(
					"Loaded PIDs: ",
					_LoadedPCBs.map(collectPID).join(', '));
				_StdOut.advanceLine();
			}
		}
	}
	
	function shellLoad(args, invokedCommand, shellCommand)
	{
		var pcb = null;
		
		try
		{
			var priority = SCHEDULER_DEFAULT_PRIORITY;
			
			if (1 == args.length) 
			{
				priority = parseInt(args[0]);
			}
			
			if (priority < SCHEDULER_LOWEST_PRIORITY ||
				priority > SCHEDULER_HIGHEST_PRIORITY)
			{
				_StdOut.putText(invokedCommand, ": error: ",
					"priority must be in: ",
						rangeToString(
							"[]",
							SCHEDULER_LOWEST_PRIORITY,
							SCHEDULER_HIGHEST_PRIORITY));
				
				return;
			}
			
			pid = krnLoadProgram(priority);
		}
		catch (error)
		{
			if (_SarcasticMode)
			{
				_StdOut.putLines(
					"You buggered your program.  Fix it, ya screw up.",
					"Even though I have better things to do than ",
					"double checking it out to be sure, I could be wrong.",
					"Who knows, or cares for that matter.",
					"This is what I think your problem is:",
					error.message
				);
			}
			else
			{
				_StdOut.putText(invokedCommand, ": error: ", error.message);
			}
			
			return;
		}
		
		if (pid)
		{
			_StdOut.putText("Program loaded, PID: ", pid);
		}
		else
		{
			if (_SarcasticMode)
			{
				// Some of these will "break" if the layout changes.
				// Oh well, such is life.
				
				_StdOut.putLines(
						"                       ^^^^^^^^^^^^^^^^^^^",
						"",
						"Idiot.  I can try to load and run nothing",
						"if you really, really want me to, but I'd",
						"rather not.  I have better things to do than",
						"listen to your stupid requests.",
						"Do me a favor: put something over there:",
						"",
						"---------------------------------->>>",
						"---------------------------------->>>",
						"---------------------------------->>>",
						"",
						"like a good little nerd and then try it again.",
						"If you can't manage that, I suggest you click",
						"the Emergency Halt button up there,",
						"the one I'm pointing to,",
						"and then click the shutdown button.",
						"-kthx"
					);
			}
			else
			{
				_StdOut.putText(invokedCommand, ": error: no data provided");
			}
		}
	}
	
	function shellRun(args, invokedCommand, shellCommand)
	{
		// Nothing given.
		if (0 == args.length) 
		{
			_StdOut.putText(invokedCommand, ": missing PID(s)");
		}
		// Some number of arguemnts given.
		else
		{
			// 1 argument, a special value of "all"
			if (1 == args.length && "all" === args[0])
			{
				krnRunAllLoadedProcesses();
			}
			// Otherwise, assume a list of PIDs.
			else
			{
				for (var i = 0; i < args.length; i++)
				{
					var pid = parseInt(args[i]);
					
					try
					{
						krnRunProcess(pid)
						
						_StdOut.putText("Process ", pid, " started");
						_StdOut.advanceLine();
					} 
					catch (error)
					{
						_StdOut.putText(
							invokedCommand, ": error: ", error.message);
					}
				}
			}
		}
	}
	
	function shellKill(args, invokedCommand, shellCommand)
	{
		if (args.length == 0)
		{
			_StdOut.putText(invokedCommand, ": missing PID(s)");
		}
		else
		{
			if (1 == args.length && "all" === args[0]) 
			{
				krnEndAllProcesses();
			}
			else 
			{
				for (var i = 0; i < args.length; i++) 
				{
					var pid = parseInt(args[i]);
					
					try 
					{
						krnEndProcess(pid, "KILLED");
						
						_StdOut.putText("Process ", pid, " killed");
						_StdOut.advanceLine();
					} 
					catch (error) 
					{
						_StdOut.putText(
							invokedCommand, ": error: ", error.message);
						_StdOut.advanceLine();
					}
				}
			}
		}
	}
	
	// Call the constructor.
	this.init();
}
