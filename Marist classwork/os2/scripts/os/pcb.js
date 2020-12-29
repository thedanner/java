function PCB(myPid, myPriority)
{
	// Variables
	var pid = myPid;
	var priority;
	
	// Registers
	// General purpose
	var acc;
	var x;
	var y;
	
	// Status and others
	var ir; // Instruction Register
	var pc; // Program Counter
	
	var c; // Carry
	var z; // Zero
	var b; // Break
	var i; // Interrupt
	
	var base; // Memory access base
	var limit; // Memory access limit
	
	// Methods
	
	this.init = function ()
	{
		this.setPriority(myPriority);
		
		// Set all registers to a default.
		this.setAcc(0);
		this.setX(0);
		this.setY(0);
		
		this.setIR(0);
		this.setPC(0);
		
		this.setC(0);
		this.setZ(0);
		this.setB(0);
		this.setI(0);
		
		this.setBase(0);
		this.setLimit(0);
	}
	
	this.pid = function ()
	{
		return pid;
	}
	
	this.priority = function ()
	{
		return priority;
	}
	
	this.setPriority = function(newPriority)
	{
		if (undefined === newPriority) 
		{
			newPriority = SCHEDULER_DEFAULT_PRIORITY;
		}
		
		if ("number" !== typeof newPriority)
		{
			error("PCB.setPriority",
				"priority must be a number");
		}
		
		if (newPriority < SCHEDULER_LOWEST_PRIORITY ||
			newPriority > SCHEDULER_HIGHEST_PRIORITY)
		{
			error("PCB.setPriority",
				"priority must be in: ",
					rangeToString(
						"[]",
						SCHEDULER_LOWEST_PRIORITY,
						SCHEDULER_HIGHEST_PRIORITY),
				", got: ", newPriority);
		}
		
		priority = newPriority;
	}
	
	// Accumulator
	this.setAcc = function (newAcc)
	{
		acc = _CPU.validateValue(newAcc, MEMORY_SIZE_OF_WORD, "PCB.setAcc");
	}
	
	this.getAcc = function ()
	{
		return acc;
	}
	
	// X register
	this.setX = function (newX)
	{
		x = _CPU.validateValue(newX, MEMORY_SIZE_OF_WORD, "PCB.setX");
	}
	
	this.getX = function ()
	{
		return x;
	}
	
	// Y register
	this.setY = function (newY)
	{
		y = _CPU.validateValue(newY, MEMORY_SIZE_OF_WORD, "PCB.setY");
	}
	
	this.getY = function ()
	{
		return y;
	}
	
	// IR register
	this.setIR = function (newIR)
	{
		ir = _CPU.validateValue(newIR, MEMORY_SIZE_OF_WORD, "PCB.setIR");
	}
	
	this.getIR = function ()
	{
		return ir;
	}
	
	// PC register
	this.setPC = function (newPC)
	{
		pc = _CPU.validateValue(newPC, MEMORY_WORD_COUNT, "PCB.setPC");
	}
	
	this.getPC = function ()
	{
		return pc;
	}
	
	// Carry register
	this.setC = function (newC)
	{
		c = _CPU.validateValue(newC, 2, "PCB.setC");
	}
	
	this.getC = function ()
	{
		return c;
	}
	
	// Zero register
	this.setZ = function (newZ)
	{
		z = _CPU.validateValue(newZ, 2, "PCB.setZ");
	}
	
	this.getZ = function ()
	{
		return z;
	}
	
	// Break register
	this.setB = function (newB)
	{
		b = _CPU.validateValue(newB, 2, "PCB.setB");
	}
	
	this.getB = function ()
	{
		return b;
	}
	
	// Interrupt register
	this.setI = function (newI)
	{
		i = _CPU.validateValue(newI, 2, "PCB.setI");
	}
	
	this.getI = function ()
	{
		return i;
	}
	
	// Base register
	this.setBase = function (newBase)
	{
		base = _CPU.validateValue(newBase, MEMORY_WORD_COUNT, "PCB.setBase");
	}
	
	this.getBase = function ()
	{
		return base;
	}
	
	// Limit register
	this.setLimit = function (newLimit)
	{
		limit = _CPU.validateValue(newLimit, MEMORY_SIZE_OF_WORD, "PCB.setLimit");
	}
	
	this.getLimit = function ()
	{
		return limit;
	}
	
	// Utility methods.
	this.readFromCPU = function (cpu)
	{
		acc = cpu.getAcc();
		x = cpu.getX();
		y = cpu.getY();
		
		ir = cpu.getIR();
		pc = cpu.getPC();
		
		c = cpu.getC();
		z = cpu.getZ();
		b = cpu.getB();
		i = cpu.getI();
		
		base = cpu.getBase();
		limit = cpu.getLimit();
	}
	
	this.writeToCPU = function (cpu)
	{
		cpu.setAcc(acc);
		cpu.setX(x);
		cpu.setY(y);
		
		cpu.setIR(ir);
		cpu.setPC(pc);
		
		cpu.setC(c);
		cpu.setZ(z);
		cpu.setB(b);
		cpu.setI(i);
		
		cpu.setBase(base);
		cpu.setLimit(limit);
	}
	
	this.init();
}
