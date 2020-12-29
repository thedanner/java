/* ------------  
   tty.js
   
   This class represents a *nix TTY-like character matrix which constains
   the characters that are displayed on the screen.  It represents a
   rectangular 2D array, with a default width and height that seem to
   work well for the canvas's current font ('i's are short, 'a's are
   good, but '-'s go off the screen.  After any change to the characters,
   the entire canvas is repainted.
   
   It also has a dedicated section for the "taskbar", which is
   currently at the bottom of the screen, taking up two rows,
   including a row of '-' to visually separate it.  This means
   that the matrix is effectively divided into two sections: a
   "user text" section, and the taskbar section (which includes
   the border).
   
   It keeps track of an internal cursor.  When printing text, the caller
   can opt to either use the cursor's values, or provide a coordinate to
   start at.  If no coordinates are given, the provided string is written,
   and the cursor is set to the next available position.  advanceLine()
   simply does a CR and LF-like action.  Character wrap is in effect in
   each case.
   
   When the display needs to scroll, it appends another row to the
   bottom of the "user text" section, effectively pushing the first
   row "off the top".  This is because the part of "user text" area
   that is displayed is like a viewport: only a certain range of rows
   have their contents displayed to the contents (that is, the last
   TTY_USER_TEXT_HEIGHT rows before the final task bar rows).  The
   history is saved, but becomes inaccessible.  Scrolling back is
   possible, but not currently implemented (I tried to have a
   scrollbar section off to the right, but the variable-width font
   made that look horrible, and it was much simpler to not do it).
   
   TODO:
   	-optimize the painting routine so it doesn't lag when we get near
   	 near the end bottom of the canvas and it has lots of text on it
   	 (the problem lies with the frequency of repaints, most of which
   	 are unnecessary).
   
   ------------ */

// Note: const isn't supported outside of mozilla-land, or IE
// at least doesn't support it (change to var for those cases),
// but we we don't care.
const TTY_WIDTH = 50;
const TTY_HEIGHT = 29;

const TTY_TASK_BAR_HEIGHT = 2;

const TTY_USER_TEXT_WIDTH = TTY_WIDTH;
const TTY_USER_TEXT_HEIGHT = TTY_HEIGHT - TTY_TASK_BAR_HEIGHT;

function TTY()
{
	this.cursorX = null;
	this.cursorY = null;
	
	this.characterMatrix = null;
	
	var canvas = new CanvasDisplay(this);
	
	this.init = function ()
	{
		this.cursorX = 0;
		this.cursorY = 0;
		
		characterMatrix = new Array(TTY_HEIGHT);
		
		for (var i = 0; i < TTY_HEIGHT; i++)
		{
			characterMatrix[i] = initializeRowArray();
		}
		
		canvas.repaint();
	}
	
	this.putText = function (text, xPos, yPos, allowWrap)
	{
		// validateCharacterPoint() returns true if the two specified values
		// represent a valid point, and false if not.
		// If it returns false (e.g. no point specified),
		// then we want to use the current cursor value.
		var usePositionParameters = validateCharacterPoint(xPos, yPos);
		
		// We'll be using X and Y for the drawing indices.
		// If no position was specified, these will essentially serve as
		// aliases for cursorX and cursorY.
		var x = null;
		var y = null;
		
		// If both an xPos and yPos are specified,
		// use that without modiying the cursor position.
		if (usePositionParameters)
		{
			x = xPos;
			y = yPos;
		}
		else
		{
			x = this.cursorX;
			y = this.cursorY;
			
			allowWrap = true;
		}
		
		// Write the text.
		
		keepWriting = true;
		
		for (var i = 0; i < text.length && keepWriting; i++)
		{
			characterMatrix[x][y] = text.charAt(i);
			
			// Increment the pointer to the next slot on the same line.
			y ++;
			
			// If we're past the right edge of the matrix.
			if (y >= TTY_USER_TEXT_WIDTH)
			{
				if (allowWrap)
				{
					// Advance to the next line.
					this.advanceLine();
					
					// advanceLine() changed the cursor position,
					// so get the new values.
					x = this.cursorX;
					y = this.cursorY;
				}
				// Stop if allowWrap is false, and we're writing
				// past the side of the matrix.
				else
				{
					keepWriting = false;
				}
			}
		}
		
		// If we were writing with the cursor's position
		// (and not the parameters), update the cursor.
		if (!usePositionParameters) // ^^ pretty cool how the parens lined up
		{
			this.cursorX = x;
			this.cursorY = y;
		}
		
		// TODO: optimize?
		canvas.repaint();
	}
	
	this.advanceLine = function ()
	{
		this.cursorX ++;
		this.cursorY = 0;
		
		if (this.cursorX >= characterMatrix.length - TTY_TASK_BAR_HEIGHT)
		{
			scrollUserTextArea();
		}
		
		// TODO: optimize?
		canvas.repaint();
	}
	
	this.backspace = function ()
	{
		this.cursorY --;
		
		if (this.cursorY < 0)
		{
			if (this.cursorX >= 0)
			{
				this.cursorX --;
				this.cursorY = TTY_USER_TEXT_WIDTH - 1;
			}
			else
			{
				this.cursorY = 0;
			}
		}
		
		characterMatrix[this.cursorX][this.cursorY] = null;
		
		// TODO: optimize?
		canvas.repaint();
	}
	
	this.updateTaskBarText = function (text)
	{
		var taskBarBorderIndex = characterMatrix.length - TTY_TASK_BAR_HEIGHT;
		
		// Draw border line, and clear previous message.
		for (var i = 0; i < TTY_USER_TEXT_WIDTH; i++)
		{
			characterMatrix[taskBarBorderIndex][i] = "-";
			characterMatrix[1 + taskBarBorderIndex][i] = null;
		}
		
		this.putText(text, (1 + taskBarBorderIndex), 0, false);
	}
	
	this.clearScreen = function ()
	{
		// Discard the current matrix and rebuild it.
		characterMatrix = null;
		
		this.init();
	}
	
	this.charAt = function (x, y)
	{
		return characterMatrix[x][y];
	}
	
	this.rowAt = function (rowIndex)
	{
		return characterMatrix[rowIndex];
	}
	
	// Private functions.
	
	/*
	 * Given (up to) two arguemts that represent a point, returns:
	 * true if the point represents a valid point in the TTY's
	 *   character matrix,
	 * false if neither of the arguments are numbers.
	 * An exception is thrown if only one point was specified.
	 */
	function validateCharacterPoint (xPos, yPos)
	{
		// JavaScript can die in a fire (next to PHP) for saying
		// that (0 == false).  This breaks code like  "if (xPos)"
		// when xPos is 0.  So, in order to determine if a parameter
		// is a number, we explicitly compare its type.
		
		var hasXPos = ("number" === typeof(xPos));
		var hasYPos = ("number" === typeof(yPos));
		
		// Check to see if only one parameter was given.
		// If so, throw an exception.
		if (( hasXPos && !hasYPos) ||
			(!hasXPos &&  hasYPos))
		{
			throw new Error(join(
				"only one dimension specified: ",
				(hasXPos ? "x" : "y") ));
		}
		
		// Neither argument was specified, so no point given.
		if (!hasXPos && !hasYPos)
		{
			return false;
		}
		
		// Ensure the X coord is in bounds.
		if (hasXPos)
		{
			var xLo = getFirstDisplayedUserRowIndex();
			var xHi = characterMatrix.length;
			
			if (xPos < xLo || xPos >= xHi)
			{
				throw new Error(join(
					"x position must be within [",
					xLo, ",", (xHi - 1), "]: ", xPos ));
			}
		}
		
		// Ensure the Y coord is in bounds.
		if (hasYPos)
		{
			var yLo = 0;
			var yHi = TTY_WIDTH;
			
			if (yPos < yLo || yPos >= yHi)
			{
				throw new Error(join(
					"y position must be within [",
					yLo, ",", (yHi - 1), "]: ", yPos ));
			}
		}
		
		// Both points were provided and are in bounds.
		return true;
	}
	
	/*
	 * Gets the index of the top of the user text "viewport".
	 */
	function getFirstDisplayedUserRowIndex ()
	{
		return  characterMatrix.length -
				TTY_TASK_BAR_HEIGHT -
				TTY_USER_TEXT_HEIGHT;
	}
	
	/*
	 * Gets the index of the bottom of the user text "viewport".
	 */
	function getLastDisplayedYserRowIndex ()
	{
		return  characterMatrix.length -
				TTY_TASK_BAR_HEIGHT;
	}
	
	function scrollUserTextArea ()
	{
		// We're going to add a new row to the bottom of the user text area.
		var insertIndex = characterMatrix.length - TTY_TASK_BAR_HEIGHT;
		
		var newArray = initializeRowArray();
		
		// At the given index, remove 0 rows, and insert the new row.
		characterMatrix.splice(insertIndex, 0, newArray);
	}
	
	/*
	 * Creates a new array, and initializes it with TTY_WIDTH null entries.
	 */
	function initializeRowArray ()
	{
		var rowArray = new Array(TTY_WIDTH);
		
		return rowArray;
	}
	
	/*
	 * Clear the canvas, and redraw the character matrix.
	 */
	function repaintCanvas ()
	{
		canvas.clear();
		canvas.drawText();
	}
	
	/*
	 * An inner "class" that actually paints the TTY's contents to the canvas.
	 */
	function CanvasDisplay(myTTY)
	{
		// Properties
		var tty = myTTY;
		
		var currentFont = DEFAULT_FONT;
		var currentFontSize = DEFAULT_FONT_SIZE;
		var currentXPosition = 0;
		var currentYPosition = DEFAULT_FONT_SIZE;
		
		// Public functions
		
		this.init = function ()
		{
			this.repaint();
		}
		
		/*
		 * Wipe the canvas clean, and repaint the character matrix.
		 */
		this.repaint = function ()
		{
			clearScreen();
			resetXY();
			
			paintText();
		}
		
		function resetXY ()
		{
			currentXPosition = 0;
		    currentYPosition = currentFontSize;
		}
		
		/*
		 * Read the data in the associated TTY's character matrix,
		 * and paint the string that result from joining each row's
		 * characters to a line, with each column representing a line.
		 * Only paint the area in the "viewable range"; see TTY().
		 */
		function paintText ()
		{
			var baseRowIndex = getFirstDisplayedUserRowIndex();
			
			for (var i = 0; i < TTY_HEIGHT; i++)
			{
				var currentRow = tty.rowAt(baseRowIndex + i);
				
				var newRowValues = new Array(currentRow.length);
				
				for (var j = 0; j < currentRow.length; j++)
				{
					var value = currentRow[j];
					
					// If a character hasn't been set for this
					// specific cell, paint a space instead.
					if (null === value || undefined === value)
					{
						value = " ";
					}
					
					newRowValues[j] = value;
				}
				
				paintString(newRowValues.join(''));
				
				advanceLine();
			}
		}
		
		/*
		 * Actually paint the given string onto the canvas.
		 */
		function paintString(text)
		{
			if (text.length > 0)
			{
				// Draw the text at the current X and Y coordinates.
				DRAWING_CONTEXT.drawText(currentFont, currentFontSize, currentXPosition, currentYPosition, text);
				
				// Move the current X position.
				var offset = DRAWING_CONTEXT.measureText(currentFont, currentFontSize, text);
				
				currentXPosition += offset;
			}
		}
		
		/*
		 * Reset X to 0, and advance the Y to the next "line",
		 * based on the font's size and height margin.
		 */
		function advanceLine ()
		{
		    currentXPosition = 0;
		    currentYPosition += DEFAULT_FONT_SIZE + FONT_HEIGHT_MARGIN;
		}
		
		/*
		 * Wipe the canvas clean.
		 */
		function clearScreen ()
		{
			DRAWING_CONTEXT.clearRect(0, 0, CANVAS.width, CANVAS.height);
		}
	}
}
