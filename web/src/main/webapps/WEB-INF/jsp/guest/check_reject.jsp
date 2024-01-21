<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<head>
	<title><spring:message code="title" text="SensEMS"/></title>

	<!--[if lt IE 10]>
	<link type="text/css" rel="stylesheet" href="/sens-static/css/ie.css" />
	<![endif]-->

	<!--[if lt IE 9]>
	<script src="/sens-static/js/common/html5shiv.min.js"></script>
	<script src="/sens-static/js/common/ie9.min.js"></script>
	<![endif]-->

	<script type="text/javascript">
		$(document).ready(function(){
			var param = {
				email:"${email}",
				msgid:"${msgid}"
			};

			if(confirm("<spring:message code="E0758" text="실수로 수신거부 버튼을 누르셨다면 취소버튼을 눌러주세요."/>")){
				$.ajax({
					url: "reject.json",
					type: "post",
					data: param,
					dataType: "json",
					async: false,
					error:function(xhr, txt){
						AjaxUtil.error( xhr );
					},
					success: function (data){
						 alert(data.message);
						 self.close();
					}
				});
			} else{
				self.close();
			}

		});
	</script>

</head>
