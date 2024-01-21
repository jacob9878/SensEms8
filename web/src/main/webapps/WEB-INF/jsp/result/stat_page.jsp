
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<%@ include file="../inc/common.jsp" %>
<!DOCTYPE HTML>

<body class="skin1">
<input type="hidden" id="msgid" value="${emsbean.msgid}">
<div class="statisticsPage">
    <!-- content area start -->
    <div class="article content">
        <div>
            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                <tbody>
                <tr >
                    <td>${emsbean.contents}</td>
                    </td>

                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>

