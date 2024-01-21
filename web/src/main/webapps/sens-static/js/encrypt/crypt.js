var password;
var encryptKey;
var rsa = new RSAKey(); 
var crypt = {
	enCryptInit:function(){
		var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
		var string_length = 16;
		var rdmStr = '';
		for (var i=0; i<string_length; i++) {
			var rnum = Math.floor(Math.random() * chars.length);
			rdmStr += chars.substring(rnum,rnum+1);
		} 
		
		password = rdmStr;
		
		var n;
		var e;
		
		$.ajax({
			url: "/jCrypt/json/makeKey.json" ,
			type : "get",
			dataType:"json",
			async:false,
			error:function(xhr, txt){
				jAlert("암호화에 실패하였습니다.");
			},
			success:function(data,status){
				if(status=="success"){
					n = data.n;
					e = data.e;
					rsa.setPublic(n, e);
				}
			}
		});
		
	},
	enCrypt:function(data){
		var encryptedString = null;
		//password로 전송할 데이터 암호화
		GibberishAES.size(128);
		encryptedString = GibberishAES.aesEncrypt(data, password);
		
		//base64로 암호화된 password를 인코딩
		encryptKey = rsa.encrypt(password);
		encryptKey = hex2b64(encryptKey);
		
		return encryptedString;
	}
};