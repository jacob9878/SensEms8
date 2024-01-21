/**
 * 첨부파일 정보
 */
function Attach( name , size , islink , type , id ){
	this.name = name;
	this.size = size;
	this.islink = islink;
	this.type = type;
	this.id = id;
    this.toString = function(){
        return "attach[\nname="+this.name+"\nsize="+this.size+"\nislink=,"+this.islink+"\ntype="+this.type+"\nid="+this.id+"]";
    }
}

/**
 * ActiveX 업로드 컴포넌트를 이용시 필요한 메서드
 */
var uploadComponent = {	
	/**
	 * 웹하드 첨부
	 */	
	WEBHARD_ATTACH:3,
	/**
	 * 원본 첨부 파일
	 */
	ORIGINAL_ATTACH:2,
	/**
	 * 전달 메일 첨부
	 */
	FORWARD_ATTACH:4,
	/**
	 * 업로드 첨부파일
	 */
	UPLOAD_ATTACH:1,
	/**
	 * 최대 업로드 파일 수 
	 */
	max_file_count:10,
	/**
	 * 현재 등록된 파일 수
	 */
	curr_file_count:0,
	/**
	 * 최대 일반 첨부파일 크기
	 */
	max_nfile_size:1024*1024*10,//10MB
	/**
	 * 최대 대용량 첨부파일 크기
	 */
	max_bfile_size:1024*1024*500,//500MB
	/**
	 * 일반 첨부파일 사이즈
	 */
	nfile_size:0,
	/**
	 * 대용량 첨부파일 사이즈
	 */
	bfile_size:0,
	/**
	 * 첨부파일을 관리하는 MAP
	 */
	attachList:new Array(),
    _delete:null,
	init:function( option ){
        var uploadConfig = null;
        if( option ){
            uploadConfig = option._uploadConfig;
        }
		// 서버에서 설정한 최대 첨부파일 크기를 설정한다.
		if( uploadConfig ){
			this.max_bfile_size = uploadConfig.max_bigfile_size;
			this.max_file_count = uploadConfig.max_uploadfile_cnt;
			this.max_nfile_size = uploadConfig.max_attachfile_size;
			
			log.debug("max_bfile_size: "+this.max_bfile_size);			
			if(this.max_bfile_size < 0){
				$("#label_max_bigfile").hide();
			} else {
				$("#max_bigfile_size").html( this.max_bfile_size.byteFormat(0) );
			}
			
			log.debug("max_nfile_size: "+this.max_nfile_size);			
			if(this.max_nfile_size < 0){
				$("#div_add_file").hide();
			}else{
				$("#max_attachfile_size").html( this.max_nfile_size.byteFormat(0) );
			}
			
			$("#max_uploadfile_cnt").html( this.max_file_count );		
			
		}
        
        // 브라우저가 64비트인경우 64비트용 ActiveX 를 사용한다.
		if( navigator.platform == 'Win64'){
            uploadConfig.codebase = uploadConfig.codebase.replace("SensUpload.cab","SensUpload_x64.cab"); 
		}
		
		// 첨부파일 초기화
		this.attachList = new Array();
		var SendUploadComponent = new sensUpload("objFileUp");
		SendUploadComponent.setWidth("100%");
		SendUploadComponent.setHeight("120");
		SendUploadComponent.setCodeBase( uploadConfig.codebase );
		SendUploadComponent.setVersion( uploadConfig.version );
		SendUploadComponent.setCertKey( uploadConfig.certkey );
		SendUploadComponent.setTitle("File Upload");
		SendUploadComponent.setIsUTF("1");
		SendUploadComponent.setLimitFileCount(10);
		SendUploadComponent.setLimitFileSize( uploadConfig.max_attachfile_size );
		if( uploadConfig.use_doublepath ){
			SendUploadComponent.setUseDoublePath("1");	
		}else{
			SendUploadComponent.setUseDoublePath("0");
		}		
		
		if(uploadConfig.max_bigfile_size > 0){
			SendUploadComponent.setIsBigFile("1");
			SendUploadComponent.setLimitBigFile( uploadConfig.max_bigfile_size );
		}
		
		SendUploadComponent.setDownTerm("7");
		//SendUploadComponent.setlimitSuffix(sensmail.restrict_file);
		SendUploadComponent.writeObject("sensupload_component");
				
	},
	/**
	 * 파일 찾기 창을 띄운다.
	 */
	doSelFile:function(){
		var objFileUp = document.getElementById('objFileUp');
		objFileUp.AddFile();
	},
	/**
	 * 컴포넌트에서 선택된 파일들을 삭제한다.
	 */
	doDelFile:function(){
		var objFileUp = document.getElementById('objFileUp');
		objFileUp.DeleteFiles();
	},
	/**
	 * 컴포넌트에 등록된 파일들을 규칙에 맞게 정의함.
	 */
    getAttachments:function(){
        var attachSize = uploadComponent.attachList.length;
        var attachments = "";
        for(var i = 0 ; i < attachSize ; i++ ) {
            var attach = uploadComponent.attachList[i];
            if (attachments.length > 0) {
                attachments += ";";
            }
            attachments += attach.id + ":" + attach.islink + ":" + attach.type;
        }
        log.debug("setFile - attachments : "+attachments );
        return attachments;
	},
	/**
	 * 컴포넌트에 등록된 파일들을 규칙에 맞게 정의함.
	 */
    getAttachInfoList:function(){
        var attachSize = uploadComponent.attachList.length;
        var attachments = "";
        for(var i = 0 ; i < attachSize ; i++ ) {
            var attach = uploadComponent.attachList[i];
            if (attachments.length > 0) {
                attachments += ";";
            }
            attachments += attach.name + ":" + attach.size + ":" + attach.type;
        }
        log.debug("setFile - attachments : "+attachments );
        return attachments;
	},
	/**
	 * 웹하드 첨부파일을 컴포넌트에 등록한다.
	 * @param id
	 * @param name
	 * @param size
	 */
	addWebhardFile:function( id, name, size ){		
		this.addServerFile( name , size , id , uploadComponent.WEBHARD_ATTACH );
	},
	/**
	 * 전달메일 추가
	 */
	addForwardMailFile:function( id, name, size ){
        this.addServerFile( name , size , id , uploadComponent.FORWARD_ATTACH );
	},
	/**
	 * 원본 첨부파일 추가
	 */
	addOriginalFile:function( id, name, size ){
	    this.addServerFile( name , size , id , uploadComponent.ORIGINAL_ATTACH );
	},
	/**
	 * 서버 파일( 웹하드,원본,전달메일)를 추가한다.
	 * @param fileName
	 * @param fileSize
	 * @param param
	 * @param flag
	 */
	addServerFile:function(fileName , fileSize , param , flag ){
		
		log.debug("addServerFile - name : "+ fileName );
		/*log.debug("addServerFile - size : "+ fileSize );
		log.debug("addServerFile - id : "+ param );
		log.debug("addServerFile - type : "+ flag );*/ 
		
		if( this.curr_file_count >= uploadComponent.max_file_count ){
			jAlert( msg.get( uploaderMessage.MAX_ATTACH_COUNT , uploadComponent.max_file_count ) );
			return;
		}
		
		var time = 50;
		//log.debug("time - " + time);		
		var sensupload = document.getElementById("objFileUp");
		// 컴포넌트에 서버파일을 등록한다.
		setTimeout(function(){    		
    		sensupload.addServerFile( fileName , fileSize , param , flag );
    	}, time);
	},
	/**
	 * 컴포넌트에 등록된 파일 유형이 변경되었을 경우 실행한다.
	 * @param index
	 * @param islink
	 */
	changeAttachFileType:function( index , islink ){
		
		log.debug("changeAttachFileType index : "+ index);		
		
		var sensupload = document.getElementById("objFileUp");	
		
		var attach = uploadComponent.attachList[index];		
		if( !attach ){
			return;
		}
		
		var after_size = 0;
		
		if( islink == 0){
			after_size = uploadComponent.nfile_size + attach.size;			
			log.debug( "changeAttachFileType islink : " + islink + ", after_size : " + after_size );
			
			if( after_size > uploadComponent.max_nfile_size){
				sensupload.ChangeBigfile( index , attach.islink ); // 다시 대용량 파일로 변경한다.
				jAlert( uploaderMessage.IMPOSSIBLE_CHANGE_NORMALFILE );
				return;
			}			
		} else {
			after_size = uploadComponent.bfile_size + attach.size;
			log.debug( "changeAttachFileType islink : " + islink + ", after_size : " + after_size );
			
			if( after_size > uploadComponent.max_bfile_size ){
				jAlert( uploaderMessage.IMPOSSIBLE_CHANGE_BIGFILE );
				return;
			}
			
		}
		attach.islink = islink;
        uploadComponent.attachList[index] = attach;
		this.setAttachInfo();
	},
	/**
	 * 파일수,일반파일 용량, 대용량 파일 용량 계산
	 */
	setAttachInfo:function(){
		
		var attachSize = this.attachList.length;
		
		var filecount = 0;
		var nfile_size = 0;
		var bfile_size = 0;
		for(var i = 0 ; i < attachSize ; i++){
			var attach = this.attachList[i];
			if( attach.islink == 0 ){
				nfile_size += attach.size;
			}else{
				bfile_size += attach.size;
			}
			filecount++;
		}
		this.curr_file_count = filecount;
		this.nfile_size = nfile_size;
		this.bfile_size = bfile_size;
        log.debug("attach count : "+ uploadComponent.attachList.length );

		$("#curr_uploadfile_cnt").html( filecount );
		$("#attach_size").html( nfile_size.byteFormat(2) );
		$("#bigfile_size").html( bfile_size.byteFormat(2) );
	},
	/**
	 * 파일을 추가한다.
	 * @param attach
	 */
	add:function( index , attach ){

		// 업로드할 파일일 경우 type 을 변경한다.
		if( attach.type == -1 ){
			attach.type = uploadComponent.UPLOAD_ATTACH;
		}
        log.debug("file add - "+ attach.toString());
		this.attachList.push( attach );

		var sensupload = document.getElementById("objFileUp");
		
		// 일반첨부일때 파일 용량이 최대 일반첨부파일 용량보다 클 경우 대용량으로 변경해준다.
		if( attach.islink == 0 ){
			var after_size = this.nfile_size + attach.size;
			log.debug( "after_size : "+ after_size );
			log.debug( "this.max_nfile_size : "+ this.max_nfile_size );
			
			if( after_size > this.max_nfile_size){
				if(this.max_bfile_size > 0){
					attach.islink = 1;
					// 컴포넌트에 대용량파일로 변경한다.
					sensupload.ChangeBigfile( index , 1 );
					return;
				} else {
					// 등록된 첨부파일을 제거한다.
					sensupload.deleteFileForIndex( index );
					jAlert( msg.get( uploaderMessage.MAX_ATTACH_SIZE , uploadComponent.max_nfile_size.byteFormat(0) ) );
					return;
				}				
			} 		
		}
		
		// 첨부파일이 대용량일 경우 최대 용량보다 클 경우 등록하지 못한다고 처리한다.		
		if( attach.islink == 1){
			var after_size = this.bfile_size + attach.size;
			log.debug( "this.bfile_size : "+ this.bfile_size );
			log.debug( "attach.size : "+ attach.size );
			log.debug( "after_size : "+ after_size );
			log.debug( "this.max_bfile_size : "+ this.max_bfile_size );
			
			// attach.size < 0 은 컴포넌트에서 2G가 넘는 파일의 사이즈를 체크하지 못하고 음수로 나오기 때문에 추가함.
			if( after_size > uploadComponent.max_bfile_size || attach.size < 0 || uploadComponent.max_bfile_size < 0){
				// 등록된 첨부파일을 제거한다.
				sensupload.deleteFileForIndex( index );
								
				var max_file_size = (uploadComponent.max_bfile_size > 0)? uploadComponent.max_bfile_size.byteFormat(0) : uploadComponent.max_nfile_size.byteFormat(0);
				jAlert(msg.get( uploaderMessage.MAX_ATTACH_SIZE , max_file_size ) );
				return;
			}
		}
		this.setAttachInfo();
	},
	/**
	 * 등록된 첨부파일을 삭제한다.
	 * @param index
	 */
	del:function( index ){
        var attach = uploadComponent.attachList[index];
        /*if( attach.type == uploadComponent.ORIGINAL_ATTACH ) {
            this._delete(attach.id);
        }*/
        uploadComponent.attachList.remove(index);
        uploadComponent.setAttachInfo();
	}
	/**
	 * 컴포넌트를 초기화 한다.
	 */
	,reset:function(){
	},
	/**
	 * 등록된 첨부파일 목록을 가져온다
	 * @returns {Array}
	 */
	getFileList:function(){
    	
		var attachList = new Array();    	
    	var attachSize = uploadComponent.attachList.length;
    	for(var i = 0 ; i < attachSize ; i++ ){			
			var attach = uploadComponent.attachList[i];
			attachList.push( attach );
    	}
		return attachList;		
    },
    /**
     * 업로드 후 받은 파일 정보를 attachList 에 반영한다.
     * @param upload_attach
     */
    setFile:function(upload_attach){
        // 업로드 후 받은 업로드 파일 키를 attachList 의 ID 값으로 변환한다.
        log.debug("upload_attach : "+upload_attach);
        var arrUpload_attach = upload_attach.split(";");
        var arrIndex = 0;
        var attachments = "";
        var attachSize = uploadComponent.attachList.length;
        for(var i = 0 ; i < attachSize ; i++ ){
            var attach = uploadComponent.attachList[i];
            var attachType = attach.type;
            if( attachments.length > 0 ){
                attachments += ";";
            }
            if( attachType == uploadComponent.UPLOAD_ATTACH ){
                log.debug( arrUpload_attach[arrIndex] + " - "+ attach.name );
                uploadComponent.attachList[i].id = arrUpload_attach[arrIndex];
                arrIndex++;
            }
        }
    },
    refresh:function(){}
};