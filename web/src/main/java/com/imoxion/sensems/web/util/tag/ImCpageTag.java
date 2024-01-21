package com.imoxion.sensems.web.util.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * cpage 태그; 현재 페이지 번호를 받아서 PageNavigation의 cpage에 저장한다.
 */
public class ImCpageTag extends BodyTagSupport {

    public int doAfterBody() throws JspTagException {
        ImPageNavigationTag parent =
            (ImPageNavigationTag) findAncestorWithClass(this,
            		ImPageNavigationTag.class);
        if (parent == null) {
            throw new JspTagException("cpage는 PageNavigation안에 있어야 됩니다.");
        }
        int cpage = 1;
        if( getBodyContent().getString().trim().length() > 0 ){
        	cpage = Integer.parseInt(getBodyContent().getString().trim());
        }
        parent.setCpage(cpage);

        return SKIP_BODY;
    }

}
