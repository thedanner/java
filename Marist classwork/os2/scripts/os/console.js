/* ------------
   Console.js

   Requires globals.js

   The OS Console - StdIn and StdOut by default.
   Note: This is not the Shell.  The Shell is the "command line interface"
   (CLI) or interpreter for this console.
   ------------ */

function Console()
{
    // Properties
    this.CurrentFont      = DEFAULT_FONT;
    this.CurrentFontSize  = DEFAULT_FONT_SIZE;
    this.CurrentXPosition = 0;
    this.CurrentYPosition = DEFAULT_FONT_SIZE;
    this.buffer = "";
    
	var tty = new TTY();
	
    // Methods
	
	this.init = function ()
	{
	    this.clearScreen();
		
		tty.init();
	}
	
	this.clearScreen = function ()
	{
		tty.clearScreen();
	}
	
	this.handleInput = function ()
	{
	    while (_KernelInputQueue.size() > 0)
	    {
	        // Get the next character from the kernel input queue.
	        var chr = _KernelInputQueue.dequeue();
			
	        // Check to see if it's "special" (enter or ctrl-c) or "normal" (anything else that the keyboard device driver gave us).
	        if (chr == String.fromCharCode(13))  // Enter key   
	        {
	            // The enter key marks the end of a console command, so ...
	            // ... tell the shell ... 
	            _Shell.handleInput(this.buffer);
	            // ... and reset our buffer.
	            this.buffer = "";
	        }
			else if (chr == String.fromCharCode(8)) // Backspace?
			{
				// Delete the last character on the buffer.
				// -1 means one from the end.
				if (this.buffer.length > 0)
				{
					this.buffer = this.buffer.slice(0, -1);
					
					this.backspace();
				}
			}
	        // TODO: Write a case for Ctrl-C.
	        else
	        {
	            // This is a "normal" character, so ...
	            // ... draw it on the screen...
	            this.putText(chr);
				
	            // ... and add it to our buffer.
	            this.buffer += chr;
	        }
	    }
	}
	
	// My first inclination here was to write two functions: putChar() and
	// putString().  Then I remembered that Javascript is (sadly) untyped
	// and it won't differentiate between the two.  So rather than be like
	// PHP and write two (or more) functions that do the same thing,
	// thereby encouraging confusion and decreasing readability, I
	// decided to write one function and use the term "text" to connote
	// string or char.
	// The function accepts unliminited parameters.
	this.putText = function ()
	{
		var textArray = sliceArgs(arguments);
		
		var text = textArray.join('');
		
		tty.putText(text);
		
		syncWithTTYCursor();
	}
	
	this.putLines = function ()
	{
		var lines = sliceArgs(arguments);
		
		for (var i = 0; i < lines.length; i++)
		{
			this.putText(lines[i]);
			
			if (i < lines.length - 1)
			{
				this.advanceLine();
			}
		}
		
		syncWithTTYCursor();
	}
	
	this.advanceLine = function ()
	{
		tty.advanceLine();
		
		syncWithTTYCursor();
	}
	
	this.backspace = function ()
	{
		tty.backspace();
		
		syncWithTTYCursor();
	}
	
	this.updateTaskBar = function ()
	{
		var date = new Date();
		
		var month = date.getMonth() + 1;
		var dayOfMonth = date.getDate();
		var hours = date.getHours();
		var minutes = date.getMinutes();
		var seconds = date.getSeconds();
		
		var monthStr = month.toString();
		var dayOfMonthStr = dayOfMonth.toString();
		var hoursStr = hours.toString();
		var minutesStr = minutes.toString();
		var secondsStr = seconds.toString();
		
		if (month < 10)
		{
			monthStr = "0" + month;
		}
		
		if (dayOfMonth < 10)
		{
			dayOfMonthStr = "0" + dayOfMonth;
		}
		
		if (hours < 10)
		{
			hoursStr = "0" + hours;
		}
		
		if (minutes < 10)
		{
			minutesStr = "0" + minutes;
		}
		
		if (seconds < 10)
		{
			secondsStr = "0" + seconds;
		}
		
		var currentDate = [
				date.getFullYear(), "-",
				monthStr , "-",
				dayOfMonthStr
			].join('');
		
		var currentTime = [
				hoursStr, ":", minutesStr, ":", secondsStr
			].join('');
		
		statusString = [
				"[", currentDate, " ", currentTime, "] " , _StatusMessage
			].join('');
		
		tty.updateTaskBarText(statusString);
	}
	
	function syncWithTTYCursor()
	{
		this.CurrentXPosition = tty.cursorX;
		this.CurrentYPosition = tty.cursorY;
	}
	
	// Call the constructor.
	this.init();
}
