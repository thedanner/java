function MemoryAccessor(physicalMemory)
{
	var memory = physicalMemory;
	
	this.get = function (address, length)
	{
		return memory.get(address, length);
	}
	
	this.set = function (address, bytes)
	{
		return memory.set(address, bytes);
	}
	
	this.clear = function()
	{
		physicalMemory.init();
	}
}
