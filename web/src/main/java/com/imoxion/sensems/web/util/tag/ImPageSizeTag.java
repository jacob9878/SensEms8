package com.imoxion.sensems.web.util.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ImPageSizeTag extends BodyTagSupport {

    public int doAfterBody() throws JspTagException {
        ImPageNavigationTag parent =
            (ImPageNavigationTag) findAncestorWithClass(this,
            		ImPageNavigationTag.class);
        if (parent == null) {
            throw new JspTagException("pageSize�� PageNavigation�ȿ� �־�� �˴ϴ�.");
        }
        int size = 1;
        if( getBodyContent().getString().trim().length() > 0 ){
        	size = Integer.parseInt(getBodyContent().getString().trim());
        }
        parent.setPageSize(size);

        return SKIP_BODY;
    }

}
