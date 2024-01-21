package com.imoxion.sensems.web.util.tag;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * 페이지 네비게이션을 표시하는 태그
 *
 * @author kenu
 *
 */
public class ImPageNavigationTag extends TagSupport {

    private int cpage;
    private int total;
    private int pageSize;
    private String link = null;
    private String jslink = null;

    public int doStartTag() {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() {
    //    전체페이지
    	int totalPage = (total-1)/pageSize + 1;
    //    이전5개, 다음5개
    //    이전 마지막 페이지 0 이면 이전5개 없음
        int prev5 = (int)Math.floor((cpage-1) / 5.0) * 5;
        if( prev5 < 0 ) prev5 = 0;
    //    다음 첫페이지 totalPage 보다 크면 다음5개 없음
        int next5 = prev5 + 6;
        StringBuffer sbuf = new StringBuffer();
        // 현재 1페이지일 경우 가장 앞으로 가는 버튼을 안나오게 한다.
        sbuf.append("<ul>\n");
        if( cpage > 1 ){
	        if(link != null && link.length() > 0){
	            sbuf.append("<li><a class=\"page_first icon_public\" href=\"").append(link)
                        .append("&cpage=1").append("\"></a></li>\n");
	        } else if(jslink != null && jslink.length() > 0){
                sbuf.append("<li><a class=\"page_first icon_public\" href=\"javascript:;\" onclick=\"").append(jslink)
                        .append("('1')").append("\"></a></li>\n");
	        }
        }else{

        }
        if(prev5 > 0) {
            if(link != null && link.length() > 0){
                sbuf.append("<li><a class=\"page_prev icon_public\" href=\"").append(link)
                        .append("&cpage=").append(prev5)
                        .append("\"></a></li>\n");

            } else if(jslink != null && jslink.length() > 0){
                sbuf.append("<li><a class=\"page_prev icon_public\" href=\"javascript:;\" onclick=\"").append(jslink)
                        .append("('").append(prev5).append("')\"></a></li>\n");
            }
        }else{
        }
        // end if 이전5개

        //각 페이지 처리

        for (int i = 1+prev5 ; i < next5 && i <= totalPage ; i++ ) {
            if (i==cpage) {
            	sbuf.append("<li class=\"on\"><a>").append(i).append("</a></li>\n");
            } else {
                if(link != null && link.length() > 0){
                    sbuf.append("<li><a href=\"").append(link)
                        .append("&cpage=").append(i).append("\">")
                        .append(i).append("</a></li>\n");
                } else if(jslink != null && jslink.length() > 0){
                    sbuf.append("<li><a href=\"javascript:;\" onclick=\"").append(jslink)
                        .append("('").append(i).append("')\">")
                        .append(i).append("</a></li>\n");
                }
            } // end if 현재페이지 링크제거
        } // end for

        if(totalPage >= next5) {
            if(link != null && link.length() > 0){
                sbuf.append("<li><a class=\"page_next icon_public\" href=\"").append(link)
                        .append("&cpage=").append(next5).append("\"></a></li>\n");
            } else if(jslink != null && jslink.length() > 0){
                sbuf.append("<li><a class=\"page_next icon_public\" href=\"javascript:;\" onclick=\"").append(jslink)
                        .append("('").append(next5).append("')\"></a></li>\n");
            }
        }else{

        }
        // end if 다음10개

        if( totalPage > cpage ){ // 가장 마지막 페이지에서 가장 뒤로 가는 페이지를 안보이게 한다.
	        if(link != null && link.length() > 0){
                sbuf.append("<li><a class=\"page_last icon_public\" href=\"").append(link)
                        .append("&cpage=").append(totalPage)
                        .append("\"></a></li>\n");
	        } else if(jslink != null && jslink.length() > 0){
                sbuf.append("<li><a class=\"page_last icon_public\" href=\"javascript:;\" onclick=\"").append(jslink)
                        .append("('").append(totalPage).append("')\"></a></li>\n");
	        }
        }else{

        }
        sbuf.append("</ul>");
        JspWriter out = pageContext.getOut();
        try {
        	if( total > 0 ){
        		out.print(sbuf.toString());
        	}
        } catch (IOException e) {
            System.out.println( e.toString() );
        }
        return EVAL_PAGE;
    }
    /**
     * Sets the cpage.
     * @param cpage The cpage to set
     */
    public void setCpage(int cpage) {
        this.cpage = cpage;
    }

    /**
     * Sets the pageSize.
     * @param pageSize The pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Sets the total.
     * @param total The total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Sets the link.
     * @param link The link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Sets the javascript link.
     * @param jslink The jslink to set
     */
    public void setJsLink(String jslink) {
        this.jslink = jslink;
    }
}
