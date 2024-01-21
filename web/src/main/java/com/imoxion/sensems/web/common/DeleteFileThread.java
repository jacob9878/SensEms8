package com.imoxion.sensems.web.common;

import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteFileThread extends Thread {


	private Logger logger = LoggerFactory.getLogger( "error");
	private File file = null;

	public DeleteFileThread(File file) {
		this.file = file;
	}

	public void run() {
		deleteFile( file );
	}
	
	public synchronized void deleteFile(File file){
		try{
			if( file.exists() ){
				if (file.isDirectory()) {
					if (file.getAbsolutePath().equals("/")) {
						return;
					}
					File[] fileList = file.listFiles();
					for (int i = 0; fileList != null && i < fileList.length; i++) {
						File f = fileList[i];
						this.deleteFile(f);
					}
						file.delete();
					} else {
						file.delete();
					}
			}
		}catch (NullPointerException ne){
			String errorId = ErrorTraceLogger.log(ne);
			logger.error("NullPointerException : {}", errorId);
		} catch (Exception e1){
			String errorId = ErrorTraceLogger.log(e1);
			logger.error("deleteFile Error : {}",errorId);
		}
	}
	
	/*public static void main(String[] args){
		File f = new File("d:/test");
		DeleteFileThread deleteFileThread = new DeleteFileThread(f);
		deleteFileThread.start();
	}*/
}