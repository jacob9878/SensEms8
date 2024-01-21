/*--------------------------------------------------------------------------------------------------
// ??? : setday.js
// ?    ? : ?? ??? ????? ?? ?? ????
// ??? : 2003.4.28
// ??? : Copyright(c) Imoxion.Inc. 2001 -
// ??? : webmaster@imoxion.com
//------------------------------------------------------------------------------------------------*/
var obj;
var strNowMinute;
var today = new Date();
var nowYear = today.getFullYear();
var nowMonth = today.getMonth() + ((today.getMonth() < 13) ? 1 : -1);
strNowMonth = (nowMonth < 10)? "0"+nowMonth : nowMonth;
var nowDate = today.getDate();	// ??
strNowDate = (nowDate < 10)? "0"+nowDate : nowDate;
var nowHour = today.getHours();
strNowHour = (nowHour < 10)? "0"+nowHour : nowHour;
var nowMinute = today.getMinutes();

// 6??
var resp_day=new Date();
var newtimems=resp_day.getTime()+(6*24*60*60*1000); // 6? * 24?? * 60? * 60? * 1000???
resp_day.setTime(newtimems);
//var resp_day = new Date(nowYear,nowMonth,nowDate+6);
var respYear = resp_day.getFullYear();
var respMonth = resp_day.getMonth()+1;
strRespMonth = (respMonth < 10)? "0"+respMonth : respMonth;
var respDate = resp_day.getDate();	// ??
strRespDate = (respDate < 10)? "0"+respDate : respDate;


if(nowMinute <= 10) strNowMinute = "10";
else if(nowMinute > 10 && nowMinute <= 20) strNowMinute = "20";
else if(nowMinute > 20 && nowMinute <= 30) strNowMinute = "30";
else if(nowMinute > 30 && nowMinute <= 40) strNowMinute = "40";
else if(nowMinute > 40 && nowMinute <= 50) strNowMinute = "50";
else if(nowMinute > 50 && nowMinute <= 60){
	strNowMinute = "00";
	var nextHour = nowHour + 1;
	strNowHour = (nextHour < 10)? "0"+nextHour : nextHour;
}

var nowWeek = today.getDay();	// ??


function setYear(){	
	var sForm = obj;
	var tmp = new Option(nowYear,nowYear,true);
	sForm.year.options[0] = tmp;
	var tmp = new Option(nowYear+1,nowYear+1,true);
	sForm.year.options[1] = tmp;
	sForm.year.length = 2;
}

function setRespYear(){	
	var sForm = obj;
	var tmp = new Option(respYear,respYear,true);
	sForm.resp_year.options[0] = tmp;
	var tmp = new Option(nowYear+1,nowYear+1,true);
	sForm.resp_year.options[1] = tmp;
	sForm.resp_year.length = 2;
}

function setMonth(){
	var sForm = obj;
	var i=0;
	var j=0;
	var strMonth;
	for(i=0; i< 12; i++){
		j = i+1;
		if(j < 10) 	strMonth = "0" + j;
		else		strMonth = j;
		var tmp = new Option(j,strMonth,true);
		sForm.month.options[i] = tmp;
	}
	sForm.month.value = strNowMonth;
}

function setRespMonth(){
	var sForm = obj;
	var i=0;
	var j=0;
	var strMonth;
	for(i=0; i< 12; i++){
		j = i+1;
		if(j < 10) 	strMonth = "0" + j;
		else		strMonth = j;
		var tmp = new Option(j,strMonth,true);
		sForm.resp_month.options[i] = tmp;
	}
	sForm.resp_month.value = strRespMonth;
}


function setDay(year, month){
	var sForm = obj;
	var i=0;
	var j=0;
	var totalDays = getDays(month, year);
	var strDay;
	for(i=sForm.day.length-1; i >= 0; i--){
		sForm.day.remove(i);
	}
	for(i=0; i< totalDays; i++){
		j = i+1;
		if(j < 10) 	strDay = "0" + j;
		else		strDay = j;
		var tmp = new Option(j,strDay,true);
		sForm.day.options[i] = tmp;
	}
	sForm.day.value = strNowDate;
}

function setRespDay(year, month){
	var sForm = obj;
	var i=0;
	var j=0;
	var totalDays = getDays(month, year);
	var strDay;
	for(i=sForm.resp_date.length-1; i >= 0; i--){
		sForm.resp_date.remove(i);
	}
	for(i=0; i< totalDays; i++){
		j = i+1;
		if(j < 10) 	strDay = "0" + j;
		else		strDay = j;
		var tmp = new Option(j,strDay,true);
		sForm.resp_date.options[i] = tmp;
	}
	sForm.resp_date.value = strRespDate;
}

function setHour(){
	var sForm = obj;
	var i=0;
	var strHour;
	for(i=0; i< 24; i++){
		if(i < 10)  strHour = "0" + i;
		else 		strHour = i;
		var tmp = new Option(i, strHour, true);
		sForm.hour.options[i] = tmp;
	}
	sForm.hour.value = strNowHour;
}

function setMinute(){
	var sForm = obj;
	var i=0;
	var strmin;
	for(i=0; i< 6; i++){
		min = i* 10;
		if(min == 0) strmin = "00";
		else strmin = min;
		var tmp = new Option(strmin, strmin, true);
		sForm.minute.options[i] = tmp;
	}
	sForm.minute.value = strNowMinute;
}

function getDays(month, year) {
	// ? ?? ? ?? ???? ?? ??
	var ar = new Array(12)
	ar[0] = 31 // January
	ar[1] = (leapYear(year)) ? 29 : 28 // February
	ar[2] = 31 // March
	ar[3] = 30 // April
	ar[4] = 31 // May
	ar[5] = 30 // June
	ar[6] = 31 // July
	ar[7] = 31 // August
	ar[8] = 30 // September
	ar[9] = 31 // October
	ar[10] = 30 // November
	ar[11] = 31 // December

	// ?? ?? ? ? ??
	return ar[month-1];
}

function leapYear(year) {
	if (year % 4 == 0 && year%100 != 0 || year%400 == 0) // ?? ??
	        return true // ??
	else  // else?? ?? ??? ?
		return false // ?? ??
}

function initDay(form, sendmode,year,month,day,hour,minute){
	obj = form;
	setYear();
	setMonth();
	setDay(nowYear, nowMonth);
	setRespYear();
	setRespMonth();
	setRespDay(nowYear, nowMonth);
	setHour();
	setMinute();
	
	if(sendmode == "1") {
		obj.year.value = year;
		obj.month.value = month;
		obj.day.value = day;
		obj.hour.value = hour;
		obj.minute.value = minute;
	}

	
}