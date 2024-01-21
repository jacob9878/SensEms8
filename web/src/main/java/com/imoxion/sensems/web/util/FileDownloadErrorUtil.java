package com.imoxion.sensems.web.util;

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
 * 파일다운로드 오류 발생시 에러메세지를 처리를 담당하는 유틸이다.
 * jdownload 파라미터 여부에 따라 String 또는 에러페이지로 보여준다.
 * @author sunggyu
 */
public class FileDownloadErrorUtil {

    public static ModelAndView getMessage(final String msg){
        View view = new AbstractView() {
            @Override
            protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

                boolean jdownload = "true".equals(request.getParameter("jdownload"));
                if( !jdownload ){
                    response.sendError(400);
                    return;
                }

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

}
