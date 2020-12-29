const FS_FIRST_TABLE_TRACK = 1;
const FS_FIRST_DATA_TRACK = 2;

const FS_FREE_BLOCK_STATE = "0000";

const FS_MAX_ENTRIES_PER_BLOCK = 4;
const FS_FILE_ENTRY_SIZE = 8;
const FS_MAX_FILENAME_LENGTH = 5;

const FS_DATA_BLOCK_STATE_LENGTH = 4;
const FS_DATA_BLOCK_DATA_LENGTH =
		HDD_BLOCK_SIZE - FS_DATA_BLOCK_STATE_LENGTH;

const FS_STATE_FLAG_BLOCK_FREE = 0;
const FS_STATE_FLAG_NOT_EOF = 1
const FS_STATE_FLAG_EOF = 3;

Driver_FileSystem.prototype = new DeviceDriver;

function Driver_FileSystem(physicalDrive)
{
	var drive = physicalDrive;
	
	var fileTables;
	
	
    this.driverEntry = function ()
	{
		loadFileTables();
		
		
	    this.status = "loaded";
	}
	
    this.isr = function (params)
	{
	    
	}
	
	function loadFileTables()
	{
		var fileTableCount =
			(FS_FIRST_DATA_TRACK - FS_FIRST_TABLE_TRACK) *
			HDD_SECTOR_COUNT * HDD_BLOCK_COUNT;
		
		fileTables = new Array(fileTableCount);
		
		var fileTableIndex = 0;
		
		for (var track = FS_FIRST_TABLE_TRACK;
				track < FS_FIRST_DATA_TRACK;
				track++) 
		{
			for (var sector = 0; sector < HDD_SECTOR_COUNT; sector++) 
			{
				for (var block = 0; block < HDD_BLOCK_COUNT; block++) 
				{
					var location = toLocation(track, sector, block)
					
					fileTables[fileTableIndex] =
						new FileEntryBlock(location);
					
					fileTableIndex++;
				}
			}
		}
	}
	
	// File operations
	
	this.createFile = function(name)
	{
		if (0 == name.length) 
		{
			throw new Error("filenames cannot be empty");
		}
		
		if (name.length > FS_MAX_FILENAME_LENGTH) 
		{
			throw new Error(join(
				"filename too long: ", name.length,
				", max of ", FS_MAX_FILENAME_LENGTH));
				
		}
		
		if (findLocationOfFile(name))
		{
			throw new Error(
				join("a file with name '", name, "' already exists"));
		}
		
		var location = findFreeDataBlock();
		
		if (location)
		{
			var table = findFileTableWithFreeSpace();
			
			if (table) 
			{
				table.addEntry(name, location);
			}
			else 
			{
				throw new Error(
					join("no room in file entry table"));
			}
			
			writeBlocks(location, "");
			
			table.write();
			
			return true;
		}
		else
		{
			throw new Error("out of disk space");
		}
	}
	
	this.readFile = function(name, length)
	{
		var location = findLocationOfFile(name);
		
		if (location) 
		{
			if (undefined === length || length < 0) 
			{
				length = countCharacters(location);
			}
			
			return readBlocks(location, length);
		}
		else 
		{
			return null;
		}
	}
	
	this.writeFile = function (name, data)
	{
		var location = findLocationOfFile(name);
		
		if (location) 
		{
			writeBlocks(location, data);
			
			return true;
		}
		else 
		{
			return null;
		}
	}
	
	this.deleteFile = function (name)
	{
		var table = findTableWithFileEntry(name);
		
		if (table) 
		{
			freeBlocks(table.getLocationOfFile(name));
			
			table.removeEntry(name);
			
			table.write();
			
			return true;
		}
		else 
		{
			return null;
		}
	}
	
	this.getFilesize = function (name)
	{
		var location = findLocationOfFile(name);
		
		if (location) 
		{
			return countCharacters(location);
		}
		else 
		{
			return -1;
		}
	}
	
	this.format = function ()
	{
		for (var i = 0; i < fileTables.length; i++) 
		{
			var fileEntries = fileTables[i].getFileEntries();
			
			for (var j = 0; j < fileEntries.length; j++) 
			{
				var startBlock = fileEntries[j]['location'];
				
				freeBlocks(startBlock);
				
				fileTables[i].removeEntry(fileEntries[j]['filename']);
			}
			
			fileTables[i].write();
		}
		
		loadFileTables();
		
		return true;
	}
	
	function readBlocks(startLocation, length)
	{
		var data = drive.getBlock(
				startLocation['track'],
				startLocation['sector'],
				startLocation['block']);
		
		var state = extractDataBlockState(data);
		
		var nextBlock = getNextBlock(state);
		
		var nextDataString = [];
		
		// Get the next block's data, if it has any.
		if (nextBlock) 
		{
			var nextLength = length - FS_DATA_BLOCK_DATA_LENGTH;
			
			nextDataString = readBlocks(nextBlock, nextLength);
		}
		
		// Skip the "state" bytes.
		var data = drive.getBlock(
				startLocation['track'],
				startLocation['sector'],
				startLocation['block']
			).substring(FS_DATA_BLOCK_STATE_LENGTH);
		
		// When .join('') is called later, null
		// elements will essentially be excluded.
		var thisData = new Array(FS_DATA_BLOCK_DATA_LENGTH);
		
		for (var i = 0; i < Math.min(data.length, length); i++) 
		{
			thisData[i] = data.charAt(i) || ' ';
		}
		
		var thisDataString = thisData.join('');
		
		var allDataString = join(thisDataString, nextDataString);
		
		return allDataString;
	}
	
	function writeBlocks(startLocation, data)
	{
		var currentDataInBlock = drive.getBlock(
				startLocation['track'],
				startLocation['sector'],
				startLocation['block']);
		
		var currentState = extractDataBlockState(
				currentDataInBlock);
		
		var currentNextBlock = null;
		
		if (FS_STATE_FLAG_NOT_EOF == currentState[0])
		{
			currentNextBlock = toLocation(
					currentState[1],
					currentState[2],
					currentState[3]);
		}
		
		var needsAnotherBlock = data.length > FS_DATA_BLOCK_DATA_LENGTH;
		
		var dataToWrite =
				data.substr(0, FS_DATA_BLOCK_DATA_LENGTH);
		
		// Yes, substring and substr are different.
		var remainingData =
				data.substring(FS_DATA_BLOCK_DATA_LENGTH);
		
		// 4 cases:
		// --------
		// 1. We don't need another block for data,
		//    this block is EOF.
		//    - Write the data and be done.
		// 2. We don't need another block,
		//    this block is NOT EOF.
		//    - Write the data, call freeBlocks on the
		//      remaining blocks.
		// 3. We DO need another block,
		//    this block is NOT EOF.
		//    - Write first part of data here,
		//      call writeBlocks with next block
		//      and data to write truncated.
		// 4. We DO need another block, AND
		//    this block is EOF.
		//    - Write data, get a free block, call
		//      writeBlocks with that block and
		//      truncated data to write.
		
		var newBlockStateData = null;
		
		var nextBlock = null;
		
		if (needsAnotherBlock) 
		{
			if (currentNextBlock) 
			{
				nextBlock = currentNextBlock;
			}
			else 
			{
				nextBlock = findFreeDataBlock(startLocation);
			}
		}
		else 
		{
			if (currentNextBlock)
			{
				freeBlocks(currentNextBlock);
			}
		}
		
		
		if (nextBlock)
		{
			newBlockStateData = [
					FS_STATE_FLAG_NOT_EOF,
					nextBlock['track'],
					nextBlock['sector'],
					nextBlock['block']
				];
		}
		else
		{
			var length_tens = Math.floor(dataToWrite.length / 10 % 10);
			var length_ones = dataToWrite.length % 10;
			
			newBlockStateData = [
					FS_STATE_FLAG_EOF,
					0, 					// always 0 if EOF
					length_tens,
					length_ones
				];
		}
		
		var newData = new Array(FS_DATA_BLOCK_DATA_LENGTH);
		
		for (var i = 0; i < dataToWrite.length; i++)
		{
			newData[i] = dataToWrite.charAt(i);
		}
		
		var blockStateDataString = newBlockStateData.join('');
		var blockDataString = newData.join('');
		
		var newDataString = join(blockStateDataString, blockDataString);
		
		var setBlockResult =
			drive.setBlock(
				startLocation['track'],
				startLocation['sector'],
				startLocation['block'],
				newDataString);
		
		// Do this here so blocks that will be written to
		// don't incorreclty form a cycle and gte overwritten.
		if (needsAnotherBlock)
		{
			writeBlocks(nextBlock, remainingData);
		}
		
		return setBlockResult;
	}
	
	function freeBlocks(startLocation)
	{
		var data = drive.getBlock(
				startLocation['track'],
				startLocation['sector'],
				startLocation['block']);
		
		var state = extractDataBlockState(data);
		
		var nextBlock = getNextBlock(state);
		
		drive.setBlock(
			startLocation['track'],
			startLocation['sector'],
			startLocation['block'],
			FS_FREE_BLOCK_STATE);
		
		if (nextBlock) 
		{
			freeBlocks(nextBlock);
		}
	}
	
	function countCharacters(startLocation)
	{
		var data = drive.getBlock(
				startLocation['track'],
				startLocation['sector'],
				startLocation['block']);
		
		var state = extractDataBlockState(data);
		
		var nextBlock = getNextBlock(state);
		
		if (nextBlock)
		{
			return (HDD_BLOCK_SIZE - FS_DATA_BLOCK_STATE_LENGTH) +
					countCharacters(nextBlock);
		}
		else
		{
			// 00xx, where xx is the size of actual bytes
			// in this block that are part of the file.
			var charCount = (state[2] * 10) + state[3];
			
			return charCount;
		}
	}
	
	function extractDataBlockState(data)
	{
		// Empty or corrupted data, assume it's a free block.
		if (data.length < FS_DATA_BLOCK_STATE_LENGTH) 
		{
			return [0,0,0,0];
		}
		
		var stateString = data.substr(0, FS_DATA_BLOCK_STATE_LENGTH);
		
		var stateArray = new Array(FS_DATA_BLOCK_STATE_LENGTH);
		
		for (var i = 0; i < stateArray.length; i++) 
		{
			stateArray[i] = parseInt(stateString.charAt(i));
		}
		
		return stateArray;
	}
	
	function getNextBlock(stateArray)
	{
		if (FS_STATE_FLAG_BLOCK_FREE == stateArray[0] ||
			FS_STATE_FLAG_EOF == stateArray[0])
		{
			return null;
		}
		else if (1 == stateArray[0])
		{
			return toLocation(
				stateArray[1], stateArray[2], stateArray[3]);
		}
	}
	
	function findFileTableWithFreeSpace()
	{
		for (var i = 0; i < fileTables.length; i++) 
		{
			if (fileTables[i].hasFreeEntrySpace())
			{
				return fileTables[i];
			}
		}
	}
	
	function findFreeDataBlock(exclude)
	{
		for (var track = FS_FIRST_DATA_TRACK;
				track < HDD_TRACK_COUNT;
				track++) 
		{
			for (var sector = 0; sector < HDD_SECTOR_COUNT; sector++) 
			{
				for (var block = 0; block < HDD_BLOCK_COUNT; block++) 
				{
					var location = toLocation(track, sector, block);
					
					// If a point to exclude is specified, compare
					// that before seeing if the data block is
					// free, and potentially 
					if ( !exclude ||
						(exclude && !locationsEqual(location, exclude)) )
					{
						if (isDataBlockFree(location))
						{
							return location;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	function findTableWithFileEntry(name)
	{
		for (var i = 0; i < fileTables.length; i++) 
		{
			if (fileTables[i].hasEntryForFile(name)) 
			{
				return fileTables[i];
			}
		}
		
		return null;
	}
	
	function findLocationOfFile(name)
	{
		var table = findTableWithFileEntry(name);
		
		return table ? table.getLocationOfFile(name) : null;
	}
	
	function isDataBlockFree(location)
	{
		var data = drive.getBlock(
			location['track'], location['sector'], location['block']);
		
		if (0 == data.length) 
		{
			return true;
		}
		
		if (0 === parseInt(data.substr(0, 1)))
		{
			return true;
		}
		
		return false;
	}
	
	
	function locationsEqual(a, b)
	{
		return  a['track']  == b['track']  &&
				a['sector'] == b['sector'] &&
				a['block']  == b['block'];
	}
	
	function toLocation(track, sector, block)
	{
		var location = {
				'track'  : track,
				'sector' : sector,
				'block'  : block
			};
		
		return location;
	}
	
	function stringToLocation(string)
	{
		var track  = parseInt(string.substr(0, 1));
		var sector = parseInt(string.substr(1, 1));
		var block  = parseInt(string.substr(2, 1));
		
		return toLocation(track, sector, block);
	}
	
	
	// FEB !!
	function FileEntryBlock(myLocation)
	{
		var location = myLocation;
		var entries;
		
		this.init = function ()
		{
			entries = [];
			
			if (location) 
			{
				this.read();
			}
		}
		
		this.read = function()
		{
			var data =
				drive.getBlock(
					location['track'],
					location['sector'],
					location['block']);
			
			var entryIndex = 0;
			
			while (data.length >= (1 + entryIndex) * FS_FILE_ENTRY_SIZE &&
					entryIndex < FS_MAX_ENTRIES_PER_BLOCK)
			{
				var filename = data.substr(0, FS_MAX_FILENAME_LENGTH);
				var locationInfo = data.substr(FS_MAX_FILENAME_LENGTH);
				
				var fileLocation = stringToLocation(locationInfo);
				
				this.addEntry(filename, fileLocation);
				
				entryIndex ++;
			}
		}
		
		this.write = function()
		{
			var blockData = new Array(HDD_BLOCK_SIZE);
			
			for (var i = 0; i < entries.length; i++) 
			{
				var iBase = i * FS_FILE_ENTRY_SIZE;
				
				// Copy the file name.
				for (var nameIndex = 0;
						nameIndex < FS_MAX_FILENAME_LENGTH;
						nameIndex++) 
				{
					// Access chars in a string by index.
					// If there is no char defined, default to ' '
					blockData[iBase + nameIndex] =
						entries[i]['filename'].charAt(nameIndex) || ' ';
				}
				
				blockData[iBase + FS_MAX_FILENAME_LENGTH    ] =
					entries[i]['location']['track'];
				
				blockData[iBase + FS_MAX_FILENAME_LENGTH + 1] =
					entries[i]['location']['sector'];
				
				blockData[iBase + FS_MAX_FILENAME_LENGTH + 2] =
					entries[i]['location']['block'];
			}
			
			// Any extra, unwritten data will get truncated in the array,
			// so we only have as much data as we care about.
			drive.setBlock(
				location['track'], location['sector'], location['block'],
				blockData.join(''));
		}
		
		this.addEntry = function(filename, location)
		{
			var entry = {
					'filename' : filename.trim(),
					'location' : location
				};
			
			entries.push(entry);
		}
		
		this.removeEntry = function(filename)
		{
			var index = getIndexOfFilename(filename);
			
			if (index >= 0) 
			{
				entries.splice(index, 1);
				
				return true;
			}
			
			return false;
		}
		
		// Deep copy.
		this.getFileEntries = function()
		{
			var copiedEntries = new Array(entries.length);
			
			for (var i = 0; i < copiedEntries.length; i++)
			{
				copiedEntries[i] = {};
				
				for (var key in entries[i]) 
				{
					copiedEntries[i][key] = entries[i][key];
				}
			}
			
			return copiedEntries;
		}
		
		this.getLocationOfFile = function(filename)
		{
			var index = getIndexOfFilename(filename);
			
			if (index >= 0) 
			{
				return entries[index]['location'];
			}
			
			return false;
		}
		
		this.hasEntryForFile = function(filename)
		{
			var index = getIndexOfFilename(filename);
			
			return index >= 0
		}
		
		this.getFileEntryCount = function ()
		{
			return entries.length;
		}
		
		this.hasFreeEntrySpace = function()
		{
			return this.getFileEntryCount() < FS_MAX_ENTRIES_PER_BLOCK;
		}
		
		this.setLocation = function(myLocation)
		{
			location = myLocation;
		}
		
		this.getLocation = function()
		{
			return location;
		}
		
		this.hasLocaion = function ()
		{
			return (undefined !== loaction);
		}
		
		function getIndexOfFilename(filename)
		{
			var index = -1;
			
			filename = filename.trim();
			
			for (var i = 0; i < entries.length && index < 0; i++)
			{
				if (entries[i]['filename'] == filename) 
				{
					index = i;
				}
			}
			
			return index;
		}
		
		this.init();
	}
}
