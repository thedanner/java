/* ------------  
   globals.js

   Global CONSTANTS and _Variables.
   
   This code references page numbers in the text book: 
   Operating System Concepts 8th editiion by Silberschatz, Galvin, and Gagne.  ISBN 978-0-470-12872-5
   ------------ */

//
// Global Constants
//

// I was considering going the route of "dOS" for Danner's OS,
// or something to that effect, but the (MS-)DOS joke was
// already taken in a much more comical way.
// How many people "borrowed" GLaDOS as their name?
const APP_NAME = "mAd.OS";
const APP_VERSION = "0.09"

var OS_CLOCK = 0;       // Page 23.

var TIMER_IRQ = 0;   // Page 23.
var KEYBOARD_IRQ = 1;
var HARD_DISK_DRIVE_IRQ = 2;
var FILE_SYSTEM_IRQ      = 3;

var CLOCK_INTERVAL = 1000;   // in ms, or microseconds, so 1000 = 1 second.

var CANVAS = null;              // Initialized in simInit().
var DRAWING_CONTEXT = null;     // Initialized in simInit().
var DEFAULT_FONT = "sans";      // Ignored, just a place-holder in this version.
var DEFAULT_FONT_SIZE = 13;     
var FONT_HEIGHT_MARGIN = 4;     // Additional space added to font size when advancing a line.

//
// Global Variables
//
var _MainMemory = null;
var _MA = null;
var _MM = null;

var _CPU = null;

var _Trace = true;
var _HardwareClockId = -1;

var _KernelBuffers = null;
var _KernelInputQueue = null;
var _Console = null;
var _StdIn = null;
var _StdOut = null;
var _Shell = null;

var _SarcasticMode = false;

var _StatusMessage = null;

//
// Global Device Driver Objects
//
var krnKeyboardDriver = null;
var krnDiskDriveDeviceDriver = null;
var krnFileSystemDriver = null;
