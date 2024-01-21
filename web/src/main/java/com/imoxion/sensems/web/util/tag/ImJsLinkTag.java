package com.imoxion.sensems.web.util.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ImJsLinkTag extends BodyTagSupport {

    public int doAfterBody() throws JspTagException {
    	ImPageNavigationTag parent =
            (ImPageNavigationTag) findAncestorWithClass(this,
            		ImPageNavigationTag.class);
        if (parent == null) {
            throw new JspTagException("jslink�� PageNavigation�ȿ� �־�� �˴ϴ�.");
        }
        String jslink = getBodyContent().getString().trim();
        parent.setJsLink(jslink);

        return SKIP_BODY;
    }

}
