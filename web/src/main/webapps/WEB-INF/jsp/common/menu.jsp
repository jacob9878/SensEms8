<%@ page pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/account/myinfo.js"></script>
<body class="skin1">
<div class="wrap">
	<div class="left_menu">
			<div class="left_logo">
				<a href="/mail/result/list.do"><img src="/sens-static/images/left_logo.png" alt="로고" /></a>
			</div>

			<div class="profile ">
				<div class="info">
					<div class="info_box ">
						<a href="" class="user_name" >${UserInfo.name}</a>
						<a href="javascript:;" class="user_membership" onclick="myinfo.infoPage();">${userInfo.userid}<spring:message code="E0516" text="내정보"/></a>
<%--						<a href="" class="user_email over_text">${UserInfo.email}</a>--%>
					</div>

				</div>
				<div class="info_box2">
					<div class="help"><a href="/account/helpDownload.do" ><spring:message code="E0517" text="HELP"/></a></div>
						<p></p>
					<div class="logout"><a href="/account/logout.do"><spring:message code="E0582" text="LOGOUT"/></a></div>

<%--					<div class="language">--%>
<%--						<span class="icon_public"></span>--%>
<%--						<select class="languageList" onclick="">--%>
<%--							<option ${f:isSelected( 'ko' , UserInfo.language ) } value="ko">KOR</option>--%>
<%--							<option ${f:isSelected( 'en' , UserInfo.language ) } value="en">ENG</option>--%>
<%--							<option ${f:isSelected( 'ja' , UserInfo.language ) } value="ja">JPN</option>--%>
<%--							<option ${f:isSelected( 'zh' , UserInfo.language ) } value="zh">CHN</option>--%>
<%--						</select>--%>
<%--					</div>--%>
				</div>
			</div>

			<div class="nav">
				<ul>
					<li class="menu_1depth ${write_menu}"><a href="/mail/write/form.do"><span class="icon_public depth1_01"></span><spring:message code="E0393" text="메일쓰기"/></a></li>

					<li class="menu_1depth ${send_result_menu}">
						<a href="javascript:;" onclick="common.select_menu('send_result_menu')" class="folder_off" ><span class="icon_public depth1_02"></span><spring:message code="E0361" text="메일발송 결과"/></a>
						<ul id="send_result_menu_li" >
							<li class="menu_2depth ${result_menu}"><a href="/mail/result/list.do"><spring:message code="E0361" text="메일발송 결과"/></a></li>
							<li class="menu_2depth ${each_menu}"><a href="/mail/result/sendList.do"><spring:message code="E0572" text="개별발송 결과"/></a></li>
						</ul>
					</li>
					<%--<li class="menu_1depth ${calendar_menu}"><a href="/calendar/schedule.do"><span class="icon_public depth1_03"></span><spring:message code="E0518" text="발송일정관리"/></a></li>--%>
					<li class="menu_1depth ${receive_group_menu}">
						<a href="javascript:;" onclick="common.select_menu('receive_group_menu')" class="folder_off"><span class="icon_public depth1_04"></span><spring:message code="E0519" text="수신자 관리"/></a>
						<ul id="receive_group_menu_li">
							<li class="menu_2depth ${group_menu}"><a href="/receiver/group/list.do"><spring:message code="E0169" text="수신그룹 관리"/></a></li>
							<li class="menu_2depth ${address_menu}"><a href="/receiver/address/list.do"><spring:message code="E0306" text="개인주소록 관리"/></a></li>
						</ul>
					</li>
					<li class="menu_1depth ${send_menu}">
						<a href="javascript:;" onclick="common.select_menu('send_menu')" class="folder_off"><span class="icon_public depth1_05"></span><spring:message code="E0520" text="발송 관리"/></a>
						<ul id="send_menu_li">
							<li class="menu_2depth ${category_menu}"><a href="/send/category/list.do"><spring:message code="E0010" text="발송분류 관리"/></a></li>
							<li class="menu_2depth ${image_menu}"><a href="/send/image/list.do"><spring:message code="E0096" text="이미지 관리"/></a></li>
							<li class="menu_2depth ${template_menu}"><a href="/send/template/list.do"><spring:message code="E0118" text="템플릿 관리"/></a></li>
							<li class="menu_2depth ${testaccount_menu}"><a href="/send/demoaccount/list.do"><spring:message code="E0161" text="테스트계정 관리"/></a></li>
							<%--<li class="menu_2depth ${reserve_send_menu}"><a href="/send/reserve/list.do"><spring:message code="E0230" text="정기예약발송 관리"/></a></li>--%>
							<li class="menu_2depth ${reject_menu}"><a href="/send/reject/list.do"><spring:message code="E0521" text="수신거부 관리"/></a></li>
						</ul>
					</li>
					<sec:authorize access="hasRole('ROLE_ADMIN')">
						<li class="menu_1depth ${sysman_menu}">
							<a href="javascript:;" onclick="common.select_menu('sysman_menu')" class="folder_off"><span class="icon_public depth1_06"></span><spring:message code="E0522" text="시스템 관리자"/></a>
							<ul id="sysman_menu_li">
								<li class="menu_2depth ${user_menu}"><a href="/sysman/user/list.do"><spring:message code="E0016" text="사용자 관리"/></a></li>
								<li class="menu_2depth ${sendfilter_menu}"><a href="/sysman/sendfilter/list.do"><spring:message code="E0523" text="발송차단 설정"/></a></li>
								<li class="menu_2depth ${database_menu}"><a href="/sysman/database/list.do"><spring:message code="E0126" text="데이터베이스 관리"/></a></li>
								<li class="menu_2depth ${attach_menu}"><a href="/sysman/attach/restrict.do"><spring:message code="E0201" text="첨부파일 확장자 관리"/></a></li>
								<li class="menu_2depth ${actionlog_menu}"><a href="/sysman/actionlog/list.do"><spring:message code="E0324" text="사용자 활동로그 조회"/></a></li>
								<li class="menu_2depth ${file_menu}"><a href="/sysman/attach/list.do"><spring:message code="E0743" text="첨부파일 관리"/></a></li>
								<li class="menu_2depth ${receipt_menu}"><a href="/sysman/receipt/list.do"><span class="${now_receiptlog}"><spring:message code="E0571" text="수신확인 코드 조회"/></span></a></li>
								<li class="menu_2depth ${dkim_menu}"><a href="/sysman/dkim/list.do"><spring:message code="E0617" text="DKIM 관리"/></a></li>
									<%-- gs 인증 끝난 후 다시 주석해제 예정--%>
                                        <%--<li class="menu_2depth ${individual_menu}"><a href="javascript:;" onclick="common.select_menu('individual_menu')"><spring:message code="E0522" text="개별발송 설정"/></a>
                                        <ul id="individual_menu_li">
                                            <li class="menu_3depth ${relayip_menu}"><a href="/sysman/relay/list.do"><spring:message code="E0324" text="연동IP 관리"/></a></li>
                                            <li class="menu_3depth ${blockip_menu}"><a href="/sysman/block/list.do"><spring:message code="E0324" text="차단IP 관리"/></a></li>
                                            <li class="menu_3depth ${limit_menu}"><a href="/sysman/limit/list.do"><spring:message code="E0324" text="발송제한 설정"/></a></li>
                                                &lt;%&ndash;									<li class="menu_2depth ${smtp_menu}"><a href="/sysman/smtp/list.do"><spring:message code="E0324" text="SMTP인증 계정"/></a></li>&ndash;%&gt;
                                        </ul>
                                    </li>--%>
							</ul>
						</li>
					</sec:authorize>
				</ul>
			</div>
	</div>
		</div>
</body>
		<!-- left area end -->