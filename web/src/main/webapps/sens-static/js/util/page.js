function pageInfo(cpage, pageSize, total, link, jslink) {
    var str = [], n = -1;
    str[++n] = "<ul>";
    if (total == 0) {
        str[++n] = "<li class=\"none\"><a href=\"javascript:;\"><span class=\"btn first\"></span></a></li>";
        str[++n] = "<li class=\"none\"><a href=\"javascript:;\"><span class=\"btn prev\"></span></a></li>";
        str[++n] = "<li class=\"on\"><a href=\"javascript:;\"><span class=\"number\">1</span></a></li>";
        str[++n] = "<li class=\"none\"><a href=\"javascript:;\"><span class=\"btn next\"></span></a></li>";
        str[++n] = "<li class=\"none\"><a href=\"javascript:;\"><span class=\"btn end\"></span></a></li>";
        str[++n] = "</ul>";
        return str.join('');
    }

    var pageGroupSize = 5;
    //   전체페이지
    var totalPage = Math.ceil(total / pageSize); // 페이지수

    if (totalPage < cpage) cpage = totalPage;

    //    이전5개, 다음5개
    //    이전 마지막 페이지 0 이면 이전5개 없음
    var prev5 = Math.floor((cpage - 1) / pageGroupSize) * pageGroupSize;

    if (prev5 < 0) prev5 = 0;

    //    다음 첫페이지 totalPage 보다 크면 다음10개 없음
    var next5 = prev5 + pageGroupSize + 1;

    // 현재 1페이지일 경우 가장 앞으로 가는 버튼을 안나오게 한다.
    if (cpage > 1) {
        if (link != null && link.length > 0) {
            str[++n] = "<li><a class=\"page_first icon_public\" href=\"" + link + "&cpage=1" + "\"></a></li>";
        } else if (jslink != null && jslink.length > 0) {
            str[++n] = "<li><a class=\"page_first icon_public\" href=\"javascript:;\" onclick=\"" + jslink + "'1')" + "\"></a></li>";
        }
    } else {

    }
    if (prev5 > 0) {
        if (link != null && link.length > 0) {
            str[++n] = "<li><a class=\"page_prev icon_public\" href=\"" + link + "&cpage=" + prev5 + "\"></a></li>";
        } else if (jslink != null && jslink.length > 0) {
            str[++n] = "<li><a class=\"page_prev icon_public\" href=\"javascript:;\" onclick=\"" + jslink + " '" + prev5 + "')\"></a></li>";
        }
    } else {

    } // end if 이전10개

    for (var i = 1 + prev5; i < next5 && i <= totalPage; i++) {
        if (i == cpage) {
            str[++n] = "<li class=\"on\"><a href=\"javascript:;\">" + i + "</a></li>";
            if ((i + 1) < next5 && (i + 1) <= totalPage) {// 이후 표시할 페이지 있을 경우 num_bar 를 표시한다.
                str[++n] = "";
            }
        } else {
            if (link != null && link.length > 0) {
                str[++n] = "<li><a href=\"" + link + "&cpage=" + i + "\">" + i + "</a></li>";
            } else if (jslink != null && jslink.length > 0) {
                str[++n] = "<li><a href=\"javascript:;\" onclick=\"" + jslink + "'" + i + "')\">" + i + "</a></li>";
            }
        } // end if 현재페이지 링크제거
    } // end for
    //str[++n] = "</span>";
    if (totalPage >= next5) {
        if (link != null && link.length > 0) {
            str[++n] = "<li><a class=\"page_next icon_public\" href=\"" + link + "&cpage=" + next5 + "\"></a></li>";
        } else if (jslink != null && jslink.length > 0) {
            str[++n] = "<li><a class=\"page_next icon_public\" href=\"javascript:;\" onclick=\"" + jslink + "'" + next5 + "')\" title='next'></a></li>";
        }
    } else {  // end if 다음10개
    
    }
    if (totalPage > cpage) { // 가장 마지막 페이지에서 가장 뒤로 가는 페이지를 안보이게 한다.
        if (link != null && link.length > 0) {
            str[++n] = "<li><a class=\"page_last icon_public\" href=\"" + link + "&cpage=" + totalPage + "\"></a></li>";
        } else if (jslink != null && jslink.length > 0) {
            str[++n] = "<li><a class=\"page_last icon_public\" href=\"javascript:;\" onclick=\"" + jslink + " '" + totalPage + "')\"></a></li>";
        }
    } else {

    }


    return str.join('');

    // 바로가기(직접 페이지 입력)
    /*
    var goto_str = "<input type='text' id='current_page' value='"+cpage+"' align='absmiddle' class='input current_page'><a href='javascript:lst.gotoPage()' class='go'>GO</a>";
    if( total > 0 ){
    	return str.join('')+goto_str;
    }else{
    	return "";
    }
    */
}

/**
 *
 * @param cpage
 * @param pageSize
 * @param total
 * @param link
 * @param jslink
 * @return
 */
function pageInfoForPrevNext(cpage, pageSize, total, link, jslink) {

    if (total == 0) {
        return "";
    }

//	var pageGroupSize = 10;

    //   전체페이지
    var totalPage = Math.ceil(total / pageSize); // 페이지수

    if (totalPage < cpage) {
        cpage = totalPage;
    }

    var prevPage = 1;
    var nextPage = 1;
    if (totalPage > 1) {
        if (cpage > 1) {
            prevPage = cpage - 1;
        }

        if (totalPage > cpage) {
            nextPage = cpage + 1;
        }
    }

    var str = [], n = -1;
    ;
    // 현재 1페이지일 경우 가장 앞으로 가는 버튼을 안나오게 한다.
    if (cpage > 1) {
        if (link != null && link.length > 0) {
            str[++n] = "<a href=\"" + link + "&cpage=" + prevPage + "\" class=\"arrow_prev_on\">";
            str[++n] = message_common.CM0008; // 이전

            str[++n] = "</a>";
        } else if (jslink != null && jslink.length > 0) {
            str[++n] = "<a href=\"javascript:;\" onclick=\"" + jslink + "('" + prevPage + "')" + "\" class=\"arrow_prev_on\">";
            str[++n] = message_common.CM0008; // 이전
            str[++n] = "</a>";
        }
    } else {
        str[++n] = "<a href=\"javascript:;\" onclick=\"" + jslink + "('" + prevPage + "')" + "\" class=\"arrow_prev_off\">";
        str[++n] = message_common.CM0008; // 이전
        str[++n] = "</a>";
    }

    // str[++n] = "&nbsp;<img width=\"5\" height=\"11\" src=\"/sens-static/images/common/tit_bar.gif\"/>&nbsp;";
    if (totalPage > cpage) {
        if (link != null && link.length > 0) {
            str[++n] = "<a href=\"" + link + "&cpage=" + nextPage + "\" class=\"arrow_next_on\">";
            str[++n] = message_common.CM0009; //다음
            str[++n] = "</a>";
        } else if (jslink != null && jslink.length > 0) {
            str[++n] = "<a href=\"javascript:;\" onclick=\"" + jslink + "('" + nextPage + "')\" class=\"arrow_next_on\">";
            str[++n] = message_common.CM0009; //다음
            str[++n] = "</a>";
        }
    } else {
        str[++n] = "<a href=\"javascript:;\" class=\"arrow_next_off\">";
        str[++n] = message_common.CM0009; //다음
        str[++n] = "</a>";
    }
    return str.join('');
}