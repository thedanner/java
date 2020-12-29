function krnSetStatus(status)
{
	_StatusMessage = status;
}

function krnDate()
{
	var date = new Date();
	
	// JS engines do this 0-based for some reason, hence the '+ 1'.
	var month = date.getMonth() + 1;
	var dayOfMonth = date.getDate();
	
	var monthStr = month.toString();
	var dayOfMonthStr = dayOfMonth.toString();
	
	if (month < 10)
	{
		monthStr = "0" + month;
	}
	
	if (dayOfMonth < 10)
	{
		dayOfMonthStr = "0" + dayOfMonth;
	}
	
	var currentDate = [
			date.getFullYear(), "-",
			monthStr , "-",
			dayOfMonthStr
		].join('');
	
	return currentDate;
}

function krnTime()
{
	var date = new Date();
		
		var month = date.getMonth() + 1;
		var dayOfMonth = date.getDate();
		var hours = date.getHours();
		var minutes = date.getMinutes();
		var seconds = date.getSeconds();
		
		var monthStr = month.toString();
		var dayOfMonthStr = dayOfMonth.toString();
		var hoursStr = hours.toString();
		var minutesStr = minutes.toString();
		var secondsStr = seconds.toString();
		
		if (month < 10)
		{
			monthStr = "0" + month;
		}
		
		if (dayOfMonth < 10)
		{
			dayOfMonthStr = "0" + dayOfMonth;
		}
		
		if (hours < 10)
		{
			hoursStr = "0" + hours;
		}
		
		if (minutes < 10)
		{
			minutesStr = "0" + minutes;
		}
		
		if (seconds < 10)
		{
			secondsStr = "0" + seconds;
		}
		
		var currentDate = [
				date.getFullYear(), "-",
				monthStr , "-",
				dayOfMonthStr
			].join('');
		
		var currentTime = [
				hoursStr, ":", minutesStr, ":", secondsStr
			].join('');
		
		return currentTime;
}
