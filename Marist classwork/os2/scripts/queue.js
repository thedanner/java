/* ------------
   Queue.js
   
   A simple Queue, which is really just a dressed-up Javascript Array.
   See the Javascript Array documentation at http://www.w3schools.com/jsref/jsref_obj_array.asp .
   Look at the push and shift methods.
   
   ------------ */
   
function Queue()
{
    // Properties
    var queue = [];
	
    // Methods
	this.size = function ()
	{
	    return queue.length;
	}
	
	this.isEmpty = function ()
	{
		return (0 == this.size());
	}
	
	this.enqueue = function (element)
	{
	    queue.push(element);
	}
	
    this.dequeue = function ()
	{
	    if (!this.isEmpty())
		{
			return queue.shift();
		}
		else
		{
			throw new Error("dequeue: queue is empty");
		}
	}
	
	this.peek = function ()
	{
		if (!this.isEmpty())
		{
			return queue[0];
		}
		else
		{
			throw new Error("peek: queue is empty");
		}
	}
	
	this.contains = function(element)
	{
		return queue.contains(element);
	}
	
	this.remove = function(element)
	{
		var index = queue.indexOf(element);
		
		if (index >= 0)
		{
			queue.splice(index, 1);
			
			return element;
		}
		
		return null;
	}
	
	this.toString = function ()
	{
		return join("[", queue.join("],["), "]");
	}
	
	// Copies the internal data structure so it can be iterated over "safely".
	this.toArray = function ()
	{
		var newArray = new Array(this.size());
		
		for (var i = 0; i < newArray.length; i++)
		{
			newArray[i] = queue[i];
		}
		
		return newArray;
	}
}
