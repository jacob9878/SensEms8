/*
 * Created on 2005. 2. 15.
 */
package com.imoxion.sensems.web.util;

import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImUtility {
	public static Logger log = LoggerFactory.getLogger(ImUtility.class);

    public static String byteFormat( long size , int dot ) {
    	long kb = 1024;        // Kilobyte
    	long mb = 1024 * kb;   // Megabyte
    	long gb = 1024 * mb;   // Gigabyte
    	long tb = 1024 * gb;   // Terabyte
    	if( size == 0) return "0 Byte";
    	java.text.DecimalFormat df = dot( dot );
    	if(size < kb) {
    		return df.format( size )+" Byte";
    	} else if(size < mb) {
    		return df.format( size/kb )+" KB";
    	} else if(size < gb) {
    		return df.format( size/mb )+" MB";
    	} else if(size < tb) {
    		return df.format( size/gb )+" GB";
    	} else {
    		return df.format( size/tb )+" TB";
    	}
    }
    
    private static java.text.DecimalFormat dot(int dot){ 
    	String sdot = "";
    	for(int i = 0 ; i < dot ; i++){ 
    		sdot += "#";
    	}
    	java.text.DecimalFormat df = new java.text.DecimalFormat("0."+sdot);
    	return df;
    }
    
	/**
	 * @param day
	 * @return String
	 */
	public static String eval( int day ){ 
		
		String str_day = null;
		if(day < 10){ 
			str_day = "0"+Integer.toString(day);
		}else{ 
			str_day = Integer.toString(day);
		}
		return str_day;
	}
    
	public static String eval( String day ){ 
        
        String str_day = null;
        if(day.length() < 2){ 
            str_day = "0"+day;
        }else{ 
            str_day = day;
        }
        return str_day;
    }
	/**
	 * @return
	 */
	public static String getDate(){
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    return sdf.format(new Date());
	}
    
    public static String getCurrentYMDHMS(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
        return sdf.format(new Date());
    }
    
    public static Date getDateFromString( String str_date, String format ){
	    if(str_date == null) return null;
	    //if(str_date.equals("-")) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dt = null;
        try {
            dt = sdf.parse(str_date);
        } catch (ParseException e) {
			log.error("getDateFromString error");
		}catch (Exception ee) {
			log.error("getDateFromString error");
		}

		return dt;
     }
	/**
	 * @return String
	 */
	public static String getToday(){ 
		
		Calendar today = Calendar.getInstance();	
		
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		int hours = today.get(Calendar.HOUR_OF_DAY);
		int minuate = today.get(Calendar.MINUTE);
		
		String result = Integer.toString(year)+eval(month)+eval(day)+eval(hours)+eval(minuate);
		return result;
	}
	
	public static String getToday2(){ 
        
        Calendar today = Calendar.getInstance();    
        
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH)+1;
        int day = today.get(Calendar.DATE);
        int hours = today.get(Calendar.HOUR_OF_DAY);
        int minuate = today.get(Calendar.MINUTE);
        int sec = today.get(Calendar.SECOND);
        
        String result = Integer.toString(year)+eval(month)+eval(day)+eval(hours)+eval(minuate)+eval(sec);
        return result;
    }
	
	public static long getTimeInMilis(String time, String format){
        DateFormat df = new SimpleDateFormat(format);
        long mili = -1;

		try {
			Date dt = df.parse(time);
			mili = dt.getTime();
		} catch (ParseException pe) {
			String errorId = ErrorTraceLogger.log(pe);
			log.error("{} - getTimeInMilis pe error",errorId);
		}catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - getTimeInMilis error",errorId);
		}
		//System.out.println(dt.getTime() + " / " + System.currentTimeMillis());


        return mili;
    }
	
	/**
	 * @return String
	 */
	public static String changeRegdate(String regdate){
		if( regdate == null ) return regdate;		
	    if( regdate.length() < 12 ) return regdate;
		return regdate.substring(0,4)+"-"+regdate.substring(4,6)+"-"+regdate.substring(6,8)+" "+regdate.substring(8,10)+":"+regdate.substring(10,12);
	}
	/**
	 * long ����� Ÿ��; YYYYMMDDHHSS ���8�� ��ȯ.
	 * @param longtime 
	 * @return String
	 */
	public static String getToday(long longtime){
	    
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(longtime);		
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		int hours = today.get(Calendar.HOUR_OF_DAY);
		int minuate = today.get(Calendar.MINUTE);		
		String result = Integer.toString(year)+eval(month)+eval(day)+eval(hours)+eval(minuate);
		return result;
	}
	
	
	
	/**
	 * YYYYMMDD
	 * @param time
	 * @return String
	 */
	public static String changeDate(String time){
        if( time.equals("") || time == null) return "";
        return time.substring(0,4)+time.substring(4,6)+time.substring(6,8);
    }
    public static String changeDatesub(String time){
        if( time.equals("") || time == null) return "";
        return time.substring(0,4)+time.substring(5,7)+time.substring(8,10);
    }
	/**
	 * @param hmTime
	 * @return
	 */
	public static String strToHM(String hmTime){ 
	    if( hmTime.equals("") || hmTime == null) return "";
	    return hmTime.substring(0,2)+":"+hmTime.substring(2,4);
	}
	
	public static String strToHM2(String hmTime){ 
	    if( hmTime.equals("") || hmTime == null) return "";
	   return hmTime.substring(8,10)+":"+hmTime.substring(10,12);
		//return hmTime.substring(7,8)+":"+hmTime.substring(9,10);
	}

	/**
	 *
	 *	HH만 출력
	 *
	 */
	public static String stroToHH(String hhTime) {
		if(hhTime.equals("") ||  hhTime == null) return "";
		return hhTime.substring(8,10);
	}

	/**
	 *
	 *	MM만 출력
	 *
	 */
	public static String stroToMM(String mmTime) {
		if(mmTime.equals("") || mmTime == null) return "";
		return mmTime.substring(10,12);
	}

	/**
	 * @param mdTime
	 * @return
	 */
	public static String strToMD(String mdTime){ 
	    if( mdTime == null || mdTime.equals("") || mdTime.equals("null")){
	        return "";
	    }else{
	        return mdTime.substring(0,2)+"-"+mdTime.substring(2,4);
	    }
	}
    
    public static Date getTime( long termday )
    {         
         Date dt = new Date();
         long term = termday * 86400000;
         dt.setTime( dt.getTime() + term );
       
         return dt;
    } 
    
    public static Timestamp getTimestamp( Date uDate ){
        if( uDate == null ) return null;
        return new Timestamp( uDate.getTime() );
    }
    
    public static String getSimpleDate( Date date ){
		 if(date == null) return "";
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 return sdf.format( date );
	 }
    
    public static String getDateFormat( Date date, String format ){
		 if(date == null) return "";
		 String result = "";


		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			result = sdf.format( date );
		}catch (NullPointerException ne) {
			result = getSimpleDate(date);
		}
		catch (Exception e) {
			result = getSimpleDate(date);
		}

		return result;
	 }
    
	/**
	 * @param str
	 * @return
	 */
	public static String notNull(String str ){ 
		if( str == null ) str = "";
		return str;
	}
	
	/**
	 * @param str
	 * @param pattern
	 * @param replace
	 * @return
	 */
	public static String replace(String str, String pattern, String replace) {
	    int s = 0;
	    int e = 0;
	    StringBuffer result = new StringBuffer();
	    
	    while ((e = str.indexOf(pattern, s)) >= 0) {
	   	 result.append(str.substring(s, e));
	   	 result.append(replace);
	   	 s = e + pattern.length();
	    }
	    result.append(str.substring(s));
	    return result.toString();
	}
	
	public static String replaceMore(String str, String pattern, String replace) {
	    int s = 0;
	    int e = 0;
	    StringBuffer result = new StringBuffer();
	    
	    if ( ( e = str.indexOf(pattern , s ) ) >= 0) {
	   	 result.append(str.substring(s, e));
	   	 result.append(replace);
	   	 s = e + pattern.length();
	    }
	    result.append(str.substring(s));
	    return result.toString();
	}
	/**
	 * @param src
	 * @return
	 */
	public static String trim(String src){ 
		if( src != null){ 
			src = src.trim();
		}else{ 
			src = "";
		}
		return src;
	}
	
	/**
	 * @return
	 * @throws ClassNotFoundException
	 */
	
/*	public static String getWeek() throws ClassNotFoundException{
		
		Calendar today = Calendar.getInstance();
		
		int check_week = today.get(Calendar.DAY_OF_WEEK);
	
		String week = null;
	
		if( check_week == 1){ 
			week = "��";
		}else if( check_week == 2){ 
			week = "��";
		}else if( check_week == 3){ 
			week = "ȭ";
		}else if( check_week == 4){ 
			week = "��";
		}else if( check_week == 5){ 
			week = "��";
		}else if( check_week == 6){ 
			week = "��";
		}else if( check_week == 7){ 
			week = "��";
		}
		return week;
	}*/
	
	/**
	 * @param todate
	 * @return String
	 * @throws ClassNotFoundException
	 */
	/*public static String getWeek( String todate ) throws ClassNotFoundException{
		
		int year = Integer.parseInt(todate.substring(0,4) );
		int month = Integer.parseInt(todate.substring(4,6) );
		int day = Integer.parseInt(todate.substring(6,8) );
		
		Calendar today = Calendar.getInstance();
		today.set( year , month-1 , day );
		int check_week = today.get(Calendar.DAY_OF_WEEK);
	
		String week = null;
	
		if( check_week == 1){ 
			week = "��";
		}else if( check_week == 2){ 
			week = "��";
		}else if( check_week == 3){ 
			week = "ȭ";
		}else if( check_week == 4){ 
			week = "��";
		}else if( check_week == 5){ 
			week = "��";
		}else if( check_week == 6){ 
			week = "��";
		}else if( check_week == 7){ 
			week = "��";
		}
		return week;
	}*/
	
	/**
	 * @return int
	 * @throws ClassNotFoundException
	 */
	public static int getWeekInt() throws ClassNotFoundException{
	    Calendar today = Calendar.getInstance();		
		int check_week = today.get(Calendar.DAY_OF_WEEK);
		return check_week;
	}
	
	/**
	 * @param todate
	 * @return int 
	 * @throws ClassNotFoundException
	 */
	public static int getWeekInt( String todate ) throws ClassNotFoundException{
	    int year = Integer.parseInt(todate.substring(0,4) );
		int month = Integer.parseInt(todate.substring(4,6) );
		int day = Integer.parseInt(todate.substring(6,8) );
		
		Calendar today = Calendar.getInstance();
		today.set( year , month-1 , day );
		int check_week = today.get(Calendar.DAY_OF_WEEK);
		return check_week;
	}
	/**
	 * @param year
	 * @param month
	 * @param day
	 * @return int 
	 * @throws ClassNotFoundException
	 */
	public static int getWeekInt( int year , int month , int day ) throws ClassNotFoundException{	    
		Calendar today = Calendar.getInstance();
		today.set( year , month-1 , day );
		int check_week = today.get(Calendar.DAY_OF_WEEK);
		return check_week;
	}
	
	public static int getWeek( long day ) throws ClassNotFoundException{	    
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(day);
		int check_week = today.get(Calendar.DAY_OF_WEEK);
		return check_week;
	}
	
	
	public static String by_year(String year){
		int toyear = Integer.parseInt(year);
		String date_year = "";	 
		for (int y=toyear-3 ; y<toyear+5 ; y++) {
			if (y==toyear) 	{
				date_year +="<option value='"+y+"' selected>"+y+"</option>\n";	
			}	else {
				date_year +="<option value='"+y+"'>"+y+"</option>\n";
			}
		}
		return date_year;		
	}


	public static String by_month(String month)
	 {
		
		int tomonth=Integer.parseInt(month);
		String date_month = "";
		for (int m=1 ; m<=12 ; m++) 
		{
			String mm= (m<10) ? "0"+Integer.toString(m) : Integer.toString(m);
			if (m==tomonth) 	{
				date_month +="<option value='"+mm+"' selected>"+m+"</option>\n";	
			}	else	 {
				date_month +="<option value='"+mm+"'>"+m+"</option>\n";	
			}		
		}
	return date_month ;
	}
	 

	public static String by_day(String day)
	{
		int nowday=Integer.parseInt(day);
		String date_day = "";
		for (int d=1 ; d<=31  ; d++) 
		{			
			String dd= (d<10) ? "0"+Integer.toString(d) : Integer.toString(d);
			if (d==nowday)  {
				date_day +="<option value='"+dd+"' selected>"+d+"</option>\n"; 
			} else	{
				date_day +="<option value='"+dd+"'>"+d+"</option>\n";				 
			}
		
		}

	return date_day;
	}
    
    public static String by_day(int day, int lastday)
    {
        int nowday=day;
        String date_day = "";
        for (int d=1 ; d<=lastday  ; d++) 
        {           
            String dd= (d<10) ? "0"+Integer.toString(d) : Integer.toString(d);
            if (d==nowday)  {
                date_day +="<option value='"+dd+"' selected>"+d+"</option>\n"; 
            } else  {
                date_day +="<option value='"+dd+"'>"+d+"</option>\n";                
            }
        
        }

    return date_day;
    }

	public static String by_time(String time)
	{
		String date_time = "";
		int stime = Integer.parseInt(time);
		for (int t=0; t<=23 ; t++) 
		{
			if (t==stime) 
			{
					if (t <= 12)	{	
						date_time +="<option value='"+t+"' selected>AM "+t+"</option>\n";	
					} else	{	
						int tt = t-12;
						date_time +="<option value='"+t+"' selected>PM "+tt+"</option>\n";	
					}
			}
			else 
			{	
					if (t <= 12)	{	
						date_time +="<option value='"+t+"'>AM "+t+"</option>\n";	
					} else	{
						int tt = t-12;
						date_time +="<option value='"+t+"'>PM "+tt+"</option>\n";	
					}
			}

		 }

	return date_time;
	}

	public static String by_min(String min)
	{
		int tomin=Integer.parseInt(min);
		String date_min = "";
		for (int m=0 ; m<=5  ; m++) 
		{
			int mm = m*10;
			if (mm==tomin)  {
				date_min +="<option value='"+mm+"' selected>"+mm+"</option>\n"; 
			} else	{
				date_min +="<option value='"+mm+"'>"+mm+"</option>\n"; 
			}		
		}

	return date_min;
	}

	/**
	 * @param stringer
	 * @param len
	 * @return
	 */
	public static String strCutDot(String stringer , int len ){
	    if( stringer.length() >= len ){ 
	        return stringer.substring(0,len)+"...";
	    }else{ 
	        return stringer;    
	    }
	    
	}
	
	public static String strCut(String stringer , int len ){
	    if( stringer.length() >= len ){ 
	        return stringer.substring(0,len);
	    }else{ 
	        return stringer;    
	    }
	    
	}
	
	public static int exec_LastDay( int datMonth, int datYear) {
	    int datLastDay = 0;
		if(datMonth == 4 || datMonth == 6 || datMonth == 9 || datMonth ==11){  
			datLastDay = 30;
		} else if(datMonth == 2 && (datYear % 4) != 0){  
			datLastDay = 28;
		} else if(datMonth == 2 && (datYear % 4) == 0){  
			if((datYear % 100) == 0){
				if((datYear % 400) == 0){
					datLastDay = 29;
				} else {
					datLastDay = 28;
				}
			} else {
				datLastDay = 29;
			}
		} else {
			datLastDay = 31;
		}
		
		return datLastDay;
	}
	
	public static int parseInt(String str ){
	    int returnValue = 0;
	    if( str != null && !str.equals("") ){
	        returnValue = Integer.parseInt(str);
	    }
	    return returnValue;
	}
	
	public static long parseLong(String str ){
	    long returnValue = 0;
	    if( str != null && !str.equals("") ){
	        returnValue = Long.parseLong(str);
	    }
	    return returnValue;
	}
	
	public static String hexdec(String str ){ 
	    String uni = "";

	    for ( int i = 0 ; i < str.length() ; i++){
		    char chr = str.charAt(i) ;
		    String hex = Integer.toHexString(chr) ;
		    uni += "\\u"+hex ;
	    }

	    return uni ;
	}
	/**
     *  ex)
     *  msg.Me_MSG_BIGFILE_MIDDLE = "%s?mailkey=%s&szHost=%s";
     *  String[] args = { "http://mail.imoxion.com" , "230434" , "mail.imoxion.com" );
     *  String temp = Utility.sprint( msg.ME_MSG_BIGFILE_MIDDLE , args  );
     *  temp = "http://mail.imoxion.com?mailkey=230434&szHost=mail.imoxion.com";
	 */
	public static String sprint( String str , String[] args ){
	    for(int i = 0 ; i < args.length ; i++ ){
	        str = replaceMore( str , "%s" , args[i] );
	    }
	    return str;
	}
	
	public static String toUniString(String in,String sDefCharset){
		String sRet = in;
		String	sCharset = sDefCharset;
		
		if(sRet == null || sRet.length() <= 0)
			return sRet;


		try {
			int nEncIndex = sRet.indexOf(":");
			if(nEncIndex != -1){
				sCharset = sRet.substring(0,nEncIndex);
				if(Charset.isSupported(sCharset)){
					sRet = sRet.substring(nEncIndex+1);
				}else{
					sCharset = sDefCharset;
				}
				sRet = new String(sRet.getBytes(),sCharset);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("toUniString error");
		}catch (Exception ee) {
			log.error("toUniString error");
		}


		return sRet;
	}

	public static String toUniString(String in,String sCharset,String sDefCharset){
		String sRet = in;
		String sChar = sDefCharset;
		
		if(sRet == null || sRet.length() <= 0)
			return sRet;


		try {
			if(sCharset != null && sCharset.length() > 0 && Charset.isSupported(sCharset)){
				sChar = sCharset;
				sRet = new String(sRet.getBytes(),sChar);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("toUniString error");
		}catch (Exception ee) {
			log.error("toUniString error");
		}


		return sRet;
	}

	public static String readUniString(String in,String sCharset,String sDefCharset){
		String sRet = in;
		String sChar = sDefCharset;
		
		if(sRet == null || sRet.length() <= 0)
			return sRet;
		
		try{
			if(sCharset != null && sCharset.length() > 0 && Charset.isSupported(sCharset)){
				sChar = sCharset;
			}
			sRet = new String(sRet.getBytes(sChar));
		} catch (UnsupportedEncodingException e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - readUniString encoding error", errorId);
		} catch (Exception ee) {
			String errorId = ErrorTraceLogger.log(ee);
			log.error("{} - readUniString error", errorId);
		}

		return sRet;
	}
	
	/**
     * 메시지를 보여주고 뒤로 가는 HTML을 만들어 준다.
     * @param msg - 보여줄 메시지
     * @return 생성된 HTML String
     */
    public static String viewMessage(String msg)
    {   
        return "<script language=javascript>\n alert('"+msg+"');\n window.history.go(-1);\n </script>";
    }
    
    /**
     * 메시지를 보여주고 창을 닫는 HTML을 만들어 준다.
     * @param msg - 보여줄 메시지
     * @return 생성된 HTML String
     */
    public static String viewMessage2(String msg)
    {
        return "<script language=javascript>alert('"+msg+"');window.close();</script>";
    }
    
    /**
     * 메시지를 보여주고 창을 닫고 opener 를 다시 올려주는 HTML을 만들어 준다.
     * @param msg - 보여줄 메시지
     * @return 생성된 HTML String
     */
    public static String viewMessage3(String msg)
    {
        return "<script language=javascript>alert('"+msg+"');window.close();window.opener.location.reload();</script>";
    }
    
    /**
     * 메시지를 보여주고 현재 링크를 이동하는 HTML 스크립트를 만든다.
     * @param msg - 보여줄 메시지
     * @param url - 이동할 링크 URL
     * @return 생성된 HTML String
     */
    public static String viewMessageEx(String msg, String url)
    {
        return "<script language=javascript>alert('"+msg+"');location.href = '"+url+"';</script>";
    }
    
    /**
     * 메시지를 보여주고 창을 닫고 target 에 이동할 경로를 준다.
     * @param msg - 보여줄 메시지
     * @param target - 변경될 target
     * @param url - target 이 이동할 url
     * @return 생성된 HTML String
     */
    public static String viewMessageEx(String msg , String target , String url )
    {
        return "<script language=javascript>alert('"+msg+"');window.close();"+target+".location.href = '"+url+"';</script>";
    }

    
    /**
     * 해당 메시지만 보여준다.
     * @param msg - 보여줄 메시지
     * @return 생성된 HTML String
     */
    public static final String viewMsg(String msg){ 
        String result = null;       
        result = "<script>\n alert('"+msg+"');\n</script>\n";
        return result;
    }

	public static boolean validCharacter(String str){
		String regex = "^[a-zA-Z0-9ㄱ-ㅎ가-힣-._\\s]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		matcher = pattern.matcher(str);
		boolean check = matcher.matches();

		if(!check){
			log.error("validCharacter error");
			return false;
		}
		return true;
	}

	/**
	 * 영문, 숫자만 허용하도록 검사
	 * @param str
	 * @return
	 */
	public static boolean validFormatChar(String str){
		char cUser;
		int i;
		for (i = 0; i < str.length(); i++) {
			cUser = str.charAt(i);
			if (!((cUser >= 48 && cUser <= 57) || (cUser >= 41 && cUser <= 90) || (cUser >= 97 && cUser <= 122) || cUser == 45 || cUser == 95)) {
				return false;
			}
		}
		return true;
	}

	public static boolean validCharacter2(String str){
		boolean isValid = true;
		if (str.indexOf("'") != -1 || str.indexOf('"') != -1 || str.indexOf('@') != -1 || str.indexOf(',') != -1
				|| str.indexOf('?') != -1 || str.indexOf('<') != -1 || str.indexOf('>') != -1 || str.indexOf(';') != -1
				|| str.indexOf(':') != -1 || str.indexOf('/') != -1 || str.indexOf('(') != -1 || str.indexOf(')') != -1
				|| str.indexOf('+') != -1 || str.indexOf('|') != -1 || str.indexOf('\\') != -1 || str.indexOf('*') != -1
				|| str.indexOf('&') != -1 || str.indexOf('^') != -1 || str.indexOf('%') != -1 || str.indexOf('$') != -1
				|| str.indexOf('!') != -1 || str.indexOf('~') != -1 || str.indexOf('#') != -1 || str.indexOf('=') != -1
				|| str.indexOf('`') != -1 || str.indexOf('{') != -1 || str.indexOf('}') != -1 || str.indexOf('[') != -1
				|| str.indexOf(']') != -1)  {
			isValid = false;
		}
		return isValid;
	}
}
