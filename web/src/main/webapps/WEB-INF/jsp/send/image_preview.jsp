<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE HTML>
<html lang="ko-KR">
<head>
	<meta charset="UTF-8">
	<title>SensEMS</title>
	<link rel="stylesheet" href="/sens-static/css/style.css" />
	<link rel="stylesheet" href="/sens-static/css/login.css" />
	<link rel="stylesheet" href="/sens-static/css/skin.css" />	
	<script type="text/javascript" src="/sens-static/plugin/jquery/jquery-2.2.2.min.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
</head>
<body class="skin1">
	<table>
		<tbody>
			<tr>
				<td>
					<a href="javascript:self.close();"><img src="/send/${type}/view.do?ukey=${ukey}" /></a>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>

