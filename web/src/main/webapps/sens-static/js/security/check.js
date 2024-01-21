var securityChk = {
	check : function(weburl, secuParam, userInsertPasswd,bodyText) {
		
		if(weburl == '' || secuParam == '' || bodyText == ''){
			alert("잘못된 접근입니다.");
			return;
		}

		var url = weburl + '/secumail/json/limitchk.do';
		var param = {
			'secuParam' : secuParam,
			'inputP':userInsertPasswd
		};
		$.ajax({
					url : url,
					data : param,
					dataType : "jsonp",
					async : false,
					jsonpCallback : "callback",
					success : function(encoded) {
						var result = encoded.result;
						if (result == '0') {
							alert(encoded.resultMsg);
						}else {
							var aesUtil = new AesUtil();

							try {
								var result = aesUtil.checkPassDecrypt(encoded.iv,encoded.salt,
										userInsertPasswd,  bodyText);
								if (result == "0") {
									alert("비밀번호가 틀렸습니다. 다시한번 확인해주세요.");
									return;
								}

								$("#passwordModal").dialog("close");
								document.body.innerHTML = result;
								document.getElementById("idSendDate").textContent = encryptForm.senddate.value;
								document.getElementById("idfrom").textContent = encryptForm.from.value;
								document.getElementById("idto").textContent = encryptForm.to.value;
								document.getElementById("idcc").textContent = encryptForm.cc.value;
								document.getElementById("idSubject").textContent = encryptForm.subject.value;

							} catch (exception) {
								alert("오류가 발생하였습니다.::\n"+exception);
							}					
						}
					}
				});
	}
};