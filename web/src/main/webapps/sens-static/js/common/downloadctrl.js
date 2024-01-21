function Param(name, val) {
	this.name = name;
	this.val= val;
};

function sensDownload(objName) {
	this.objName = objName;
	this.version = null;
	this.codebase = null;
	this.arrParams = [];
	this.width = "100%";
	this.height = "80";
	this.title = "SensMail File Download";
	this.UserID = "";
	this.DownURL = "";
	this.DownTargetPath = "";

	this.language = "0";
	this.limit_file_count = 10;
	this.is_utf = 0;
	this.limit_file_size = 0;
	this.certkey = null;
	this.uninstall_msg = "";
	this.url_path = "";
};

sensDownload.prototype.addParam = function(name, value) {
	this.arrParams[this.arrParams.length] = new Param(name, value);
};


sensDownload.prototype.setVersion = function(ver) {
	this.version = ver;
};

sensDownload.prototype.setWidth = function(w) {
	this.width = w;
};

sensDownload.prototype.setHeight = function(h) {
	this.height = h;
};

sensDownload.prototype.setCodeBase = function(path) {
	this.codebase = path;
};

sensDownload.prototype.setUserID = function(userid) {
	this.setUserID = userid;
	this.addParam("UserID",userid);
};

sensDownload.prototype.setTitle = function(tit) {
	this.title = tit;
	this.addParam("Title",tit);
};

sensDownload.prototype.setDownURL = function(downurl) {
	this.DownURL = downurl;
	this.addParam("DownURL",downurl);
};

sensDownload.prototype.setDownTargetPath = function(downtargetpath) {
	this.DownTargetPath = downtargetpath;
	this.addParam("DownTargetPath",downtargetpath);
};


sensDownload.prototype.writeParam = function(){
	if(obj = document.getElementById(this.objName)){
		var i = 0;
		for(i = 0; i < this.arrParams.length; i++){
			document.write("<param name='"+this.arrParams[i].name+"' value='"+this.arrParams[i].val+"'>");
		}
	}
};
	  
sensDownload.prototype.writeUninstalledMsg = function(){
	if(this.uninstall_msg != null && this.uninstall_msg != ""){
		document.write(	 this.uninstall_msg );
	}
};

sensDownload.prototype.initObject = function(){
	var objStrStart = "<object width='"+this.width+"' height='"+this.height+"' id='"+this.objName+"' name='"+this.objName+"'"
				+ "classid='CLSID:9F4151FD-FD9B-462A-8F60-F54F3A0CD4B7' codebase='"+this.codebase+"#version="+this.version+"'>"; 
	var objStrEnd = "</object>";

	document.write ( objStrStart );
	this.writeParam();
	this.writeUninstalledMsg();
	document.write ( objStrEnd );
}


