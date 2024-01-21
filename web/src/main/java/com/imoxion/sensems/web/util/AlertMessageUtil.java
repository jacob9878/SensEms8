/**
 * 
 */
package com.imoxion.sensems.web.util;

import com.imoxion.common.util.ImHtmlUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 스크립트 처리 Util
 * 
 * @author : sunggyu
 * @date : 2012. 11. 30.
 * @desc :
 * 
 */
public class AlertMessageUtil {

	public static ModelAndView getMessage(final String msg) {
		View view = new AbstractView() {
			@Override
			protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

				if (StringUtils.isNotEmpty(msg)) {
					out.println(msg);
				}
				out.flush();
				out.close();
			}
		};
		return new ModelAndView(view);
	}

	/**
	 * 다운로드 로직 중 에러발생시 화면상에 스크립트 표시하기 위한 Util
	 * 
	 * @Method Name : getMessageView
	 * @Method Comment :
	 * 
	 * @param msg
	 * @param script
	 * @return
	 */
	public static ModelAndView getMessageView(final String msg) {
		View view = new AbstractView() {
			@Override
			protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

				if (StringUtils.isNotEmpty(msg)) {
					out.println(ImHtmlUtil.viewMsg(msg));
				}
				out.flush();
				out.close();
			}
		};
		return new ModelAndView(view);
	}

	/**
	 * Alert메세지가 아닌
	 * 
	 * @Method Name : getMessageViewOfScript
	 * @Method Comment :
	 * 
	 * @param script
	 * @return
	 */
	public static ModelAndView getMessageViewOfScript(final String script) {
		View view = new AbstractView() {
			@Override
			protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

				out.println("<script>");
				if (StringUtils.isNotEmpty(script)) {
					out.println(script);
				}
				out.println("</script>");
				out.flush();
				out.close();
			}
		};
		return new ModelAndView(view);
	}

	/**
	 * 
	 * @Method Name : getMessageViewOfScriptAndMsg
	 * @Method Comment : 메세지 + 스크립트
	 * 
	 * @param script
	 * @param msg
	 * @return
	 */
	public static ModelAndView getMessageViewOfScriptAndMsg(final String script, final String msg) {
		View view = new AbstractView() {
			@Override
			protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

				if (StringUtils.isNotEmpty(msg)) {
					out.println(ImHtmlUtil.viewMsg(msg));
				}
				out.println("<script>");
				if (StringUtils.isNotEmpty(script)) {
					out.println(script);
				}
				out.println("</script>");
				out.flush();
				out.close();
			}
		};
		return new ModelAndView(view);
	}
}