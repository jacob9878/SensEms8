/**
 * @author zpqdnjs
 * 시스템 관리자 매뉴 preparer
 */
package com.imoxion.sensems.web.preparer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SysmanPreparer extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		request.setAttribute("write_menu", "");
		request.setAttribute("send_result_menu", "");
		request.setAttribute("calendar_menu", "");
		request.setAttribute("receive_group_menu", "");
		request.setAttribute("send_menu", "");
		request.setAttribute("sysman_menu", "on");

		
		String url = request.getRequestURI();
		if(url.indexOf("/user/") > -1){
			request.setAttribute("user_menu", "on");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");

		}else if(url.indexOf("/sendfilter/") > -1){			
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "on");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("reject_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/database/") > -1){			
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "on");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/attach/restrict.do") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "on");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/actionlog/") > -1){			
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "on");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/relay/") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "on");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("individual_menu", "on");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/attach/list.do") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "on");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/receipt/") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "on");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/dkim/") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "on");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/block/") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("individual_menu", "on");
			request.setAttribute("blockip_menu", "on");
			request.setAttribute("limit_menu", "");
		}else if(url.indexOf("/limit/") > -1){
			request.setAttribute("user_menu", "");
			request.setAttribute("sendfilter_menu", "");
			request.setAttribute("database_menu", "");
			request.setAttribute("attach_menu", "");
			request.setAttribute("actionlog_menu", "");
			request.setAttribute("relayip_menu", "");
			request.setAttribute("file_menu", "");
			request.setAttribute("receipt_menu", "");
			request.setAttribute("dkim_menu", "");
			request.setAttribute("individual_menu", "on");
			request.setAttribute("blockip_menu", "");
			request.setAttribute("limit_menu", "on");
		}
		
		return super.preHandle(request, response, handler);
	}
}