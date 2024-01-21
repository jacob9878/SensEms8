
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
        return "attach[\nname="+this.name+"\nsize="+this.size+"\nislink="+this.islink+"\ntype="+this.type+"\nid="+this.id+"]";
    }
    this.equals = function(id){
        return this.id == id;
    }
}

//플래시 버그 대응
function pluploadChangeFocus(){
	
	document.body.tabIndex = 0;
	document.body.focus();
	
}
/**
 * plupload 업로드 컴포넌트를 이용시 필요한 메서드
 */
var uploadComponent = {
    uploader:null,
    
    uploadURL:null,

    attachList:new Array(),
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
	 * 일반 업로드 첨부 파일
	 */
	UPLOAD_FILE:1,
	/**
	 * 최대 업로드 파일 수 
	 */
	max_file_count:10,
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
     * 파일을 선택하고 나서 실행할 로직
     */
    _callback:null,
    /**
     * 업로드가 완료되고 실행할 로직
     */
	_complete:null,
    /**
     * 파일을 삭제하고 실행할 로직
     */
    _delete:null,
    uploadProgress:null,
    /**
	 * 업로드 컴포넌트 초기화 
	 */
	init:function( option ){

        if( option ) {
            if (option._callback) {
                uploadComponent._callback = option._callback;
            }

            if( option._delete ){
                uploadComponent._delete = option._delete;
            }
            if( option._uploadConfig ){
                uploadComponent.uploadConfig = option._uploadConfig;
            }
            if( option._uploadURL ){
                uploadComponent.uploadURL = option._uploadURL;
            }
            if( option._complete ){
                uploadComponent._complete = option._complete;
            }
        }
		// 서버에서 설정한 최대 첨부파일 크기를 설정한다.
		if( uploadComponent.uploadConfig ){
			this.max_bfile_size = uploadComponent.uploadConfig.max_bigfile_size;
			this.max_file_count = uploadComponent.uploadConfig.max_uploadfile_cnt;
			this.max_nfile_size = uploadComponent.uploadConfig.max_attachfile_size;
            log.debug("max_bfile_size: "+this.max_bfile_size);
            log.debug("max_nfile_size: "+this.max_nfile_size);
            log.debug("max_file_count: "+this.max_file_count);

			if(this.max_bfile_size < 0){
				$("#label_max_bigfile").hide();
			} else {
				$("#max_bigfile_size").html( this.max_bfile_size.byteFormat(0) );
			}
			if(this.max_nfile_size < 0){
				$("#div_add_file").hide();
			} else {
				$("#max_attachfile_size").html( this.max_nfile_size.byteFormat(0) );
			}
			$("#max_uploadfile_cnt").html( this.max_file_count );
		}
		
		// 파일 수 및 용량 초기화
		this.nfile_size = 0;
		this.bfile_size = 0;
		var runtime_string = 'html5,flash,gears,silverlight,html4';

//		if(clientAgent.indexOf('Mac') > 0 && clientAgent.indexOf('Safari') > 0 &&  clientAgent.indexOf('Chrome') < 0){	
//			// 맥OS 사파리: html5가 업로드가 잘 안되는 문제, 플래시는 잘 되기는 하지만 좀 오래걸리는 문제..
//			//0903 : 맥OS 사파리에서 파일누락현상 발생. 
//			runtime_string = 'gears,html5,flash,silverlight';
//		}
		log.debug("uploadURL  :"+ this.uploadURL);
        var csrfToken = $("meta[name='_csrf']").attr("content");
		uploadComponent.uploader = new plupload.Uploader({
			// opera는 html4로 업로드한다.(flash가 잘 안됨)	
			runtimes : runtime_string,
			browse_button : 'pickfiles',
			drop_element : 'filelist', 	// html5에서 파일을 drag해서 놓을 위치
			container : 'pluploadComponent',
			unique_names : true,
			chunk_size: '100mb',
			max_file_size : plupload.formatSize( (uploadComponent.max_bfile_size > 0)? uploadComponent.max_bfile_size : ((uploadComponent.max_nfile_size > 0)? uploadComponent.max_nfile_size : 0) ),
			url : uploadComponent.uploadURL + "?_csrf="+ csrfToken,
			//resize : {width : 320, height : 240, quality : 90},
			flash_swf_url : app.static_server + '/sens-static/plugin/plupload/Moxie.swf',
			silverlight_xap_url : app.static_server + '/sens-static/plugin/plupload/Moxie.xap'
		});

        log.debug("plupload bind start...");
		// 초기화
		uploadComponent.uploader.bind('Init',function(){
            log.debug("plupload init ............................................" + uploadComponent.uploader.runtime);
            // 드래그 앤 드롭이 가능한 위치에 있을 경우 표시해주는 역활을 한다.
            if( uploadComponent.uploader.features.dragdrop && uploadComponent.uploader.runtime != 'html4' ) { // 드래그앤 드롭이 가능한 경우

            	if(uploadComponent.attachList.length ==0){
            		$('#drag_info').show(); // 파일을 이곳으로 끌어오세요.
            	}else{
            		$('#drag_info').hide();
            	}
                

                $("#filelist").bind("dragover",function(e){
                    //log.debug("dragover");
                    $(this).addClass("dragover");

                });
                $("#filelist").bind("dragenter",function(e){
                    log.debug("dragenter");
                    $(this).addClass("dragover");
                    //$("#drag_layer").show();
                });

                $("#filelist").bind("dragleave",function(e){
                    log.debug("dragleave");
                    $(this).removeClass("dragover");
                    //$("#drag_layer").hide();
                });

                $("#filelist").bind("drop",function(e){
                    $(this).removeClass("dragover");
                    log.debug("drop");
                    //$("#drag_layer").hide();
                });
            }else{
    			$("#drag_info").hide();
    		}
		});
		
		// 파일이 추가되었을때
		uploadComponent.uploader.bind('FilesAdded', function(up, files) {
			
			pluploadChangeFocus();

			var curr_file_length = uploadComponent.attachList.length; // 추가 전 현재 파일 수
			var isOverFileCount = false;	

			$.each(files, function(i, file) {
				if(!file) return true;

				// 파일 사이즈가 0byte 인경우 size 가 구해지지 않는 현상이 발생함. 0인경우 plupload 에 추가되지 않도록 수정함. by sunggyu
				/*if( !file.size ){
					return true;
				}*/
				curr_file_length++;
				
				// 파일사이즈가 최대 대용량 파일 사이즈 초과시 추가 금지
				var max_file_size = (uploadComponent.max_bfile_size > 0 ) ? uploadComponent.max_bfile_size : uploadComponent.max_nfile_size;

				if( file.size > max_file_size ){
				    uploadComponent.uploader.removeFile(file);				    
				    jAlert( msg.get( uploaderMessage.MAX_ATTACH_SIZE , max_file_size.byteFormat(0) ) );
					
				}else if( curr_file_length > uploadComponent.max_file_count ){ //등록된 파일 수가 기준치 오버
					
					// 제한 파일수가 넘어가면 uploader 에서 해당 파일을 제거한다.
					uploadComponent.uploader.removeFile(file);
					isOverFileCount = true;
																	
				} else {
					
					$("#filelist").show();
					
					// 현재 등록된 일반 첨부 용량 + 현재 파일 용량이 일반 첨부 제한용량보다 크면 대용량으로 변환한다.
					// 대용량 파일 최대업로드가 가능한지 체크
					var islink = 0;
					if( uploadComponent.max_bfile_size > 0 && uploadComponent.nfile_size + file.size > uploadComponent.max_nfile_size ){
						islink = 1;
					}
					
					if( islink == 1 ){
						// 대용량파일이경우 최대 대용량파일 용량이 오버했는지 체크 
						if( uploadComponent.bfile_size + file.size > uploadComponent.max_bfile_size ){
							uploadComponent.uploader.removeFile(file);
							return true;
						}
					}
					/*if(uploadComponent.uploader.runtime == 'html4'){
						return false;
					}*/
					uploadComponent.addServerFile( file.name , file.size , file.id , uploadComponent.UPLOAD_FILE );
				}			
			});
			if( isOverFileCount ){
				jAlert( msg.get( uploaderMessage.MAX_ATTACH_COUNT , uploadComponent.max_file_count ) );
				//alert(bundle.getString("1461"));
			}
		});

		// 업로드 직전에 처리(chunk일때 필수)
		uploadComponent.uploader.bind('BeforeUpload', function(up, file){
			up.settings.multipart_params = {
				filename: file.name
			};
		});

		// 모든 파일 업로드가 완료된 이후에 메일을 전송한다.
		uploadComponent.uploader.bind('UploadComplete', function(up, files) {

		    // 업로드 진행창을 닫는다.
		    uploadComponent.closeUploadProgress();
            setTimeout(function(){
				uploadComponent._complete();
            }, 500);
		});
		
		// 해당 파일이 업로드 완료 되었을때
		uploadComponent.uploader.bind('FileUploaded', function(up, file, response){
			log.debug("uploadComponent FileUploaded : "+ file.id);
			var obj;
			try{
				obj = jQuery.parseJSON(response.response);
			}catch(e){
				var serverData = response.response.replace(/^<pre.*>(.+)<\/pre>$/,'$1');
				obj = jQuery.parseJSON(serverData);
			}
			if( obj.result == JSONResult.FAIL ){
				file.status = plupload.FAILED;
				jAlert( obj.resultMsg);			
				uploadComponent.uploader.removeFile(uploadComponent.uploader.getFile(file.id));
                uploadComponent.attachDelete(file.id);
				uploadComponent.uploader.stop();
			}else if( obj.result == JSONResult.SUCCESS ) {
                var index = uploadComponent.findAttachIndexById(file.id);
                if (index != -1) {
                    uploadComponent.attachList[index].filekey = obj.filekey;
                }
            }
		});
				
		uploadComponent.uploader.bind('UploadFile', function(up, file) {
			$('<input type="hidden" name="file-' + file.id + '" value="' + file.name + '" />').appendTo('#submit-form');
		});
		
		uploadComponent.uploader.bind('Error', function(up, err) { 
			if(err.code == '-600'){	// 파일사이즈 초과
				var max_file_size = (uploadComponent.max_bfile_size > 0)? uploadComponent.max_bfile_size.byteFormat(0) : uploadComponent.max_nfile_size.byteFormat(0);								
				jAlert(msg.get( uploaderMessage.MAX_ATTACH_SIZE , max_file_size ) );
			} else {
				//$("#uploader_err").show();
				$("#idAttNoObj").hide();
				
				jAlert("Upload error: " + err.message + '(' + err.code + ')');
				//uploadComponent.reset();
			}
			// 업로드 실패시 메일 발송이 안되도록 차단
			uploadComponent.uploader.stop();
			// e.preventDefault();
		});

		// 업로드가 진행중일때
		uploadComponent.uploader.bind('UploadProgress', function(up, file) {
			//$('#' + file.id + " b").html(file.percent + "%");
			//$('#'+file.id+' .percentage').html('('+file.percent + " / " + up.total.size + '%)');
			log.debug("uploading.. attach_"+file.id + " ( "+ file.percent +" ) ");			
			uploadComponent.progress( file.percent, "attach_"+ file.id );
		});
		
		// 파일찾기에서 파일을 선택하는 순간.
		uploadComponent.uploader.bind('QueueChanged', function( up ) {

			// 확장자 제한 확인
			var restrict = ','+sensmail.restrict_file+',';
			var allow_ext = ','+sensmail.allow_file+',';
			var b_restrict = false;
			$.each(up.files, function(i, file) {
				if(!file) return true;
				var ext = '.'+ImUtils.getFileExtension(file.name)+',';
				
				if(sensmail.use_file_ext_chk == '1'){
					if(restrict.indexOf(ext) > -1){
	                    uploadComponent.uploader.removeFile(file);
	                    uploadComponent.attachDelete(file.id);
						b_restrict = true;
					}
				}else if(sensmail.use_file_ext_chk == '2'){
					if(allow_ext.indexOf(ext) < 0){
	                    uploadComponent.uploader.removeFile(file);
	                    uploadComponent.attachDelete(file.id);
						b_restrict = true;
					}
				}
			});
			if(b_restrict){
				if(sensmail.use_file_ext_chk == '1'){
					jAlert(msg.get(message_common.CM0079,sensmail.restrict_file));
				}else if(sensmail.use_file_ext_chk == '2'){
					jAlert(msg.get(message_common.CM0089,sensmail.allow_file));
				}				
				return false;
			}
			up.refresh(); 
			//uploadComponent.uploader.start();
			// 파일 첨부 영역 스타일 변경
			//$("#filelist").addClass("attach_added");
			pluploadChangeFocus();
			
	    });

        $('#uploadfiles').click(function(e) {
			uploadComponent.uploader.start();
			e.preventDefault();
		});
		
		$('#uploadstop').click(function(e) {
			uploadComponent.uploader.stop();
			e.preventDefault();
		});
        uploadComponent.uploader.init();
        log.debug("upload init");
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
            attachments += attach.filekey + ":" + attach.islink + ":" + attach.type;
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
	 * 업로드 컴포넌트에 데이터를 표시한다.
	 * @param id
	 * @param name
	 * @param size
	 */
	addServerFile:function( name , size , id , type ) {

        var curr_file_count = uploadComponent.attachList.length; // 현재 파일 수
        curr_file_count++;

        // 파일 최대 수를 초과하면 등록을 못하게 한다.
        if (curr_file_count > uploadComponent.max_file_count) {
            jAlert(msg.get( uploaderMessage.MAX_ATTACH_COUNT, uploadComponent.max_file_count));
            return;
        }

        // 현재 등록된 일반 첨부 용량 + 현재 파일 용량이 일반 첨부 제한용량보다 크면 대용량으로 변환한다.
        var islink = 0;
        if (uploadComponent.nfile_size + parseInt(size) > uploadComponent.max_nfile_size) {
            if (uploadComponent.max_bfile_size > 0) {
                islink = 1;
            } else {
                log.debug("uploadComponent.max_bfile_size: " + uploadComponent.max_bfile_size);
                log.debug("uploadComponent.nfile_size + parseInt(size): " + uploadComponent.nfile_size + parseInt(size));
                log.debug("uploadComponent.max_nfile_size: " + uploadComponent.max_nfile_size);
                jAlert(msg.get( uploaderMessage.MAX_ATTACH_SIZE, uploadComponent.max_nfile_size.byteFormat(0)));
                return;
            }
        }
        var attach = new Attach();
        attach.name = name;
        attach.size = size;
        attach.id = id;
        attach.filekey = id;
        attach.type = type;
        attach.islink = islink;
        this.attachList.push( attach );
        log.debug("addServerFile : "+ attach.toString());
        uploadComponent.setAttachInfo();
	},
	
	/**
	 * 컴포넌트에 등록된 파일 유형이 변경되었을 경우 실행한다.
	 * @param file_id
	 * @param flag
	 */
	changeAttachFileType:function( file_id , flag ){
		
		var max_nfile_size = uploadComponent.max_nfile_size;
		var max_bfile_size = uploadComponent.max_bfile_size;

        var index = uploadComponent.findAttachIndexById(file_id);
        var attach = uploadComponent.attachList[index];

		var islink = attach.islink;
		var filesize = attach.size;

		// 대용량첨부에서 일반첨부로 변경할때 일반첨부파일 제한용량을 오버 했는지 체크한다.		
		if( islink == 1 ){
			//alert( attach_limit + " : " + (parseInt(attach_size) + parseInt(filesize)) ); 
			if( max_nfile_size < uploadComponent.nfile_size + parseInt(filesize) ){
				jAlert( uploaderMessage.IMPOSSIBLE_CHANGE_NORMALFILE );
				return;
			}
		}else{
			if( max_bfile_size < uploadComponent.bfile_size + parseInt(filesize) ){
				jAlert( uploaderMessage.IMPOSSIBLE_CHANGE_BIGFILE );
				return;
			}
		}
		var n_islink = islink == 1 ? 0 : 1;
        uploadComponent.attachList[index].islink = n_islink;
		uploadComponent.setAttachInfo();
	},
	/**
	 * 업로드 파일 삭제
	 */
	delAttach:function(file_id){
		try{
			uploadComponent.uploader.removeFile(uploadComponent.uploader.getFile(file_id));
		}catch(e){
			log.error("delAttach error - " + e);
		}
		uploadComponent.attachDelete( file_id );
	},
	/**
	 * 첨부파일 목록을 인터페이스에서 제거한다.
	 */
	attachDelete:function( id ){
        var index = uploadComponent.findAttachIndexById(id);
        if( index != -1 ) {
            uploadComponent.attachList.remove(index);
        }
        if( this._delete ) {
            this._delete(id);
        }
        uploadComponent.setAttachInfo();
	},
	/**
	 * 첨부파일 모두 선택
	 */
	attach_selectall:function(){
		var checked = $("#attach_all").is(":checked");
		$("input[name='attach']").each(function(){
			$(this).attr("checked",checked);
		});
	},
	/**
	 * 선택된 파일 삭제
	 */
	doDelFile:function(){
		var check_attach_cnt = 0;
        // UI 에서 체크된 attach id 를 구한다.
		$("input[name='attach']:checked").each(function(){
			var id = $(this).attr("value");
            var type = $(this).attr("attach_type");
            log.debug("doDelFile - id : "+ id );
            log.debug("doDelFile - type : "+ type );
			if( type == uploadComponent.UPLOAD_FILE ){
				uploadComponent.delAttach( id );
			}else{
				uploadComponent.attachDelete( id );
			}
			check_attach_cnt++;
		});
		if( check_attach_cnt == 0 ){
			jAlert( uploaderMessage.DELETEFILE_SELECT);
			return;
		}
		$("#attach_all").attr("checked",false); // 모두 선택 해제
	},
	/**
	 * 컴포넌트에 등록된 파일들의 용량 및 개수를 구한다.
	 * 개수 및 용량을 구한 뒤에 첨부파일 정보란에 업데이트 한다.
	 */
	setAttachInfo:function() {

        uploadComponent.nfile_size = 0; // 설정된 값 초기화
        uploadComponent.bfile_size = 0;// 설정된 값 초기화
        var curr_file_count = 0;// 첨부파일 카운트 초기화

        var attachCount = uploadComponent.attachList.length;
        for(var i = 0 ; i < attachCount ; i++ ){
            var attach = uploadComponent.attachList[i];
            var islink = attach.islink;
            if(attach.size == undefined){
            	uploadComponent.nfile_size = undefined;
            	uploadComponent.bfile_size = undefined;
            }else{
                if (islink == 1) {
                    uploadComponent.bfile_size += parseInt(attach.size);
                } else {
                    uploadComponent.nfile_size += parseInt(attach.size);
                }
                curr_file_count++;
            }
        }
        $("#curr_uploadfile_cnt").html(uploadComponent.attachList.length);
        if(uploadComponent.nfile_size == undefined){
            $("#attach_size").html("N/A");
        }else{
            $("#attach_size").html(uploadComponent.nfile_size.byteFormat(2));
        }

        if (uploadComponent.max_bfile_size > 0) {
            if(uploadComponent.bfile_size == undefined){
                $("#bigfile_size").html("N/A");
            }else{
                $("#bigfile_size").html(uploadComponent.bfile_size.byteFormat(2));
            }
        }
		if( uploadComponent.uploader && uploadComponent.uploader.features.dragdrop && uploadComponent.uploader.runtime != 'html4' ){
			// 등록된 파일이 존재하면 드래그 안내를 없앤다.
			if( uploadComponent.attachList.length == 0 ){
				$("#drag_info").show();
			}else{
				$("#drag_info").hide();
			}
		}else{
			$("#drag_info").hide();
		}
        if( uploadComponent._callback ) {
            uploadComponent._callback();
        }
	},
	reset:function(){
        if( this.uploader ) {
            var attachCount = uploadComponent.attachList.length;
            for (var i = 0; i < attachCount; i++) {
                var attach = uploadComponent.attachList[i];
                // attach가 null 인 경우가 있어서..
                if(!attach) continue;
                var id = attach.id;
                if (attach.type == uploadComponent.UPLOAD_FILE) {
                    uploadComponent.delAttach(id);
                } else {
                    uploadComponent.attachDelete(id);
                }
            }
            uploadComponent.attachList = new Array();
        }
	},
	/**
	 * 업로드 프로그레스바 상태를 업데이트 한다. 
     * @param {Object} p 진행률(%)
     * @param {Object} id 업데이트 객체 아이디
	 */
    progress:function(p, id) {
        $('#'+id+' .progress_bar').progressbar("value",p);
    },
    /**
     * 업로드 중지 
     */
    stop:function(){
        uploadComponent.uploader.stop();        
        jAlert( message_common.CM0060 );
    },
    getFileList:function(){
        var attachList = new Array();
        var attachSize = uploadComponent.attachList.length;
        for(var i = 0 ; i < attachSize ; i++ ){
            var attach = uploadComponent.attachList[i];
            attachList.push( attach );
        }
        return attachList;
    },
    openUploadProgress:function(){
        log.debug("upload progress init");
        /**
         * 첨부파일 업로드 프로그레스
         */
        var html = [],n=-1;
        html[++n] = "<div class=\"modal_area\">";
        html[++n] = "<div class=\"modal\" style=\"padding:5px;\">";
        html[++n] = "<ul class=\"upload_progress\" id=\"upload_progress\">";
        var attachCount = uploadComponent.attachList.length;
        for(var i = 0 ; i < attachCount ; i++ ){
            var attach = uploadComponent.attachList[i];
            var fileid = attach.id;
            var filename = attach.name;
            html[++n] = "<li id=\"attach_"+ fileid +"\">";
            html[++n] = "<dl>";
            html[++n] = "<dt>";
            html[++n] = filename;
            html[++n] = "</dt>";
            html[++n] = "<dd>";
            html[++n] = "<div class=\"progress_bar\"></div>";
            html[++n] = "</dd>";
            html[++n] = "</dl>";
            html[++n] = "</li>";
        };
        html[++n] = "</ul>";
        html[++n] = "</div>";
        html[++n] = "</div>";

        var modal = html.join('');
        this.uploadProgress = $(modal).dialog({
            title: uploaderMessage.FILE_UPLOAD,
            bgiframe: true,
            autoOpen: true,
            height: 400,
            width: 500,
            modal: true,
            resizable : false,
            closeOnEscape : false,
            draggable : false,
            buttons:[
                {
                    text : uploaderMessage.CANCEL,
                    click:function(){
                        uploadComponent.uploadProgress.dialog("close");
                        uploadComponent.stop();
                    }
                }
            ],
            close:function(){
                uploadComponent.uploadProgress.dialog("destroy");
            },
            open:function(){
                log.debug("upload progress open");
                $("#upload_progress .progress_bar").progressbar({
                    value:0
                });
            }
        });
    },
    closeUploadProgress:function() {
        log.debug("upload progress close");
        uploadComponent.uploadProgress.dialog("close");
    },
    /**
     * PLUPLOAD 파일 ID 를 이용하여 attachList의 index 를 구한다.
     * @param id
     * @returns {number}
     */
    findAttachIndexById:function(id){
        var attachCount = uploadComponent.attachList.length;
        for(var i=0;i<attachCount;i++){
            if( uploadComponent.attachList[i].id == id ){
                return i;
            }
        }
        return -1;
    },
    refresh:function(){
        if( this.uploader ){
            this.uploader.refresh();
            log.debug("upload refresh");
        }
    },
    start:function(){
        uploadComponent.openUploadProgress();
        this.uploader.start();
    },
    /**
	 * 보내기 말고 사전에 개별 업로드 하는 기능
     */
	preUpload:function(){
        $(body).append("<div id='FilePreviewContainer' style='display:none;'><button id='previewBtn'></button><div id='preViewDropzone'></div></div>");

        var csrfToken = $("meta[name='_csrf']").attr("content");
        var runtime_string = 'gears,html5,flash,silverlight,html4';
        var uploader = new plupload.Uploader({
            runtimes : runtime_string,
            container : 'FilePreviewContainer',
            browse_button : 'previewBtn',
            drop_element : 'preViewDropzone', 	// html5에서 파일을 drag해서 놓을 위치
            unique_names : true,
            //chunk_size: '5mb',
            max_file_size : plupload.formatSize( (uploadComponent.max_bfile_size > 0)? uploadComponent.max_bfile_size : ((uploadComponent.max_nfile_size > 0)? uploadComponent.max_nfile_size : 0) ),
            url : uploadComponent.uploadURL + "?_csrf="+ csrfToken,
            flash_swf_url : app.static_server + '/sens-static/plugin/plupload/Moxie.swf',
            silverlight_xap_url : app.static_server + '/sens-static/plugin/plupload/Moxie.xap'
        });
        // 파일이 추가되었을때
        uploader.bind('FilesAdded', function (up, files) {
            $.each(files, function (i, file) {
                log.debug("FilesAdded : "+ file );

                if (!file) return true;

                // 파일사이즈가 최대 대용량 파일 사이즈 초과시 추가 금지
                var max_file_size = (uploadComponent.max_bfile_size > 0 ) ? uploadComponent.max_bfile_size : uploadComponent.max_nfile_size;
                if (file.size > max_file_size) {
                    uploader.removeFile(file);
                    jAlert(msg.get(uploaderMessage.MAX_ATTACH_SIZE, max_file_size.byteFormat(0)));
                }else{
                }
            });
        });

        // 모든 파일 업로드가 완료된 이후에 메일을 전송한다.
        uploader.bind('UploadComplete', function (up, files) {
            log.debug("file preview upload complete");
            up.destroy();
        });

        // 해당 파일이 업로드 완료 되었을때
        uploader.bind('FileUploaded', function (up, file, response) {
            log.debug("FileUploaded");
            var obj;
            try {
                obj = jQuery.parseJSON(response.response);
            } catch (e) {
                var serverData = response.response.replace(/^<pre.*>(.+)<\/pre>$/, '$1');
                obj = jQuery.parseJSON(serverData);
            }
            if (obj.result == JSONResult.FAIL) {
                file.status = plupload.FAILED;
                jAlert(uploaderMessage.UPLOAD_ERROR);
            } else if (obj.result == JSONResult.SUCCESS) {
                log.debug("upload file key : " + obj.filekey );
                // 이 obj.filekey 를 이용하면 됨
                // 실제 메일 발송시에 이미 업로드된 파일을 구하기 위해 plupload id 에 해당하는 attach 의 file key 를 구한다.
                var index = uploadComponent.findAttachIndexById(file.id);
                if (index != -1) {
                    uploadComponent.attachList[index].filekey = obj.filekey;
                }
            }
        });

        uploader.bind('UploadFile', function (up, file) {
            $('<input type="hidden" name="file-' + file.id + '" value="' + file.name + '" />').appendTo('#submit-form');
        });

        uploader.bind('Error', function (up, err) {
            log.debug("Upload error " + err.code );
            if (err.code == '-600') {	// 파일사이즈 초과
                var max_file_size = (uploadComponent.max_bfile_size > 0) ? uploadComponent.max_bfile_size.byteFormat(0) : uploadComponent.max_nfile_size.byteFormat(0);
                jAlert(msg.get(uploaderMessage.MAX_ATTACH_SIZE, max_file_size));
            } else {
                jAlert("Upload error: " + err.message + '(' + err.code + ')');
            }
            up.destroy();
        });

        // 업로드가 진행중일때
        uploader.bind('UploadProgress', function (up, file) {
            log.debug("uploading.. attach_" + file.id + " ( " + file.percent + " ) ");
        });
        uploader.bind('Destroy', function (up) {
            $("#FilePreviewContainer").remove();
        });
        uploader.init();
        var file = uploadComponent.uploader.getFile(id);
        log.debug("preview id :"+ id +" , file : "+ file.id );
        uploader.addFile(file);
        window.setTimeout(function(){
            uploader.start();
        },500);
	}
};