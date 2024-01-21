package com.imoxion.sensems.web.util.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ImLinkTag extends BodyTagSupport {

    public int doAfterBody() throws JspTagException {
    	ImPageNavigationTag parent =
            (ImPageNavigationTag) findAncestorWithClass(this,
            		ImPageNavigationTag.class);
        if (parent == null) {
            throw new JspTagException("link�� PageNavigation�ȿ� �־�� �˴ϴ�.");
        }
        String link = getBodyContent().getString().trim();
        parent.setLink(link);

        return SKIP_BODY;
    }

}
