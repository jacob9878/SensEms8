package com.imoxion.sensems.web.util.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * total 태그; 전체 게시물 갯수를 받아온다.
 */
public class ImTotalTag extends BodyTagSupport {

    public int doAfterBody() throws JspTagException {
        ImPageNavigationTag parent =
            (ImPageNavigationTag) findAncestorWithClass(this,
            		ImPageNavigationTag.class);
        if (parent == null) {
            throw new JspTagException("total는 PageNavigation안에 있어야 됩니다.");
        }
        int total = 0;
        if( getBodyContent().getString().trim().length() > 0 ){
        	total = Integer.parseInt(getBodyContent().getString().trim());
        }
        parent.setTotal(total);

        return SKIP_BODY;
    }

}
