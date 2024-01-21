

var AesUtil = function() {
  this.keySize = 128 / 32;
  this.iterationCount = 10000;  
};

AesUtil.prototype.generateKey = function(salt, passPhrase) {
  var key = CryptoJS.PBKDF2(
      passPhrase, 
      CryptoJS.enc.Hex.parse(salt),
      { keySize: this.keySize, iterations: this.iterationCount });
  return key;
}

AesUtil.prototype.encrypt = function(iv_key,salt,passPhrase, plainText) {
  var key = this.generateKey(salt, passPhrase);
  var encrypted = CryptoJS.AES.encrypt(
      plainText,
      key,
      { iv: CryptoJS.enc.Hex.parse(iv_key) });
  return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
}

AesUtil.prototype.decrypt = function(iv_key,salt,passPhrase, cipherText) {
  var key = this.generateKey(salt, passPhrase);
  var cipherParams = CryptoJS.lib.CipherParams.create({
    ciphertext: CryptoJS.enc.Base64.parse(cipherText)
  });
  var decrypted = CryptoJS.AES.decrypt(
      cipherParams,
      key,
      { iv: CryptoJS.enc.Hex.parse(iv_key) });
  return decrypted.toString(CryptoJS.enc.Utf8);
}

AesUtil.prototype.checkdateDecrypt = function(iv,salt,passPhrase, param1, param2, param3){
	var today = new Date();
	var day = today.getDate();
	var month = today.getMonth() + 1;
	var year = today.getFullYear();
	
	if(day < 10){
		day = "0" + day;
	}
		
	if (month  < 10){
		month  = "0" + month;
	}
	
	var open_today = year + "" + month + "" + day;
	
	var de_param1 = this.decrypt(iv,salt,passPhrase, param1);
	var de_param2 = this.decrypt(iv,salt,passPhrase, param2);
	
	if (de_param1 == "" || de_param2 == "") {
		return "0";
	}
	
	if ((de_param1 != de_param2) && (open_today < de_param1 || open_today > de_param2)){
		return "2";		
	}
	
	var de_param3 = this.decrypt(iv,salt,passPhrase, param3);
	if (de_param3 == "") {
		return "0";
	}
	
	return de_param3;
}


AesUtil.prototype.checkPassDecrypt = function(iv,salt,passPhrase, param){
	var de_param = this.decrypt(iv,salt,passPhrase, param);
	if (de_param == "") {
		return "0";
	}
	
	return de_param;
}