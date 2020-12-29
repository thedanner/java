function krnInitLogging()
{
	// Not much to do here.
	// See CUP's no-op implementation.
}

function krnTrace(msg, source)
{
	// Check globals to see if trace is set ON.  If so, 
    if (_Trace)
    {
		// If msg is undefined, then we only have 1 argument.
		// Assume source is the real message.
		if ("undefined" === msg)
		{
			msg = source;
			source = "OS";
		}
		
        simLog(msg, source);
    }
}

function krnTrapError(msg)
{
    simLog("OS ERROR - TRAP: " + msg);
	
    // TODO: Display error on display.
    krnShutdown();
	
	throw new Error(msg);
}
