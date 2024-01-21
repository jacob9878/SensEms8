package com.imoxion.sensems.web.util;

import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import javassist.NotFoundException;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.NULLRecord;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImStringHandler {
    public static Logger log = LoggerFactory.getLogger(ImStringHandler.class);

    public ImStringHandler()
    {
    }

    public ArrayList divideString(String s)
    {
        String as[] = new String[s.length()];
        ArrayList arraylist = new ArrayList();
        for(int i = 0; i < as.length; i++)
            as[i] = String.valueOf(s.charAt(i));

        String s1 = new String(as[0]);
        for(int j = 0; j < as.length - 1; j++)
        {
            s1 = s1 + as[j];
            arraylist.add(s1);
        }

        return arraylist;
    }

    public static String freshString(String s)
    {
        return s;
    }
    
    /**
     * 문자열을 정수형으로 바꿔준다.
     * 
     * @param str - 바꿀 문자열
     * @return 변형된 정수형. 
     */
    public static int parseInt(String str) throws Exception{
        int returnValue = 0;
        if (str != null && !str.equals("")) {
            returnValue = Integer.parseInt(str);
        }

        return returnValue;
    }

    /**
     * 문자열을 실수형(long) 형으로 바꿔서 리턴한다.
     * @param str - 바꿀 문자열
     * @return 변형된 long 형
     */
    public static long parseLong(String str) throws Exception{
        long returnValue = 0;
        if (!str.equals("")) {
            returnValue = Long.parseLong(str);
        }
        return returnValue;
    }


    public static String getStringBetfore(String s, String s1){
    	int i = s.indexOf(s1);
    	
    	if(s == null) return null;
    	
    	if(i <= 0){
    		return new String();
    	} 
    	
    	return s.substring(0, i);
    }
    
    public static String getStringBetween(String s, String s1)
    {
        int i = s.indexOf(s1) + s1.length();
        int j = s.lastIndexOf(s1);
        if(i < 0 || j < 0 || i > j)
            //return new String();
        	return s;
        else
            return s.substring(i, j);
    }

    public static String getStringBetween(String s, String s1, String s2)
    {
        int i = s.indexOf(s1) + s1.length();
        int j = s.indexOf(s2);
        if(i < 0 || j < 0 || i > j)
            //return new String();
        	return s;
        else
            return s.substring(i, j);
    }
    
    public static String getStringBetweenRev(String s, String s1, String s2)
    {
        try{
            int i = s.indexOf(s1) + s1.length();
            int j = s.lastIndexOf(s2);
            if(i < 0 || j < 0 || i > j)
                //return new String();
                return s;
            else
                return s.substring(i, j);
        }catch (NullPointerException ne) {
            return  s;
        }
        catch(Exception e){
            return s;
        }
    }

    public static String[] getTokenizedString(String s)
    {
        ArrayList arraylist = new ArrayList();
        for(StringTokenizer stringtokenizer = new StringTokenizer(s); stringtokenizer.hasMoreTokens(); arraylist.add(stringtokenizer.nextToken()));
        String as[] = new String[arraylist.size()];
        arraylist.toArray(as);
        return as;
    }

    public static String[] getTokenizedString(String s, String s1)
    {
        ArrayList arraylist = new ArrayList();
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, s1); stringtokenizer.hasMoreTokens(); arraylist.add(stringtokenizer.nextToken()));
        String as[] = new String[arraylist.size()];
        arraylist.toArray(as);
        return as;
    }

    public static ArrayList indexOfIgnoreCase(String s, String s1)
    {
        String s2 = s.toLowerCase();
        String s3 = s1.toLowerCase();
        ArrayList vector = new ArrayList();
        for(int j = -1; (j = s2.indexOf(s3, j)) >= 0; j++)
            vector.add(new Integer(j));

        return vector;
    }

    public static String insertString(String s, String s1, String s2, String s3)
    {
        StringBuffer stringbuffer = new StringBuffer(s);
        ArrayList vector = indexOfIgnoreCase(s, s1);
        int i = s1.length();
        int j = vector.size();
        for(int k = j - 1; k >= 0; k--)
        {
            int l = ((Integer)vector.get(k)).intValue();
            stringbuffer = stringbuffer.insert(l + i, s3);
            stringbuffer = stringbuffer.insert(l, s2);
        }

        return stringbuffer.toString();
    }

    public static String readFile(String s)
    {
        BufferedReader bufferedreader = null;
        try
        {
            StringBuffer stringbuffer = new StringBuffer();
            bufferedreader = new BufferedReader(new FileReader(s));
            for(String s1 = null; (s1 = bufferedreader.readLine()) != null;){
                stringbuffer.append(s1);
                stringbuffer.append( "\r\n");
            }
            bufferedreader.close();
            return stringbuffer.toString();
        }catch (IOException ie) {
            String errorId = ErrorTraceLogger.log(ie);
            log.error("{} - Error ie occured in DataManager.readFile( String )",errorId);
        }catch(Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Error occured in DataManager.readFile( String )",errorId);
        }finally {
            try{ if( bufferedreader != null ) bufferedreader.close(); }catch (NullPointerException ne) {} catch (Exception e){}
        }
        return null;
    }

    public static ArrayList readFileByLine(String s)
    {
        BufferedReader bufferedreader = null;
        try
        {
            ArrayList arraylist = new ArrayList();
            String s1 = null;
            bufferedreader = new BufferedReader(new FileReader(s));
            while((s1 = bufferedreader.readLine()) != null) 
            {
                s1 = s1.trim();
                arraylist.add(s1);
            }
            bufferedreader.close();
            return arraylist;
        }catch (IOException ie) {

            log.error("Error ie occured in DataManager.readFile( String )");
        } catch(Exception e) {

            log.error("Error occured in DataManager.readFile( String )");
        }finally {
            try{ if( bufferedreader != null ) bufferedreader.close(); }catch (NullPointerException ne ) {}catch (Exception e){}
        }
        return null;
    }

    public static String replace(String s, String s1, String s2)
    {
        try
        {
            StringBuffer stringbuffer = new StringBuffer();
            ArrayList vector = stringTokenize(s, s1);
            int i = vector.size();
            for(int j = 0; j < i - 1; j++)
                stringbuffer.append((String)vector.get(j)).append(s2);

            stringbuffer.append((String)vector.get(i - 1));
            return stringbuffer.toString();
        }catch (NullPointerException ne){
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - StringHandler.replace Null ( String, String, String ) ",errorId);
        }
        catch(Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - StringHandler.replace( String, String, String ) ",errorId);
        }
        return new String();
    }

    public static String replaceIngnoreCase(String s, String s1, String s2)
    {
        try
        {
            StringBuffer stringbuffer = new StringBuffer();
            ArrayList vector = stringTokenizeIgnoreCase(s, s1);
            int i = vector.size();
            for(int j = 0; j < i - 1; j++)
                stringbuffer.append((String)vector.get(j)).append(s2);

            stringbuffer.append((String)vector.get(i - 1));
            return stringbuffer.toString();
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - StringHandler.replace Null ( String, String, String )",errorId);
        } catch(Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - StringHandler.replace( String, String, String )",errorId);
        }
        return new String();
    }

    public static ArrayList stringTokenize(String s, String s1)
    {
        ArrayList vector = new ArrayList();
        String s2 = new String(s);
        int i = s1.length();
        int j;
        while((j = s2.indexOf(s1)) >= 0) 
        {
            vector.add(s2.substring(0, j));
            s2 = s2.substring(j + i, s2.length());
        }
        vector.add(s2);
        return vector;
    }

    static ArrayList stringTokenizeIgnoreCase(String s, String s1)
    {
        String s2 = s.toLowerCase();
        String s3 = s1.toLowerCase();
        ArrayList vector = new ArrayList();
        int i = s1.length();
        int j;
        int k;
        for(k = 0; (j = s2.indexOf(s3)) >= 0; k += j + i)
        {
            vector.add(s.substring(k, j + k));
            s2 = s2.substring(j + i, s2.length());
        }

        vector.add(s.substring(k, s.length()));
        return vector;
    }

    static int countOfString(String src, String s){
        int nResult = 0;
        
        try {
            String[] arrStr = src.split(s);
            nResult = arrStr.length -1;
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - StringHandler.countOfString Null ( String, String )",errorId);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - StringHandler.countOfString( String, String )",errorId);
        }
        
        return nResult;
    }
    
    public static int countOfStringIgnoreCase(String src, String s){
        int nResult = 0;
        
        try {
            String[] arrStr = src.split(s.toLowerCase());
            nResult = arrStr.length -1;
        }catch (NullPointerException ne) {
            log.error("StringHandler.countOfStringIgnoreCase Null ( String, String )");
        }
        catch(Exception e){
            log.error("StringHandler.countOfStringIgnoreCase( String, String )");
        }
        
        return nResult;
    }
    
    /**
     * 문자열을 바이트로 변환하여 길이를 계산 
     * @param s
     * @return
     */
    public static int getByteLength(String s) {
        return s.getBytes().length;
    }

    /**
     * 한글포함 문자열 자르기
     * @param s  input
     * @param len   자를 길이
     * @param tail  자른문자열 뒤에 붙일 문자열("..." 과 같은 꼬리말)
     * @return
     */
    public static String stringCut(String s, int len, String tail) {
    	String result = s;
        if (s == null) return null;
    
        int srcLen = getByteLength(s);
        if (srcLen < len) return s;

        String tmpTail = tail;
        if (tail == null) tmpTail = "";
 
        int tailLen = getByteLength(tmpTail);
        if (tailLen > len) return "";

        try {
	        char a;
	        int i = 0;
	        int realLen = 0;
	        for (i = 0; i < len - tailLen && realLen < len - tailLen; i++) {
	           a = s.charAt(i);
	           if ((a & 0xFF00) == 0)
	               realLen += 1;
	           else
	               realLen += 2;
	        }
	
	        while (getByteLength(s.substring(0, i)) > len - tailLen) {
	            i--;
	        }

	        result = s.substring(0, i) + tmpTail;
        }catch (NullPointerException ne) {
            log.error("string cut error");
        }
        catch(Exception e){
            log.error("string cut error");
        }
        
        return result;
    }

    /**
     * 한글포함 문자열을 잘라서 자른부분과 나머지부분을 배열로 리턴 
     * @param s
     * @param len
     * @return
     */
    public static ArrayList stringCut2Array(String s, int len) {
        ArrayList arrlst = new ArrayList();
        
        arrlst.add(0, s);
        arrlst.add(1, "");
        
        if (s == null) return arrlst;
    
        int srcLen = getByteLength(s);
        if (srcLen < len) return arrlst;

        try {
            char a;
            int i = 0;
            int realLen = 0;
            for (i = 0; i < len  && realLen < len ; i++) {
               a = s.charAt(i);
               if ((a & 0xFF00) == 0)
                   realLen += 1;
               else
                   realLen += 2;
            }
    
            while (getByteLength(s.substring(0, i)) > len ) {
                i--;
            }
    
            for(int k=0; k < arrlst.size(); k++){
                arrlst.remove(k);
            }
            
            arrlst.add(0, s.substring(0, i));
            arrlst.add(1, s.substring(i));
        }catch (NullPointerException ne) {
            log.error("stringCut2Array error");
        }
        catch (Exception e){
            log.error("stringCut2Array error");
        }
        return arrlst;
    }
    
    public static String winToJIS(String input) {
        StringBuffer sb = new StringBuffer();
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            switch (c) {
            /*case 0xff3c: // 
                //「\」 변환
                c = 0x005c; 
                break;
            */
            case 0xff5e: // 
                //「~」를 변환
                c = 0x301c; 
                break;
            case 0x2225: // 
                // 「∥」을 변환
                c = 0x2016; 
                break;
            case 0xff0d: // 
                //「－」을 변환
                c = 0x2212;
                break;
            case 0xffe0: // 
                //「￠」을 변환
                c = 0x00a2;
                break;
            case 0xffe1: // 
                //「￡」을 변환
                c = 0x00a3;
                break;
            case 0xffe2: // 
                // 「￢」을 변환
                c = 0x00ac; 
                break;
                
            
            }
            
            sb.append(c);
        }
        return sb.toString();
    }
    
    public static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([.[^; ]]*)");

    // returns charset parameter value, NULL if not present, NULL if httpContentType is NULL
    public static String getContentTypeEncoding(String httpContentType) {
        String encoding = null;
        if (httpContentType!=null) {
            int i = httpContentType.indexOf(";");
            if (i>-1) {
                String postMime = httpContentType.substring(i+1);
                Matcher m = CHARSET_PATTERN.matcher(postMime);
                encoding = (m.find()) ? m.group(1) : null;
                encoding = (encoding!=null) ? encoding.toUpperCase() : null;
            }
        }
        return encoding;
    }

    public static String StrtoUni(String str)
    {
        String uni = "" ;
    
        for ( int i = 0 ; i < str.length() ; i++)
        {
            char chr = str.charAt(i) ;
            String hex = Integer.toString( chr );
            //uni += "\\u"+hex;
            uni += "&#"+hex + ";" ;
        }
    
        return uni ;
    }
    
    /*
    16진수 유니코드를 문자열로 변경
    */
    public static String UnitoStr(String uni) {
        String str = "" ;
    
        StringTokenizer str1 = new StringTokenizer(uni,"\\u") ;
    
        while(str1.hasMoreTokens())
        {
            String str2 = str1.nextToken() ;
            int i = Integer.parseInt(str2,16) ;
            str += (char)i ;
        }
        return str ;
    }
    
    /**
     * 문자열의 0 bytes 문자를 제거하고 다시 생성한다.
     * @param str - 원문
     * @return 재생성된 문자
     * @author 송성규<xgxong@imoxion.com>
     * @since 2007.08.07
     */
    public static String repairString( String str ){
        String returnName = "";
        char[] cc = str.toCharArray();
        for( int i = 0 ; i < cc.length ; i++ ){
            if( cc[i] == 0 ) continue;     
            if(cc[i] == 65279) continue;    // BOM (유니코드 파일)
            returnName += cc[i];
        }
        return returnName;
    }
    
    
    public static String trim(String src){ 
        if( src != null){ 
            src = src.trim();
        }else{ 
            src = "";
        }
        return src;
    }
    
    public static String extractDigit( String str ){
        StringBuffer sb = new StringBuffer();
        if(str == null) return null;
        
        
        for(int i=0; i<str.length(); i++){
            if(str.charAt(i) >= 48 && str.charAt(i) <= 57){
                sb.append(str.charAt(i));
            }
        }
        
        return sb.toString();
    }
    
    /*public static void main(String args[])
    {
        //String s = insertString("alkjdoshfnaLwemAlflnlekoiwef", "AL", "<B>", "</B>");
        //System.out.println(s);
        
        String str = "<object id=\"player\" name=\"player\" type=\"application/x-shockwave-flash\" classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" align=\"middle\" " +
        		" height=\"265\" test width=\"320\">test</object>";
        
        Pattern p =
            Pattern.compile("<object\\s+(.*?)test(.*?)</object>",
                    Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        System.out.println(m.groupCount());
        //System.out.println(m.find());
        
        while (m.find()) {
            System.out.println(m.group(0));
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
        
        String s = "babo<realkoy@fafa.com>";
        System.out.println(ImStringHandler.getStringBetfore(s, "<"));
        
    }*/

}
