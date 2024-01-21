/**
 * @author zpqdnjs
 * 수신그룹관리 매뉴 preparer
 */
package com.imoxion.sensems.web.preparer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class ReceiverPreparer extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
						
		request.setAttribute("write_menu", "");
		request.setAttribute("send_result_menu", "");
		request.setAttribute("calendar_menu", "");
		request.setAttribute("receive_group_menu", "on");
		request.setAttribute("send_menu", "");
		request.setAttribute("sysman_menu", "");
		
		String url = request.getRequestURI();
		if(url.indexOf("/group/") > -1){
			request.setAttribute("group_menu", "on");
			request.setAttribute("address_menu", "");
		}else if(url.indexOf("/address/") > -1){
			request.setAttribute("group_menu", "");
			request.setAttribute("address_menu", "on");
		}
		
		return super.preHandle(request, response, handler);
	}
}