function CPU (memoryManager)
{
	// Variables
	var mm = memoryManager;
	
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
	var i; // Break
	
	var base; // Memory access base
	var limit; // Memory access limit
	
	var clockTimer;
	
	var singleStepMode;
	var doSingleStep;
	
	// Methods
	this.init = function ()
	{
		this.setAcc(0);
		this.setX(0);
		this.setY(0);
		
		this.setIR(0);
		this.setPC(0);
		
		this.setC(0)
		this.setZ(0);
		this.setB(0);
		this.setI(0);
		
		this.setBase(0);
		this.setLimit(0);
		
		clockTimer = 0;
		
		singleStepMode = false;
		doSingleStep = false;
	}
	
	// Accumulator
	this.setAcc = function (newAcc)
	{
		acc = this.validateValue(newAcc, MEMORY_SIZE_OF_WORD, "CPU.setAcc");
	}
	
	this.getAcc = function ()
	{
		return acc;
	}
	
	// X register
	this.setX = function (newX)
	{
		x = this.validateValue(newX, MEMORY_SIZE_OF_WORD, "CPU.setX");
	}
	
	this.getX = function ()
	{
		return x;
	}
	
	// Y register
	this.setY = function (newY)
	{
		y = this.validateValue(newY, MEMORY_SIZE_OF_WORD, "CPU.setY");
	}
	
	this.getY = function ()
	{
		return y;
	}
	
	// IR register
	this.setIR = function (newIR)
	{
		ir = this.validateValue(newIR, MEMORY_SIZE_OF_WORD, "CPU.setIR");
	}
	
	this.getIR = function ()
	{
		return ir;
	}
	
	// PC register
	this.setPC = function (newPC)
	{
		pc = this.validateValue(newPC, MEMORY_WORD_COUNT, "CPU.setPC");
	}
	
	this.adjustPC = function (offset)
	{
		this.setPC(pc + offset);
	}
	
	this.getPC = function ()
	{
		return pc;
	}
	
	// Carry register
	this.setC = function (newC)
	{
		c = this.validateValue(newC, 2, "CPU.setC");
	}
	
	this.getC = function ()
	{
		return c;
	}
	
	// Zero register
	this.setZ = function (newZ)
	{
		z = this.validateValue(newZ, 2, "CPU.setZ");
	}
	
	this.getZ = function ()
	{
		return z;
	}
	
	// Break register
	this.setB = function (newB)
	{
		b = this.validateValue(newB, 2, "CPU.setB");
	}
	
	this.getB = function ()
	{
		return b;
	}
	
	// Interrupt register
	this.setI = function (newI)
	{
		i = this.validateValue(newI, 2, "CPU.setI");
	}
	
	this.getI = function ()
	{
		return i;
	}
	
	// Base register
	this.setBase = function (newBase)
	{
		base = this.validateValue(newBase, MEMORY_WORD_COUNT, "CPU.setBase");
	}
	
	this.getBase = function ()
	{
		return base;
	}
	
	// Limit register
	this.setLimit = function (newLimit)
	{
		limit = this.validateValue(newLimit, MEMORY_SIZE_OF_WORD, "CPU.setLimit");
	}
	
	this.getLimit = function ()
	{
		return limit;
	}
	
	// End registers
	
	// Timer ticks
	this.getTimerTicks = function ()
	{
		return clockTimer;
	}
	
	this.timerTick = function ()
	{
		clockTimer ++;
		
		return clockTimer;
	}
	
	this.singleStep = function()
	{
		singleStepMode = true;
		doSingleStep = true;
	}
	
	this.isSingleStepMode = function()
	{
		return singleStepMode;
	}
	
	this.resume = function ()
	{
		singleStepMode = false;
		doSingleStep = false;
	}
	
	this.doCycle = function ()
	{
		if (!singleStepMode ||
			(singleStepMode && doSingleStep)) 
		{
			this.runCycle();
			
			doSingleStep = false;
		}
		
		simUpdateDebug();
	}
	
	this.runCycle = function ()
	{
		krnSetMode(Mode.KERNEL);
		
		if (this.getB()) 
		{
			krnTrace("B flag set, terminating current PCB", "CPU");
			
			krnEnqueueSoftwareInterrupt(KRN_SW_INT_END_PROCESS, krnGetCurrentPCB().pid(), "normal termination");
			
			this.setB(0);
		}
		else if (krnHasUnservicedSoftwareInterrupts()) 
		{
			krnTrace("servicing software interrupt", "CPU");
			
			// kernel mode
			krnHandleSoftwareInterrupt();
		}
		else if (krnHasCurrentPCB()) 
		{
			krnTrace("entering USER mode to execute program", "CPU");
			
			krnSetMode(Mode.USER);
			
			// user mode
			this.executeUserProgram();
			
			krnTimeSliceCounterTick();
			
			krnSetMode(Mode.KERNEL);
			
			// Check for time slice.
			krnCheckTimeSlice();
		}
		
		this.timerTick();
	}
	
	this.executeUserProgram = function ()
	{
		// CYCLE !!!
		
		// 1. Fetch
		this.setIR(mm.getWord(pc));
		
		// 2. Increment
		this.adjustPC(1);
		
		// 3. Decode
		cpuInstruction = this.decodeOpcode();
		
		// 4. Execute
		execute(this, cpuInstruction);
	}
	
	this.decodeOpcode = function ()
	{
		var opcode = this.getIR();
		
		krnTrace(join("opcode fetched: 0x", opcode.toString(16)), "CPU");
		
		var value = this.validateValue(
				opcode, MEMORY_WORD_COUNT, "CPU.decodeOpcode");
		
		return IntructionImplementations[value];
	}
	
	function execute(cpu, cpuInstruction)
	{
		if (cpuInstruction)
		{
			cpuInstruction.functionPtr(cpu, cpuInstruction);
		}
		else
		{
			krnTrace(
				"terminating current program: invalid opcode: ",
				"CPU");
			
			krnEnqueueSoftwareInterrupt(
				KRN_SW_INT_END_PROCESS,
				krnGetCurrentPCB().pid(), "invalid opcode");
		}
	}
	
	/*
	These are the instructions our OS/CPU needs to implement.
	
	iP2:
		Load the accumulator with a constant
			A9		LDA		LDA #$07		A9 07
		Load the accumulator from memory
			AD 		LDA 	LDA $0010 		AD 10 00
		Store the accumulator in memory
			8D 		STA 	STA $0010 		8D 10 00
		Add with carry
			6D 		ADC		ADC $0010		6D 10 00
			(Adds contents of an address to the contents
			of the accumulator and keep result in accuculator)
		Load the X register with a constant
			A2		LDX		LDX #$01		A2 01
		Load the X register from memory
			AE		LDX		LDX $0010		AE 10 00
		Load the Y register with a constant
			A0		LDY		LDY #$04		A0 04
		Load the Y register from memory
			AC		LDY		LDY $0010		AC 10 00
		No Operation
			EA		NOP		EA				EA
		Break (which is really a system call)
			00		BRK		00				00
		System Call
			FF		SYS		FF				SYS
	
	iP3:
		Compare a byte in memory to the X reg
			EC		CPX		EC $0010		EC 10 00
			Sets the Z (zero) flag if equal
		Branch X bytes if Z flag = 0
			D0		BNE		D0 EF			F0 EF
		Increment the value of a byte
			EE		INC		EE $0021		EE 21 00
	*/
	
	var IntructionImplementations = {
		// LDA const
		0xA9 : new CpuInstruction(
			2,
			function (cpu, instruction)
			{
				// argument: constant
				var value = mm.getWord(pc, 1);
				
				instruction.adjustPC(cpu);
				
				cpu.setAcc(value);
			}),
		
		// LDA mem
		0xAD : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc);
				
				instruction.adjustPC(cpu);
				
				cpu.setAcc(mm.getWord(address));
			}),
		
		// STA mem
		0x8D : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc);
				
				instruction.adjustPC(cpu);
				
				mm.setWord(address, cpu.getAcc());
			}),
		
		// ADC mem
		0x6D : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc);
				
				instruction.adjustPC(cpu);
				
				var value = mm.getWord(address, 1);
				
				var newValue = acc + value;
				
				if (newValue >= MEMORY_SIZE_OF_WORD)
				{
					this.setC(true);
				}
				
				newValue %= MEMORY_SIZE_OF_WORD;
				
				cpu.setAcc(newValue);
			}),
		
		// LDX const
		0xA2 : new CpuInstruction(
			2,
			function (cpu, instruction)
			{
				// argument: const
				
				var value = mm.getWord(pc, 1);
				
				instruction.adjustPC(cpu);
				
				cpu.setX(value);
			}),
		
		// LDX mem
		0xAE : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc, 1);
				
				instruction.adjustPC(cpu);
				
				value = mm.getWord(address, 1);
				
				cpu.setX(value);
			}),
		
		// LDY const
		0xA0 : new CpuInstruction(
			2,
			function (cpu, instruction)
			{
				// argument: const
				
				var value = mm.getWord(pc, 1);
				
				instruction.adjustPC(cpu);
				
				cpu.setY(value);
			}),
		
		// LDY mem
		0xAC : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc);
				
				instruction.adjustPC(cpu);
				
				var value = mm.getWord(address, 1);
				
				cpu.setY(value);
			}),
		
		// No-op
		0xEA : new CpuInstruction(
			1,
			function (cpu, instruction)
			{
				// argument: none
				
				// La la la... I'm doing nothing.  Forever and ever.
				// I'm just living my life, as simply as can be.
				// I hit the jackpot, and I didn't even pull a lever.
				// Because I've been told to...been told to be free!
				
				// I hope that I do not get stumped,
				// I hope this doesn't end without my pick.
				// A plesent sight it is not if I core dump,
				// When again my clock ticks.
				
				// ~
				
				instruction.adjustPC(cpu);
			}),
		
		// Break
		0x00 : new CpuInstruction(
			1,
			function (cpu, instruction)
			{
				// argument: none
				
				cpu.setB(true);
				
				instruction.adjustPC(cpu);
			}),
		
		// System call
		/*
		 * Contens of X:
		 *  #$01 - Print the integer stored in Y
		 *  #$02 - Print the 00-terminated string started at the address
		 *  		referenced in Y
		 */
		0xFF : new CpuInstruction(
			1,
			function (cpu, instruction)
			{
				// argument: none
				
				switch (cpu.getX())
				{
					// print integer stored in Y
					case 0x01:
						printY();
						break;
					
					// print null-terminated string starting at address in Y
					case 0x02:
						printYString();
						break;
				}
				
				instruction.adjustPC(cpu);
				
				function printY()
				{
					krnEnqueueSoftwareInterrupt(
							KRN_SW_INT_PRINT_INTEGER,
							cpu.getY()
						);
				}
				
				function printYString()
				{
					var nullReached = false;
					var characters = [];
					
					for (var i = cpu.getY(); ! nullReached; i++)
					{
						charCode = mm.getWord(i);
						
						if (0 == charCode)
						{
							nullReached = true;
						}
						else
						{
							characters.push(String.fromCharCode(charCode));
						}
					}
					
					var string = characters.join('');
					
					krnEnqueueSoftwareInterrupt(
							KRN_SW_INT_PRINT_STRING,
							string
						);
				}
			}),
		
		// CPX
		0xEC : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc);
				
				instruction.adjustPC(cpu);
				
				value = mm.getWord(address);
				
				// set ZERO to:
				//  1 (true)  if X and mem are the same,
				//  0 (false) otherwise.
				// UUUUUUUUUUUUUUUUUUUUUUUUUUUGGGGGGGGGGGGGGGGGGHHHHHHHHHHHHHHH
				// HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				cpu.setZ(cpu.getX() == value);
			}),
		
		// BNE - Branch
		0xD0: new CpuInstruction(
			2,
			function (cpu, instruction)
			{
				// argument: offset
				var offset =
					cpu.validateValue(mm.getWord(pc, 1),
							MEMORY_SIZE_OF_WORD,
							"instruction: BNE");
				
				instruction.adjustPC(cpu);
				
				// if ZERO is false -- not set...
				// compute the offset and branch.
				if (cpu.getZ() == 0)
				{
					// If bit 7 is set, the number is negative;
					// assuming 2's complement.
					// This is the first time I've ever used bitwise-and
					// in an actual program. Yay!
					
					// Unsigned:
					// 0-127 are unaffected,
					// 128 (0x80) is -128, and
					// 255 (0xFF) is -1.
					if ((offset & 0x80) != 0)
					{
						offset = -(0x100 - offset);
					}
					
					// Otherwise, leave offset alone; 0 <= offset <= 127.
					
					cpu.adjustPC(offset);
				}
			}),
		
		// INC
		0xEE : new CpuInstruction(
			3,
			function (cpu, instruction)
			{
				// argument: address
				
				var address = getAddressFromMemory(pc);
				
				var value = mm.getWord(address, 1);
				
				// Make sure we don't overflow;
				// wrap around if we increment past the
				// size of the register.
				value = (1 + value) % MEMORY_SIZE_OF_WORD;
				
				mm.setWord(address, value);
				
				instruction.adjustPC(cpu);
			}),
	}
	
	function CpuInstruction(mySize, functionPointer)
	{
		this.functionPtr = functionPointer;
		
		var size = mySize;
		
		this.size = function ()
		{
			return size;
		}
		
		this.adjustPC = function (cpu)
		{
			cpu.adjustPC(size - 1);
		}
	}
	
	function getAddressFromMemory(memoryAddress)
	{
		// Endian-ness.
		
		// Get both of the words,
		// mmke them into hex strings,
		// concat them, with the higher indexed word first,
		// and finally, parse that string back into an int,
		// and return.
		
		var addressLo = mm.getWord(    memoryAddress);
		var addressHi = mm.getWord(1 + memoryAddress);
		
		// Bitshift addressHi to the xx00 place in the address.
		addressHi << 0x8;
		
		var fullAddress = addressHi + addressLo;
		
		return fullAddress;
	}
	
	this.validateValue = function (value, registerSize, caller)
	{
		// If the register can only hold 2 states,
		// it's a flag, so allow booleans.
		if ((2 == registerSize) && ("boolean" === typeof(value)))
		{
			if (true === value)
			{
				value = 1;
			}
			else //if (false === value)
			{
				value = 0
			}
		}
		
		// Sure, redo the check to make sure nothing messed up above.
		if ("number" !== typeof(value))
		{
			error(
				caller,
				"value (", value, ") expected to be a number, was a: ",
				typeof(value) );
		}
		
		//TODO: negative numbers?
		// Make sure value is valid.
		if (value < 0 || value >= registerSize)
		{
			error(
				caller,
				"value must be in range ",
				rangeToString("[)", 0, registerSize), ": ",
				value );
		}
		
		return value;
	}
	
	// Call the constructor.
	this.init();
}
