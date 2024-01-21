/**
 * @author zpqdnjs
 * 메일쓰기 매뉴 preparer
 */
package com.imoxion.sensems.web.preparer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class MailWritePreparer extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
						
		request.setAttribute("write_menu", "on");
		request.setAttribute("send_result_menu", "");
		request.setAttribute("calendar_menu", "");
		request.setAttribute("receive_group_menu", "");
		request.setAttribute("send_menu", "");
		request.setAttribute("sysman_menu", "");
		
		return super.preHandle(request, response, handler);
	}
}