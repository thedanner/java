/* ----------------------------------
   DeviceDriver_Keyboard.js
   
   Requires deviceDriver.js
   
   The Kernel Keyboard Device Driver.
   ---------------------------------- */

DeviceDriver_Keyboard.prototype = new DeviceDriver;  // "Inherit" from prototype DeviceDriver in deviceDriver.js.

function DeviceDriver_Keyboard()                     // Add or override specific attributes and method pointers.
{
    // "subclass"-specific attributes.
	
    // Override the base method pointers.
    this.driverEntry = function ()
	{
	    // Initialization routine for this, the kernel-mode Keyboard Device Driver.
	    this.status = "loaded";
		
	    // More?
	}
	
    this.isr = function (params)
	{
		var which = params["which"];
		var keyCode = params["keyCode"];
	    var isShifted = params["shifted"];
		
	    krnTrace(
			join(
				"Which:", which,
				"; key code:", keyCode,
				"; shifted:", isShifted));
		
	    var chr = String.fromCharCode(which);
		
	    _KernelInputQueue.enqueue(chr);
		
		// Ban all unicode, mwahaha!
		if (which > 127)
		{
			krnTrapError(
				"drivers/keyboard.isr(): unknown key code: " + keyCode);
		}
	}
}
