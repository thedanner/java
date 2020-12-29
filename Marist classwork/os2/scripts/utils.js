/* --------  
   Utils.js

   Utility functions.
   -------- */
//

if (!String.prototype.trim)
{
	// Use a regular expression (or two) to remove leading and trailing spaces.
	String.prototype.trim = function ()
	{
		// OMFG...WTF... Is Fx's regex bugged?
		// This doesn't trim single spaces.
		// I was also having issues with it over in hardware sim.
		//return this.replace(/^\s+ | \s+$/g, "");
		
		// Try this instead:
		return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
		
		// From  http://blog.stevenlevithan.com/archives/faster-trim-javascript
		// The above is trim1();
		// The original, which should work perfectly damn fine, is trim4();
	}
}

if (!String.prototype.rot13)
{
	// An easy-to understand implementation of
	// the famous and common Rot13 obfuscator.
	String.prototype.rot13 = function()
	{
		// You can do this in three lines with a complex regular experssion,
		// but I'd have trouble explaining it in the future.
		// There's a lot to be said for obvious code.
		
		var retVal = "";
		
		for (var i = 0; i < this.length; i++) {
			var ch = this[i];
			var code = 0;
			
			if ("abcedfghijklmABCDEFGHIJKLM".indexOf(ch) >= 0) {
				// It's okay to use 13.
				// It's not a magic number; it's called rot13.
				code = this.charCodeAt(i) + 13;
				retVal = retVal + String.fromCharCode(code);
			}
			else
			{
				if ("nopqrstuvwxyzNOPQRSTUVWXYZ".indexOf(ch) >= 0)
				{
					// It's okay to use 13.  See above.
					code = this.charCodeAt(i) - 13;
					retVal = retVal + String.fromCharCode(code);
				}
				else
				{
					retVal = retVal + ch;
				}
			}
		}
		return retVal;
	}
}

// Array convenience methods.
if (!Array.prototype.indexOf)
{
    // Predefined in moz, but IE doesn't know about it.
    // Try to define it so it behaves the same way in IE as it does in moz.
    Array.prototype.indexOf = function (element)
    {
        for (var i = 0; i < this.length; i++)
        {
            if (this[i] == element)
            {
                return i;
            }
        }
        
        return -1;
    }
}

if (!Array.prototype.contains)
{
    /* 
    * Uses Array.indexOf to check if there was a matching element. 
    * Returns true if found, false otherwise.
	* Thanks Rob.
    */
    Array.prototype.contains = function (element)
    {
        return (this.indexOf(element) >= 0);
    }
}

if (!Array.prototype.isEmpty)
{
	Array.prototype.isEmpty = function ()
	{
		return (0 == this.length);
	}
}


// Function helpers
function sliceArgs(args, startIndex, endIndex)
{
	if (undefined === startIndex)
	{
		startIndex = 0;
	}
	
	if (undefined === endIndex)
	{
		endIndex = args.length;
	}
	
	var array = new Array(endIndex - startIndex);
	
	for (var i = 0; i < array.length; i++)
	{
		array[i] = args[i + startIndex];
	}
	
	return array;
}

function join()
{
	return sliceArgs(arguments).join('');
}


/* A helper function to help represent ranges of values as a string.
 * e.g.:
 * 	rangeToString("()",  3,  6) //=> "(3, 6)"
 * 	rangeToString("[]",  5,  9) //=> "[5, 9]"
 * 	rangeToString("(]", -5,  2) //=> "(-5, 2]"
 */
function rangeToString(boundsString, lo, hi)
{
	return join(
			boundsString.substring(0, 1),
			lo, ", ", hi,
			boundsString.substring(1, 2) );
}

// It would be nice here if JS was more like its similarly named cousin
// Java, in that parseInt() throws an exception or something, or even
// Java's evil ^H^H^H^H friend, C# and its tryParse().
function isIntParseable(string)
{
	var parsed = parseInt(string);
	
	// Yeah, it's awesome.  I know.
	// What we do here is this: parse the given string.  If it was successfully
	// and parsed, it does a very strict comparison to the provided argument.
	// If the parsed result is NaN, it's easy; just return false.  This
	// case is separated for readability.
	// If the parsed number, when converted back to a string, is really really
	// the same as (hence the ===) as the argument, then the string was
	// strictly parseable to an int.  parseInt() ignores problems in the given
	// string if they occur after a parseable number,
	// e.g. parseInt("123abc") => 123;
	//      parseInt("abc123") => NaN;
	
	if (isNaN(parsed))
	{
		return false;
	}
	
	return (parsed.toString() === string);
}

/* A helper function for making it easier to throw exceptions (errors?)
 * with nicely formatted and informative messages.
 * Messages may be a single element or an array of values. */
function error(caller)
{
	const MESSAGE_START_INDEX = 1;
	
	var callerName = "";
	
	var messages = sliceArgs(arguments, MESSAGE_START_INDEX);
	
	// Make sure each message really is a string.
	for (var i = 0; i < messages.length; i++)
	{
		if (messages[i])
		{
			messages[i] = messages[i].toString();
		}
	}
	
	if (caller)
	{
		callerName = join("in ", caller, "(): ");
	}
	
	throw new Error(join(
			callerName,
			messages.join('') ));
}

/*
 * Numbers returned are in the range [0, limit)
 */
function nextInt(limit)
{
	// This code snippit from
	// http://www.javascriptkit.com/javatutors/randomnum.shtml
	
	return Math.floor(nextDouble() * limit);
}

function nextDouble()
{
	return Math.random();
}


function collectPID(element)
{
	return element.pid();
}

