/**
 * @author zpqdnjs
 * 메일발송 결과 매뉴 preparer
 */
package com.imoxion.sensems.web.preparer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class SendResultPreparer extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
						
		request.setAttribute("write_menu", "");
		request.setAttribute("send_result_menu", "on");
		request.setAttribute("calendar_menu", "");
		request.setAttribute("receive_group_menu", "");
		request.setAttribute("send_menu", "");
		request.setAttribute("sysman_menu", "");

		String url = request.getRequestURI();
		if(url.indexOf("/list.do") > -1){
			request.setAttribute("result_menu", "on");
			request.setAttribute("each_menu", "");
		}else if(url.indexOf("/staticSend.do") > -1){
			request.setAttribute("result_menu", "on");
			request.setAttribute("each_menu", "");
		}else if(url.indexOf("/staticLink.do") > -1){
			request.setAttribute("result_menu", "on");
			request.setAttribute("each_menu", "");
		}else if(url.indexOf("/staticReceipt.do") > -1){
			request.setAttribute("result_menu", "on");
			request.setAttribute("each_menu", "");
		}else if(url.indexOf("/staticPage.do") > -1){
			request.setAttribute("result_menu", "on");
			request.setAttribute("each_menu", "");
		}
		else if(url.indexOf("/sendList.do") > -1){ //개별발송,, URL은 알아서 변경하시길.
			request.setAttribute("result_menu", "");
			request.setAttribute("each_menu", "on");
		}
		
		return super.preHandle(request, response, handler);
	}
}