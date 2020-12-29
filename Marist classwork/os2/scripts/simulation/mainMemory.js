const MEMORY_SIZE_OF_WORD = 0x100; // What can be represented by 2 bytes.
const MEMORY_WORD_COUNT = 0x300; // 0x100 = 256
const MEMORY_BLANK_WORD_VALUE = 0;

const MEMORY_FRAME_SIZE = 0x80;

function MainMemory ()
{
	// Variables.
	var memory = [];
	
	// Wannabe constructor.
	this.init = function ()
	{
		memory = new Array(MEMORY_WORD_COUNT);
		
		for (var i = 0; i < memory.length; i++)
		{
			this.set(i, MEMORY_BLANK_WORD_VALUE);
		}
	}
	
	// Nobody but CPU or hardware should call this.
	this.get = function (baseAddress, length)
	{
		if (undefined === length)
		{
			length = 1;
		}
		
		var contents = new Array(length);
		
		for (var i = 0; i < length; i++)
		{
			var address = baseAddress + i;
			
			address = validateAddress(address, "MainMemory.get");
			
			contents[i] = memory[address];
		}
		
		return contents;
	}
	
	// Nobody but CPU or MA should call this.
	this.set = function (baseAddress, bytes)
	{
		// Place a lone value into an array, with it as the only object.
		if ( ! (bytes instanceof Array) )
		{
			bytes = [ bytes ];
		}
		
		for (var i = 0; i < bytes.length; i++)
		{
			var address = baseAddress + i;
			var value = bytes[i];
			
			address = validateAddress(address, "MainMemory.set");
			value = validateValue(value, "MainMemory.set");
			
			memory[address] = value;
			
			var a = typeof(value);
			var b = null;
		}
	}
	
	function validateAddress(address, caller)
	{
		if ("number" !== typeof(address))
		{
			error(
				caller,
				"address (", address, ") expected to be a number, was a: ",
				typeof(address) );
		}
		
		// Make sure address refers to a valid word index.
		if (address < 0 || address >= MEMORY_WORD_COUNT)
		{
			error(
				caller,
				"address must be in range ",
				rangeToString("[)", 0, MEMORY_WORD_COUNT), ": ",
				address );
		}
		
		address = parseInt(address);
		
		return address;
	}
	
	function validateValue(value, caller)
	{
		if ("number" !== typeof(value))
		{
			error(
				caller,
				"value (", value, ") expected to be a number, was a: ",
				typeof(value) );
		}
		
		//TODO: negative numbers?
		// Make sure value is valid.
		if (value < 0 || value >= MEMORY_SIZE_OF_WORD)
		{
			error(
				caller,
				"value must be in range ",
				rangeToString("[)", 0, MEMORY_SIZE_OF_WORD), ": ",
				value );
		}
		
		value = parseInt(value);
		
		return value;
	}
	
	// Call the constructor.
	this.init();
}
