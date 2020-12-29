DeviceDriver_DiskDrive.prototype = new DeviceDriver;

const DDD_USERNAME = "mangiarelli";
const DDD_BASE_URL = "http://www.3nfconsulting.com/msdos/";
const DDD_GET_ACTION = "GetBlock.aspx";
const DDD_SET_ACTION = "SetBlock.aspx";

const HDD_LOCATION_SEPARATOR = "~";
const HDD_DIMENSION_SEPARATOR = "_"

const HDD_COOKIE_NAME = "b";
const HDD_COOKIE_OPTIONS = {expires: 100};

const HDD_TRACK_COUNT = 4;
const HDD_SECTOR_COUNT = 8;
const HDD_BLOCK_COUNT = 8;
const HDD_BLOCK_SIZE = 32;


function DeviceDriver_DiskDrive()
{
	var matrix = null;
	
	var dataLocationList = null;
	
	this.driverEntry = function ()
	{
		this.init();
		
		this.status = "loaded";
	}
	
	this.init = function ()
	{
		matrix = new Array(HDD_TRACK_COUNT);
		
		for (var trackIndex = 0;
				trackIndex < HDD_TRACK_COUNT;
				trackIndex++) 
		{
			matrix[trackIndex] = new Array(HDD_SECTOR_COUNT);
			var track = matrix[trackIndex];
			
			for (var sectorIndex = 0;
					sectorIndex < HDD_SECTOR_COUNT;
					sectorIndex++)
			{
				track[sectorIndex] = new Array(HDD_BLOCK_COUNT);
				var sector = track[sectorIndex];
				
				for (var blockIndex = 0;
						blockIndex < HDD_BLOCK_COUNT;
						blockIndex++) 
				{
					// Initialize an empty block.
					sector[blockIndex] = null;
				}
			}
		}
		
		
		// Now, grab the data from the remote host.
		var dataLocationCookieData = $.cookie(HDD_COOKIE_NAME) || '';
		
		if (0 == dataLocationCookieData.length) 
		{
			dataLocationList = [];
		}
		else 
		{
			dataLocationList =
				dataLocationCookieData.split(HDD_LOCATION_SEPARATOR);
		}
		
		for (var i = 0; i < dataLocationList.length; i++) 
		{
			var location = parseLocation(dataLocationList[i]);
			
			var track = location['track'];
			var sector = location['sector'];
			var block = location['block'];
			
			readFromDatabase(track, sector, block);
		}
	}
	
	this.isr = function (params)
	{
		
	}
	
	this.getBlock = function (track, sector, block)
	{
		validateLocation(track, sector, block);
		
		var data = matrix[track][sector][block] || "";
		
		return data;
	}
	
	this.setBlock = function (track, sector, block, data)
	{
		if (null === data) 
		{
			data = "";
		}
		
		validateLocation(track, sector, block);
		validateData(data);
		
		// If the data is different, write it.
		// Yeah, it's probably easier for real hardware
		// to just write over, but w/e...
		if (data != this.getBlock(track, sector, block)) 
		{
			matrix[track][sector][block] = data;
			
			writeToDatabase(track, sector, block, data);
			
			if (0 == data.length) 
			{
				removeDataLocationFromList(track, sector, block);
			}
			else 
			{
				addDataLocationToList(track, sector, block);
			}
		}
		
		return true;
	}
	
	function readFromDatabase(track, sector, block)
	{
		validateLocation(track, sector, block);
		
		$.getJSON(url("get", track, sector, block),
			function (response, statusText)
			{
				var data = response['data'];
				
				matrix[track][sector][block] = data;
				
				krnTrace(
					join("data READ from database at ("
						,track , ",", sector , "," , block,")"),
					"disk_drive");
			}
		);
	}
	
	// It's RAID-esque, kinda.
	function writeToDatabase(track, sector, block, data)
	{
		validateLocation(track, sector, block);
		validateData(data);
		
		$.getJSON(url("set", track, sector, block, data),
			function (response, statusText)
			{
				krnTrace(
					join("data WRITTEN to database at ("
						,track , ",", sector , "," , block,")"),
					"disk_drive");
			}
		);
	}
	
	this.erase = function ()
	{
		var result = true;
		
		for (var track = 0; track < HDD_TRACK_COUNT; track++) 
		{
			for (var sector = 0; sector < HDD_SECTOR_COUNT; sector++) 
			{
				for (var block = 0; block < HDD_BLOCK_COUNT; block++) 
				{
					validateLocation(track, sector, block);
					
					result = result &&
						this.setBlock(track, sector, block, null);
				}
			}
		}
		
		return result;
	}
	
	function validateLocation(track, sector, block)
	{
		// Validate the datatype.
		if ("number" !== typeof(track)) 
		{
			error("CDD.validateLocation",
				"track (", track, ") expected to be number, got ",
					typeof(track));
		}
		
		if ("number" !== typeof(sector)) 
		{
			error("CDD.validateLocation",
				"sector (", sector, ") expected to be number, got ",
					typeof(sector));
		}
		
		if ("number" !== typeof(block)) 
		{
			error("CDD.validateLocation",
				"block (", block, ") expected to be number, got ",
					typeof(block));
		}
		
		// Now, validate the values.
		if (track < 0 || track >= HDD_TRACK_COUNT) 
		{
			error("CDD.validateLocation",
				"track number must be in ",
					rangeToString("[)", 0, HDD_TRACK_COUNT), ": ", track);
		}
		
		if (sector < 0 || sector >= HDD_SECTOR_COUNT) 
		{
			error("CDD.validateLocation",
				"sector number must be in ",
					rangeToString("[)", 0, HDD_SECTOR_COUNT), ": ", sector);
		}
		
		if (block < 0 || block >= HDD_BLOCK_COUNT) 
		{
			error("CDD.validateLocation",
				"block number must be in ",
					rangeToString("[)", 0, HDD_BLOCK_COUNT), ": ", block);
		}
		
		return true;
	}
	
	function validateData(data)
	{
		if ("string" !== typeof(data))
		{
			error("CDD.validateWrite",
				"data expected to be a string, got: '", typeof(data), "'");
		}
		
		if (data.length > HDD_BLOCK_SIZE) 
		{
			error("CDD.validateWrite",
				"data must be less than ", HDD_BLOCK_SIZE,
				" characters, got ", data.length, ": ", data);
		}
		
		return true;
	}
	
	function formatLocation(track, sector, block)
	{
		return [ track, sector, block ].join(HDD_DIMENSION_SEPARATOR);
	}
	
	function parseLocation(locationString)
	{
		var parts = locationString.split(HDD_DIMENSION_SEPARATOR);
		
		var location = {
				'track'  : parseInt(parts[0]),
				'sector' : parseInt(parts[1]),
				'block'  : parseInt(parts[2]),
			};
		
		return location;
	}
	
	function addDataLocationToList(track, sector, block)
	{
		validateLocation(track, sector, block);
		
		var string = formatLocation(track, sector, block)
		
		if (!dataLocationList.contains(string))
		{
			dataLocationList.push(string);
			
			writeDataLocationListToCookie();
		}
	}
	
	function removeDataLocationFromList(track, sector, block)
	{
		validateLocation(track, sector, block);
		
		var index = dataLocationList.indexOf(
				formatLocation(track, sector, block));
		
		if (index >= 0)
		{
			dataLocationList.splice(index, 1);
			
			writeDataLocationListToCookie();
		}
	}
	
	function writeDataLocationListToCookie()
	{
		var cookieData = dataLocationList.join(HDD_LOCATION_SEPARATOR);
		
		$.cookie(HDD_COOKIE_NAME, cookieData, HDD_COOKIE_OPTIONS);
	}
	
	function url(action, track, sector, block, data)
	{
		var actionPath = null;
		
		switch (action.toLowerCase())
		{
			case "get":
				actionPath = DDD_GET_ACTION;
				break;
			case "set":
				actionPath = DDD_SET_ACTION;
				break;
			default:
				error("HDD.url", "unknown action: ", method)
		}
		
		var params = [
				join( "u=", encodeURIComponent(DDD_USERNAME)),
				join("&t=", encodeURIComponent(track)),
				join("&s=", encodeURIComponent(sector)),
				join("&b=", encodeURIComponent(block)),
				join("&format=json&jsoncallback=?")
			];
		
		if ("string" === typeof(data))
		{
			params.push(join("&d=", encodeURIComponent(data)));
		}
		
		var url = join(DDD_BASE_URL, actionPath, "?", params.join(''));
		
		return url;
	}
}
