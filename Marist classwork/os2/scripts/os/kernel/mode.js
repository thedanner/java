/*
 * Mode "enum", modeled as an "associative array".
 */

// 0 = Kernel Mode, 1 = User Mode.  See page 21.
const Mode = {
	KERNEL : 0,
	USER : 1
}

var _Mode;

function krnSetMode(mode)
{
	// If we're not going to be chaning the mode, simply stop.
	if (mode == _Mode)
	{
		return;
	}
	
	for (var modeKey in Mode)
	{
		if (mode == Mode[modeKey])
		{
			krnTrace(
				join("setting mode bit: ", mode,
					" (", ((mode == Mode.KERNEL) ? "KERNEL" : "USER"), ")"),
				"OS");
			
			_Mode = mode;
			return mode;
		}
	}
	
	error("krnSetMode", "illegal mode value: ", mode)
}

function krnGetMode()
{
	return _Mode;
}

