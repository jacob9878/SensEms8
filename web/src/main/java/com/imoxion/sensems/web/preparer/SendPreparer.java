/**
 * @author zpqdnjs
 * 메일쓰기 매뉴 preparer
 */
package com.imoxion.sensems.web.preparer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class SendPreparer extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
						
		request.setAttribute("write_menu", "");
		request.setAttribute("send_result_menu", "");
		request.setAttribute("calendar_menu", "");
		request.setAttribute("receive_group_menu", "");
		request.setAttribute("send_menu", "on");
		request.setAttribute("sysman_menu", "");
		
		String url = request.getRequestURI();
		if(url.indexOf("/category/") > -1){			
			request.setAttribute("category_menu", "on");
			request.setAttribute("image_menu", "");
			request.setAttribute("template_menu", "");
			request.setAttribute("testaccount_menu", "");
			request.setAttribute("reserve_send_menu", "");
			request.setAttribute("reject_menu", "");
		}else if(url.indexOf("/image/") > -1){
			request.setAttribute("category_menu", "");
			request.setAttribute("image_menu", "on");
			request.setAttribute("template_menu", "");
			request.setAttribute("testaccount_menu", "");
			request.setAttribute("reserve_send_menu", "");
			request.setAttribute("reject_menu", "");
		}else if(url.indexOf("/template/") > -1){
			request.setAttribute("category_menu", "");
			request.setAttribute("image_menu", "");
			request.setAttribute("template_menu", "on");
			request.setAttribute("testaccount_menu", "");
			request.setAttribute("reserve_send_menu", "");
			request.setAttribute("reject_menu", "");
		}else if(url.indexOf("/demoaccount/") > -1){
			request.setAttribute("category_menu", "");
			request.setAttribute("image_menu", "");
			request.setAttribute("template_menu", "");
			request.setAttribute("testaccount_menu", "on");
			request.setAttribute("reserve_send_menu", "");
			request.setAttribute("reject_menu", "");
		}else if(url.indexOf("/reserve/") > -1){
			request.setAttribute("category_menu", "");
			request.setAttribute("image_menu", "");
			request.setAttribute("template_menu", "");
			request.setAttribute("testaccount_menu", "");
			request.setAttribute("reserve_send_menu", "on");
			request.setAttribute("reject_menu", "");
		}else if(url.indexOf("/reject/") > -1){
			request.setAttribute("category_menu", "");
			request.setAttribute("image_menu", "");
			request.setAttribute("template_menu", "");
			request.setAttribute("testaccount_menu", "");
			request.setAttribute("reserve_send_menu", "");
			request.setAttribute("reject_menu", "on");
		}
		
		return super.preHandle(request, response, handler);
	}
}