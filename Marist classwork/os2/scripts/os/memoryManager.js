const MEMORY_PAGE_SIZE = MEMORY_FRAME_SIZE;

function MemoryManager(accessor)
{
	var memoryAccessor = accessor;
	
	var pagesAllocated = null;
	var freePageList = null;
	
	this.init = function()
	{
		pagesAllocated = new Array(this.getPageCount());
		freePageList = new Array(this.getPageCount());
		
		for (var i = 0; i < pagesAllocated.length; i++) 
		{
			pagesAllocated[i] = false;
			freePageList[i] = i;
		}
	}
	
	this.getWord = function(address)
	{
		return this.get(address, 1)[0];
	}
	
	this.get = function(address, length)
	{
		var physicalAddress = -1;
		
		switch (krnGetMode())
		{
			// Direct access.
			case Mode.KERNEL:
				physicalAddress = address;
				break;
				
			case Mode.USER:
				physicalAddress = virtualToPhysical(address);
				validateLimit(address, length);
				break;
				
			default:
				error("MM.get", "illegal mode value: ", krnGetMode());
		}
		
		var moo = memoryAccessor.get(physicalAddress, length);
		
		var b = typeof(moo);
		var c = typeof(moo[0]);
		
		return moo;
	}
	
	this.setWord = function(address, word)
	{
		return this.set(address, [ word ]);
	}
	
	this.set = function(address, bytes)
	{
		var physicalAddress = -1;
		
		switch (krnGetMode())
		{
			// Direct access.
			case Mode.KERNEL:
				physicalAddress = address;
				break;
				
			case Mode.USER:
				physicalAddress = virtualToPhysical(address);
				validateLimit(address, bytes.length);
				break;
				
			default:
				error("MM.set", "illegal mode value: ", krnGetMode());
		}
		
		return memoryAccessor.set(physicalAddress, bytes);
	}
	
	this.setPage = function(pageNumber, bytes)
	{
		return this.set(this.getBaseAddressOfPage(pageNumber), bytes);
	}
	
	this.allocateFreePage = function()
	{
		if (0 == this.getFreePageCount()) 
		{
			krnTrace(
				"failed to allocate page: out of memory (no free pages)",
				"MM");
			
			return -1;
		}
		
		var indexOfPageNumber = nextInt(this.getFreePageCount());
		
		var pageNumber = freePageList[indexOfPageNumber];
		
		freePageList.splice(indexOfPageNumber, 1);
		
		pagesAllocated[pageNumber] = true;
		
		return pageNumber;
	}
	
	this.releasePage = function(pageNumber)
	{
		krnTrace(join("releasing page ", pageNumber), "MM");
		
		pagesAllocated[pageNumber] = true;
		
		freePageList.push(pageNumber);
	}
	
	function setPageAllocated(pageNumber, allocated)
	{
		if (pageNumber < 0 || pageNumber >= this.getPageCount()) 
		{
			error("MM.setPageAllocated", "page number out of range: ", rangeToString("[)", 0, this.getPageCount()))
		}
		
		if (true !== allocated && false !== allocated) 
		{
			error("MM.setPageAllocated", "invalid value for allocated: ", alloctaed)
		}
		
		// Both arguments are valid.
		pagesAllocated[pageNumber] = allocated;
	}
	
	this.getBaseAddressOfPage = function(pageNumber)
	{
		return pageNumber * MEMORY_PAGE_SIZE;
	}
	
	this.getPageNumberOfAddress = function(address)
	{
		return address / MEMORY_PAGE_SIZE;
	}
	
	this.getPageCount = function()
	{
		return MEMORY_WORD_COUNT / MEMORY_PAGE_SIZE;
	}
	
	this.getFreePageCount = function()
	{
		return freePageList.length;
	}
	
	function virtualToPhysical(address)
	{
		return address + _CPU.getBase();
	}
	
	function validateLimit(baseAddress, length)
	{
		if ("number" !== typeof(length) || length <= 0)
		{
			error(
				"MM.validateLimit",
				"length (", length, ") must be a number > 0");
		}
		
		// We take both base and limit for whenn a process requests
		// memory near the end of its valid area, but requests a
		// length beyond what it's allowed to access.
		// E.g. if limit is 128, accessing bytes 0-127 is allowed,
		// as is accessing 126 + 2 words (126, 127).
		// 126 + 3 blocks, though, is not (126-OK, 127-OK, 128-BAD).
		// Accessing a 127 + 1 word is legal; this means word 127 only.
		// If we were to simply add the two up, we'd get 128.
		// 128 is NOT < the limit, so it would appear to be invalid,
		// though it is legal--hence the - 1 below.
		
		var topRequestedAddress = baseAddress + (length - 1);
		var topAllowedAddress = _CPU.getLimit();
		
		// If the base + 
		if (topRequestedAddress < topAllowedAddress)
		{
			return true;
		}
		
		krnEnqueueSoftwareInterrupt(
			KRN_SW_INT_END_PROCESS,
			(krnHasCurrentPCB() ? krnGetCurrentPCB().pid() : "na"),
			"invalid memeory access");
		
		var message =
			join("invalid memory access: ", baseAddress, " + ", length,
				" = ", topRequestedAddress,
				" must be < ", topAllowedAddress);
		
		krnTrace(
			join("PID: ",
				(krnHasCurrentPCB() ? krnGetCurrentPCB().pid() : "na"),
				": ", message),
			"MM");
		
		throw new Error(message);
	}
	
	this.clear = function()
	{
		memoryAccessor.clear();
		
		this.init();
	}
	
	this.init();
}
