package com.imoxion.sensems.web.util;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class ImMessageUtil {
	public static Logger log = LoggerFactory.getLogger(ImMessageUtil.class);
	
	public static String doMessage(ImMessage message , String sIsHtml) {
		
		String html_Content = message.getHtml();
		String text_Content = message.getText();
		/**
		 * true : HTML
		 * false : TEXT
		 */
		boolean isHTML = true;
		String Content = null;
		if ( sIsHtml.equals("1") ){
			Content = html_Content;
			if( !Content.equals("") ){
				isHTML = true; 
			}else{
				isHTML = false;
			}
		}else{
			Content = text_Content;
			if( !Content.equals("") ){
				isHTML = false;
			}else{
				isHTML = true; 
			}
		}
		Content = removeBasicTag( Content );
		Content = getContent( Content , isHTML );		
		return Content;
	}
	
	public static String getContent(String Content , boolean isHTML ){
				
		if( isHTML ){
			Content = ImStringUtil.replaceIngnoreCase(Content, "<PRE>", "");
			Content = ImStringUtil.replaceIngnoreCase(Content, "<base ", "<x-base ");
			Content = ImStringUtil.replaceIngnoreCase(Content, "src=\"\"", "");
			Content = ImStringUtil.replaceIngnoreCase(Content, "src=''", "");
		}else{
			Content = ImStringUtil.replaceIngnoreCase(Content, "src=\"\"", "");
			Content = ImStringUtil.replaceIngnoreCase(Content, "src=''", "");
			Content = ImStringUtil.replaceIngnoreCase(Content, "\n", "<br>");
		}
		return Content;	
	}
	
	/*public static void main(String[] args){
		File file = new File("d:\\apps\\sensmail2009\\tempfile\\test.txt");
		String s = "";
		BufferedReader br = null;
		try {			
			Reader r = new FileReader( file );
			br = new BufferedReader( r );
			String t = null;
			while(  ( t = br.readLine() ) != null ){
				s += t+"";
			}
		}catch (IOException ie) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - ImMessageUtilERROR ie",errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - ImMessageUtilERROR",errorId);	
		}finally{
			try{ br.close(); }catch(Exception e){}
		}
		//Pattern SCRIPTS = Pattern.compile("<(no)?SCRIPT[^>]*>.*?</(no)?SCRIPT>",Pattern.DOTALL );
		Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>",Pattern.CASE_INSENSITIVE );
		//Pattern SCRIPTS = Pattern.compile("<SCRIPT>",Pattern.DOTALL );
		Matcher m = SCRIPTS.matcher( s );
		while( m.find() ){
			System.out.println( m.toString() );
			//m.replaceAll("");
		}
		//System.out.println( m.replaceAll("") );
	}*/
	
	public static String removeBasicTag(String s ){		
		//Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>",Pattern.CASE_INSENSITIVE );
		//Pattern SCRIPTS = Pattern.compile("<(no)?SCRIPT[^>]*>.*?</(no)?SCRIPT>",Pattern.DOTALL );//성공
		Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>",Pattern.DOTALL|Pattern.CASE_INSENSITIVE );//성공
		s = SCRIPTS.matcher( s ).replaceAll("");
		return s;
	}
	
}
